package github.jadetang.aiven.metric.consumer;

import github.jadetang.aiven.metric.common.model.Metric;
import github.jadetang.aiven.metric.common.model.Metric.MetricBuilder;
import github.jadetang.aiven.metric.common.model.MetricType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestUtil {

  private TestUtil() {

  }

  public static List<Metric> metricList(int size) {
    List<Metric> metrics = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      metrics.add(randomMetric());
    }
    return metrics;
  }

  public static Metric randomMetric() {
    final MetricBuilder builder = new MetricBuilder();
    return builder.withMessageId(UUID.randomUUID().toString())
        .withDescription("this is a test.")
        .withTimeStamp(Instant.now())
        .withType(MetricType.TOTAL_MEMORY)
        .withValue(80.0D)
        .withMachineIdentify("MyMac").build();
  }
}
