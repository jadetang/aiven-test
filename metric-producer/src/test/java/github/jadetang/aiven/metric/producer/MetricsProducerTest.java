package github.jadetang.aiven.metric.producer;

import static java.time.temporal.ChronoUnit.MILLIS;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.salesforce.kafka.test.junit5.SharedKafkaTestResource;
import github.jadetang.aiven.metric.common.MetricDeserializer;
import github.jadetang.aiven.metric.common.MetricSerializer;
import github.jadetang.aiven.metric.common.model.Metric;
import github.jadetang.aiven.metric.producer.collector.MetricCollector;
import github.jadetang.aiven.metric.producer.collector.MetricCollectorFactory;
import github.jadetang.aiven.metric.producer.collector.MetricCollectorFactory.MetricCategory;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

class MetricsProducerTest {

  @RegisterExtension
  static final SharedKafkaTestResource kafka = new SharedKafkaTestResource()
      .withBrokers(1)
      .withBrokerProperty("auto.create.topics.enable", "false");

  @Test
  void testProduceMetricHappyPath() throws InterruptedException, IOException, ConfigurationException {
    //given
    final ProducerConfiguration producerConfiguration = new ProducerConfiguration(
        "src/test/resources/test.properties");
    kafka.getKafkaTestUtils().createTopic(producerConfiguration.getTopic(), 2, (short) 1);
    final KafkaProducer<String, Metric> kafkaProducer = kafka.getKafkaTestUtils()
        .getKafkaProducer(StringSerializer.class, MetricSerializer.class);
    final String machineIdentify = "testMachine";
    final List<MetricCollector> metricCollectors = Collections
        .singletonList(MetricCollectorFactory.newCollector(() -> machineIdentify, MetricCategory.MEMORY));
    final MetricsProducer metricsProducer = new MetricsProducer(producerConfiguration, metricCollectors,
        kafkaProducer);

    //when
    metricsProducer.start();
    TimeUnit.SECONDS.sleep(2L);
    metricsProducer.stop();

    // then
    try (final KafkaConsumer<String, Metric> kafkaConsumer =
        kafka.getKafkaTestUtils().getKafkaConsumer(StringDeserializer.class, MetricDeserializer.class)) {
      final List<TopicPartition> topicPartitionList = new ArrayList<>();
      for (final PartitionInfo partitionInfo : kafkaConsumer.partitionsFor(producerConfiguration.getTopic())) {
        topicPartitionList.add(new TopicPartition(partitionInfo.topic(), partitionInfo.partition()));
      }
      kafkaConsumer.assign(topicPartitionList);
      kafkaConsumer.seekToBeginning(topicPartitionList);

      int consumedMessage = 0;
      // Pull records from kafka, keep polling until we get nothing back
      ConsumerRecords<String, Metric> records;
      do {
        records = kafkaConsumer.poll(Duration.of(4000L, MILLIS));
        for (ConsumerRecord<String, Metric> record : records) {
          // Validate
          Assertions.assertNotNull(record.value());
          Assertions.assertEquals(machineIdentify, record.key());
          consumedMessage++;
        }
      }
      while (!records.isEmpty());

      assertTrue(consumedMessage > 0);
    }
  }
}