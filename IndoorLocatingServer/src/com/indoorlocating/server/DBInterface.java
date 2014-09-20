package com.indoorlocating.server;

import java.sql.*;
import java.util.*;

/**
 * ���ݿ⽻���ࡣ�ṩ���ݿ⽻���ľ�̬������
 */
public class DBInterface 
{	
	/**
	 * ˽�й��캯������������ʵ����
	 */
	private DBInterface(){}
	
	/**
	 * �жϵص��ǩ�Ƿ���ڡ�
	 * @param label �ص��ǩ
	 * @return true-���ڣ�false-������
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
	 * �½��ص�
	 * @param label �ص��ǩ
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
	 * ���ݵص�label��õص�id
	 * @param label �ص�label
	 * @return �ص�id��-1��ʾ������
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
	 * �ж�wifi��BSSID�Ƿ����
	 * @param bssid wifi��BSSID
	 * @return true-���ڣ�false-������
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
	 * ����wifi��Ϣ
	 * @param bssid wifi��BSSID
	 * @param freq wifi��Ƶ��
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
	 * ͨ��wifi��bssid���wifi�����ݿ�id
	 * @param bssid wifi��BSSID
	 * @return wifi�����ݿ�id��-1��ʾ������
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
	 * ����������¼
	 * @param lid �ص�id
	 * @param wid wifi id
	 * @param level wifiǿ�ȣ�dbm��
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
	 * ͨ���ص�label���HashMap��ʽ��ǿ������
	 * @param label �ص�label
	 * @param floor ����wifi�ȵ��ǿ������ 
	 * @return HashMap��ʽ��ǿ������
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
	 * ������еص�label���ɵ�ArrayList
	 * @return ���еص�label���ɵ�ArrayList
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
	 * ����wifi id�����Ƶ��
	 * @param wid wifi id
	 * @return Ƶ�ʡ�-1��ʾ������
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
