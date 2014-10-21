package com.ywang.alone.servlet;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ywang.alone.Constant;
import com.ywang.alone.db.DataSourceFactory;
import com.ywang.alone.utils.AloneUtil;
import com.ywang.alone.utils.UploadUtil;
import com.ywang.utils.LoggerUtil;
import com.ywang.utils.MD5;

/**
 * Servlet implementation class UploadAvatarServlet
 */
@WebServlet(name = "UploadAvatarServlet", urlPatterns = "/api/uploadAvatar")
@MultipartConfig( maxFileSize = 1024 * 1024 * 2)
public class UploadAvatarServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String UPLOAD_DIR = "uploads";
	private static final String AVATAR_DIR = "images/header";
	private static final String PHOTO_DIR = "images/photo";
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
			jsonObject.put("ret", 1);
			return;
		}
		LoggerUtil.logMsg(message);
		JSONObject param = JSON.parseObject(message);
		String token = param.getString("key");
		int type = param.getIntValue("type");
		if (StringUtils.isEmpty(token)) {
			jsonObject.put("errDesc", Constant.ErrorDesc.PARAM_ILLEGAL);
			jsonObject.put("errCode", Constant.ErrorCode.PARAM_ILLEGAL);
			jsonObject.put("ret", 1);
			return;
		}
		
		// gets absolute path of the web application
        String applicationPath = request.getServletContext().getRealPath("");
        // constructs path of the directory to save uploaded file
        String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;
		LoggerUtil.logMsg(uploadFilePath);

		DruidPooledConnection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = DataSourceFactory.getInstance().getConn();
			conn.setAutoCommit(false);
			stmt = conn
					.prepareStatement("select USER_ID from userbase where PKEY = ? ");
			stmt.setString(1, token);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				long userId = rs.getLong("USER_ID");
				String subDir = AVATAR_DIR;
				if(type == 2) //上传照片
				{
					long dir = userId/1000;
					subDir = PHOTO_DIR + File.separator + dir;
				}
				
				// creates the save directory if it does not exists
				Part part = request.getPart("file");
				String relativePath =  subDir + File.separator + MD5.getMD5String(System.currentTimeMillis()+"") + "_" + userId + "." + UploadUtil.getFileType(part);
				String diskPath = uploadFilePath + File.separator + relativePath;
				
				File fileSaveDir = new File(diskPath).getParentFile();
				if (!fileSaveDir.exists()) {
					fileSaveDir.mkdirs();
					LoggerUtil.logMsg("mkdirs------");
				}
				
				part.write(diskPath);
				LoggerUtil.logMsg(relativePath);
				
				PreparedStatement insertUploadStmt = conn
						.prepareStatement("insert into uploads(USER_ID, TYPE, PHOTO_PATH) values(?,?,?)");
				insertUploadStmt.setLong(1, userId);
				insertUploadStmt.setInt(2, type);
				insertUploadStmt.setString(3, relativePath);
				
				int result = insertUploadStmt.executeUpdate();
				if (result != 1) {
					jsonObject.put("ret", 2);
					jsonObject.put("errCode", Constant.ErrorCode.UPDATE_DB_FAIL);
					jsonObject.put("errDesc", Constant.ErrorDesc.UPDATE_DB_FAIL);
				}
				else
				{
					jsonObject.put("data", "{'imgPath':'" + relativePath + "'}");
				}
				insertUploadStmt.close();
			}
			else
			{
				jsonObject.put("ret", 1);
				jsonObject.put("errCode", Constant.ErrorCode.NO_ACCESS_AUTH);
				jsonObject.put("errDesc", Constant.ErrorDesc.NO_ACCESS_AUTH);
			}
		}catch (SQLException e) {
			LoggerUtil.logServerErr(e);
			jsonObject.put("ret", 2);
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
