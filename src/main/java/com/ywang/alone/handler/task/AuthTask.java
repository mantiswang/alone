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

package com.ywang.alone.handler.task;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ywang.alone.Constant;
import com.ywang.alone.db.DataSourceFactory;
import com.ywang.alone.modeles.UserInfo;
import com.ywang.alone.utils.AloneUtil;
import com.ywang.utils.LoggerUtil;
import com.ywang.utils.MD5;

/*
 * song
 *
 * @author apple
 *
 * @since 1.0
 */
public class AuthTask {

	private static final int REG_NEW_USER = 1;
	private static final int LOGIN = 2;

	private static final int NEARBY = 3;

	public static String execute(int code, String param) {

		switch (code) {
		case REG_NEW_USER:
			return regNewUser(param);
		case LOGIN:
			return login(param);
		case NEARBY:
			return nearby(param);
		default:
			break;
		}
		return null;
	}

	/**
	 * 附近的人
	 * 
	 * @param param
	 *            <pre>
	 *    {
	 *    'key':'2597aa1d37d432a','lng':'117.157954','lat':'31.873432','currPage':'1','pageSize':'50' 
	 *    }
	 * </pre>
	 * 
	 * @return
	 */
	private static String nearby(String msg) {
		JSONObject jsonObject = AloneUtil.newRetJsonObject();
		JSONObject user = JSON.parseObject(msg);

		String token = user.getString("key");
		if (StringUtils.isEmpty(token)) {
			jsonObject.put("ret", 1);
			jsonObject.put("errCode", Constant.ErrorCode.NO_ACCESS_AUTH);
			jsonObject.put("errDesc", Constant.ErrorDesc.NO_ACCESS_AUTH);
			return jsonObject.toJSONString();
		}

		DruidPooledConnection conn = null;
		PreparedStatement stmt = null;
		try {

			conn = DataSourceFactory.getInstance().getConn();
			conn.setAutoCommit(false);
			// 检查是否登录
			stmt = conn
					.prepareStatement("select USER_ID from userbase where PKEY = ?");
			stmt.setString(1, token);

			ResultSet rs = stmt.executeQuery();
			if (!rs.next()) {
				jsonObject.put("ret", 1);
				jsonObject.put("errCode", Constant.ErrorCode.NO_ACCESS_AUTH);
				jsonObject.put("errDesc", Constant.ErrorDesc.NO_ACCESS_AUTH);

				rs.close();
				return jsonObject.toJSONString();
			}

			// TODO 把需要的column 列出来
			PreparedStatement nearbyStmt = conn
					.prepareStatement("SELECT USER_ID,AVATAR,NICKNAME,AGE,ROLENAME,`ONLINE`,LAST_LOGIN_TIME,INTRO,"
							+ "ROUND(6378.138 * 2 * ASIN(SQRT(POW(SIN((? * PI()/180-lat * PI()/180)/2),2) +"
							+ " COS(? * PI()/180) * COS(lat * PI()/180) * POW(SIN((? * PI()/180-lng * PI()/180)/2),2))) * 1000) "
							+ " AS  DISTANCE "
							+ "FROM userbase ORDER BY distance LIMIT ?,?");
			nearbyStmt.setString(1, user.getString("lat"));
			nearbyStmt.setString(2, user.getString("lat"));
			nearbyStmt.setString(3, user.getString("lng"));
			nearbyStmt.setInt(4, Integer.valueOf(user.getString("currPage")));
			nearbyStmt.setInt(5, Integer.valueOf(user.getString("pageSize")));

			JSONArray nearbyUserArray = new JSONArray();
			UserInfo userInfo = null;
			ResultSet nearbyRs = stmt.executeQuery();
			while (nearbyRs.next()) {

				userInfo = new UserInfo();
				userInfo.setUserId(rs.getString("USER_ID"));
				userInfo.setAvatar(rs.getString("AVATAR"));
				userInfo.setNickName(rs.getString("NICKNAME"));
				userInfo.setAge(rs.getString("AGE"));
				userInfo.setRoleName(rs.getString("ROLENAME"));
				userInfo.setOnline(rs.getString("ONLINE"));
				userInfo.setLastLoginTime(new Date(rs
						.getDate("LAST_LOGIN_TIME").getTime()));
				userInfo.setIntro(rs.getString("INTRO"));
				userInfo.setDistance(rs.getString("DISTANCE"));

				nearbyUserArray.add(userInfo);
			}
			jsonObject.put("data", nearbyUserArray);
			nearbyRs.close();

			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			LoggerUtil.logServerErr(e);
			jsonObject.put("ret", 2);
		} finally {
			try {
				if (null != stmt) {
					stmt.close();
				}
				if (null != conn) {
					conn.close();
				}
			} catch (SQLException e) {
				LoggerUtil.logServerErr(e.getMessage());
			}
		}

		return jsonObject.toJSONString();
	}

	/**
	 * 登录 如果没有
	 * 
	 * @param param
	 * 
	 *            {
	 *            'phoneNum':'ywang','password':'e10adc3949ba59abbe56e057f20f883
	 *            e ','lng':'117.157954','lat':'31.873432',
	 *            'deviceToken':'8a2597aa1d37d432a88a446d82b6561e','osVersion':'8
	 *            . 0 ' , 'systemType':'iOS','phoneModel':'iPhone 5s'}
	 * 
	 *            OR
	 * 
	 *            {'key':'2597aa1d37d432a','lng':'117.157954','lat':'31.873432',
	 *            'deviceToken':'8a2597aa1d37d432a88a446d82b6561e','osVersion':'8
	 *            . 0 ' , 'systemType':'iOS','phoneModel':'iPhone 5s'}
	 * 
	 * @return
	 */
	private static String login(String msg) {
		JSONObject jsonObject = AloneUtil.newRetJsonObject();
		JSONObject user = JSON.parseObject(msg);

		DruidPooledConnection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = DataSourceFactory.getInstance().getConn();
			conn.setAutoCommit(false);
			String token = user.getString("key");
			boolean needUpdateToken = false;

			if (StringUtils.isEmpty(token)) {
				needUpdateToken = true;
				String uuid = UUID.randomUUID().toString();
				uuid = uuid.replaceAll("-", "");
				token = MD5.getMD5TokenString(uuid);

				stmt = conn
						.prepareStatement("select * from userbase where PHONE_NUM = ? and PWD = ?");
				stmt.setString(1, user.getString("phoneNum"));
				stmt.setString(2, user.getString("password"));
			} else {
				stmt = conn
						.prepareStatement("select * from userbase where PKEY = ? ");
				stmt.setString(1, token);
			}

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {

				UserInfo userInfo = new UserInfo();
				userInfo.setUserId(rs.getString("USER_ID"));
				userInfo.setAvatar(rs.getString("AVATAR"));
				userInfo.setNickName(rs.getString("NICKNAME"));
				userInfo.setAge(rs.getString("AGE"));
				userInfo.setHoroscope(rs.getString("HORO_SCOPE"));
				userInfo.setHeight(rs.getString("HEIGHT"));
				userInfo.setWeight(rs.getString("WEIGHT"));
				userInfo.setRoleName(rs.getString("ROLENAME"));
				userInfo.setAffection(rs.getString("AFFECTION"));
				userInfo.setPurpose(rs.getString("PURPOSE"));
				userInfo.setEthnicity(rs.getString("ETHNICITY"));
				userInfo.setOccupation(rs.getString("OCCUPATION"));
				userInfo.setLivecity(rs.getString("LIVECITY"));
				userInfo.setLocation(rs.getString("LOCATION"));
				userInfo.setTravelcity(rs.getString("TRAVELCITY"));
				userInfo.setMovie(rs.getString("MOVIE"));
				userInfo.setMusic(rs.getString("MUSIC"));
				userInfo.setBooks(rs.getString("BOOKS"));
				userInfo.setFood(rs.getString("FOOD"));
				userInfo.setOthers(rs.getString("OTHERS"));
				userInfo.setIntro(rs.getString("INTRO"));
				userInfo.setMessagePwd(rs.getString("MESSAGE_PWD"));
				userInfo.setMessageUser(rs.getString("MESSAGE_USER"));
				userInfo.setKey(token);

				jsonObject.put("data", JSONObject.toJSON(userInfo));
			} else {
				jsonObject.put("ret", 1);
				jsonObject.put("errCode", Constant.ErrorCode.LOGIN_FAIL);
				jsonObject.put("errDesc", Constant.ErrorDesc.LOGIN_FAIL);
			}

			rs.close();

			// 更新token 最后登录时间
			PreparedStatement updatestmt = null;
			if (needUpdateToken) {
				updatestmt = conn
						.prepareStatement("update userbase set PKEY = ? and LAST_LOGIN_TIME = ? and OS_VERSION=? and SYSTEM_TYPE=? and PHONE_MODEL=? and DEVICE_TOKEN=? and LNG=? and LAT=?");
				updatestmt.setString(1, token);
				updatestmt.setDate(2,
						new java.sql.Date(System.currentTimeMillis()));
				updatestmt.setString(3, user.getString("osVersion"));
				updatestmt.setString(4, user.getString("systemType"));
				updatestmt.setString(5, user.getString("phoneModel"));
				updatestmt.setString(6, user.getString("deviceToken"));
				updatestmt.setString(7, user.getString("lng"));
				updatestmt.setString(8, user.getString("lat"));
			} else {
				updatestmt = conn
						.prepareStatement("update userbase set LAST_LOGIN_TIME = ? and LNG=? and LAT=?  and OS_VERSION=? and SYSTEM_TYPE=? and PHONE_MODEL=? and DEVICE_TOKEN=? ");
				updatestmt.setDate(1,
						new java.sql.Date(System.currentTimeMillis()));
				updatestmt.setString(2, user.getString("lng"));
				updatestmt.setString(3, user.getString("lat"));
				updatestmt.setString(4, user.getString("osVersion"));
				updatestmt.setString(5, user.getString("systemType"));
				updatestmt.setString(6, user.getString("phoneModel"));
				updatestmt.setString(7, user.getString("deviceToken"));
			}
			int result = updatestmt.executeUpdate();

			if (result != 1) {
				jsonObject.put("ret", 1);
				jsonObject.put("errCode", Constant.ErrorCode.TOKEN_UPDATE_FAIL);
				jsonObject.put("errDesc", Constant.ErrorDesc.TOKEN_UPDATE_FAIL);
			}
			updatestmt.close();

			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			LoggerUtil.logServerErr(e);
			jsonObject.put("ret", 2);
		} finally {
			try {
				if (null != stmt) {
					stmt.close();
				}
				if (null != conn) {
					conn.close();
				}
			} catch (SQLException e) {
				LoggerUtil.logServerErr(e.getMessage());
			}
		}

		return jsonObject.toString();
	}

	/**
	 * 注册用户
	 * 
	 * @param msg
	 *            {
	 *            'phoneNum':'ywang','password':'e10adc3949ba59abbe56e057f20f883
	 *            e ' , 'deviceToken':'8a2597aa1d37d432a88a446d82b6561e',
	 *            'lng':'117.157954','lat':'31.873432','osVersion':'8.0',
	 *            'systemType':'iOS','phoneModel':'iPhone 5s','key':''}
	 * 
	 * @return
	 */
	private static String regNewUser(String msg) {
		JSONObject jsonObject = AloneUtil.newRetJsonObject();
		JSONObject user = JSON.parseObject(msg);
		DruidPooledConnection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = DataSourceFactory.getInstance().getConn();

			conn.setAutoCommit(false);
			stmt = conn
					.prepareStatement("select PHONE_NUM, USER_ID from userbase where PHONE_NUM = ?");
			stmt.setString(1, user.getString("phoneNum"));

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				jsonObject.put("ret", 1);
				jsonObject.put("errCode", Constant.ErrorCode.USER_EXISTS);
				jsonObject.put("errDesc", Constant.ErrorDesc.USER_EXISTS);
				JSONObject userId = new JSONObject();
				userId.put("userId", rs.getInt("USER_ID"));
				jsonObject.put("data", userId);
			} else {
				PreparedStatement updatestmt = conn
						.prepareStatement(
								"insert into userbase (PHONE_NUM, PWD, REG_TIME, LNG, LAT, DEVICE_TOKEN, SYSTEM_TYPE, OS_VERSION,PHONE_MODEL, PKEY) VALUES (?,?,?, ?,?,?,?,?,?,?)",
								Statement.RETURN_GENERATED_KEYS);
				updatestmt.setString(1, user.getString("phoneNum").trim());
				updatestmt.setString(2, user.getString("password").trim());
				updatestmt.setDate(3,
						new java.sql.Date(System.currentTimeMillis()));
				updatestmt.setString(4, user.getString("lng").trim());
				updatestmt.setString(5, user.getString("lat").trim());

				updatestmt.setString(6, user.getString("deviceToken").trim());
				updatestmt.setString(7, user.getString("systemType").trim());
				updatestmt.setString(8, user.getString("osVersion").trim());
				updatestmt.setString(9, user.getString("phoneModel").trim());
				updatestmt.setString(10, user.getString("key").trim());

				int result = updatestmt.executeUpdate();

				conn.commit();
				if (result == 1) {

					ResultSet idRS = updatestmt.getGeneratedKeys();
					if (idRS.next()) {
						JSONObject userId = new JSONObject();
						userId.put("userId", idRS.getInt(1));
						jsonObject.put("data", userId);
					}
					idRS.close();
					jsonObject.put("ret", 0);
				} else {
					jsonObject.put("ret", 2);
				}
				updatestmt.close();
			}

			rs.close();

			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			LoggerUtil.logServerErr(e);
			jsonObject.put("ret", 2);
		} finally {
			try {
				if (null != stmt) {
					stmt.close();
				}
				if (null != conn) {
					conn.close();
				}
			} catch (SQLException e) {
				LoggerUtil.logServerErr(e.getMessage());
			}
		}

		return jsonObject.toString();
	}
}
