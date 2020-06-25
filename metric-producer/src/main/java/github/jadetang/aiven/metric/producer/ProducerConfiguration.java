package github.jadetang.aiven.metric.producer;

import github.jadetang.aiven.metric.common.AbstractConfiguration;
import github.jadetang.aiven.metric.producer.collector.MetricCollectorFactory.MetricCategory;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.EnumUtils;

public class ProducerConfiguration extends AbstractConfiguration {

  public static final String METRIC_CATEGORIES = "metric.categories";

  private static final String METRIC_INTERVAL = "metric.intervalInSec";

  private static final int DEFAULT_INTERVAL = 1;

  private static final String SCHEDULER_THREAD_NUMBER = "metric.threadNumber";

  private static final int DEFAULT_THREAD_NUMBER = 20;

  private static final String MACHINE_IDENTIFY = "machine.id";

  public ProducerConfiguration(final String configFilePath) throws IOException, ConfigurationException {
    super(configFilePath);
  }

  public List<MetricCategory> getMetricCategories() {
    return configuration.getList(String.class, METRIC_CATEGORIES, Collections.emptyList())
        .stream().map(name -> EnumUtils.getEnumIgnoreCase(MetricCategory.class, name))
        .collect(Collectors.toList());
  }

  public int getSchedulerThreadNumber() {
    return configuration.getInt(SCHEDULER_THREAD_NUMBER, DEFAULT_THREAD_NUMBER);
  }

  public long getMetricCollectIntervalInSec() {
    return configuration.getInt(METRIC_INTERVAL, DEFAULT_INTERVAL);
  }

  public String getMachineIdentify() {
    return Objects.requireNonNull(configuration.getString(MACHINE_IDENTIFY));
  }
}
