package com.indoorlocating.server;

import java.sql.*;
import java.util.ArrayList;

public class DBInterface 
{	
	public static boolean labelExists(String label)
		throws SQLException
	{
		Connection conn=null;
		ResultSet res=null;
		try
		{
			conn=DBUtil.getConnForMySql();
			PreparedStatement stmt=conn.prepareStatement("SELECT COUNT(lid) FROM location WHERE label=?");
			stmt.setString(1,label);
			res=stmt.executeQuery();
			res.first();
			return res.getInt(1)==1?true:false;
		}
		finally
		{
			DBUtil.closeResources(conn,res);
		}				
	}
	
	public static void createLocation(String label)
		throws SQLException
	{
		Connection conn=null;
		try
		{
			conn=DBUtil.getConnForMySql();
			PreparedStatement stmt=conn.prepareStatement("INSERT INTO location (label) VALUES(?)");
			stmt.setString(1,label);
			stmt.executeUpdate();
		}
		finally
		{
			DBUtil.closeResources(conn);
		}		
	}
	
	public static int getLidByLabel(String label)
		throws SQLException
	{
		Connection conn=null;
		ResultSet res=null;
		try
		{
			conn=DBUtil.getConnForMySql();
			PreparedStatement stmt=conn.prepareStatement("SELECT lid FROM location WHERE label=?");
			stmt.setString(1,label);
			res=stmt.executeQuery();
			if (res.next())
			{
				return res.getInt("lid");
			}
			else
				return -1;
		}
		finally
		{
			DBUtil.closeResources(conn,res);
		}				
	}
	
	public static boolean bssidExists(String bssid)
		throws SQLException
	{
		Connection conn=null;
		ResultSet res=null;
		try
		{
			conn=DBUtil.getConnForMySql();
			PreparedStatement stmt=conn.prepareStatement("SELECT count(wid) FROM wifi WHERE bssid=?");
			stmt.setString(1,bssid);
			res=stmt.executeQuery();
			res.first();
			return res.getInt(1)==1?true:false;
		}
		finally
		{
			DBUtil.closeResources(conn,res);
		}	
	}
	
	public static void createWifi(String bssid)
		throws SQLException
	{
		Connection conn=null;
		try
		{
			conn=DBUtil.getConnForMySql();
			PreparedStatement stmt=conn.prepareStatement("INSERT INTO wifi (bssid) VALUES(?)");
			stmt.setString(1,bssid);
			stmt.executeUpdate();
		}
		finally
		{
			DBUtil.closeResources(conn);
		}			
	}
	
	public static int getWidByBssid(String bssid)
		throws SQLException
	{
		Connection conn=null;
		ResultSet res=null;
		try
		{
			conn=DBUtil.getConnForMySql();
			PreparedStatement stmt=conn.prepareStatement("SELECT wid FROM wifi WHERE bssid=?");
			stmt.setString(1,bssid);
			res=stmt.executeQuery();
			if (res.next())
			{
				return res.getInt("wid");
			}
			else
				return -1;
		}
		finally
		{
			DBUtil.closeResources(conn,res);
		}	
	}
	
	public static void createSample(int lid,int wid,int level)
		throws SQLException
	{
		long time=new java.util.Date().getTime();
		Connection conn=null;
		try
		{
			conn=DBUtil.getConnForMySql();
			PreparedStatement stmt=conn.prepareStatement("INSERT INTO sample (lid,wid,level,time) VALUES(?,?,?,?)");
			stmt.setInt(1,lid);
			stmt.setInt(2, wid);
			stmt.setInt(3, level);
			stmt.setLong(4, time);
			stmt.executeUpdate();
		}
		finally
		{
			DBUtil.closeResources(conn);
		}	
	}
	
	public static ArrayList<WIFI_MES> getVectorALByLabel(String label,int floor)
		throws SQLException
	{
		Connection conn=null;
		ResultSet res=null;
		try
		{
			ArrayList<WIFI_MES> result=new ArrayList<WIFI_MES>();
			int lid=getLidByLabel(label);
			conn=DBUtil.getConnForMySql();
			PreparedStatement stmt=conn.prepareStatement("SELECT wid,AVG(level) FROM sample WHERE lid=? AND wid NOT IN (SELECT wid FROM sample WHERE lid=? AND level<?) GROUP BY wid");
			stmt.setInt(1,lid);
			stmt.setInt(2,lid);
			stmt.setInt(3,floor);
			res=stmt.executeQuery();
			while (res.next())
			{
				result.add(new WIFI_MES(res.getInt(1),res.getDouble(2)));
			}
			return result;
		}
		finally
		{
			DBUtil.closeResources(conn,res);
		}	
	}
	
	public static ArrayList<String> getAllLabelAL()
		throws SQLException
	{
		Connection conn=null;
		ResultSet res=null;
		try
		{
			ArrayList<String> result=new ArrayList<String>();
			conn=DBUtil.getConnForMySql();
			Statement stmt=conn.createStatement();
			res=stmt.executeQuery("SELECT label FROM location GROUP BY label");
			while (res.next())
			{
				result.add(res.getString(1));
			}
			return result;
		}
		finally
		{
			DBUtil.closeResources(conn,res);
		}	
	}
}
