package lz.gateway.server.config;
 
import org.apache.log4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lz.gateway.server.serializer.FastJson2JsonRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;
 

@Configuration
@EnableCaching
public class RedisConfig
{

    private static Logger logger = Logger.getLogger(RedisConfig.class);

//    @SuppressWarnings("rawtypes")
//    @Bean
//    public CacheManager cacheManager(RedisTemplate redisTemplate)
//    {
//        RedisCacheManager rcm = new RedisCacheManager(redisTemplate);
//        // 多个缓存的名称
//        List<String> redisList = new ArrayList<>();
//        redisList.add(CACHE_KEY_REDIS);             //基础redis
//        redisList.add(CACHE_KEY_DATA_DICTIONARY);   //数据字典
//        redisList.add(REDIS_AREA_PARENT_);          //区域
//        redisList.add(REDIS_AUTHORITY_USER_);       //用户菜单
//        redisList.add(REDIS_ORGANIZATION_PARENT_);  //组织部门
//        redisList.add(REDIS_PUBLICCONFIG_KEY);  //组织部门
//        redisList.add(REDIS_QUALIFICATION_);  //网点资质模板
//        redisList.add(REDIS_ROLE_);  //角色
//        rcm.setCacheNames(redisList);
//        return rcm;
//    }

    @Bean
    @ConfigurationProperties(prefix = "spring.redis.pool")
    public JedisPoolConfig getRedisConfig()
    {
        JedisPoolConfig config = new JedisPoolConfig();
        return config;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.redis")
    public JedisConnectionFactory getConnectionFactory()
    {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        JedisPoolConfig config = getRedisConfig();
        factory.setPoolConfig(config);
        logger.info("JedisConnectionFactory bean init success.");
        return factory;
    }


    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory)
    {
        FastJson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new FastJson2JsonRedisSerializer<Object>(Object.class);
        RedisSerializer<String> keySerializer = new StringRedisSerializer();

        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(keySerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashKeySerializer(keySerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }


}