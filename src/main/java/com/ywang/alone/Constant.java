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
		int USER_EXISTS = 9001;
		int LOGIN_FAIL = 9002;
		int TOKEN_UPDATE_FAIL = 9003;
	}
	
	public interface ErrorDesc
	{
		String USER_EXISTS = "用户已经存在";
		String LOGIN_FAIL = "用户不存在或者密码错误";
		String TOKEN_UPDATE_FAIL = "更新token失败";
	}
}
