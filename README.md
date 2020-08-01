![Java CI with Maven](https://github.com/jadetang/aiven-test/workflows/Java%20CI%20with%20Maven/badge.svg?branch=master)
### Aiven home task

This is a home task for [Aiven](https://aiven.io/) interview. See the [requirement](Home_Assigment_Aiven_Backend_Java.txt).
The project mainly consists of 2 parts, metric producer and metric consumer.
The metric producer leverages [OSHI](https://github.com/oshi/oshi) to collect metrics from the system and send them to a Kafka topic at a fixed interval.
The metric consumer reads metrics from Kafka and stores them into a Postgres database. The metric delivery semantics is at least once, and writing to database operation is idempotent.

### Supported system metric
 |metric category| metric type | description|
 |---|---|---|
 | cpu | load average last 1 minute |  System load average for last 1 minute. |
 | cpu | load average last 5 minutes | System load average for last 5 minutes.|
 | cpu | load average for last 15 minutes. | System load average for last 15 minutes.|
 | memory  | used memory     | Used memory in bytes. |
 | memory | total memory  | Total memory in bytes. |

### The message schema
```json
{
  "messageId": "690a2c74-b6e2-4ab6-bfd3-faf9a4b6a835",  
  "machineIdentify": "a unique identify for the current machine.",
  "value": 2000.0,                
  "type": "TOTAL_MEMORY",
  "timeStamp": 1593181138.076000000,
  "description": "Test metric."
}
```


### Dependency 
Java 8+, Maven3+, Kafka2.5, Postgres 9.5+
### Package
```shell script
mvn clean package -Dmaven.test.skip=true
```
### Running the service step by step
1. Create a Postgres database on Aiven.io, check [this](https://help.aiven.io/en/articles/489573-getting-started-with-aiven-postgresql) for how to connecting to the database.
2. Create a Kafka cluster of version 2.5 [aiven.io](https://aiven.io/).
   * Create a topic, for example, **test-topic**.
   * Follow the [Java example](https://help.aiven.io/en/articles/489572-getting-started-with-aiven-kafka) to set up your Java Keystore.
3. Run metric producer:
```shell script
java -jar metric-producer/target/metric-producer-1.0-SNAPSHOT-jar-with-dependencies.jar [propertities_file_path]
```
4. Run metric consumer:
```shell script
java -jar metric-consumer/target/metric-consumer-1.0-SNAPSHOT-jar-with-dependencies.jar [propertities_file_path] [datasource_properties_path]
```

### Configuration
The consumer and producer both use properties file to manage configuration. These are examples:
[metric-consumer.properties](metric-consumer.properties), [metric-producer.properties](metric-producer.properties), [datasource.properties](datasource.properties).

#### Producer configuration
The internal Kafka producer inside the metric producer also uses the metric producer properties file to config itself. In other words, you can put all the [Kafka producer configurations](https://docs.confluent.io/current/installation/configuration/producer-configs.html#cp-config-producer) in the properties.

**Note**: 
- The key **key.serializer** and **value.serializer** are ignored.

Besides Kafka's producer configuration, some properties specific to the metric producer are available.

 |property| description | default value|
 |---|---|---|
 | metric.categories | The metric categories the application will report, split by comma. | None |
 | metric.topic| The Kafka topic the metric will be sent to. | None |
 | metric.intervalInSec| The interval in second between each metric collection activities. | 1 |
 | metric.threadNumber  | The thread number used to run metric collectors. | 20 |
 | machine.id | The unique identity of the machine on which the application runs.  | None |

#### Consumer configuration
Same as a metric producer, the internal Kafka consumer inside the metric consumer also use the metric consumer properties. Check the full list of the configuration of the [Kafka consumer](https://docs.confluent.io/current/installation/configuration/consumer-configs.html).

**Note**: 
- The **group.id** is mandatory.
- The key **key.deserializer** and **value.deserializer** are ignored.

Besides Kafka consumer configuration, some properties specific to the metric consumer are available.
 |property| description | default value|
 |---|---|---|
 | metric.topic| The Kafka topic the metric will be fetched from. | None|
 | metric.consumer.number| The number of Kafka consumers. | 3 |
 | metric.pool.duration.in.ms| The Kafka consumer poll duration in millisecond. See [details](https://kafka.apache.org/25/javadoc/index.html?org/apache/kafka/clients/consumer/KafkaConsumer.html). | 1000 |
 
#### Datasource configuration 
The metric consumer also requires a configuration for connecting the database. The see all the properties [here](https://github.com/brettwooldridge/HikariCP) if you want to tune the data source.

#### Logging configuration
The jar is bundled with [Logback](http://logback.qos.ch/), you may specify the location of the default configuration file with a system property named `logback.configurationFile`.

### Attributes
- [Introducing the Kafka Consumer: Getting Started with the New Apache Kafka 0.9 Consumer Client](https://www.confluent.io/blog/tutorial-getting-started-with-the-new-apache-kafka-0-9-consumer-client/)
- [Kafka: The Definitive Guide](https://www.oreilly.com/library/view/kafka-the-definitive/9781491936153/)
- [OSHI](https://github.com/oshi/oshi)
- [Kafka unit](https://github.com/salesforce/kafka-junit)

