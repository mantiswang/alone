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
import com.ywang.utils.AloneConfiguration;

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

	private DataSourceFactory() throws SQLException {
		
		dataSource = new DruidDataSource();

		AloneConfiguration config = AloneConfiguration.getInstance();
		
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

		//五分钟输出一次状态到日志中
//		dataSource.setConnectionProperties("druid.timeBetweenLogStatsMillis=900000");
//		dataSource.setConnectionProperties("druid.stat.loggerName=druidLog");
//		dataSource.setConnectionProperties("druid.stat.mergeSql=true");

		// be advice: validation query cant work in Oracle.
		dataSource.setValidationQuery("select 'song'");
		dataSource.init();
	}

	// 初始化方法。
	public static DataSourceFactory initFactory()
			throws SQLException {

		if (null == factory) {
			factory = new DataSourceFactory();
		}

		return factory;
	}

	// 实例在使用时通过事先初始化，可随时被调用。
	public static DataSourceFactory getInstance() throws SQLException {
		if (null == factory) {
			factory = new DataSourceFactory();
		}

		return factory;
	}

	public DruidPooledConnection getConn() throws SQLException {
		return dataSource.getConnection();
	}

	public int getActiveCount() {
		return dataSource.getActiveCount();
	}
}
