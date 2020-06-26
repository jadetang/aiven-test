package github.jadetang.aiven.metric.consumer;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import github.jadetang.aiven.metric.common.model.Metric;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import javax.sql.DataSource;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  private static final Logger log = LoggerFactory.getLogger(Main.class);

  private static final ReentrantLock LOCK = new ReentrantLock();

  private static final Condition STOP = LOCK.newCondition();

  public static void main(final String[] args) throws IOException, ConfigurationException {
    if (args.length != 2) {
      log.error("Please provide command line argument: consumer_properties_path datasoure_properties_path");
      System.exit(1);
    }

    final ConsumerConfiguration consumerConfiguration = new ConsumerConfiguration(args[0]);

    final DataSource dataSource = new HikariDataSource(new HikariConfig(args[1]));
    initializeDbSchema(dataSource);

    final Jdbi jdbi = Jdbi.create(dataSource).installPlugin(new SqlObjectPlugin())
        .installPlugin(new PostgresPlugin());
    final MetricWriter metricWriter = jdbi.onDemand(MetricWriter.class);

    final MetricConsumer metricConsumer = new MetricConsumer(
        initializeConsumerThreads(consumerConfiguration, metricWriter));

    addHook(metricConsumer);
    log.info("Start consuming metric from topic {}", consumerConfiguration.getTopic());
    metricConsumer.start();

    try {
      LOCK.lock();
      STOP.await();
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      log.warn("Interrupted by other thread", e);
    } finally {
      LOCK.unlock();
    }
  }

  private static void initializeDbSchema(final DataSource dataSource) {
    FluentConfiguration configuration = Flyway.configure().dataSource(dataSource).
        locations("classpath:database")
        .baselineOnMigrate(true).baselineVersion("1_20200625");
    Flyway flyway = configuration.load();
    flyway.migrate();
  }

  private static List<ConsumerThread> initializeConsumerThreads(final ConsumerConfiguration consumerConfiguration,
      final MetricWriter metricWriter) {
    final List<ConsumerThread> consumerThreads = new ArrayList<>();
    final Properties consumerProperties = consumerConfiguration.cloneProperties();
    consumerProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    consumerProperties.put("value.deserializer", "github.jadetang.aiven.metric.common.MetricDeserializer");
    for (int i = 0; i < consumerConfiguration.getConsumerCount(); i++) {
      Consumer<String, Metric> consumer = new KafkaConsumer<>(consumerProperties);
      consumer.subscribe(Collections.singleton(consumerConfiguration.getTopic()));
      consumerThreads.add(new ConsumerThread(consumer, metricWriter, consumerConfiguration.getPollDuration()));
    }
    return consumerThreads;
  }

  private static void addHook(final MetricConsumer metricConsumer) {
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      metricConsumer.stop();
      log.info("JVM exits, MetricConsumer stopped.");

      try {
        LOCK.lock();
        STOP.signal();
      } finally {
        LOCK.unlock();
      }
    }, "MetricsProducer-shutdown-hook"));
  }
}
