# example cluster

## dependency
### open-rec
rec-server & data-processor & rec-algorithm

### system env
kafka & flink & redis & elasticsearch & hadoop & spark & hive

## steps

### install redis

```shell
brew install redis
brew services start redis
```

### install elasticsearch
```shell
curl -O https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-8.5.0-darwin-x86_64.tar.gz
tar -xzf elasticsearch-8.5.0-darwin-x86_64.tar.gz
cd elasticsearch-8.5.0/
./bin/elasticsearch -d
```

> please record your elastic user and password, that would be used later.

### install kafka
```shell
curl -O https://archive.apache.org/dist/kafka/2.5.0/kafka_2.12-2.5.0.tgz
tar -xzf kafka_2.12-2.5.0.tgz
cd kafka_2.12-2.5.0
bash bin/zookeeper-server-start.sh config/zookeeper.properties &
bash bin/kafka-server-start.sh config/server.properties &
```

