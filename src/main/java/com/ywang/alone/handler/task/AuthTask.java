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
import java.util.ArrayList;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import redis.clients.jedis.Jedis;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ywang.alone.Constant;
import com.ywang.alone.db.DataSourceFactory;
import com.ywang.alone.modeles.UserInfo;
import com.ywang.alone.utils.AloneUtil;
import com.ywang.utils.JedisUtil;
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
	private static final int FOLLOW = 4;
	private static final int GET_ALL_FOLLOWS = 5;
	private static final int GET_USERINFO = 6;
	/**
	 * 
	 * @param code
	 * @param param
	 * @return ret 1
	 */
	public static String execute(int code, String param) {

		switch (code) {
		// case REG_NEW_USER:
		// return regNewUser(param);
		case LOGIN:
			return login(param);
		case NEARBY:
			return nearby(param);
		case FOLLOW:
			return follow(param);
		case GET_ALL_FOLLOWS:
			return getAllFollows(param);
		case GET_USERINFO:
			return getUserInfo(param);
		default:
			break;
		}
		return null;
	}

	/**
	 * 关注 { 'key':'2597aa1d37d432a','uid':'1020293' }
	 * 
	 * @param param
	 * @return
	 */
	private static String getUserInfo(String msg) {
		
		JSONObject jsonObject = AloneUtil.newRetJsonObject();
		JSONObject param = JSON.parseObject(msg);
		String token = param.getString("key");
		String userId = null;
		
		if (StringUtils.isEmpty(token)) {
			jsonObject.put("ret", Constant.RET.NO_ACCESS_AUTH);
			jsonObject.put("errCode", Constant.ErrorCode.NO_ACCESS_AUTH);
			jsonObject.put("errDesc", Constant.ErrorDesc.NO_ACCESS_AUTH);
			return jsonObject.toJSONString();
		}

		Jedis jedis = JedisUtil.getJedis();
		Long tokenTtl = jedis.ttl("TOKEN:" + token);
		if(tokenTtl == -1)
		{
			jsonObject.put("ret",  Constant.RET.NO_ACCESS_AUTH);
			jsonObject.put("errCode", Constant.ErrorCode.NO_ACCESS_AUTH);
			jsonObject.put("errDesc", Constant.ErrorDesc.NO_ACCESS_AUTH);
		}else
		{
			userId = jedis.get("TOKEN:" + token);
			LoggerUtil.logMsg("uid is " + userId);
		}
		
		JedisUtil.returnJedis(jedis);
		
		if (StringUtils.isEmpty(userId)) {
			jsonObject.put("ret",  Constant.RET.NO_ACCESS_AUTH);
			jsonObject.put("errCode", Constant.ErrorCode.NO_ACCESS_AUTH);
			jsonObject.put("errDesc", Constant.ErrorDesc.NO_ACCESS_AUTH);
			return jsonObject.toJSONString();
		}
		
		
		String aimUid = param.getString("uid");//如果没有uid，即为自己的个人信息
		if (StringUtils.isEmpty(aimUid)) {
			aimUid = userId;
		}
		DruidPooledConnection conn = null;
		PreparedStatement stmt = null;
		JSONObject data = new JSONObject();
		try {
			conn = DataSourceFactory.getInstance().getConn();

			conn.setAutoCommit(false);

			stmt = conn
					.prepareStatement(
							"SELECT * FROM USERBASE WHERE USER_ID = ?",
							Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, aimUid);

			ResultSet userInfoRs = stmt.executeQuery();
			if(userInfoRs.next())
			{
				UserInfo userInfo = new UserInfo();
				userInfo.setRegTime(userInfoRs.getLong("REG_TIME"));
				userInfo.setUserId(userInfoRs.getString("USER_ID"));
				userInfo.setAvatar(userInfoRs.getString("AVATAR"));
				userInfo.setNickName(userInfoRs.getString("NICKNAME"));
				userInfo.setAge(userInfoRs.getString("AGE"));
				userInfo.setHoroscope(userInfoRs.getString("HORO_SCOPE"));
				userInfo.setHeight(userInfoRs.getString("HEIGHT"));
				userInfo.setWeight(userInfoRs.getString("WEIGHT"));
				userInfo.setRoleName(userInfoRs.getString("ROLENAME"));
				userInfo.setAffection(userInfoRs.getString("AFFECTION"));
				userInfo.setPurpose(userInfoRs.getString("PURPOSE"));
				userInfo.setEthnicity(userInfoRs.getString("ETHNICITY"));
				userInfo.setOccupation(userInfoRs.getString("OCCUPATION"));
				userInfo.setLivecity(userInfoRs.getString("LIVECITY"));
				userInfo.setLocation(userInfoRs.getString("LOCATION"));
				userInfo.setTravelcity(userInfoRs.getString("TRAVELCITY"));
				userInfo.setMovie(userInfoRs.getString("MOVIE"));
				userInfo.setMusic(userInfoRs.getString("MUSIC"));
				userInfo.setBooks(userInfoRs.getString("BOOKS"));
				userInfo.setFood(userInfoRs.getString("FOOD"));
				userInfo.setOthers(userInfoRs.getString("OTHERS"));
				userInfo.setIntro(userInfoRs.getString("INTRO"));
				userInfo.setLastLoginTime(userInfoRs.getLong("LAST_LOGIN_TIME"));
//				userInfo.setMessagePwd(userInfoRs
//						.getString("MESSAGE_PWD"));
				userInfo.setMessageUser(userInfoRs.getString("MESSAGE_USER"));
//			
				userInfo.setOnline("1");

				data.put("userInfo", JSONObject.toJSON(userInfo));
				
				PreparedStatement pstmt = conn.prepareStatement(
						"SELECT * FROM uploads WHERE USER_ID = ? and ENABLING=1",
						Statement.RETURN_GENERATED_KEYS);
				pstmt.setString(1, aimUid);	
				
				ResultSet pSet = pstmt.executeQuery();
				JSONArray jsonArray = new JSONArray();
				while(pSet.next())
				{
					jsonArray.add(pSet.getString("PHOTO_PATH"));
				}
				data.put("photos", JSONObject.toJSON(jsonArray));
				jsonObject.put("data", JSONObject.toJSON(data));
				
				pSet.close();
				pstmt.close();
				
			}
			else
			{
				jsonObject.put("ret", Constant.RET.SYS_ERR);
				jsonObject.put("errCode", Constant.ErrorCode.SYS_ERR);
				jsonObject.put("errDesc", Constant.ErrorDesc.SYS_ERR);
			}
			
			userInfoRs.close();
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			LoggerUtil.logServerErr(e);
			jsonObject.put("ret", Constant.RET.SYS_ERR);
			jsonObject.put("errCode", Constant.ErrorCode.SYS_ERR);
			jsonObject.put("errDesc", Constant.ErrorDesc.SYS_ERR);
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
	 * 关注 { 'key':'2597aa1d37d432a','fid':'1020293' }
	 * 
	 * @param param
	 * @return
	 */
	private static String follow(String msg) {
		JSONObject jsonObject = AloneUtil.newRetJsonObject();
		JSONObject param = JSON.parseObject(msg);
		String token = param.getString("key");
		String userId = null;
		
		if (StringUtils.isEmpty(token)) {
			jsonObject.put("ret",  Constant.RET.NO_ACCESS_AUTH);
			jsonObject.put("errCode", Constant.ErrorCode.NO_ACCESS_AUTH);
			jsonObject.put("errDesc", Constant.ErrorDesc.NO_ACCESS_AUTH);
			return jsonObject.toJSONString();
		}

		Jedis jedis = JedisUtil.getJedis();
		Long tokenTtl = jedis.ttl("TOKEN:" + token);
		if(tokenTtl == -1)
		{
			jsonObject.put("ret", Constant.RET.NO_ACCESS_AUTH);
			jsonObject.put("errCode", Constant.ErrorCode.NO_ACCESS_AUTH);
			jsonObject.put("errDesc", Constant.ErrorDesc.NO_ACCESS_AUTH);
		}else
		{
			userId = jedis.get("TOKEN:" + token);
			LoggerUtil.logMsg("uid is " + userId);
		}
		
		JedisUtil.returnJedis(jedis);
		
		if (StringUtils.isEmpty(userId)) {
			jsonObject.put("ret", Constant.RET.NO_ACCESS_AUTH);
			jsonObject.put("errCode", Constant.ErrorCode.NO_ACCESS_AUTH);
			jsonObject.put("errDesc", Constant.ErrorDesc.NO_ACCESS_AUTH);
			return jsonObject.toJSONString();
		}
		
		DruidPooledConnection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = DataSourceFactory.getInstance().getConn();

			conn.setAutoCommit(false);

			stmt = conn
					.prepareStatement(
							"insert into follow (USER_ID, FOLLOWED_ID, `TIME`) VALUES (?,?,?)",
							Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, userId);
			stmt.setString(2, param.getString("fid").trim());
			stmt.setLong(3, System.currentTimeMillis());

			int result = stmt.executeUpdate();
			if (result != 1) {
				
				jsonObject.put("ret", Constant.RET.UPDATE_DB_FAIL);
				jsonObject.put("errCode", Constant.ErrorCode.UPDATE_DB_FAIL);
				jsonObject.put("errDesc", Constant.ErrorDesc.UPDATE_DB_FAIL);
			}

			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			LoggerUtil.logServerErr(e);
			jsonObject.put("ret", Constant.RET.SYS_ERR);
			jsonObject.put("errCode", Constant.ErrorCode.SYS_ERR);
			jsonObject.put("errDesc", Constant.ErrorDesc.SYS_ERR);
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
	 * 获取所有关注的人 { 'key':'2597aa1d37d432a', 'pNo':'0' }
	 * 
	 * @param param
	 * @return
	 */
	private static String getAllFollows(String msg) {
		JSONObject jsonObject = AloneUtil.newRetJsonObject();
		JSONObject param = JSON.parseObject(msg);
		String token = param.getString("key");
		String userId = null;

		if (StringUtils.isEmpty(token)) {
			jsonObject.put("ret",  Constant.RET.NO_ACCESS_AUTH);
			jsonObject.put("errCode", Constant.ErrorCode.NO_ACCESS_AUTH);
			jsonObject.put("errDesc", Constant.ErrorDesc.NO_ACCESS_AUTH);
		} else {
			Jedis jedis = JedisUtil.getJedis();
			Long tokenTtl = jedis.ttl("TOKEN:" + token);
			if(tokenTtl == -1)
			{
				jsonObject.put("ret", Constant.RET.NO_ACCESS_AUTH);
				jsonObject.put("errCode", Constant.ErrorCode.NO_ACCESS_AUTH);
				jsonObject.put("errDesc", Constant.ErrorDesc.NO_ACCESS_AUTH);
				
			}else
			{
				userId = jedis.get("TOKEN:" + token);
				if (StringUtils.isEmpty(userId)) {
					jsonObject.put("ret", Constant.RET.NO_ACCESS_AUTH);
					jsonObject.put("errCode", Constant.ErrorCode.NO_ACCESS_AUTH);
					jsonObject.put("errDesc", Constant.ErrorDesc.NO_ACCESS_AUTH);
	
				}
			}
			JedisUtil.returnJedis(jedis);
		}

		if (StringUtils.isEmpty(userId)) {
			return jsonObject.toJSONString();
		}

		LoggerUtil.logMsg("uid is " + userId);

		DruidPooledConnection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = DataSourceFactory.getInstance().getConn();
			conn.setAutoCommit(false);

			stmt = conn
					.prepareStatement("SELECT * FROM USERBASE WHERE USER_ID IN (SELECT FOLLOWED_ID FROM FOLLOW WHERE USER_ID = ?)");
			stmt.setString(1, userId);

			ArrayList<UserInfo> followedList = new ArrayList<UserInfo>();
			UserInfo userInfo = null;
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {

				userInfo = new UserInfo();
				userInfo.setUserId(rs.getString("USER_ID"));
				userInfo.setAvatar(rs.getString("AVATAR"));
				userInfo.setNickName(rs.getString("NICKNAME"));
				userInfo.setAge(rs.getString("AGE"));
				userInfo.setRoleName(rs.getString("ROLENAME"));
				userInfo.setOnline(rs.getString("ONLINE"));
				userInfo.setLastLoginTime(rs.getLong("LAST_LOGIN_TIME"));
				userInfo.setIntro(rs.getString("INTRO"));
				userInfo.setDistance(rs.getString("DISTANCE"));

				followedList.add(userInfo);
			}

			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			LoggerUtil.logServerErr(e);
			jsonObject.put("ret", Constant.RET.SYS_ERR);
			jsonObject.put("errCode", Constant.ErrorCode.SYS_ERR);
			jsonObject.put("errDesc", Constant.ErrorDesc.SYS_ERR);
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
		String userId = null;
		if (StringUtils.isEmpty(token)) {
			jsonObject.put("ret", Constant.RET.NO_ACCESS_AUTH);
			jsonObject.put("errCode", Constant.ErrorCode.NO_ACCESS_AUTH);
			jsonObject.put("errDesc", Constant.ErrorDesc.NO_ACCESS_AUTH);
			return jsonObject.toJSONString();
		}
		

		Jedis jedis = JedisUtil.getJedis();
		Long tokenTtl = jedis.ttl("TOKEN:" + token);
		if(tokenTtl == -1)
		{
			jsonObject.put("ret",  Constant.RET.NO_ACCESS_AUTH);
			jsonObject.put("errCode", Constant.ErrorCode.NO_ACCESS_AUTH);
			jsonObject.put("errDesc", Constant.ErrorDesc.NO_ACCESS_AUTH);
		}else
		{
			userId = jedis.get("TOKEN:" + token);
			LoggerUtil.logMsg("uid is " + userId);
		}
		
		JedisUtil.returnJedis(jedis);
		
		if (StringUtils.isEmpty(userId)) {
			jsonObject.put("ret",  Constant.RET.NO_ACCESS_AUTH);
			jsonObject.put("errCode", Constant.ErrorCode.NO_ACCESS_AUTH);
			jsonObject.put("errDesc", Constant.ErrorDesc.NO_ACCESS_AUTH);
			return jsonObject.toJSONString();
		}
		
		

		DruidPooledConnection conn = null;
		PreparedStatement stmt = null;
		try {

			conn = DataSourceFactory.getInstance().getConn();
			conn.setAutoCommit(false);

			// TODO 把需要的column 列出来
			stmt = conn
					.prepareStatement("SELECT USER_ID,AVATAR,NICKNAME,AGE,ROLENAME,`ONLINE`,LAST_LOGIN_TIME,INTRO, MESSAGE_USER,"
							+ "ROUND(6378.138 * 2 * ASIN(SQRT(POW(SIN((? * PI()/180-lat * PI()/180)/2),2) +"
							+ " COS(? * PI()/180) * COS(lat * PI()/180) * POW(SIN((? * PI()/180-lng * PI()/180)/2),2))) * 1000) "
							+ " AS  DISTANCE "
							+ "FROM userbase WHERE USER_ID <> ? ORDER BY distance LIMIT ?,?");
			stmt.setString(1, user.getString("lat"));
			stmt.setString(2, user.getString("lat"));
			stmt.setString(3, user.getString("lng"));
			stmt.setString(4, userId);
			stmt.setInt(5, Integer.valueOf(user.getString("currPage")));
			stmt.setInt(6, Integer.valueOf(user.getString("pageSize")));

			JSONArray nearbyUserArray = new JSONArray();
			UserInfo userInfo = null;
			ResultSet nearbyRs = stmt.executeQuery();
			while (nearbyRs.next()) {

				userInfo = new UserInfo();
				userInfo.setUserId(nearbyRs.getString("USER_ID"));
				userInfo.setAvatar(nearbyRs.getString("AVATAR"));
				userInfo.setNickName(nearbyRs.getString("NICKNAME"));
				userInfo.setAge(nearbyRs.getString("AGE"));
				userInfo.setRoleName(nearbyRs.getString("ROLENAME"));
				userInfo.setOnline(nearbyRs.getString("ONLINE"));
				userInfo.setLastLoginTime(nearbyRs.getLong("LAST_LOGIN_TIME"));
				userInfo.setIntro(nearbyRs.getString("INTRO"));
				userInfo.setDistance(nearbyRs.getString("DISTANCE"));
				userInfo.setMessageUser(nearbyRs.getString("MESSAGE_USER"));
				nearbyUserArray.add(userInfo);
			}
			jsonObject.put("data", nearbyUserArray);
			nearbyRs.close();

			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			LoggerUtil.logServerErr(e);
			jsonObject.put("ret", Constant.RET.SYS_ERR);
			jsonObject.put("errCode", Constant.ErrorCode.SYS_ERR);
			jsonObject.put("errDesc", Constant.ErrorCode.SYS_ERR);
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
	 *            'deviceToken':'8a2597aa1d37d432a88a446d82b6561e','osVersion':'
	 *            8 . 0 ' , 'systemType':'iOS','phoneModel':'iPhone 5s'}
	 * 
	 *            OR
	 * 
	 *            {'key':'2597aa1d37d432a','lng':'117.157954','lat':'31.873432',
	 *            'deviceToken':'8a2597aa1d37d432a88a446d82b6561e','osVersion':'
	 *            8 . 0 ' , 'systemType':'iOS','phoneModel':'iPhone 5s'}
	 * 
	 * @return {"data":{"key":"e1e10c1ba704d1f10acda309138a2153","messagePwd":
	 *         "alone123456"
	 *         ,"messageUser":"15256551134324","online":"1","regTime"
	 *         :1415177594592,"userId":"23"},"errCode":0,"errDesc":"","ret":5}
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
			String userId = null;
			boolean needUpdateToken = false;

			if (StringUtils.isEmpty(token)) {// 使用用户名密码登录
				needUpdateToken = true;
				String uuid = UUID.randomUUID().toString();
				uuid = uuid.replaceAll("-", "");
				token = MD5.getMD5String(uuid);

				stmt = conn
						.prepareStatement("SELECT * FROM userbase WHERE PHONE_NUM = ? AND PWD = ? ");
				stmt.setString(1, user.getString("phoneNum"));
				stmt.setString(2, user.getString("password"));

			} else {// 使用 token 登录
				Jedis jedis = JedisUtil.getJedis();
				Long tokenTtl = jedis.ttl("TOKEN:" + token);
				if(tokenTtl == -1)
				{
					jsonObject.put("ret",  Constant.RET.NO_ACCESS_AUTH);
					jsonObject.put("errCode", Constant.ErrorCode.NO_ACCESS_AUTH);
					jsonObject.put("errDesc", Constant.ErrorDesc.NO_ACCESS_AUTH);
				}else
				{
					userId = jedis.get("TOKEN:" + token);
					LoggerUtil.logMsg("uid is " + userId + "");
				}
				
				JedisUtil.returnJedis(jedis);
				
				if(StringUtils.isEmpty(userId + ""))
				{
					return jsonObject.toJSONString();
				}
				stmt = conn
						.prepareStatement("SELECT * FROM userbase WHERE u.user_id = ?");
				stmt.setString(1, userId + "");
			}

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) { // 登录
				UserInfo userInfo = new UserInfo();
				userId = rs.getString("USER_ID");
				userInfo.setRegTime(rs.getLong("REG_TIME"));
				userInfo.setUserId(userId + "");
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
				userInfo.setOnline("1");

				jsonObject.put("data", JSONObject.toJSON(userInfo));

				// 更新token 最后登录时间
				PreparedStatement updatestmt = null;
				if (needUpdateToken) {
					updatestmt = conn
							.prepareStatement("update userbase set PKEY = ?, LAST_LOGIN_TIME = ? , OS_VERSION=? , SYSTEM_TYPE=? , PHONE_MODEL=? , DEVICE_TOKEN=? , LNG=? , LAT=? , ONLINE='1' WHERE USER_ID=?");
					updatestmt.setString(1, token);
					updatestmt.setLong(2, System.currentTimeMillis());
					updatestmt.setString(3, user.getString("osVersion"));
					updatestmt.setString(4, user.getString("systemType"));
					updatestmt.setString(5, user.getString("phoneModel"));
					updatestmt.setString(6, user.getString("deviceToken"));
					updatestmt.setString(7, user.getString("lng"));
					updatestmt.setString(8, user.getString("lat"));
					updatestmt.setString(9, userInfo.getUserId());
				} else {
					updatestmt = conn
							.prepareStatement("update userbase set LAST_LOGIN_TIME = ? , LNG=? , LAT=? , OS_VERSION=? , SYSTEM_TYPE=? , PHONE_MODEL=? , DEVICE_TOKEN= ? , ONLINE='1'  WHERE USER_ID=?");
					updatestmt.setLong(1, System.currentTimeMillis());
					updatestmt.setString(2, user.getString("lng"));
					updatestmt.setString(3, user.getString("lat"));
					updatestmt.setString(4, user.getString("osVersion"));
					updatestmt.setString(5, user.getString("systemType"));
					updatestmt.setString(6, user.getString("phoneModel"));
					updatestmt.setString(7, user.getString("deviceToken"));
					updatestmt.setString(8, userInfo.getUserId());
				}
				int result = updatestmt.executeUpdate();

				if (result != 1) {
					jsonObject.put("ret", Constant.RET.TOKEN_UPDATE_FAIL);
					jsonObject.put("errCode",
							Constant.ErrorCode.TOKEN_UPDATE_FAIL);
					jsonObject.put("errDesc",
							Constant.ErrorDesc.TOKEN_UPDATE_FAIL);
				}
				updatestmt.close();
			}
			// 没有用户，直接注册
			else {
				jsonObject = regNewUser(msg);
				JSONObject data = jsonObject.getJSONObject("data");
				userId = data.getString("userId");
				token = data.getString("key");
				LoggerUtil.logMsg("register succ userId is: " + userId + " token is "+ token);
			}

			rs.close();
			conn.commit();
			conn.setAutoCommit(true);
		
			
			//更新redis
			Jedis jedis = JedisUtil.getJedis();
			//两周失效
			jedis.setex("TOKEN:" + token, 86400 * 14, userId + "");
			JedisUtil.returnJedis(jedis);
			
			
			
		} catch (SQLException e) {
			LoggerUtil.logServerErr(e);
			jsonObject.put("ret", Constant.RET.SYS_ERR);
			jsonObject.put("errCode", Constant.ErrorCode.SYS_ERR);
			jsonObject.put("errDesc", Constant.ErrorCode.SYS_ERR);
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
	private static JSONObject regNewUser(String msg) {
		JSONObject jsonObject = AloneUtil.newRetJsonObject();
		JSONObject user = JSON.parseObject(msg);
		DruidPooledConnection conn = null;
		PreparedStatement updatestmt = null;
		try {
			conn = DataSourceFactory.getInstance().getConn();
			conn.setAutoCommit(false);
			String uuid = UUID.randomUUID().toString();
			uuid = uuid.replaceAll("-", "");
			String token = MD5.getMD5String(uuid);

			String im_user = user.getString("phoneNum").trim();
			UserInfo userInfo = new UserInfo();
			userInfo.setRegTime(System.currentTimeMillis());
			userInfo.setOnline("1");
			userInfo.setKey(token);
			userInfo.setMessageUser(im_user);
			userInfo.setMessagePwd("alone123456");
			
			updatestmt = conn
					.prepareStatement(
							"insert into userbase (PHONE_NUM, PWD, REG_TIME, LNG, LAT, DEVICE_TOKEN, SYSTEM_TYPE, OS_VERSION,PHONE_MODEL, PKEY, MESSAGE_USER, MESSAGE_PWD) VALUES (?,?,?, ?,?,?,?,?,?,?,?,?)",
							Statement.RETURN_GENERATED_KEYS);
			updatestmt.setString(1, user.getString("phoneNum").trim());
			updatestmt.setString(2, user.getString("password").trim());
			updatestmt.setLong(3, userInfo.getRegTime());
			updatestmt.setString(4, user.getString("lng").trim());
			updatestmt.setString(5, user.getString("lat").trim());

			updatestmt.setString(6, user.getString("deviceToken").trim());
			updatestmt.setString(7, user.getString("systemType").trim());
			updatestmt.setString(8, user.getString("osVersion").trim());
			updatestmt.setString(9, user.getString("phoneModel").trim());
			updatestmt.setString(10, userInfo.getKey());
			updatestmt.setString(11, userInfo.getMessageUser());
			updatestmt.setString(12, "alone123456");

			int result = updatestmt.executeUpdate();

			if (result == 1) {
				ResultSet idRS = updatestmt.getGeneratedKeys();
				if (idRS.next()) {
					int userId = idRS.getInt(1);
					userInfo.setUserId(userId+"");
				}
				jsonObject.put("ret", Constant.RET.REG_SUCC);
				jsonObject.put("data", userInfo);
			} else {
				jsonObject.put("ret", Constant.RET.SYS_ERR);
				jsonObject.put("errCode", Constant.ErrorCode.SYS_ERR);
				jsonObject.put("errDesc", Constant.ErrorCode.SYS_ERR);
				LoggerUtil.logServerErr("insert into userbase no result");
			}

			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			LoggerUtil.logServerErr(e);
			jsonObject.put("ret", Constant.RET.SYS_ERR);
			jsonObject.put("errCode", Constant.ErrorCode.SYS_ERR);
			jsonObject.put("errDesc", Constant.ErrorCode.SYS_ERR);
		} finally {
			try {
				if (null != updatestmt) {
					updatestmt.close();
				}
				if (null != conn) {
					conn.close();
				}
			} catch (SQLException e) {
				LoggerUtil.logServerErr(e.getMessage());
			}
		}

		return jsonObject;
	}

	public static void main(String[] args) {

		/*
		 * 测试注册
		 * 
		 * 
		 * ArrayList<String> regInfos = new ArrayList<String>(); regInfos.add(
		 * "{'phoneNum':'15256551134','password':'e10adc3949ba59abbe56e057f20f883e','deviceToken':'8a2597aa1d37d432a88a446d82b6561e','lng':'117.057954','lat':'31.173432','osVersion':'8.0','systemType':'iOS','phoneModel':'iPhone 5s','key':''}"
		 * ); regInfos.add(
		 * "{'phoneNum':'18018956659','password':'e10adc3949ba59abbe56e057f20f883e','deviceToken':'8a2597aa1d37d432a88a446d82b6561e','lng':'117.557954','lat':'31.103432','osVersion':'8.0','systemType':'iOS','phoneModel':'iPhone 4s','key':''}"
		 * ); regInfos.add(
		 * "{'phoneNum':'18018256907','password':'e10adc3949ba59abbe56e057f20f883e','deviceToken':'8a2597aa1d37d432a88a446d82b6561e','lng':'117.668041','lat':'32.068562','osVersion':'7.0.2','systemType':'iOS','phoneModel':'iPhone 4','key':''}"
		 * ); regInfos.add(
		 * "{'phoneNum':'13856796230','password':'e10adc3949ba59abbe56e057f20f883e','deviceToken':'8a2597aa1d37d432a88a446d82b6561e','lng':'116.812721','lat':'33.303466','osVersion':'8.0','systemType':'iOS','phoneModel':'iPhone 5s','key':''}"
		 * ); regInfos.add(
		 * "{'phoneNum':'15256554567','password':'e10adc3949ba59abbe56e057f20f883e','deviceToken':'8a2597aa1d37d432a88a446d82b6561e','lng':'117.454687','lat':'33.816950','osVersion':'8.0','systemType':'iOS','phoneModel':'iPhone 5s','key':''}"
		 * ); regInfos.add(
		 * "{'phoneNum':'15967565765','password':'e10adc3949ba59abbe56e057f20f883e','deviceToken':'8a2597aa1d37d432a88a446d82b6561e','lng':'117.000954','lat':'31.173432','osVersion':'8.0','systemType':'iOS','phoneModel':'iPhone 5s','key':''}"
		 * ); regInfos.add(
		 * "{'phoneNum':'15973485765','password':'e10adc3949ba59abbe56e057f20f883e','deviceToken':'8a2597aa1d37d432a88a446d82b6561e','lng':'117.000954','lat':'31.173432','osVersion':'8.0','systemType':'iOS','phoneModel':'iPhone 5s','key':''}"
		 * ); regInfos.add(
		 * "{'phoneNum':'15945678798','password':'e10adc3949ba59abbe56e057f20f883e','deviceToken':'8a2597aa1d37d432a88a446d82b6561e','lng':'117.000954','lat':'31.173432','osVersion':'8.0','systemType':'iOS','phoneModel':'iPhone 5s','key':''}"
		 * ); regInfos.add(
		 * "{'phoneNum':'18783675836','password':'e10adc3949ba59abbe56e057f20f883e','deviceToken':'8a2597aa1d37d432a88a446d82b6561e','lng':'117.123954','lat':'31.173432','osVersion':'8.0','systemType':'iOS','phoneModel':'iPhone 5s','key':''}"
		 * ); regInfos.add(
		 * "{'phoneNum':'18753764584','password':'e10adc3949ba59abbe56e057f20f883e','deviceToken':'8a2597aa1d37d432a88a446d82b6561e','lng':'117.102954','lat':'31.173432','osVersion':'8.0','systemType':'iOS','phoneModel':'iPhone 5s','key':''}"
		 * ); regInfos.add(
		 * "{'phoneNum':'18746146342','password':'e10adc3949ba59abbe56e057f20f883e','deviceToken':'8a2597aa1d37d432a88a446d82b6561e','lng':'117.030954','lat':'31.173432','osVersion':'8.0','systemType':'iOS','phoneModel':'iPhone 5s','key':''}"
		 * ); regInfos.add(
		 * "{'phoneNum':'18957265846','password':'e10adc3949ba59abbe56e057f20f883e','deviceToken':'8a2597aa1d37d432a88a446d82b6561e','lng':'117.085954','lat':'31.173432','osVersion':'8.0','systemType':'iOS','phoneModel':'iPhone 5s','key':''}"
		 * ); regInfos.add(
		 * "{'phoneNum':'18076236598','password':'e10adc3949ba59abbe56e057f20f883e','deviceToken':'8a2597aa1d37d432a88a446d82b6561e','lng':'117.573954','lat':'31.173432','osVersion':'8.0','systemType':'iOS','phoneModel':'iPhone 5s','key':''}"
		 * ); for (String string : regInfos) {
		 * System.out.println(AuthTask.execute(REG_NEW_USER, string)); }
		 */
		// System.out.println(AuthTask.execute(REG_NEW_USER,
		// "{'phoneNum':'15967565765','password':'e10adc3949ba59abbe56e057f20f883e','deviceToken':'8a2597aa1d37d432a88a446d82b6561e','lng':'117.000954','lat':'31.173432','osVersion':'8.0','systemType':'iOS','phoneModel':'iPhone 5s','key':''}"));
		/*
		 * 测试登录 // String loginInfoString =
		 * "{'phoneNum':'15256551134','password':'e10adc3949ba59abbe56e057f20f883e','lng':'117.157954','lat':'31.873432','deviceToken':'8a2597aa1d37d432a88a446d82b6561e','osVersion':'8.0','systemType':'iOS','phoneModel':'iPhone 5s'}"
		 * ; String loginInfoString =
		 * "{'key':'51a7f79b7528da0cf2fc92e0750148da','lng':'117.157954','lat':'31.873432','deviceToken':'8a2597aa1d37d432a88a446d82b6561e','osVersion':'8.0','systemType':'iOS','phoneModel':'iPhone 5s'}"
		 * ;
		 * 
		 * String loginRet = AuthTask.execute(LOGIN, loginInfoString);
		 * 
		 * System.out.println(loginRet);
		 */

		/* 测试附近 */
		String nearbyString = "{'key':'51a7f79b7528da0cf2fc92e0750148da','lng':'117.157954','lat':'31.873432','currPage':'1','pageSize':'50'}";
		String nearbyRet = AuthTask.execute(NEARBY, nearbyString);
		System.out.println(nearbyRet);

		// String regRet = AuthTask.execute(REG_NEW_USER, regString);
	}

}
