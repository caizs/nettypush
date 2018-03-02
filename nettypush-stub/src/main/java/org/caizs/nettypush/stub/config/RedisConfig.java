package org.caizs.nettypush.stub.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.caizs.nettypush.core.common.ConfigLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@Lazy(false)
public class RedisConfig {

    private static GenericJackson2JsonRedisSerializer stringSerializer = new GenericJackson2JsonRedisSerializer();

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory jedisConFactory = new JedisConnectionFactory();
        jedisConFactory.setDatabase(ConfigLoader.getPropertyInt("redis.database"));
        jedisConFactory.setHostName(ConfigLoader.getProperty("redis.host"));
        jedisConFactory.setPort(ConfigLoader.getPropertyInt("redis.port"));
        jedisConFactory.setPassword(ConfigLoader.getProperty("redis.password"));

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxWaitMillis(ConfigLoader.getPropertyInt("redis.pool.max-wait"));
        config.setMaxIdle(ConfigLoader.getPropertyInt("redis.pool.max-idle"));
        config.setMinIdle(ConfigLoader.getPropertyInt("redis.pool.min-idle"));
        jedisConFactory.setPoolConfig(config);

        jedisConFactory.setTimeout(ConfigLoader.getPropertyInt("redis.timeout"));
        return jedisConFactory;
    }

    @Bean("stringRedisTemplate")
    StringRedisTemplate stringTemplate(RedisConnectionFactory connectionFactory) {
        Jackson2JsonRedisSerializer<Object> jacksonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        jacksonSerializer.setObjectMapper(buildObjectMapper());

        StringRedisTemplate template = new StringRedisTemplate(connectionFactory);
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        //        template.setValueSerializer(jacksonSerializer);
        //        template.setHashValueSerializer(jacksonSerializer);
        template.afterPropertiesSet();
        return template;
    }

    private static ObjectMapper buildObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        // 只序列化属性，忽略setter,getter,is_getter和creater
        om.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        // 让所有的非final类型对象持久化时都存储类型信息,准确的反序列多态类型的数据
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        // 设置忽略不存在的字段
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 允许出现特殊字符和转义符
        om.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

        return om;
    }


}