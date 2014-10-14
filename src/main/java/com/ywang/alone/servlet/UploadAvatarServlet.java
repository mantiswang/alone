package com.ywang.alone.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.ywang.alone.utils.UploadUtil;
import com.ywang.utils.LoggerUtil;

/**
 * Servlet implementation class UploadAvatarServlet
 */
@WebServlet(name = "UploadAvatarServlet", urlPatterns = { "/api/uploadAvatar" })
@MultipartConfig
public class UploadAvatarServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

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
		Part part = request.getPart("file");
		PrintWriter out = response.getWriter();
		out.println("此文件的大小：" + part.getSize() + "<br />");
		out.println("此文件类型：" + part.getContentType() + "<br />");
		out.println("文本框内容：" + request.getParameter("name") + "<br />");
		out.println(UploadUtil.getFileName(part) + "<br />");
		part.write("1." + UploadUtil.getFileType(part));
	}

}
