package github.jadetang.aiven.metric.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ObjectMapperHolder {

  private static final ObjectMapper INSTANCE;

  static {
    INSTANCE = new ObjectMapper();
    INSTANCE.registerModule(new JavaTimeModule());
  }

  private ObjectMapperHolder() {
  }

  public static ObjectMapper getMapper() {
    return INSTANCE;
  }
}
