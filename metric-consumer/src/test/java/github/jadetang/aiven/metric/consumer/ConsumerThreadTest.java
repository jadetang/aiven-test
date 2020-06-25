package github.jadetang.aiven.metric.consumer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import github.jadetang.aiven.metric.common.model.Metric;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConsumerThreadTest {

  private ConsumerThread consumerThread;

  private Consumer<String, Metric> consumer;

  private MetricWriter writer;

  @SuppressWarnings("uncheck")
  @BeforeEach
  void setUp() {
    consumer = mock(Consumer.class);
    writer = mock(MetricWriter.class);
    consumerThread = new ConsumerThread(consumer, writer, Duration.ofMillis(1000L));
  }

  @Test
  void testConsumingMetricAndWriteToDatabaseHappyPath() {
    //given
    when(consumer.poll(any())).thenReturn(consumerRecords(100)).thenReturn(consumerRecords(200))
        .thenThrow(new WakeupException());

    //when
    consumerThread.run();

    //then
    verify(consumer, times(3)).poll(any());
    verify(writer, times(2)).batchInsert(anyListOf(Metric.class));
    verify(consumer, times(2)).commitAsync(any());
    verify(consumer, times(1)).commitSync();
    verify(consumer, times(1)).close();
    verifyNoMoreInteractions(consumer);
    verifyNoMoreInteractions(writer);
  }

  @Test
  void failToWriteToDatabaseShouldCallCommitAsync() {
    //given
    when(consumer.poll(any())).thenReturn(consumerRecords(100));
    when(writer.batchInsert(anyListOf(Metric.class))).thenThrow(new RuntimeException("Can not insert data in a UT"));

    //when
    consumerThread.run();

    //then
    verify(consumer, times(1)).poll(any());
    verify(writer, times(1)).batchInsert(anyListOf(Metric.class));
    verify(consumer, never()).commitAsync();
    verify(consumer, times(1)).commitSync();
    verify(consumer, times(1)).close();
    verifyNoMoreInteractions(consumer);
    verifyNoMoreInteractions(writer);
  }

  private ConsumerRecords<String, Metric> consumerRecords(final int size) {
    final String topic = "testTopic";
    final int partition = 1;
    final List<ConsumerRecord<String, Metric>> consumerRecords = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      final Metric metric = TestUtil.randomMetric();
      consumerRecords.add(new ConsumerRecord<>(topic, partition, i, metric.getMachineIdentify(), metric));
    }
    final Map<TopicPartition, List<ConsumerRecord<String, Metric>>> recordsMap = new HashMap<>();
    recordsMap.put(new TopicPartition(topic, partition), consumerRecords);
    return new ConsumerRecords<>(recordsMap);
  }

  @Test
  void stop() {
  }
}