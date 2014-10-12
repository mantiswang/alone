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
public class PropertiesUtil {
	private Properties props;
//	private URI uri;

	public PropertiesUtil(String fileName) {
		readProperties(fileName);
	}

	private void readProperties(String fileName) {
		try {
			props = new Properties();
			String path = PropertiesUtil.class.getClassLoader().getResource("").toURI().getPath();
//			InputStream fis = getClass().getResourceAsStream(fileName);
			InputStream fis = new FileInputStream(new File(path + fileName));
			props.load(fis);
//			uri = this.getClass().getResource("dbConfig.properties").toURI();
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

//	/**
//	 * 写入properties信息
//	 */
//	public void writeProperties(String key, String value) {
//		try {
//			OutputStream fos = new FileOutputStream(new File(uri));
//			props.setProperty(key, value);
//			// 将此 Properties 表中的属性列表（键和元素对）写入输出流
//			props.store(fos, "『comments』Update key：" + key);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

//	public static void main(String[] args) {
//		PropertiesUtil util = new PropertiesUtil("src/dbConfig.properties");
//		util.writeProperties("dbtype", "MSSQL");
//	}

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
}
