package github.jadetang.aiven.metric.common;

import github.jadetang.aiven.metric.common.model.Metric;
import java.io.IOException;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricDeserializer implements Deserializer<Metric> {

  private static final Logger log = LoggerFactory.getLogger(MetricDeserializer.class);

  @Override
  public Metric deserialize(final String topic, final byte[] data) {
    try {
      return ObjectMapperHolder.getMapper().readValue(data, Metric.class);
    } catch (IOException e) {
      log.error("Error when deserialize data into metric", e);
      return null;
    }
  }
}
