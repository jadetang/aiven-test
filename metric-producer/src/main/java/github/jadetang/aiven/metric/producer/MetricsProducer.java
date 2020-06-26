package github.jadetang.aiven.metric.producer;

import static java.time.temporal.ChronoUnit.SECONDS;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import github.jadetang.aiven.metric.common.model.Metric;
import github.jadetang.aiven.metric.producer.collector.MetricCollector;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.errors.InterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricsProducer {

  private static final Logger log = LoggerFactory.getLogger(MetricsProducer.class);

  private final List<MetricCollector> metricCollectors;

  private final ProducerConfiguration producerConfiguration;

  private final Producer<String, Metric> kafkaProducer;

  private final ScheduledExecutorService scheduledExecutor;


  private volatile boolean running;

  public MetricsProducer(final ProducerConfiguration producerConfiguration,
      final List<MetricCollector> metricCollectors,
      final Producer<String, Metric> kafkaProducer) {
    this.producerConfiguration = producerConfiguration;
    this.metricCollectors = metricCollectors;
    this.running = false;
    this.scheduledExecutor = Executors.newScheduledThreadPool(this.producerConfiguration.getSchedulerThreadNumber(),
        new ThreadFactoryBuilder().setNameFormat("Metric-producer-thread-%d").build());
    this.kafkaProducer = kafkaProducer;
  }

  public synchronized void start() {
    if (running) {
      return;
    }
    running = true;
    log.info("Start reporting metrics: {} to topic {}.", producerConfiguration.getMetricCategories(),
        producerConfiguration.getTopic());
    scheduledExecutor.scheduleAtFixedRate(
        new ProducerThread(producerConfiguration.getTopic(), metricCollectors, kafkaProducer),
        1, producerConfiguration.getMetricCollectIntervalInSec(), TimeUnit.SECONDS);
  }

  public synchronized void stop() {
    if (!running) {
      return;
    }
    closeThreadPool();
    closeProducer();
  }

  private void closeThreadPool() {
    log.info("Closing scheduler thread pool producer.");
    scheduledExecutor.shutdown();
    try {
      scheduledExecutor.awaitTermination(10, TimeUnit.SECONDS);
    } catch (final InterruptedException e) {
      log.error("Error while stopping the scheduler thread pool");
      Thread.currentThread().interrupt();
    }
  }

  private void closeProducer() {
    log.info("Closing Kafka producer.");
    try {
      kafkaProducer.flush();
      kafkaProducer.close(Duration.of(10, SECONDS));
    } catch (final InterruptException e) {
      log.error("Error while closing the producer.");
      Thread.currentThread().interrupt();
    }
  }
}
