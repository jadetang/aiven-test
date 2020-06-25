package github.jadetang.aiven.metric.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import github.jadetang.aiven.metric.common.model.Metric;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricSerializer implements Serializer<Metric> {

  private static final Logger log = LoggerFactory.getLogger(MetricSerializer.class);


  @Override
  public byte[] serialize(final String topic, final Metric data) {
    byte[] bytes;
    try {
      bytes = ObjectMapperHolder.getMapper().writeValueAsString(data).getBytes();
      return bytes;
    } catch (final JsonProcessingException e) {
      MetricSerializer.log.error("Error serialize metric {}", data, e);
    }
    return null;
  }
}
