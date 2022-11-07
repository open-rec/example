package com.openrec.example;

import com.openrec.example.util.RedisUtil;
import com.openrec.proto.model.Event;
import com.openrec.proto.model.Item;
import com.openrec.proto.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

@Slf4j
public class Init1 {

    private static final String TEST_DATA_DIR = System.getProperty("user.dir") + "/data/test";
    private static final String TEST_ITEM_DATA = TEST_DATA_DIR + "/item.csv";
    private static final String TEST_USER_DATA = TEST_DATA_DIR + "/user.csv";
    private static final String TEST_EVENT_DATA = TEST_DATA_DIR + "/event.csv";

    private static final String TEST_RECALL_DATA_DIR = TEST_DATA_DIR + "/recall";
    private static final String TEST_RECALL_I2I_DATA = TEST_RECALL_DATA_DIR + "/i2i.csv";
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

    public static void initRedisData(String host, int port) {
        RedisTemplate redisTemplate = RedisUtil.getRedis(host, port);
        if (redisTemplate == null) {
            log.error("redis init failed");
            return;
        }
        //initRedisUserData(redisTemplate);
        //initRedisItemData(redisTemplate);
        initRedisEventData(redisTemplate);

        //initRedisI2iData(redisTemplate);
        //initRedisHotData(redisTemplate);
        //initRedisNewData(redisTemplate);
        log.info("init redis data finished");
    }


    public static void main(String[] args) {
        if (args.length != 2) {
            log.error("invalid params, please input redis host and port");
            return;
        }
        String redisHost = args[0];
        int redisPort = Integer.valueOf(args[1]);
        initRedisData(redisHost, redisPort);
    }
}
