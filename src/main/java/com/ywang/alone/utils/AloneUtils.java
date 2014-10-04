package com.ywang.alone.utils;

import com.alibaba.fastjson.JSONObject;
import com.ywang.alone.modeles.LoginUser;

public class AloneUtils {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * 注册
	 */
	public static final String REG = "A001";
	/**
	 * 登陆
	 */
	public static final String LOGIN = "A002";
	

	/**
	 * 用户请求统一处理入口
	 * 
	 * @param actionString
	 *            命令号
	 * @param paramString
	 *            参数
	 * @return 处理结果
	 */
	public static String execute(String actionString, String paramString) {

		String result = null;
		switch (actionString) {
		case REG:
			break;
		case LOGIN:
			LoginUser loginUser = JSONObject.parseObject(paramString,
					LoginUser.class);
			// jdbc 获取 附近的人列表

			result = JSONObject.toJSONString(loginUser);
			break;
		
		default:
			break;
		}

		return result;
	}

}
