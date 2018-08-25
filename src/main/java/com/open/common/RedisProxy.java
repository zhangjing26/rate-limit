package com.open.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;

/**
 * @author Jing.Zhang
 * @date 2018/8/26
 */
public class RedisProxy {
    private static Logger logger = LoggerFactory.getLogger(RedisProxy.class);

    private JedisPool jedisPool;

    public RedisProxy(String host, int port) {
        JedisPoolConfig poolConfig = buildPoolConfig(128, 128, 16);
        jedisPool = new JedisPool(poolConfig, host, port);
    }

    private JedisPoolConfig buildPoolConfig(int maxTotal, int maxIdle, int minIdle) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        return poolConfig;
    }

    public List<String> lrange(String appKey, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.lrange(appKey, start, end);
        } catch (Exception e) {
            logger.error("lrange error. key = " + appKey, e);
            return null;
        } finally {
            close(jedis);
        }
    }

    private void close(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    public boolean rpush(String key, String values) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.rpush(key, values) > 0;
        } catch (Exception e) {
            logger.error("rpush error: key = " + key, e);
            return false;
        } finally {
            close(jedis);
        }
    }

    public boolean expire(String key, int seconds) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.expire(key, seconds) == 1;
        } catch (Exception e) {
            logger.error("expire error: key = %s, seconds = %s, e = %s", key, seconds, e);
        } finally {
            close(jedis);
        }
        return false;
    }
}
