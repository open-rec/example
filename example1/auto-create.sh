#/bin/bash

# install redis
brew install redis
brew services start redis

# init redis data
java -jar rec-example-init-1.0-SNAPSHOT.jar 127.0.0.1 6379

# start rec server
nohup java -jar rec-server-1.0-SNAPSHOT.jar 2>&1 > auto-create.log &