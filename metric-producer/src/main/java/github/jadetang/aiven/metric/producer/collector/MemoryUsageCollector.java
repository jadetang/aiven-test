package github.jadetang.aiven.metric.producer.collector;

import github.jadetang.aiven.metric.common.model.Metric;
import github.jadetang.aiven.metric.common.model.Metric.MetricBuilder;
import github.jadetang.aiven.metric.common.model.MetricType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import oshi.hardware.GlobalMemory;

/**
 * Report memory related metrics
 */
public class MemoryUsageCollector extends MetricCollector {

  private static final String TOTAL_MEMORY_DESCRIPTION = "Total memory in bytes";

  private static final String USED_MEMORY_DESCRIPTION = "Used memory in bytes";

  private final GlobalMemory memory;

  public MemoryUsageCollector(final MachineIdentifyProvider machineIdentifyProvider, final GlobalMemory memory) {
    super(machineIdentifyProvider);
    this.memory = memory;
  }

  @Override
  public List<Metric> collect() {
    final List<Metric> metrics = new ArrayList<>();
    metrics.add(this.totalMemory());
    metrics.add(this.usedMemory());
    return metrics;
  }

  private Metric totalMemory() {
    final MetricBuilder builder = new MetricBuilder();
    return builder.withMachineIdentify(machineIdentifyProvider.machineIdentify())
        .withMessageId(UUID.randomUUID().toString()).withValue((double) this.memory.getTotal())
        .withType(MetricType.TOTAL_MEMORY).withTimeStamp(Instant.now())
        .withDescription(TOTAL_MEMORY_DESCRIPTION).build();
  }

  private Metric usedMemory() {
    final MetricBuilder builder = new MetricBuilder();
    return builder.withMachineIdentify(machineIdentifyProvider.machineIdentify())
        .withMessageId(UUID.randomUUID().toString()).withValue((double) this.memory.getAvailable())
        .withType(MetricType.USED_MEMORY).withTimeStamp(Instant.now()).withDescription(USED_MEMORY_DESCRIPTION)
        .build();
  }
}
