package github.jadetang.aiven.metric.common.model;

public enum MetricType {

  USED_MEMORY("Used memory in bytes."),
  TOTAL_MEMORY("Total memory in bytes."),
  LOAD_AVERAGE_LAST_1_MINUTE("System load average for last 1 minute."),
  LOAD_AVERAGE_LAST_5_MINUTES("System load average for last 5 minutes."),
  LOAD_AVERAGE_LAST_15_MINUTES("System load average for last 15 minutes.");

  private final String description;

  MetricType(final String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}

