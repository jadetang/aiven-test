package github.jadetang.aiven.metric.producer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import github.jadetang.aiven.metric.producer.collector.MetricCollectorFactory.MetricCategory;
import java.io.IOException;
import java.util.List;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProducerConfigurationTest {

  private ProducerConfiguration config;

  @BeforeEach
  void setUp() throws IOException, ConfigurationException {
    config = new ProducerConfiguration("src/test/resources/test-properties.properties");
  }

  @Test
  void getMetricCategories() {
    //given when
    final List<MetricCategory> metricCategories = config.getMetricCategories();

    //then
    assertEquals(3, metricCategories.size());
    assertTrue(metricCategories.contains(MetricCategory.MEMORY));
    assertTrue(metricCategories.contains(MetricCategory.CPU));
  }

  @Test
  void getTopic() {
    assertEquals("test-topic-1", this.config.getTopic());
  }
}