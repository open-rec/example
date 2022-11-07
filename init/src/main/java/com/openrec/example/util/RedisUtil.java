package com.openrec.example.util;

import javafx.util.Pair;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.HashMap;
import java.util.Map;

public class RedisUtil {

    private static Map<Pair<String, Integer>, RedisTemplate> redisMap;

    static {
        redisMap = new HashMap<>();
    }

    public static RedisTemplate getRedis(String host, int port) {
        Pair<String, Integer> key = new Pair<>(host, port);
        if (!redisMap.containsKey(key)) {
            JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
            jedisConnectionFactory.setHostName(host);
            jedisConnectionFactory.setPort(port);
            RedisTemplate redisTemplate = new RedisTemplate();
            redisTemplate.setConnectionFactory(jedisConnectionFactory);
            redisTemplate.afterPropertiesSet();
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisMap.put(key, redisTemplate);
        }
        return redisMap.get(key);
    }
}
