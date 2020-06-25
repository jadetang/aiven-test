package github.jadetang.aiven.metric.common.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import java.util.Objects;
import java.util.StringJoiner;

@JsonDeserialize(builder = Metric.MetricBuilder.class)
public class Metric {

  private final String messageId;

  private final String machineIdentify;

  private final Double value;

  private final MetricType type;

  private final Instant timeStamp;

  private final String description;

  private Metric(final String messageId, final String machineIdentify, final Double value, final MetricType type,
      final Instant timeStamp, final String description) {
    this.messageId = messageId;
    this.machineIdentify = machineIdentify;
    this.value = value;
    this.type = type;
    this.timeStamp = timeStamp;
    this.description = description;
  }

  public String getMessageId() {
    return messageId;
  }

  public String getMachineIdentify() {
    return machineIdentify;
  }

  public Double getValue() {
    return value;
  }

  public MetricType getType() {
    return type;
  }

  public Instant getTimeStamp() {
    return timeStamp;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Metric.class.getSimpleName() + "[", "]")
        .add("messageId='" + messageId + "'")
        .add("machineIdentify='" + machineIdentify + "'")
        .add("value=" + value)
        .add("type=" + type)
        .add("timeStamp=" + timeStamp)
        .add("description='" + description + "'")
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Metric metric = (Metric) o;
    return Objects.equals(messageId, metric.messageId) &&
        Objects.equals(machineIdentify, metric.machineIdentify) &&
        Objects.equals(value, metric.value) &&
        type == metric.type &&
        Objects.equals(timeStamp, metric.timeStamp) &&
        Objects.equals(description, metric.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(messageId, machineIdentify, value, type, timeStamp, description);
  }


  public static final class MetricBuilder {

    private String messageId;
    private String machineIdentify;
    private Double value;
    private MetricType type;
    private Instant timeStamp;
    private String description;

    public MetricBuilder withMessageId(String messageId) {
      this.messageId = messageId;
      return this;
    }

    public MetricBuilder withMachineIdentify(String machineIdentify) {
      this.machineIdentify = machineIdentify;
      return this;
    }

    public MetricBuilder withValue(Double value) {
      this.value = value;
      return this;
    }

    public MetricBuilder withType(MetricType type) {
      this.type = type;
      return this;
    }

    public MetricBuilder withTimeStamp(Instant timeStamp) {
      this.timeStamp = timeStamp;
      return this;
    }

    public MetricBuilder withDescription(String description) {
      this.description = description;
      return this;
    }

    public Metric build() {
      return new Metric(Objects.requireNonNull(messageId), Objects.requireNonNull(machineIdentify),
          Objects.requireNonNull(value), Objects.requireNonNull(type), Objects.requireNonNull(timeStamp),
          Objects.requireNonNull(description));
    }
  }
}
