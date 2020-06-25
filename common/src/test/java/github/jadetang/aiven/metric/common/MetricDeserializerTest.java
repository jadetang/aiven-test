package github.jadetang.aiven.metric.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import github.jadetang.aiven.metric.common.model.Metric;
import github.jadetang.aiven.metric.common.model.Metric.MetricBuilder;
import github.jadetang.aiven.metric.common.model.MetricType;
import java.io.IOException;
import java.time.Instant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MetricDeserializerTest {

  @Test
  void testSerializeShouldGiveTheRightValue() throws IOException {
    //given
    final ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    final MetricSerializer metricSerializer = new MetricSerializer();
    final String topic = "test topic";
    final Metric originalMetric = new MetricBuilder().withMessageId("unique")
        .withMachineIdentify("myMac").withDescription("Test metric.").withType(MetricType.TOTAL_MEMORY)
        .withValue(2000.00D).withTimeStamp(Instant.now()).build();

    //when
    final byte[] bytes = metricSerializer.serialize(topic, originalMetric);

    //then
    Assertions.assertTrue(bytes.length > 0);
    final Metric metric = mapper.readValue(bytes, Metric.class);
    Assertions.assertEquals(originalMetric, metric);
  }
}