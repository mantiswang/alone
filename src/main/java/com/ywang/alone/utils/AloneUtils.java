package com.ywang.alone.utils;

import com.alibaba.fastjson.JSONObject;
import com.ywang.alone.modeles.LoginUser;

public class AloneUtils {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static final String LOGIN = "A001";
	
	public static String execute(String actionString, String paramString) {
		
		String result = null;
		switch (actionString) {
		case LOGIN:
			LoginUser loginUser = JSONObject.parseObject(paramString, LoginUser.class);
			result = JSONObject.toJSONString(loginUser);
			break;

		default:
			break;
		}
 

		return result;
	}

	
}
