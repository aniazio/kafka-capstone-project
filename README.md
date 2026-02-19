# Kafka Capstone Project

## Description

This project is a capstone project for the Kafka course.

## Commands used to start Kafka

```bash
# start Kafka
docker compose up -d

# stop Kafka
docker compose down
```

## Commands to trigger file read by connector - need to write it in the code NOT NEEDED

```bash
curl -X POST http://localhost:8083/connectors -H "Content-Type: application/json" -d '{
  "name": "csv-loader",
  "config": {
    "connector.class": "org.apache.kafka.connect.file.FileStreamSourceConnector",
    "file": "/data/github-accounts.csv",  
    "topic": "github-accounts"
  }
}'
```

```bash
curl -X POST http://localhost:8084/connectors -H "Content-Type: application/json" -d '{
  "name": "metrics-sink",
  "config": {
    "connector.class": "org.apache.kafka.connect.file.FileStreamSinkConnector",
    "file": "/data/metrics.json",  
    "topics": "github-metrics"
  }
}'
```
