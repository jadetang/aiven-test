package github.jadetang.aiven.metric.producer.collector;

import static github.jadetang.aiven.metric.common.model.MetricType.TOTAL_MEMORY;
import static github.jadetang.aiven.metric.common.model.MetricType.USED_MEMORY;

import github.jadetang.aiven.metric.common.model.Metric;
import github.jadetang.aiven.metric.common.model.Metric.MetricBuilder;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import oshi.hardware.GlobalMemory;

/**
 * Report memory related metrics
 */
public class MemoryUsageCollector extends MetricCollector {

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
    return new MetricBuilder().withMachineIdentify(machineIdentifyProvider.machineIdentify())
        .withMessageId(UUID.randomUUID().toString()).withValue((double) this.memory.getTotal())
        .withType(TOTAL_MEMORY).withTimeStamp(Instant.now())
        .withDescription(TOTAL_MEMORY.getDescription()).build();
  }

  private Metric usedMemory() {
    return new MetricBuilder().withMachineIdentify(machineIdentifyProvider.machineIdentify())
        .withMessageId(UUID.randomUUID().toString()).withValue((double) this.memory.getAvailable())
        .withType(USED_MEMORY).withTimeStamp(Instant.now()).withDescription(USED_MEMORY.getDescription())
        .build();
  }
}
