package com.indoorlocating.server;

import java.io.IOException;
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
	    PrintWriter out=resp.getWriter();
	    out.print("HelloWorld");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException 
	{
		// TODO Auto-generated method stub
	    PrintWriter out=resp.getWriter();
	    out.print("HelloWorld");
	}
}
