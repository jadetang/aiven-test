package github.jadetang.aiven.metric.consumer;

import com.google.common.io.Resources;
import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

class DatabaseAwareTest {

  protected static EmbeddedPostgres POSTGRES;

  protected static Jdbi JDBI;

  @BeforeAll
  static void setUpBeforeAll() throws IOException {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stringtype", "unspecified");
    POSTGRES = EmbeddedPostgres.builder().start();
    final DataSource dataSource = POSTGRES.getPostgresDatabase(properties);

    JDBI = Jdbi.create(dataSource);
    JDBI.installPlugin(new SqlObjectPlugin());
    JDBI.open().execute(getDdl());
  }

  static String getDdl() throws IOException {
    final URL url = Resources.getResource("database/V1_20200625__create_table.sql");
    return Resources.toString(url, StandardCharsets.UTF_8);
  }

  @AfterAll
  static void tearDownAfterAll() throws IOException {
    POSTGRES.close();
  }
}
