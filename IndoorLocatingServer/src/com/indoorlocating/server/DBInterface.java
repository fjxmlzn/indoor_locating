package com.indoorlocating.server;

import java.sql.*;

import net.sf.json.*;

import org.omg.CORBA.*;

public class DBInterface 
{
	public static boolean InsertRecord(String label, String vector)
	{
		Connection conn=null;
		try
		{
			conn=DBUtil.getConnForMySql();
			PreparedStatement stmt=conn.prepareStatement("INSERT INTO wifi_info (label,vector) VALUES(?,?)");
			stmt.setString(1,label);
			stmt.setString(2,vector);
			stmt.executeUpdate();
			return true;
		}
		catch (Exception e)
		{
			System.out.println("Error occurs while executing InsertRecord(): "+e);
			return false;
		}
		finally
		{
			DBUtil.CloseResources(conn);
		}
	}
	
	public static JSONArray GetAllVector()
	{
		Connection conn=null;
		ResultSet res=null;
		try
		{
			conn=DBUtil.getConnForMySql();
			Statement stmt=conn.createStatement();
			res=stmt.executeQuery("SELECT id,vector FROM wifi_info");
			JSONArray jsonArray=new JSONArray();
			while (res.next())
			{
				JSONObject jsonObject=new JSONObject();
				jsonObject.put("id",res.getInt("id"));
				jsonObject.put("vector",res.getString("vector"));
				jsonArray.add(jsonObject);
			}
			return jsonArray;
		}
		catch (Exception e)
		{
			System.out.println("Error occurs while executing GetAllVector(): "+e);
			return new JSONArray();
		}
		finally
		{
			DBUtil.CloseResources(conn, res);
		}
	}
	
	public static boolean GetLabelById(int id,StringHolder result)
	{
		Connection conn=null;
		ResultSet res=null;
		try
		{
			conn=DBUtil.getConnForMySql();
			PreparedStatement stmt=conn.prepareStatement("SELECT label FROM wifi_info WHERE id=?");
			stmt.setString(1,id+"");
			res=stmt.executeQuery();
			if (res.next())
			{
				result.value=res.getString("label");
				return true;
			}
			else
				return false;
		}
		catch (Exception e)
		{
			System.out.println("Error occurs while executing GetLabelById(): "+e);
			return false;
		}
		finally
		{
			DBUtil.CloseResources(conn);
		}		
	}
}
