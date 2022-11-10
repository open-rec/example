package com.openrec.example;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.openrec.example.util.EsUtil;
import com.openrec.example.util.JsonUtil;
import com.openrec.example.util.RedisUtil;
import com.openrec.proto.model.Event;
import com.openrec.proto.model.Item;
import com.openrec.proto.model.User;
import com.openrec.proto.model.VectorResult;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
public class Init1 {

    private static final String TEST_DATA_DIR = System.getProperty("user.dir") + "/data/test";
    private static final String TEST_ITEM_DATA = TEST_DATA_DIR + "/item.csv";
    private static final String TEST_USER_DATA = TEST_DATA_DIR + "/user.csv";
    private static final String TEST_EVENT_DATA = TEST_DATA_DIR + "/event.csv";

    private static final String TEST_RECALL_DATA_DIR = TEST_DATA_DIR + "/recall";
    private static final String TEST_RECALL_I2I_DATA = TEST_RECALL_DATA_DIR + "/i2i.csv";
    private static final String TEST_RECALL_EMBEDDING_DATA = TEST_RECALL_DATA_DIR + "/embedding.csv";
    private static final String TEST_RECALL_HOT_DATA = TEST_RECALL_DATA_DIR + "/hot.csv";
    private static final String TEST_RECALL_NEW_DATA = TEST_RECALL_DATA_DIR + "/new.csv";


    private static void initRedisItemData(RedisTemplate redisTemplate) {
        try {
            Reader reader = Files.newBufferedReader(Paths.get(TEST_ITEM_DATA));
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreEmptyLines(true)
                    .withTrim()
                    .parse(reader);
            for (CSVRecord record : records) {
                Item item = new Item();
                item.setId(record.get("id"));
                item.setTitle(record.get("title"));
                item.setCategory(record.get("category"));
                item.setTags(record.get("tags"));
                item.setScene(record.get("scene"));
                item.setPubTime(record.get("pub_time"));
                item.setModifyTime(record.get("modify_time"));
                item.setExpireTime(record.get("expire_time"));
                item.setStatus(Integer.parseInt(record.get("status")));
                item.setWeight(Integer.parseInt(record.get("weight")));
                item.setExtFields(record.get("ext_fields"));
                redisTemplate.opsForValue().set(String.format("item:{%s}", item.getId()), item);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("init item data finished");
    }

    private static void initRedisUserData(RedisTemplate redisTemplate) {
        try {
            Reader reader = Files.newBufferedReader(Paths.get(TEST_USER_DATA));
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreEmptyLines(true)
                    .withTrim()
                    .parse(reader);
            for (CSVRecord record : records) {
                User user = new User();
                user.setId(record.get("id"));
                user.setDeviceId(record.get("device_id"));
                user.setName(record.get("name"));
                user.setGender(record.get("gender"));
                user.setAge(Integer.parseInt(record.get("age")));
                user.setCountry(record.get("country"));
                user.setCity(record.get("city"));
                user.setPhone(record.get("phone"));
                user.setTags(Arrays.asList(record.get("tags").split(",")));
                user.setRegisterTime(record.get("register_time"));
                user.setLoginTime(record.get("login_time"));
                user.setExtFields(record.get("ext_fields"));
                redisTemplate.opsForValue().set(String.format("user:{%s}", user.getId()), user);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("init user data finished");
    }

    private static void initRedisEventData(RedisTemplate redisTemplate) {
        try {
            Reader reader = Files.newBufferedReader(Paths.get(TEST_EVENT_DATA));
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreEmptyLines(true)
                    .withTrim()
                    .parse(reader);
            for (CSVRecord record : records) {
                Event event = new Event();
                event.setUserId(record.get("user_id"));
                event.setItemId(record.get("item_id"));
                event.setTraceId(record.get("trace_id"));
                event.setScene(record.get("scene"));
                event.setType(record.get("type"));
                event.setValue(record.get("value"));
                event.setTime(record.get("time"));
                event.setLogin(Boolean.parseBoolean(record.get("is_login")));
                event.setExtFields(record.get("ext_fields"));
                redisTemplate.opsForZSet().add(
                        String.format("event:{%s}:%s:%s", event.getUserId(), event.getScene(), event.getType()),
                        event.getItemId(),
                        Double.valueOf(event.getTime()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("init event data finished");
    }

    private static void initRedisI2iData(RedisTemplate redisTemplate) {
        try {
            Reader reader = Files.newBufferedReader(Paths.get(TEST_RECALL_I2I_DATA));
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreEmptyLines(true)
                    .withTrim()
                    .parse(reader);
            for (CSVRecord record : records) {
                String scene = record.get("scene");
                String leftItem = record.get("left_item");
                String rightItem = record.get("right_item");
                Double score = Double.valueOf(record.get("score"));
                redisTemplate.opsForZSet().add(String.format("i2i:{%s}:%s", leftItem, scene), rightItem, score);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("init i2i data finished");
    }

    private static void initRedisHotData(RedisTemplate redisTemplate) {
        try {
            Reader reader = Files.newBufferedReader(Paths.get(TEST_RECALL_HOT_DATA));
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreEmptyLines(true)
                    .withTrim()
                    .parse(reader);
            for (CSVRecord record : records) {
                String scene = record.get("scene");
                String item = record.get("item");
                Double score = Double.valueOf(record.get("score"));
                redisTemplate.opsForZSet().add(String.format("hot:{%s}", scene), item, score);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("init hot data finished");
    }

    private static void initRedisNewData(RedisTemplate redisTemplate) {
        try {
            Reader reader = Files.newBufferedReader(Paths.get(TEST_RECALL_NEW_DATA));
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreEmptyLines(true)
                    .withTrim()
                    .parse(reader);
            for (CSVRecord record : records) {
                String scene = record.get("scene");
                String item = record.get("item");
                Double score = Double.valueOf(record.get("score"));
                redisTemplate.opsForZSet().add(String.format("new:{%s}", scene), item, score);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("init new data finished");
    }

    private static final String ITEM_VECTOR_INDEX = "{\n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"vector\": {\n" +
            "        \"type\": \"dense_vector\",\n" +
            "        \"dims\": 10,\n" +
            "        \"index\": true,\n" +
            "        \"similarity\": \"l2_norm\"\n" +
            "      },\n" +
            "      \"id\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

    private static void initEsEmbeddingData(ElasticsearchClient esClient) {
        try {
            Reader reader = Files.newBufferedReader(Paths.get(TEST_RECALL_EMBEDDING_DATA));
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreEmptyLines(true)
                    .withTrim()
                    .parse(reader);
            Map<String, List<Pair<String, List<Double>>>> sceneItemVectorsMap = new HashMap<>();
            for (CSVRecord record : records) {
                String scene = record.get("scene");
                String itemId = record.get("item");
                List<Double> vector = JsonUtil.jsonToObj(record.get("vector"), List.class);
                if (!sceneItemVectorsMap.containsKey(scene)) {
                    sceneItemVectorsMap.put(scene, new LinkedList<>());
                }
                sceneItemVectorsMap.get(scene).add(new Pair<>(itemId, vector));
            }

            for (Map.Entry<String, List<Pair<String, List<Double>>>> entry : sceneItemVectorsMap.entrySet()) {
                String scene = entry.getKey();
                String indexName = String.format("%s-item-vector-index", scene);

                ExistsRequest existsRequest = ExistsRequest.of(i -> i.index(indexName));
                BooleanResponse response = esClient.indices().exists(existsRequest);
                if (response.value()) {
                    DeleteIndexRequest deleteRequest = DeleteIndexRequest.of(i -> i.index(indexName));
                    esClient.indices().delete(deleteRequest);
                }
                CreateIndexRequest indexRequest = CreateIndexRequest
                        .of(i -> i.index(indexName).withJson(new StringReader(ITEM_VECTOR_INDEX)));
                boolean created = esClient.indices().create(indexRequest).acknowledged();
                if (!created) {
                    log.error("{} create failed", indexName);
                    return;
                }

                List<Pair<String, List<Double>>> itemVectors = entry.getValue();
                int total = itemVectors.size();
                int batch = 10;
                int count = 0;
                BulkRequest.Builder bulkReqBuilder = new BulkRequest.Builder();
                for (int i = 0; i < total; i++) {
                    int finalI = i;
                    count++;
                    bulkReqBuilder.operations(op -> op.index(o -> o.index(indexName)
                            .id(String.valueOf(finalI))
                            .document(new VectorResult(
                                    String.valueOf(itemVectors.get(finalI).getKey()),
                                    itemVectors.get(finalI).getValue()))));
                    if (count == batch) {
                        esClient.bulk(bulkReqBuilder.build());
                        bulkReqBuilder = new BulkRequest.Builder();
                        count = 0;
                    }
                }
                if (count > 0) {
                    esClient.bulk(bulkReqBuilder.build());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("init embedding data finished");
    }

    public static void initRedisData(String host, int port) {
        RedisTemplate redisTemplate = RedisUtil.getRedis(host, port);
        if (redisTemplate == null) {
            log.error("redis init failed");
            return;
        }
        initRedisUserData(redisTemplate);
        initRedisItemData(redisTemplate);
        initRedisEventData(redisTemplate);

        initRedisI2iData(redisTemplate);
        initRedisHotData(redisTemplate);
        initRedisNewData(redisTemplate);
        log.info("init redis data finished");
    }

    public static void initEsData(String host, int port, String user, String password) {
        ElasticsearchClient esClient = EsUtil.getEs(host, port, user, password);
        if (esClient == null) {
            log.error("es init failed");
            return;
        }
        initEsEmbeddingData(esClient);
        log.info("init es data finished");
    }


    public static void main(String[] args) {
        if (args.length != 6) {
            log.error("invalid params, please input redis host and port & es host, port ,user and password");
            return;
        }
        String redisHost = args[0];
        int redisPort = Integer.valueOf(args[1]);
        initRedisData(redisHost, redisPort);

        String esHost = args[2];
        int esPort = Integer.valueOf(args[3]);
        String esUser = args[4];
        String esPassword = args[5];
        initEsData(esHost, esPort, esUser, esPassword);

        System.exit(0);
    }
}
