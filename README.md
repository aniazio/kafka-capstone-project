# Kafka Capstone Project

## Description

This project is a capstone project for the Kafka course.

## Commands used to start Kafka and create topics

```bash
# start Kafka
docker compose up -d

# create topic
docker exec --workdir /opt/kafka/bin/ -it broker-1 sh
./kafka-topics.sh --bootstrap-server broker-1:19092,broker-2:19092,broker-3:19092 --create --topic github-accounts

# stop Kafka
docker compose down
```

## Commands to trigger file read by connector - need to write it in the code

```bash
curl -X POST http://localhost:8083/connectors -H "Content-Type: application/json" -d '{
  "name": "csv-loader",
  "config": {
    "connector.class": "org.apache.kafka.connect.file.FileStreamSourceConnector",
    "file": "/data/github-accounts.csv",  
    "topic": "test-topic"
  }
}'
```

TODO: http://docs.github.com/en/rest/search/search?apiVersion=2022-11-28#search-commits
build a response
