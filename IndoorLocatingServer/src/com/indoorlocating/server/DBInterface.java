package com.indoorlocating.server;

import java.sql.*;
import java.util.*;

/**
 * 数据库交互类。提供数据库交互的静态方法。
 */
public class DBInterface 
{	
	/**
	 * 私有构造函数。不允许创建实例。
	 */
	private DBInterface(){}
	
	/**
	 * 判断地点标签是否存在。
	 * @param label 地点标签
	 * @return true-存在；false-不存在
	 * @throws SQLException
	 */
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
	
	/**
	 * 新建地点
	 * @param label 地点标签
	 * @throws SQLException
	 */
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
	
	/**
	 * 根据地点label获得地点id
	 * @param label 地点label
	 * @return 地点id。-1表示不存在
	 * @throws SQLException
	 */
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
	
	/**
	 * 判断wifi的BSSID是否存在
	 * @param bssid wifi的BSSID
	 * @return true-存在；false-不存在
	 * @throws SQLException
	 */
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
	
	/**
	 * 创建wifi信息
	 * @param bssid wifi的BSSID
	 * @param freq wifi的频率
	 * @throws SQLException
	 */
	public static void createWifi(String bssid,int freq)
		throws SQLException
	{
		Connection conn=null;
		try
		{
			conn=DBUtil.getConnForMySql();
			PreparedStatement stmt=conn.prepareStatement("INSERT INTO wifi (bssid,freq) VALUES(?,?)");
			stmt.setString(1,bssid);
			stmt.setInt(2,freq);
			stmt.executeUpdate();
		}
		finally
		{
			DBUtil.closeResources(conn);
		}			
	}
	
	/**
	 * 通过wifi的bssid获得wifi的数据库id
	 * @param bssid wifi的BSSID
	 * @return wifi的数据库id。-1表示不存在
	 * @throws SQLException
	 */
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
	
	/**
	 * 创建采样记录
	 * @param lid 地点id
	 * @param wid wifi id
	 * @param level wifi强度（dbm）
	 * @throws SQLException
	 */
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
	
	/**
	 * 通过地点label获得HashMap形式的强度向量
	 * @param label 地点label
	 * @param floor 所需wifi热点的强度下限 
	 * @return HashMap形式的强度向量
	 * @throws SQLException
	 */
	public static HashMap<Integer,WIFI_MES> getVectorHMByLabel(String label,int floor)
		throws SQLException
	{
		Connection conn=null;
		ResultSet res=null;
		try
		{
			HashMap<Integer,WIFI_MES> result=new HashMap<Integer,WIFI_MES>();
			int lid=getLidByLabel(label);
			conn=DBUtil.getConnForMySql();
			PreparedStatement stmt=conn.prepareStatement("SELECT sample.wid,AVG(level),freq FROM sample,wifi WHERE lid=? AND wifi.wid=sample.wid AND sample.wid NOT IN (SELECT wid FROM sample WHERE lid=? AND level<?) GROUP BY wid");
			stmt.setInt(1,lid);
			stmt.setInt(2,lid);
			stmt.setInt(3,floor);
			res=stmt.executeQuery();
			while (res.next())
			{
				result.put(res.getInt(1),new WIFI_MES(res.getDouble(2),res.getInt(3)));
			}
			return result;
		}
		finally
		{
			DBUtil.closeResources(conn,res);
		}	
	}
	
	/**
	 * 获得所有地点label构成的ArrayList
	 * @return 所有地点label构成的ArrayList
	 * @throws SQLException
	 */
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
	
	/**
	 * 根据wifi id获得其频率
	 * @param wid wifi id
	 * @return 频率。-1表示不存在
	 * @throws SQLException
	 */
	public static int getFreqByWid(int wid)
			throws SQLException
		{
			Connection conn=null;
			ResultSet res=null;
			try
			{
				conn=DBUtil.getConnForMySql();
				PreparedStatement stmt=conn.prepareStatement("SELECT freq FROM wifi WHERE wid=?");
				stmt.setInt(1,wid);
				res=stmt.executeQuery();
				if (res.next())
				{
					return res.getInt("freq");
				}
				else
					return -1;
			}
			finally
			{
				DBUtil.closeResources(conn,res);
			}	
		}	
}
