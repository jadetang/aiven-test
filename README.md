~~~markdown
### Aiven home task.
This is a home task for [Aiven](https://aiven.io/) interview. The project mainly consists of 2 parts, metric producer and metric consumer.
The metric producer leverages [OSHI](https://github.com/oshi/oshi) to collect metrics from the system and send them to a Kafka topic at a fixed interval.
The metric consumer reads metrics from Kafka and stores them into a Postgres database. The metric delivery semantics is at least once, and writing to database operation is idempotent.

### The message schema.
```json
{
  "messageId": "690a2c74-b6e2-4ab6-bfd3-faf9a4b6a835",  // a unique id for each message
  "machineIdentify": "myMac",     // a unique identify for the current machine
  "value": 2000.0,                
  "type": "TOTAL_MEMORY",
  "timeStamp": 1593181138.076000000,
  "description": "Test metric."
}
```


### Install 
Dependency: Java 8+, Maven3+, Kafka2.5, Postgres
### Package
```shell script
mvn clean package -Dmaven.test.skip=true
```
### Running the service step by step.
1. Create a Postgres database on Aiven.io, check [this](https://help.aiven.io/en/articles/489573-getting-started-with-aiven-postgresql) for how to connecting to the database.
2. Create a Kafka cluster of version 2.5 Aiven.io.
  2.1 Create a topic, such as **test-topic**.
  2.2 Follow the [Java example](https://help.aiven.io/en/articles/489572-getting-started-with-aiven-kafka) to set up your Java Keystore.
3. Run metric producer:
```shell script
java -jar metric-producer/target/metric-producer-1.0-SNAPSHOT-jar-with-dependencies.jar [propertities_file_path]
```
4. Run metric consumer:
```shell script
java -jar metric-producer/target/metric-producer-1.0-SNAPSHOT-jar-with-dependencies.jar [propertities_file_path] [datasource_properties_path]
```
### Configuration
The consumer and producer both use properties file to manage configuration. These are examples:
[metric-consumer.properties](consumer.properties), [metric-producer.properties](producer.properties), [datasource.properties](datasource.properties).

#### Producer configuration
The internal Kafka producer inside the metric producer also uses the metric producer properties file to config itself. In other words, you can put all the [Kafka producer configurations](https://docs.confluent.io/current/installation/configuration/producer-configs.html#cp-config-producer) in the properties.
**Note**: 
- The key **key.serializer** and **value.serializer** are ignored.

Besides Kafka's producer configuration, some properties specific to the metric producer are available.

 |Property| description | default value|
 |---|---|---|
 | metric.categories | The metric categories the application will report, split by comma. |  None |
 |metric.topic| The Kafka topic the metric will be sent to. | None|
 |metric.intervalInSec| The interval in second between each metric collection activities. | 1 |
 | metric.threadNumber  | The thread number used to run metric collectors.     | 20 |
 | machine.id | The unique identity of the machine on which the application runs.  | None |

#### Consumer configuration
Same as a metric producer, the internal Kafka consumer inside the metric consumer also use the metric consumer properties. Check the full list of the configuration of the [Kafka consumer](https://docs.confluent.io/current/installation/configuration/consumer-configs.html).
**Note**: 
- The **group.id** is mandatory.
- The key **key.deserializer** and **value.deserializer** are ignored.

Besides Kafka consumer configuration, some properties specific to the metric consumer are available.
 |Property| description | default value|
 |---|---|---|
 |metric.topic| The Kafka topic the metric will be fetched from. | None|
 |metric.consumer.number| The number of Kafka consumers. | 3 |
 |metric.pool.duration.in.ms| The Kafka consumer poll duration in millisecond. See [details](https://kafka.apache.org/25/javadoc/index.html?org/apache/kafka/clients/consumer/KafkaConsumer.html). | 1000 |
 
#### Datasource configuration 
The metric consumer also requires a configuration for connecting the database. The see all the properties [here](https://github.com/brettwooldridge/HikariCP) if you want to tune the data source.
