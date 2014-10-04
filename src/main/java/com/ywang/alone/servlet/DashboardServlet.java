package com.ywang.alone.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ywang.alone.utils.AloneUtils;

/**
 * Servlet implementation class DashboardServlet
 */
@WebServlet(name="dashboard", urlPatterns="/api/test")
public class DashboardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DashboardServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().write("shlweo");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String paramString = request.getParameter("param");
		String actionString = request.getParameter("action");
		System.out.println(paramString + "-----" + " Action " + actionString);
		String result = AloneUtils.execute(actionString, paramString);
		response.getWriter().print(result);
		System.err.println(result);
	}

}
