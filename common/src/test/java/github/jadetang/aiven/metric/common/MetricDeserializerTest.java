package github.jadetang.aiven.metric.common;

import static github.jadetang.aiven.metric.common.model.MetricType.TOTAL_MEMORY;

import com.fasterxml.jackson.databind.ObjectMapper;
import github.jadetang.aiven.metric.common.model.Metric;
import github.jadetang.aiven.metric.common.model.Metric.MetricBuilder;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MetricDeserializerTest {

  @Test
  void testSerializeShouldGiveTheRightValue() throws IOException {
    //given
    final ObjectMapper mapper = ObjectMapperHolder.getMapper();
    final MetricSerializer metricSerializer = new MetricSerializer();
    final String topic = "test topic";
    final Metric originalMetric = new MetricBuilder().withMessageId(UUID.randomUUID().toString())
        .withMachineIdentify("myMac").withType(TOTAL_MEMORY).withDescription(TOTAL_MEMORY.getDescription())
        .withValue(2000.00D).withTimeStamp(Instant.now()).build();

    //when
    final byte[] bytes = metricSerializer.serialize(topic, originalMetric);
    //then
    Assertions.assertTrue(bytes.length > 0);
    final Metric metric = mapper.readValue(bytes, Metric.class);
    Assertions.assertEquals(originalMetric, metric);
  }
}