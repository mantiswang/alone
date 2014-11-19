package com.ywang.alone.servlet;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.lang3.StringUtils;

import redis.clients.jedis.Jedis;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.istack.internal.logging.Logger;
import com.ywang.alone.Constant;
import com.ywang.alone.db.DataSourceFactory;
import com.ywang.alone.utils.AloneUtil;
import com.ywang.utils.JedisUtil;
import com.ywang.utils.LoggerUtil;
import com.ywang.utils.MD5;

/**
 * Servlet implementation class UploadAvatarServlet
 */
@WebServlet(name = "UploadAvatarServlet", urlPatterns = "/api/uploadImage")
@MultipartConfig( maxFileSize = 1024 * 1024 * 2)
public class UploadAvatarServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String AVATAR_DIR = "/images/header";
	private static final String PHOTO_DIR = "/images/photo";
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UploadAvatarServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		LoggerUtil.logMsg("UploadAvatarServlet doget");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		JSONObject jsonObject = AloneUtil.newRetJsonObject();
		String message = request.getParameter("msg");
		if (StringUtils.isBlank(message))
		{
			jsonObject.put("errDesc", Constant.ErrorDesc.PARAM_ILLEGAL);
			jsonObject.put("errCode", Constant.ErrorCode.PARAM_ILLEGAL);
			jsonObject.put("ret", Constant.RET.PARAM_ILLEGAL);
			return;
		}
		LoggerUtil.logMsg(message);
		JSONObject param = JSON.parseObject(message);
		String token = param.getString("key");
		int type = param.getIntValue("type");
		String suffix = param.getString("suffix");
		if (StringUtils.isEmpty(token)) {
			jsonObject.put("errDesc", Constant.ErrorDesc.PARAM_ILLEGAL);
			jsonObject.put("errCode", Constant.ErrorCode.PARAM_ILLEGAL);
			jsonObject.put("ret", Constant.RET.PARAM_ILLEGAL);
			return;
		}
		
		String userId = null;
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
		if(StringUtils.isEmpty(userId))
		{
			return;
		}
		
		// gets absolute path of the web application
        String applicationPath = request.getServletContext().getRealPath("");
        // constructs path of the directory to save uploaded file
		LoggerUtil.logMsg(applicationPath);

		DruidPooledConnection conn = null;
		PreparedStatement stmt = null;
		try {
			
			long l_userId = Long.parseLong(userId);
			//存储图片
			String subDir = AVATAR_DIR;
			if(type == 2) //上传照片
			{
				long dir = l_userId/1000;
				subDir = PHOTO_DIR + File.separator + dir;
			}
			// creates the save directory if it does not exists
			Part part = request.getPart("img");
			String relativePath =  subDir + File.separator + MD5.getMD5String(System.currentTimeMillis()+"") + "_" + userId + "." + suffix;
			String diskPath = applicationPath + File.separator + relativePath;
			LoggerUtil.logMsg(diskPath);
			
			File fileSaveDir = new File(diskPath).getParentFile();
			if (!fileSaveDir.exists()) {
				fileSaveDir.mkdirs();
				LoggerUtil.logMsg("mkdirs------");
			}
			
			part.write(diskPath);
			LoggerUtil.logMsg(relativePath);
			
			//入库
			conn = DataSourceFactory.getInstance().getConn();
			if(type == 2) //上传照片
			{
				long dir = l_userId/1000;
				subDir = PHOTO_DIR + File.separator + dir;
				stmt = conn.prepareStatement("insert into uploads(USER_ID, UPLOAD_TIME, PHOTO_PATH, ENABLING) values(?,?,?,?)");
				stmt.setString(1, userId);
				stmt.setLong(2, System.currentTimeMillis());
				stmt.setString(3, relativePath);
				stmt.setInt(4, 1);
			}
			else
			{
				stmt = conn.prepareStatement("update userbase set avatar = ? where USER_ID = ?");
				stmt.setString(1, relativePath);
				stmt.setString(2, userId);
			}
		    
			LoggerUtil.logMsg("userId is "+ userId + " time " + System.currentTimeMillis() + " relativePath " + relativePath);
			 
			int result = stmt.executeUpdate();
			if(result != 1)
			{
				LoggerUtil.logMsg("update failure....../ ");
				
			}
			JSONObject obj = new JSONObject();
		    obj.put("imgPath", relativePath);
			jsonObject.put("data", JSONObject.toJSON(obj));
			
		}catch (SQLException e) {
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
		
		response.getWriter().print(jsonObject.toJSONString());
	}
}
