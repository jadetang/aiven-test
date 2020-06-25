package github.jadetang.aiven.metric.consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import github.jadetang.aiven.metric.common.model.Metric;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class MetricWriterTest extends DatabaseAwareTest {

  private final MetricWriter dao = JDBI.onDemand(MetricWriter.class);

  @Test
  void batchInsert() {
    int metricSize = 100;
    List<Metric> metrics = TestUtil.metricList(100);
    int[] affectRow = dao.batchInsert(metrics);
    assertEquals(metricSize, affectRow.length);
    assertTrue(IntStream.of(affectRow).allMatch(value -> value == 1));
  }
}