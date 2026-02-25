# Kafka Capstone Project

## Description

This project is a capstone project to practise Kafka. In the app there are two connectors, to read from and write into
the file. Also Kafka Streams are used to process the data.

### Main flow

1. New account information is added to data/github-accounts.csv file.
2. Connector reads the file and sends the data to the topic.
3. Component reads information from the topic and uses Github API to fetch information about commits for that user and
   put that to another topic.
4. Kafka Streams processes the data and creates metrics:
    - total number of commits
    - total commiters analyzed
    - number of commits per language
    - top five contributors regarding number of commits
    - total number of lines edited
    - total increment of lines
    - top five contributors regarding number of lines edited
    - percent of commits with the same author and commiter
5. Connector reads metrics and writes them to the file.

## Commands used to start Kafka

To start kafka you need to start it with docker compose with:

```bash
# start Kafka
docker compose up -d
```

All topics will be created automatically via AppConfig. Connectors will be configured via ConnectorTrigger, so no
additional configuration is needed.

New accounts you can place in new lines of data/github-accounts.csv file. Please, add new accounts at the end of the
file, in the same format as the provided examples.
