package github.jadetang.aiven.metric.consumer;

import github.jadetang.aiven.metric.common.model.Metric;
import java.util.List;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlBatch;

public interface MetricWriter {

  @SqlBatch("INSERT INTO metrics(id, machine_identify, metric_type, metric_value, metric_time, description) "
      + "VALUES(:messageId, :machineIdentify, :type, :value, :timeStamp, :description)"
      + "ON CONFLICT DO NOTHING")
  int[] batchInsert(@BindBean List<Metric> metrics);
}
