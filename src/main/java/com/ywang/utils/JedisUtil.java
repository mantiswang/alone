package com.ywang.utils;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

public class JedisUtil {

    private JedisPool pool;
    private static JedisUtil instance = null;
    private static int dbNumber = 0;

    private JedisUtil(Config cfg) {
        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxTotal(cfg.getRedisMaxTotalConnection());
        config.setMaxIdle(cfg.getRedisMaxIdleConnection());
        config.setMaxWaitMillis(60000l);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        config.setTestWhileIdle(true);
        config.setMinEvictableIdleTimeMillis(10000);
        config.setTimeBetweenEvictionRunsMillis(30000l);

        dbNumber = cfg.getRedisDbNumber();
        
        pool = new JedisPool(config, cfg.getRedisServer(), cfg.getRedisPort());
        
    }

    public static JedisUtil init(Config cfg) {
        if (null == instance) {

            synchronized (JedisUtil.class) {
                if (null == instance) {
                    instance = new JedisUtil(cfg);
                }
            }

        }
        return instance;
    }

    private static JedisUtil getInstance() {
        return instance;
    }

    public static Jedis getJedis() {
        Jedis j = null;
        try {
            j = getInstance().pool.getResource();
            j.select(dbNumber);
        } catch (JedisException e) {
            if (null != j) {
                getInstance().pool.returnBrokenResource(j);
            }
            e.printStackTrace();
        }

        return j;
    }

    public static void returnJedis(Jedis j) {
        if (null != j) {
            getInstance().pool.returnResource(j);
        }
        j = null;
    }

    public static void returnJedisBrokenResource(Jedis j) {
        if (null != j) {
            getInstance().pool.returnBrokenResource(j);
        }
        j = null;
    }

    public static String getPoolInfo() {
        
        return "";
    }
}
