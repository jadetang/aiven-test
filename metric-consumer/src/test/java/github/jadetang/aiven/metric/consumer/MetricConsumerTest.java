package github.jadetang.aiven.metric.consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.salesforce.kafka.test.junit5.SharedKafkaTestResource;
import github.jadetang.aiven.metric.common.MetricDeserializer;
import github.jadetang.aiven.metric.common.MetricSerializer;
import github.jadetang.aiven.metric.common.model.Metric;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;

class MetricConsumerTest extends DatabaseAwareTest {

    @RegisterExtension
    static final SharedKafkaTestResource sharedKafkaTestResource = new SharedKafkaTestResource()
            .withBrokers(1)
            .withBrokerProperty("auto.create.topics.enable", "false");

    private MetricConsumer metricConsumer;

    private ConsumerConfiguration consumerConfiguration;

    @BeforeEach
    void setUp() throws IOException, ConfigurationException {
        consumerConfiguration = new ConsumerConfiguration("src/test/resources/test.properties");

        sharedKafkaTestResource.getKafkaTestUtils()
                .createTopic(consumerConfiguration.getTopic(), consumerConfiguration.getConsumerCount(), (short) 1);

        final MetricWriter metricWriter = JDBI.onDemand(MetricWriter.class);

        final List<ConsumerThread> consumerThreads = new ArrayList<>();
        final Properties consumerProperties = consumerConfiguration.cloneProperties();
        //check this https://github.com/salesforce/kafka-junit/issues/20
        consumerProperties.put("auto.offset.reset", "earliest");
        for (int i = 0; i < consumerConfiguration.getConsumerCount(); i++) {
            final Consumer<String, Metric> consumer = sharedKafkaTestResource.getKafkaTestUtils().getKafkaConsumer(
                    StringDeserializer.class, MetricDeserializer.class, consumerProperties);
            consumer.subscribe(Collections.singleton(consumerConfiguration.getTopic()));
            consumerThreads.add(new ConsumerThread(consumer, metricWriter, consumerConfiguration.getPollDuration()));
        }

        metricConsumer = new MetricConsumer(consumerThreads);
    }

    @Test
    @Timeout(value = 20, unit = TimeUnit.SECONDS)
    void consumeMetricHappyPath() throws InterruptedException, ExecutionException {
        //given
        final int dataSize = 100;
        final List<Metric> metrics = TestUtil.metricList(dataSize);
        try (final Producer<String, Metric> producer = sharedKafkaTestResource.getKafkaTestUtils()
                .getKafkaProducer(StringSerializer.class, MetricSerializer.class)) {
            for (Metric metric : metrics) {
                final Future<RecordMetadata> recordMetadataFuture = producer
                        .send(new ProducerRecord<>(consumerConfiguration.getTopic(), metric.getMachineIdentify(),
                                metric));
                recordMetadataFuture.get();
            }
            producer.flush();
        }

        //when
        metricConsumer.start();
        int storedMetric = 0;
        while ((storedMetric = JDBI.open().select("select count(*) from metrics").mapTo(Integer.TYPE).one())
                < dataSize) {
        }
        metricConsumer.stop();

        //then
        assertEquals(storedMetric, dataSize);
    }
}