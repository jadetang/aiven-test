package github.jadetang.aiven.metric.consumer;

import github.jadetang.aiven.metric.common.AbstractConfiguration;
import java.io.IOException;
import java.time.Duration;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class ConsumerConfiguration extends AbstractConfiguration {

  private static final String CONSUMER_COUNT = "metric.consumer.number";

  private static final int DEFAULT_CONSUMER_NUMBER = 3;

  private static final String POLL_DURATION_IN_MS = "metric.pool.duration.in.ms";

  private static final long DEFAULT_POLL_DURATION = 1000L;

  public ConsumerConfiguration(final String configFilePath) throws IOException, ConfigurationException {
    super(configFilePath);
  }

  public int getConsumerCount() {
    return configuration.getInt(CONSUMER_COUNT, DEFAULT_CONSUMER_NUMBER);
  }

  public Duration getPollDuration() {
    return Duration.ofMillis(configuration.getLong(POLL_DURATION_IN_MS, DEFAULT_POLL_DURATION));
  }
}