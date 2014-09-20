package com.indoorlocating.server;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IndoorLocatingServlet extends HttpServlet 
{
	private static final long serialVersionUID = 1L;

	/**Get 方法*/
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException 
	{
		doPost(req, resp);		//调用post方法处理
	}

	/**Post 方法*/
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException 
	{
		System.out.println("----REVEIVE----");
	    PrintWriter out=resp.getWriter();
	    
	    resp.setContentType("text/html;charset=UTF-8");
	    if (req.getParameter("type")==null) return;
	    String type = req.getParameter("type").toString().trim();
	    if (type.equals("input"))		//输入采样请求
	    {
	    	String label=req.getParameter("label").trim();
	    	String vector=req.getParameter("vector").toString().trim();
	    	out.println(Logic.processInput(label, vector).toString());		
	    }else 
	    if (type.equals("query")) 		//定位请求
	    {
	    	String vector=req.getParameter("vector").toString().trim();
	    	out.println(Logic.getLocation(vector));
	    }
	}
}
