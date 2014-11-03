package com.ywang.alone.handler;

import com.ywang.alone.handler.task.AuthTask;
import com.ywang.utils.LoggerUtil;

public class AloneHandler {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/*
	 * 注册 
	 */
	private static final String AUTH = "a";
	
	
	public static String handler(String message) {

		String result = "";
		String category;
		String param;
		String command;
		int code;
				
		String msg = message.trim();
		category = msg.substring(0, 1).toLowerCase();
		command = msg.substring(0, 3);
		
		code = Integer.valueOf(msg.substring(1, 3));
        param = msg.substring(3);
		

	    switch (category) {
		case AUTH:            
            result = AuthTask.execute(code, param);
			break;
			
		default:
			break;
		}
	    LoggerUtil.logMsg("command is "+ command + " ret is "+ result);
		return result;
	}

}



//
///**
// * 注册
// */
//public static final String REG = "A001";
///**
// * 登陆
// */
//public static final String LOGIN = "A002";
//
//
///**
// * 用户请求统一处理入口
// * 
// * @param actionString
// *            命令号
// * @param paramString
// *            参数
// * @return 处理结果
// */
//public static String execute(String actionString, String paramString) {
//
//	String result = null;
//	switch (actionString) {
//	case REG:
//		break;
//	case LOGIN:
//		LoginUser loginUser = JSONObject.parseObject(paramString,
//				LoginUser.class);
//		// jdbc 获取 附近的人列表
//
//		result = JSONObject.toJSONString(loginUser);
//		break;
//	
//	default:
//		break;
//	}
//
//	return result;
//}
