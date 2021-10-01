# spring-boot-kafta-integration

## Kafka installation instructions.

To install Kafka locally, run the following command:

```
docker-compose -f ./docker/docker-compose.yaml up -d
```

# Useful Kafka Scripts

Script(s) are located within the **/opt/bitnami/kafka/bin** folder of the **kafka** container.

## Topic Management

### Create a new topic

```
/opt/bitnami/kafka/bin/kafka-topics.sh --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 4 --topic test-topic
```

### Delete a topic

```
/opt/bitnami/kafka/bin/kafka-topics.sh --delete --zookeeper zookeeper:2181 --topic test-topic
```

### List topics in the cluster

```
/opt/bitnami/kafka/bin/kafka-topics.sh --zookeeper zookeeper:2181 --list
```

## Instantiate a Console Producer 

### Without Key

```
/opt/bitnami/kafka/bin/kafka-console-producer.sh --broker-list kafka:9092 --topic test-topic
```

### With Key

```
/opt/bitnami/kafka/bin/kafka-console-producer.sh --broker-list kafka:9092 --topic test-topic \
--property "key.separator=-" --property "parse.key=true"
```

## Instantiate a Console Consumer 

### Without Key

```
/opt/bitnami/kafka/bin/kafka-console-consumer.sh --bootstrap-server kafka:9092 --topic test-topic --from-beginning
```

### With Key

```
/opt/bitnami/kafka/bin/kafka-console-consumer.sh --bootstrap-server kafka:9092 --topic test-topic --from-beginning --property "key.separator=-" --property "print.key=true"
```

### With Consumer Group

```
/opt/bitnami/kafka/bin/kafka-console-consumer.sh --bootstrap-server kafka:9092 --topic test-topic --from-beginning --group <groupName>
```

## Advanced Kafka Commands

### View Consumer Groups

```
/opt/bitnami/kafka/bin/kafka-consumer-groups.sh --bootstrap-server kafka:9092 --list
```