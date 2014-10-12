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
package com.ywang.utils;

import org.apache.log4j.Logger;

/**
 * song
 *
 * @author apple
 *
 * @since 1.0
 */
public class LoggerUtil {

	
	public static void logMsg(String msg) {
		Logger.getLogger("server.messages").info(msg);
	}

	public static void logServerErr(String msg) {
		Logger.getLogger("server.error").error(msg);
	}

	public static void logServerErr(Throwable t) {

		Logger.getLogger("server.error").error(t.getMessage(), t);
	}

}
