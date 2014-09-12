package com.jsondemo.servlet;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
/**数据库操作类*/
public class DBUtil {
	private static Connection conn;
	/**
	 * An object that represents a precompiled SQL statement(����,����). A SQL statement is
	 * precompiled and stored in a PreparedStatement object. This object can
	 * then be used to efficiently execute this statement multiple times. Note:
	 * The setter methods (setShort, setString, and so on) for setting IN
	 * parameter values must specify types that are compatible with the defined
	 * SQL type of the input parameter. For instance, if the IN parameter has
	 * SQL type INTEGER, then the method setInt should be used. If arbitrary
	 * parameter type conversions are required, the method setObject should be
	 * used with a target SQL type. In the following example of setting a
	 * parameter, con represents an active connection: PreparedStatement pstmt =
	 * con.prepareStatement("UPDATE EMPLOYEES SET SALARY = ? WHERE ID = ?");
	 * pstmt.setBigDecimal(1, 153833.00) pstmt.setInt(2, 110592) See
	 * Also:Connection.prepareStatement ResultSet
	 */
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
	public static Connection getConnForMySql() {

		new DBUtil().init();

		try {
			Class.forName(driverClass);
			conn = DriverManager.getConnection(driverUrl, username, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * 根据paramsConfig.properties中数据库的参数信息，设置数据库的连接参数
	 */
	private void init() {
//		Properties pro = new Properties();
//		try {
//			pro.load(this.getClass().getClassLoader()
//					.getResourceAsStream("paramsConfig.properties"));
//			driverClass = pro.getProperty("driverClass");
//			driverUrl = pro.getProperty("dbUrl");
//			username = pro.getProperty("username");
//			password = pro.getProperty("password");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		driverClass = "com.mysql.jdbc.Driver";
		driverUrl = "jdbc:mysql://localhost:3306/mydb2";
		username = "root";
		password = "root";
	}

	/**获得PreparedStatement
	 * 	 * @param sql
	 * @return PreparedStatement pstmt
	 */
	public static PreparedStatement getPreparedStatemnt(Connection conn,
			String sql) {
		try {
			pstmt = conn.prepareStatement(sql);
		} catch (SQLException e) {
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
	public static PreparedStatement getPreparedStatemnt(Connection conn,
			String sql, String params[]) {
		try {
			pstmt = conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				pstmt.setString(i + 1, params[i]);
			}
		} catch (SQLException e) {
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
	public static PreparedStatement getPreparedStatemnt(Connection conn,
			String sql, Object params[]) {
		try {
			pstmt = conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				pstmt.setObject(i + 1, params[i]);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pstmt;
	}

	/**关闭资源
	 * @param conn
	 */
	public static void CloseResources(Connection conn) {
		try {
			if (conn != null && !conn.isClosed())
				conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param stmt
	 */
	public static void CloseResources(Statement stmt) {
		try {
			if (stmt != null)
				stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param rs
	 */
	public static void CloseResources(ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param rs
	 */
	public static void CloseResources(ResultSet rs, Statement stmt) {
		CloseResources(rs);
		CloseResources(stmt);
	}

	/**
	 * @param conn
	 * @param stmt
	 */
	public static void CloseResources(Connection conn, Statement stmt) {
		CloseResources(stmt);
		CloseResources(conn);
	}

	/**
	 * @param conn
	 * @param rs
	 */
	public static void CloseResources(Connection conn, ResultSet rs) {
		CloseResources(rs);
		CloseResources(conn);
	}

	/**
	 * @param conn
	 * @param stmt
	 * @param rs
	 */
	public static void CloseResources(Connection conn, Statement stmt,
			ResultSet rs) {
		CloseResources(rs);
		CloseResources(conn, stmt);
	}
}

