package com.ywang.alone.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ywang.alone.db.DataSourceFactory;
import com.ywang.alone.handler.AloneHandler;
import com.ywang.utils.Config;
import com.ywang.utils.JedisUtil;
import com.ywang.utils.LoggerUtil;

/**
 * Servlet implementation class DashboardServlet
 */
@WebServlet(name = "dashboard", urlPatterns = "/api/dashboard")
public class DashboardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	static
	{
		try {
			Config cfg = Config.getInstance();
			DataSourceFactory.initFactory(cfg);
			JedisUtil.init(cfg);
		} catch (SQLException e) {
			LoggerUtil.logServerErr(e);
		}
	}
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DashboardServlet() {
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

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
//		BufferedReader br = new BufferedReader(new InputStreamReader(  
//                (ServletInputStream) request.getInputStream(), "utf-8"));  
//        StringBuffer sb = new StringBuffer("");  
//        String temp;  
//        while ((temp = br.readLine()) != null) {  
//            sb.append(temp);  
//        }  
//        br.close();  
		
		
		String message = request.getParameter("msg");
		if (StringUtils.isBlank(message))
			return;

		LoggerUtil.logMsg(message);
		String result = AloneHandler.handler(message);
		
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(result);
	}

}
