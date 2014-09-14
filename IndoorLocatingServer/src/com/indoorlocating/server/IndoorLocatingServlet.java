package com.indoorlocating.server;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.*;

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
	    	JSONArray jsonArray=JSONArray.fromObject(vector);
	    	JSONObject jsonObject=new JSONObject();
	    	jsonObject.put("data",jsonArray);
	    	boolean flag=DBInterface.InsertRecord(label, vector);
	    	JSONObject reply=new JSONObject();
	    	reply.put("flag",flag?1:0);
	    	out.println(reply.toString());
	    }else 
	    if (type.equals("query")) 
	    {
	    	String vector=req.getParameter("vector").toString().trim();
	    	out.println(Logic.GetLocation(vector));
	    }
	}
}
