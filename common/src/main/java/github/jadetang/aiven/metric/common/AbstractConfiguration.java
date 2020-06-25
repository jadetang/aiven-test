package github.jadetang.aiven.metric.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Properties;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;

/**
 * A convenient class wraps {@link java.util.Properties}.
 */
public abstract class AbstractConfiguration {

  private static final String METRIC_TOPIC = "metric.topic";

  private static final String DEFAULT_TOPIC = "aiven.task";

  protected Configuration configuration;

  /**
   * @param propertiesFile the properties file location.
   * @throws IOException
   * @throws ConfigurationException
   */
  public AbstractConfiguration(final String propertiesFile) throws IOException, ConfigurationException {
    this.configuration = this.loadConfig(propertiesFile);
  }

  private Configuration loadConfig(final String configFile) throws IOException, ConfigurationException {
    if (!Files.exists(Paths.get(configFile))) {
      throw new IOException(configFile + " not found.");
    }

    final Parameters params = new Parameters();
    final FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<>(
        PropertiesConfiguration.class);
    builder.configure(params.fileBased().setListDelimiterHandler(new DefaultListDelimiterHandler(','))
        .setFile(new File(configFile)));

    return builder.getConfiguration();
  }

  public String getTopic() {
    return configuration.getString(METRIC_TOPIC, DEFAULT_TOPIC);
  }

  /**
   * Clone the internal properties as a new properties.
   *
   * @return A newly create properties.
   */
  public Properties cloneProperties() {
    final Iterator<String> keyIterator = configuration.getKeys();
    final Properties properties = new Properties();
    String key = null;
    while (keyIterator.hasNext()) {
      key = keyIterator.next();
      properties.put(key, configuration.get(Object.class, key));
    }
    return properties;
  }
}
