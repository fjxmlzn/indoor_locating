package com.indoorlocating.server;

/*copy from ToyDemo*/
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**数据库操作类*/
public class DBUtil 
{
	private static Connection conn;
	private static PreparedStatement pstmt;

	private static String driverClass = "";
	private static String driverUrl = "";
	private static String username = "";
	private static String password = "";

	/**
	 * 连接mySQL数据库
	 * 
	 * @return Connection conn
	 */
	public static Connection getConnForMySql() 
	{
		new DBUtil().init();
		try 
		{
			Class.forName(driverClass);
			conn = DriverManager.getConnection(driverUrl, username, password);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * 根据paramsConfig.properties中数据库的参数信息，设置数据库的连接参数
	 */
	private void init() 
	{
		driverClass = "com.mysql.jdbc.Driver";
		driverUrl = "jdbc:mysql://localhost:3306/indoor_locating";
		username = "root";
		password = "root";
	}

	/**获得PreparedStatement
	 * 	 * @param sql
	 * @return PreparedStatement pstmt
	 */
	public static PreparedStatement getPreparedStatemnt(Connection conn, String sql)
	{
		try 
		{
			pstmt = conn.prepareStatement(sql);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return pstmt;
	}

	/**根据参数param获得PreparedStatement
	 * @param conn
	 * @param sql
	 * @param params
	 * @return PreparedStatement pstmt
	 */
	public static PreparedStatement getPreparedStatemnt(Connection conn, String sql, String params[]) 
	{
		try 
		{
			pstmt = conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) 
			{
				pstmt.setString(i + 1, params[i]);
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return pstmt;
	}

	/**
	 * @param conn
	 * @param sql
	 * @param params
	 * @return PreparedStatement pstmt
	 */
	public static PreparedStatement getPreparedStatemnt(Connection conn, String sql, Object params[]) 
	{
		try 
		{
			pstmt = conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) 
			{
				pstmt.setObject(i + 1, params[i]);
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return pstmt;
	}

	/**关闭资源
	 * @param conn
	 */
	public static void CloseResources(Connection conn) 
	{
		try 
		{
			if (conn != null && !conn.isClosed())
				conn.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	/**
	 * @param stmt
	 */
	public static void CloseResources(Statement stmt) 
	{
		try 
		{
			if (stmt != null)
				stmt.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	/**
	 * @param rs
	 */
	public static void CloseResources(ResultSet rs) 
	{
		try 
		{
			if (rs != null)
				rs.close();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @param rs
	 */
	public static void CloseResources(ResultSet rs, Statement stmt) 
	{
		CloseResources(rs);
		CloseResources(stmt);
	}

	/**
	 * @param conn
	 * @param stmt
	 */
	public static void CloseResources(Connection conn, Statement stmt) 
	{
		CloseResources(stmt);
		CloseResources(conn);
	}

	/**
	 * @param conn
	 * @param rs
	 */
	public static void CloseResources(Connection conn, ResultSet rs) 
	{
		CloseResources(rs);
		CloseResources(conn);
	}

	/**
	 * @param conn
	 * @param stmt
	 * @param rs
	 */
	public static void CloseResources(Connection conn, Statement stmt, ResultSet rs)
	{
		CloseResources(rs);
		CloseResources(conn, stmt);
	}
}
