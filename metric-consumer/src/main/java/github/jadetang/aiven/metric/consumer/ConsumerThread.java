package github.jadetang.aiven.metric.consumer;

import github.jadetang.aiven.metric.common.model.Metric;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerThread implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(ConsumerThread.class);

  private final Consumer<String, Metric> kafkaConsumer;

  private final MetricWriter metricWriter;

  private final Duration pollDuration;

  public ConsumerThread(final Consumer<String, Metric> kafkaConsumer, final MetricWriter metricWriter,
      final Duration pollDuration) {
    this.kafkaConsumer = kafkaConsumer;
    this.metricWriter = metricWriter;
    this.pollDuration = pollDuration;
  }

  @Override
  public void run() {
    try {
      while (true) {
        final ConsumerRecords<String, Metric> records = kafkaConsumer.poll(pollDuration);
        final List<Metric> metrics = new ArrayList<>(records.count());
        for (final ConsumerRecord<String, Metric> record : records) {
          metrics.add(record.value());
        }
        metricWriter.batchInsert(metrics);
        kafkaConsumer.commitAsync((offsets, exception) -> {
          if (exception != null) {
            log.warn("Error when committing async", exception);
          }
        });
      }
    } catch (WakeupException e) {
      log.info("Received a wakeup exception.");
    } catch (Exception e) {
      log.error("Error when consuming message.", e);
    } finally {
      try {
        kafkaConsumer.commitSync();
      } finally {
        log.info("Closing the consumer.");
        kafkaConsumer.close();
      }
    }
  }

  public void stop() {
    kafkaConsumer.wakeup();
  }
}
