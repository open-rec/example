#/bin/bash

# install redis
brew install redis
brew services start redis

# install elasticsearch
# for version 8.5.0, elasticsearch access with security limit, create common test user later.
curl -O https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-8.5.0-darwin-x86_64.tar.gz
tar -xzf elasticsearch-8.5.0-darwin-x86_64.tar.gz
cd elasticsearch-8.5.0/
./bin/elasticsearch -d


# init redis data
java -jar rec-example-init-1.0-SNAPSHOT.jar 127.0.0.1 6379

# start rec server
nohup java -jar rec-server-1.0-SNAPSHOT.jar 2>&1 > auto-create.log &