# example

make `open-rec` easy to use.

## architecture
### standalone  
more details: [example standalone](https://github.com/open-rec/example/tree/master/example_standalone)

#### init data
exec the java entry class `InitStandalone`
```shell
java InitStandalone {redis_host} {redis_port} {es_host} {es_port} {es_user} {es_password}
```

check the data size：
##### redis
```shell
127.0.0.1:6379> DBSIZE
(integer) 1694378
127.0.0.1:6379> GET user:{acaebafcdde668fdf2cc2635943d1ae7}
"{\"id\":\"acaebafcdde668fdf2cc2635943d1ae7\",\"deviceId\":\"7554e402-6320-11ed-804c-000000001362\",\"name\":\"\xe6\xac\xa7\xe9\x98\xb3\xe9\x9b\xa8\xe7\x84\xb6\",\"gender\":\"0\",\"age\":31,\"country\":\"country_0\",\"city\":\"city_0\",\"phone\":\"phone_140681911227\",\"tags\":[\"tags_0\"],\"registerTime\":\"0\",\"loginTime\":\"0\",\"extFields\":\"{}\"}"
127.0.0.1:6379> GET item:{5105858}
"{\"id\":\"5105858\",\"weight\":0,\"title\":\"The Dreamer - \xe7\x94\xb5\xe5\xbd\xb1\",\"category\":\"\xe5\x96\x9c\xe5\x89\xa7/\xe6\xad\x8c\xe8\x88\x9e\",\"tags\":\"\",\"scene\":\"douban_movie\",\"pubTime\":\"0\",\"modifyTime\":\"0\",\"expireTime\":\"0\",\"status\":1,\"extFields\":\"{}\"}"
127.0.0.1:6379> ZCARD event:{acaebafcdde668fdf2cc2635943d1ae7}:douban_movie:click
(integer) 2
127.0.0.1:6379> ZSCAN event:{acaebafcdde668fdf2cc2635943d1ae7}:douban_movie:click 0
1) "0"
2) 1) "\"2072277\""
   2) "1258253885"
   3) "\xac\xed\x00\x05t\x00\a2072277"
   4) "1258253885"
```

##### es
url  
https://localhost:9200/douban_movie-item-vector-index/_search
request  
```json
{
  "knn": {
    "field": "vector",
    "query_vector": [0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9],
    "k": 10,
    "num_candidates": 20
  },
  "fields": [ "id"],
  "size": 5
}
```
response
```json
{
    "took": 8,
    "timed_out": false,
    "_shards": {
        "total": 1,
        "successful": 1,
        "skipped": 0,
        "failed": 0
    },
    "hits": {
        "total": {
            "value": 10,
            "relation": "eq"
        },
        "max_score": 0.3006939,
        "hits": [
            {
                "_index": "douban_movie-item-vector-index",
                "_id": "25611",
                "_score": 0.3006939,
                "_source": {
                    "id": "25708566",
                    "vector": [
                        0.037923265248537064,
                        -0.030717968940734863,
                        0.061975620687007904,
                        0.060993313789367676,
                        0.0680757537484169,
                        0.07531068474054337,
                        0.07300014793872833,
                        0.0438830628991127,
                        0.08153941482305527,
                        0.053830839693546295
                    ]
                },
                "fields": {
                    "id": [
                        "25708566"
                    ]
                }
            },
            {
                "_index": "douban_movie-item-vector-index",
                "_id": "36348",
                "_score": 0.29845268,
                "_source": {
                    "id": "10583864",
                    "vector": [
                        0.09464769065380096,
                        0.06388483196496964,
                        0.023691583424806595,
                        -0.037593841552734375,
                        0.035187553614377975,
                        0.026703214272856712,
                        0.0475650317966938,
                        0.0724814385175705,
                        0.09909756481647491,
                        0.09503273665904999
                    ]
                },
                "fields": {
                    "id": [
                        "10583864"
                    ]
                }
            },
            {
                "_index": "douban_movie-item-vector-index",
                "_id": "33350",
                "_score": 0.2939653,
                "_source": {
                    "id": "26013989",
                    "vector": [
                        0.03208710625767708,
                        0.07475648075342178,
                        0.06309840828180313,
                        -0.04886181280016899,
                        0.05122055485844612,
                        0.08939637988805771,
                        0.08569160103797913,
                        0.07909072935581207,
                        0.06954336911439896,
                        0.013547795824706554
                    ]
                },
                "fields": {
                    "id": [
                        "26013989"
                    ]
                }
            },
            {
                "_index": "douban_movie-item-vector-index",
                "_id": "27763",
                "_score": 0.28814808,
                "_source": {
                    "id": "30217605",
                    "vector": [
                        0.028828371316194534,
                        0.05470636487007141,
                        -0.05399073287844658,
                        0.09242211282253265,
                        -0.0182245634496212,
                        0.08572786301374435,
                        -0.07561305165290833,
                        0.08083266019821167,
                        0.07179568707942963,
                        0.09792803972959518
                    ]
                },
                "fields": {
                    "id": [
                        "30217605"
                    ]
                }
            },
            {
                "_index": "douban_movie-item-vector-index",
                "_id": "36087",
                "_score": 0.28777614,
                "_source": {
                    "id": "2139012",
                    "vector": [
                        0.05899118632078171,
                        -0.07286615669727325,
                        0.0347398966550827,
                        0.0897112712264061,
                        0.04871796816587448,
                        0.09919242560863495,
                        0.059476278722286224,
                        -0.03929898887872696,
                        0.06674426048994064,
                        0.05762871727347374
                    ]
                },
                "fields": {
                    "id": [
                        "2139012"
                    ]
                }
            }
        ]
    }
}
```

### cluster
more details: [example cluster](https://github.com/open-rec/example/tree/master/example_cluster)

