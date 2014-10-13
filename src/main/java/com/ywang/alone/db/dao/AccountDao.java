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
package com.ywang.alone.db.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ywang.alone.db.DataSourceFactory;
import com.ywang.utils.LoggerUtil;

/*
 * song
 *
 * @author apple
 *
 * @since 1.0
 */
public class AccountDao {
	public static String replaceUser(String msg) {
		JSONObject req = JSON.parseObject(msg);
		JSONObject result = new JSONObject();

		PreparedStatement stmt = null;
		DruidPooledConnection conn = null;

		try {
			conn = DataSourceFactory.getInstance().getConn();
			conn.setAutoCommit(false);

			stmt = conn
					.prepareStatement(
							"replace into account (uid, accountType, deviceType, lastOnlineTime) values (?,?,?,?)",
							Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, req.getString("uid"));
			stmt.setInt(2, req.getIntValue("accountType"));
			stmt.setInt(3, req.getIntValue("deviceType"));
			stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));

			int r = stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();

			if (r == 1 && rs.next()) {
				result.put("sid", rs.getInt(1));
			}
			rs.close();

			conn.commit();
			conn.setAutoCommit(true);

		} catch (Exception ex) {
			ex.printStackTrace();
			LoggerUtil.logServerErr(ex);
		} finally {
			try {
				if (null != stmt) {
					stmt.close();
				}
				if (null != conn) {
					conn.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		LoggerUtil.logMsg(result.toString());
		return result.toString();
	}
}
