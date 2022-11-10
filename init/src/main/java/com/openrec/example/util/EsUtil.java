package com.openrec.example.util;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import nl.altindag.ssl.SSLFactory;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.HashMap;
import java.util.Map;

public class EsUtil {

    private static Map<EsMeta, ElasticsearchClient> esMap;

    @Data
    @AllArgsConstructor
    static
    class EsMeta{
        private String host;
        private int port;
        private String user;
        private String password;
    }

    static {
        esMap = new HashMap<>();
    }

    public static ElasticsearchClient getEs(String host, int port, String user, String password) {
        EsMeta key = new EsMeta(host, port, user, password);
        if (!esMap.containsKey(key)) {
            SSLFactory sslFactory = SSLFactory.builder()
                    .withUnsafeTrustMaterial()
                    .withUnsafeHostnameVerifier()
                    .build();
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user,password));
            RestClient restClient = RestClient.builder(new HttpHost(host, port, "https"))
                    .setHttpClientConfigCallback(
                            httpAsyncClientBuilder ->
                                    httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                                            .setSSLContext(sslFactory.getSslContext())
                                            .setSSLHostnameVerifier(sslFactory.getHostnameVerifier())
                    )
                    .build();
            ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
            esMap.put(key, new ElasticsearchClient(transport));
        }
        return esMap.get(key);
    }
}
