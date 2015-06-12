package com.database;

import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlHelper {
	String dbuserId = "root";
	String dbuserPassWd = "root";
	String driverName = "com.mysql.jdbc.Driver";
	String url = "jdbc:mysql://localhost:3306/hello_stranger_db";
	PreparedStatement ps = null;
	Connection ct = null;
	ResultSet rs = null;

	// 连接数据库
	public boolean ConnectDb() {
		boolean isConected = true;
		try {
			Class.forName(driverName);
			ct = DriverManager.getConnection(url, dbuserId, dbuserPassWd);
		} catch (Exception e) {
			isConected = false;
			e.printStackTrace();
		}
		return isConected;

	}

	/** 创建数据库或新的表 */
	public boolean create(String sql) {
		boolean isOk = true;
		try {
			ConnectDb();
			ps = ct.prepareStatement(sql);
			isOk = !ps.execute();// 返回布尔值,返回false 代表成功,反之失败
		} catch (Exception e) {
			isOk = false;
			e.printStackTrace();
		} finally {
			this.close();
		}
		return isOk;
	}


	/** 查询 */
	public ResultSet query(String sql, String[] paras) {
		try {
			ConnectDb();
			ps = ct.prepareStatement(sql);
			// 对参数赋值
			for (int i = 0; i < paras.length; i++) {
				ps.setString(i + 1, paras[i]);
			}
			rs = ps.executeQuery();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	/** 增删改 */
	public boolean updExecute(String sql, String[] paras) {
		boolean b = true;
		try {
			ConnectDb();
			ps = ct.prepareStatement(sql);
			// 给ps的？赋值
			for (int i = 0; i < paras.length; i++) {
				ps.setString(i + 1, paras[i]);
			}
			ps.executeUpdate();// 执行操作 返回变化的行数
			// if (ps.executeUpdate() != 1) {
			// b = false;
			// }
		} catch (Exception e) {
			b = false;
			e.printStackTrace();
		} finally {
			this.close();
		}
		return b;
	}

	// 统一关闭资源
	public void close() {
		try {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (ct != null)
				ct.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
