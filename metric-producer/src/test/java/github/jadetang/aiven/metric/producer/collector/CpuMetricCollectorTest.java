package github.jadetang.aiven.metric.producer.collector;

import static org.junit.jupiter.api.Assertions.*;

import github.jadetang.aiven.metric.common.model.Metric;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import oshi.SystemInfo;

class CpuMetricCollectorTest {

  @Test
  void collect() {
    final CpuMetricCollector cpuMetricCollector = new CpuMetricCollector(()-> "This is a test", new SystemInfo().getHardware().getProcessor());
    final List<Metric> metrics = cpuMetricCollector.collect();
    assertEquals(3, metrics.size());
    metrics.forEach(Assertions::assertNotNull);
  }
}