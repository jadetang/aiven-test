package github.jadetang.aiven.metric.producer.collector;

import github.jadetang.aiven.metric.common.model.Metric;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

class MemoryUsageCollectorTest {

  @Test
  public void testGetMemoryUsage() {
    final SystemInfo si = new SystemInfo();
    final HardwareAbstractionLayer hal = si.getHardware();
    final MemoryUsageCollector memoryUsageCollector = new MemoryUsageCollector(() -> "testMachine",
        hal.getMemory());
    final List<Metric> metrics = memoryUsageCollector.collect();
    Assertions.assertEquals(2, metrics.size());
    metrics.forEach(Assertions::assertNotNull);
  }
}