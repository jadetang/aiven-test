package github.jadetang.aiven.metric.producer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import github.jadetang.aiven.metric.common.model.Metric;
import github.jadetang.aiven.metric.producer.collector.MetricCollectorFactory;
import github.jadetang.aiven.metric.producer.collector.MetricCollectorFactory.MetricCategory;
import java.util.Collections;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProducerThreadTest {

  private final static String TOPIC = "testTopic";

  private Producer<String, Metric> mockProducer;

  @SuppressWarnings("unchecked")
  @BeforeEach
  void setUp() {
    mockProducer = mock(Producer.class);
  }

  @SuppressWarnings("unchecked")
  @Test
  void produceEventHappyPath() {
    //given
    final ProducerThread thread = new ProducerThread(TOPIC,
        Collections.singletonList(MetricCollectorFactory.newCollector(() -> "testMachine", MetricCategory.MEMORY)),
        mockProducer);

    //when
    thread.run();

    //then
    verify(mockProducer, atLeastOnce()).send(any(ProducerRecord.class), any(Callback.class));
  }
}