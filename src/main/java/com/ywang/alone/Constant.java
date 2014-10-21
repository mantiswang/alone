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
package com.ywang.alone;

/*
 * alone
 *
 * @author apple
 *
 * @since 1.0
 */
public class Constant {

	public interface ErrorCode
	{
		int SYS_ERR     = 1001;
		int USER_EXISTS = 9001;
		int LOGIN_FAIL = 9002;
		int TOKEN_UPDATE_FAIL = 9003;
		int NO_ACCESS_AUTH = 9004;
		int PARAM_ILLEGAL  = 9008;
		int UPDATE_DB_FAIL = 9006;
		
		
	}
	
	public interface ErrorDesc
	{
		String SYS_ERR     = "系统异常";
		String USER_EXISTS = "用户已经存在";
		String LOGIN_FAIL = "用户不存在或者密码错误";
		String TOKEN_UPDATE_FAIL = "更新token失败";
		String NO_ACCESS_AUTH = "请先登录";
		String PARAM_ILLEGAL = "参数错误！";
		String UPDATE_DB_FAIL = "更新数据失败！";
	}
}
