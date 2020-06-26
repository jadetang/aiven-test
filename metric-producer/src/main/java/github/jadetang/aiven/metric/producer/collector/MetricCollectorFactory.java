package github.jadetang.aiven.metric.producer.collector;

import oshi.SystemInfo;

public class MetricCollectorFactory {

  private static final SystemInfo SYSTEM_INFO;

  static {
    SYSTEM_INFO = new SystemInfo();
  }

  private MetricCollectorFactory() {

  }

  public static MetricCollector newCollector(final MachineIdentifyProvider machineIdentifyProvider,
      final MetricCategory metricCategory) {
    switch (metricCategory) {
      case MEMORY:
        return new MemoryUsageCollector(machineIdentifyProvider, SYSTEM_INFO.getHardware().getMemory());
      case CPU:
        return new CpuMetricCollector(machineIdentifyProvider, SYSTEM_INFO.getHardware().getProcessor());
      default:
        throw new UnsupportedOperationException(
            String.format("The metric %s is not supported yet.", metricCategory));
    }
  }

  public enum MetricCategory {
    MEMORY, CPU, NET_WORK
  }
}
