# Kafka Capstone Project

## Description

This project is a capstone project for the Kafka course. 

## Commands used to start Kafka and create topics

```bash
# start Kafka
docker compose up -d

# create topic
docker exec --workdir /opt/kafka/bin/ -it broker-1 sh
./kafka-topics.sh --bootstrap-server broker-1:19092,broker-2:19092,broker-3:19092 --create --topic test-topic

# stop Kafka
docker compose down
```
