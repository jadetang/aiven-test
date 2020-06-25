package github.jadetang.aiven.metric.producer.collector;

import github.jadetang.aiven.metric.common.model.Metric;
import java.util.List;

public abstract class MetricCollector {

  protected MachineIdentifyProvider machineIdentifyProvider;

  public MetricCollector(final MachineIdentifyProvider machineIdentifyProvider) {
    this.machineIdentifyProvider = machineIdentifyProvider;
  }

  public abstract List<Metric> collect();
}
