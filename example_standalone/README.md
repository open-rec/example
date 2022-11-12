# example standalone

rec-server & redis & elasticsearch & rec-algorithm

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

### install rec client
```shell
git clone git@github.com:open-rec/sdk.git
cd sdk/java-client
mvn clean install -DskipTests
```

### init rec data
```shell
git clone git@github.com:open-rec/example.git
cd example/init
mvn clean package -DskipTests
java -cp target/rec-rec-example-init-1.0-SNAPSHOT-jar-with-dependencies.jar com.openrec.example.InitStandalone 127.0.0.1 6379 127.0.0.1 9200 elastic wz2-4d2*CfZWY71CssO3
```

### start rec server

```shell
git clone git@github.com:open-rec/rec-server.git
cd rec-server
mvn clean package -DskipTests
cd server
java -jar target/rec-server-1.0-SNAPSHOT.jar --spring.profiles.active=dev
```
more detail, please access http://localhost:13579/swagger-ui/index.html#/

