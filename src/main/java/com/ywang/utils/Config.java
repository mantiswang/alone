/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ywang.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/*
 * song
 *
 * @author apple
 *
 * @since 1.0
 */
public class Config {
	private Properties props;
	private static Config instance;
	private static final String PROP_FILENAME = "Config.properties";
	
	private int pageSize;

    private String dbURL = "";
    private String dbUserName = "";
    private String dbPassword = "";
    private int dbMinPoolSize = 1;
    private int dbMaxPoolSize = 2;
    private long dbConnMinLiveTime = 0L;


	private String redisServer = "";
    private int redisPort = 0;
    private int redisMaxTotalConnection = 0;
    private int redisMaxIdleConnection = 0;
    private boolean redisTestOnBorrow = false;
	private int redisDbNumber = 0;
	

	// private URI uri;
	// 初始化方法。
	public static Config getInstance() {

		if (null == instance) {
			instance = new Config();
		}

		return instance;
	}
	
	private Config()
	{
		try {
			props = new Properties();
			String path = Config.class.getClassLoader().getResource("")
					.toURI().getPath();
			InputStream fis = new FileInputStream(new File(path + PROP_FILENAME));
			props.load(fis);
			
			dbURL = props.getProperty("db.url").trim();
			dbUserName = props.getProperty("db.username").trim();
			dbPassword = props.getProperty("db.password").trim();
			dbMinPoolSize = Integer.valueOf(props.getProperty("db.minPoolSize").trim());
			dbMaxPoolSize = Integer.valueOf(props.getProperty("db.maxPoolSize").trim());
			dbConnMinLiveTime = Long.valueOf(props.getProperty("db.connLiveTimeMillis").trim());
			
		    redisServer = props.getProperty("redis.host").trim();
		    redisPort = Integer.valueOf(props.getProperty("redis.port").trim());
		    redisMaxTotalConnection = Integer.valueOf(props.getProperty("redis.maxTotalConnection").trim());
		    redisMaxIdleConnection = Integer.valueOf(props.getProperty("redis.maxIdleConnection").trim());
		    redisTestOnBorrow = Boolean.parseBoolean(props.getProperty("redis.setTestOnBorrow").toString().trim());;
		    redisDbNumber  = Integer.valueOf(props.getProperty("redis.dbNumber").trim());
		    
			pageSize = Integer.valueOf(props.getProperty("global.list.pageSize").trim());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 获取某个属性
	 */
	public String getProperty(String key) {
		return props.getProperty(key);
	}

	/**
	 * 在控制台上打印出所有属性，调试时用。
	 */
	public void printProperties() {
		props.list(System.out);
	}

	/**
	 * @return
	 */
	public String getDbURL() {

		return dbURL;
	}

	/**
	 * @return
	 */
	public String getDbUserName() {
		return dbUserName;
	}

	/**
	 * @return
	 */
	public String getDbPassword() {
		return dbPassword;
	}

	/**
	 * @return
	 */
	public int getDbMinPoolSize() {
		return dbMinPoolSize;
	}

	/**
	 * @return
	 */
	public int getDbMaxPoolSize() {
		return dbMaxPoolSize;

	}

	/**
	 * @return
	 */
	public long getDbConnMinLiveTime() {
		return dbConnMinLiveTime;

	}

	/**
	 * 页面数量大小
	 */
	public int getPageSize() {
		return pageSize;
	}

    public String getRedisServer() {
		return redisServer;
	}

	public int getRedisPort() {
		return redisPort;
	}

	public int getRedisMaxTotalConnection() {
		return redisMaxTotalConnection;
	}

	public int getRedisMaxIdleConnection() {
		return redisMaxIdleConnection;
	}

	public boolean isRedisTestOnBorrow() {
		return redisTestOnBorrow;
	}
	public int getRedisDbNumber() {
		return redisDbNumber;
	}

	
}
