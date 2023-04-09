package com.vau.studio.chat.server.chatgpt.chat.cache;

import com.vau.studio.chat.server.chatgpt.models.ChatMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;

@Configuration
public class CacheManager {

    @Value("${redis_host:localhost}")
    private String redisHost;

    @Value("${redis_port:6379}")
    private int redisPort;

    @Value("${redis_password:#{null}}")
    private String redisPassword;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redisHost);
        configuration.setPort(redisPort);
        if (Objects.nonNull(redisPassword)) {
            configuration.setPassword(RedisPassword.of(redisHost));
        }
        JedisConnectionFactory jedisConFactory = new JedisConnectionFactory(configuration);
        return jedisConFactory;
    }

    @Bean
    public RedisTemplate<String, ChatMessage> redisTemplate() {
        RedisTemplate<String, ChatMessage> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }
}
