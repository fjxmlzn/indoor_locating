package com.indoorlocating.server;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IndoorLocatingServlet extends HttpServlet 
{
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException 
	{
		// TODO Auto-generated method stub
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException 
	{
		// TODO Auto-generated method stub
	    PrintWriter out=resp.getWriter();
	    
	    resp.setContentType("text/html;charset=UTF-8");
	    String type = req.getParameter("type").toString().trim();
	    if (type.equals("input"))
	    {
	    	String label=req.getParameter("label").trim();
	    	String vector=req.getParameter("vector").toString().trim();
	    	out.println(Logic.processInput(label, vector).toString());
	    }else 
	    if (type.equals("query")) 
	    {
	    	String vector=req.getParameter("vector").toString().trim();
	    	out.println(Logic.getLocation(vector));
	    }
	}
}
