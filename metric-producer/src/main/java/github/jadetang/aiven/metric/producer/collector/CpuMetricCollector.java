package github.jadetang.aiven.metric.producer.collector;

import static github.jadetang.aiven.metric.common.model.MetricType.LOAD_AVERAGE_LAST_15_MINUTES;
import static github.jadetang.aiven.metric.common.model.MetricType.LOAD_AVERAGE_LAST_1_MINUTE;
import static github.jadetang.aiven.metric.common.model.MetricType.LOAD_AVERAGE_LAST_5_MINUTES;

import github.jadetang.aiven.metric.common.model.Metric;
import github.jadetang.aiven.metric.common.model.Metric.MetricBuilder;
import github.jadetang.aiven.metric.common.model.MetricType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import oshi.hardware.CentralProcessor;

public class CpuMetricCollector extends MetricCollector{

  private final CentralProcessor centralProcessor;

  public CpuMetricCollector(final MachineIdentifyProvider machineIdentifyProvider, final CentralProcessor centralProcessor) {
    super(machineIdentifyProvider);
    this.centralProcessor = centralProcessor;
  }

  @Override
  public List<Metric> collect() {
    final List<Metric> metrics = new ArrayList<>();
    double[] loadAverage = centralProcessor.getSystemLoadAverage(3);
    metrics.add(buildLoadAverage(loadAverage[0], LOAD_AVERAGE_LAST_1_MINUTE, LOAD_AVERAGE_LAST_1_MINUTE.getDescription()));
    metrics.add(buildLoadAverage(loadAverage[1], LOAD_AVERAGE_LAST_5_MINUTES, LOAD_AVERAGE_LAST_5_MINUTES.getDescription()));
    metrics.add(buildLoadAverage(loadAverage[2], LOAD_AVERAGE_LAST_15_MINUTES, LOAD_AVERAGE_LAST_15_MINUTES.getDescription()));
    return metrics;
  }

  private Metric buildLoadAverage(final double value, final MetricType type, final String description) {
    return new MetricBuilder().withMessageId(UUID.randomUUID().toString()).withMachineIdentify(machineIdentifyProvider.machineIdentify())
                  .withValue(value).withType(type).withDescription(description).withTimeStamp(Instant.now()).build();
  }
}
