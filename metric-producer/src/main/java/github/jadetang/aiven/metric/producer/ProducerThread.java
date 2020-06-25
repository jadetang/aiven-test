package github.jadetang.aiven.metric.producer;

import github.jadetang.aiven.metric.common.model.Metric;
import github.jadetang.aiven.metric.producer.collector.MetricCollector;
import java.util.List;
import java.util.Objects;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerThread implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(ProducerThread.class);

  private final String topic;

  private final List<MetricCollector> reporters;

  private final Producer<String, Metric> kafkaProducer;

  public ProducerThread(final String topic, final List<MetricCollector> reporters,
      final Producer<String, Metric> kafkaProducer) {
    this.topic = Objects.requireNonNull(topic, "Topic should not be null.");
    this.reporters = reporters;
    this.kafkaProducer = kafkaProducer;
  }

  @Override
  public void run() {
    for (final MetricCollector reporter : reporters) {
      try {
        final List<Metric> metrics = reporter.collect();
        log.debug("Report {} metrics.", metrics.size());
        metrics.forEach(metric -> kafkaProducer
            .send(new ProducerRecord<>(topic, metric.getMachineIdentify(), metric), (metadata, exception) -> {
              if (exception != null) {
                log.error("Error sending message, metadata: {}.", metadata, exception);
              }
            }));
      } catch (Exception e) {
        log.error("Error when collecting message.", e);
      }
    }
  }
}
