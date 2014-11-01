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
package com.ywang.alone.db;

import java.sql.SQLException;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.ywang.utils.Config;

/*
 * song
 *
 * @author apple
 *
 * @since 1.0
 */
public class DataSourceFactory {
	private DruidDataSource dataSource = null;
	private static DataSourceFactory factory = null;

	private DataSourceFactory(Config config) throws SQLException {
		
		dataSource = new DruidDataSource();
		
		dataSource.setUrl(config.getDbURL());
		dataSource.setUsername(config.getDbUserName());
		dataSource.setPassword(config.getDbPassword());
		dataSource.setTestWhileIdle(true);

		dataSource.setMinIdle(config.getDbMinPoolSize());
		dataSource.setMaxActive(config.getDbMaxPoolSize());
		dataSource.setTimeBetweenEvictionRunsMillis(config.getDbConnMinLiveTime());

		dataSource.setFilters("druidLog,log4j");

		dataSource.setRemoveAbandoned(true);
		dataSource.setRemoveAbandonedTimeout(60);
		dataSource.setLogAbandoned(true);

		dataSource.setValidationQuery("select 'song'");
		dataSource.init();
	}

	// 初始化方法。
	public static DataSourceFactory initFactory(Config cfg)
			throws SQLException {

		if (null == factory) {
			factory = new DataSourceFactory(cfg);
		}

		return factory;
	}

	public static DataSourceFactory getInstance() throws SQLException {
		return factory;
	}

	public DruidPooledConnection getConn() throws SQLException {
		return dataSource.getConnection();
	}

	public int getActiveCount() {
		return dataSource.getActiveCount();
	}
}
