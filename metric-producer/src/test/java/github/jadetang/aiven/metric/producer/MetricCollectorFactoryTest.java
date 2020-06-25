package github.jadetang.aiven.metric.producer;

import github.jadetang.aiven.metric.producer.collector.MetricCollector;
import github.jadetang.aiven.metric.producer.collector.MetricCollectorFactory;
import github.jadetang.aiven.metric.producer.collector.MetricCollectorFactory.MetricCategory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MetricCollectorFactoryTest {

  @Test
  void testCreateMemoryReporter() {
    final MetricCollector reporter = MetricCollectorFactory.newCollector(() -> "testMachine", MetricCategory.MEMORY);
    Assertions.assertNotNull(reporter);
  }

  @Test
  void unsupportedTypeShouldThrowException() {
    Assertions.assertThrows(UnsupportedOperationException.class,
        () -> MetricCollectorFactory.newCollector(() -> "testMachine", MetricCategory.CPU));
  }
}