package github.jadetang.aiven.metric.producer;

import github.jadetang.aiven.metric.common.model.Metric;
import github.jadetang.aiven.metric.producer.collector.MachineIdentifyProvider;
import github.jadetang.aiven.metric.producer.collector.MetricCollector;
import github.jadetang.aiven.metric.producer.collector.MetricCollectorFactory;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  private static final Logger log = LoggerFactory.getLogger(Main.class);

  private static final ReentrantLock LOCK = new ReentrantLock();

  private static final Condition STOP = LOCK.newCondition();

  public static void main(final String[] args) throws IOException, ConfigurationException {
    if (args.length != 1) {
      log.error("Please provide command line argument: producer_properties_path");
      System.exit(1);
    }

    final ProducerConfiguration producerConfiguration = new ProducerConfiguration(args[0]);

    final Properties producerProperties = producerConfiguration.cloneProperties();
    producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        "org.apache.kafka.common.serialization.StringSerializer");
    producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        "github.jadetang.aiven.metric.common.MetricSerializer");
    final Producer<String, Metric> producer = new KafkaProducer<>(producerProperties);

    final MachineIdentifyProvider identifyProvider = producerConfiguration::getMachineIdentify;
    final List<MetricCollector> metricCollectors = producerConfiguration.getMetricCategories().stream()
        .map(metricCategory -> MetricCollectorFactory.newCollector(identifyProvider, metricCategory))
        .collect(Collectors.toList());

    final MetricsProducer metricsProducer = new MetricsProducer(producerConfiguration, metricCollectors, producer);

    addHook(metricsProducer);
    metricsProducer.start();

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

  private static void addHook(final MetricsProducer metricsProducer) {
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      metricsProducer.stop();
      log.info("JVM exits, MetricsProducer stopped.");

      try {
        LOCK.lock();
        STOP.signal();
      } finally {
        LOCK.unlock();
      }
    }, "MetricsProducer-shutdown-hook"));
  }
}
