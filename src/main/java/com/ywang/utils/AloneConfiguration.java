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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/*
 * song
 *
 * @author apple
 *
 * @since 1.0
 */
public class AloneConfiguration {
	private Properties props;
	private static AloneConfiguration instance;
	private static final String PROP_FILENAME = "Config.properties";
	
	// private URI uri;
	// 初始化方法。
	public static AloneConfiguration getInstance() {

		if (null == instance) {
			instance = new AloneConfiguration();
		}

		return instance;
	}

	private AloneConfiguration() {
		readProperties(PROP_FILENAME);
	}

	private void readProperties(String fileName) {
		try {
			props = new Properties();
			String path = AloneConfiguration.class.getClassLoader().getResource("")
					.toURI().getPath();
			// InputStream fis = getClass().getResourceAsStream(fileName);
			InputStream fis = new FileInputStream(new File(path + fileName));
			props.load(fis);
			// uri = this.getClass().getResource("dbConfig.properties").toURI();
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
	 * 获取所有属性，返回一个map,不常用 可以试试props.putAll(t)
	 */
	public Map<Object, Object> getAllProperty() {
		Map<Object, Object> map = new HashMap<Object, Object>();
		Enumeration enu = props.propertyNames();
		while (enu.hasMoreElements()) {
			String key = (String) enu.nextElement();
			String value = props.getProperty(key);
			map.put(key, value);
		}
		return map;
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

		return props.getProperty("server.db.url");
	}

	/**
	 * @return
	 */
	public String getDbUserName() {
		return props.getProperty("server.db.username");
	}

	/**
	 * @return
	 */
	public String getDbPassword() {
		return props.getProperty("server.db.password");
	}

	/**
	 * @return
	 */
	public int getDbMinPoolSize() {
		return Integer.valueOf(props.getProperty("server.db.minPoolSize"));
	}

	/**
	 * @return
	 */
	public int getDbMaxPoolSize() {
		return Integer.valueOf(props.getProperty("server.db.maxPoolSize"));

	}

	/**
	 * @return
	 */
	public long getDbConnMinLiveTime() {
		return Integer.valueOf(props
				.getProperty("server.db.connLiveTimeMillis"));

	}

	/**
	 * 页面数量大小
	 */
	public int globalPageSize() {
		return Integer.valueOf(props.getProperty("global.list.pageSize"));
	}

}
