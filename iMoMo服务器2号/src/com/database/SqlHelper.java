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

	// �������ݿ�
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

	/** �������ݿ���µı� */
	public boolean create(String sql) {
		boolean isOk = true;
		try {
			ConnectDb();
			ps = ct.prepareStatement(sql);
			isOk = !ps.execute();// ���ز���ֵ,����false ����ɹ�,��֮ʧ��
		} catch (Exception e) {
			isOk = false;
			e.printStackTrace();
		} finally {
			this.close();
		}
		return isOk;
	}


	/** ��ѯ */
	public ResultSet query(String sql, String[] paras) {
		try {
			ConnectDb();
			ps = ct.prepareStatement(sql);
			// �Բ�����ֵ
			for (int i = 0; i < paras.length; i++) {
				ps.setString(i + 1, paras[i]);
			}
			rs = ps.executeQuery();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	/** ��ɾ�� */
	public boolean updExecute(String sql, String[] paras) {
		boolean b = true;
		try {
			ConnectDb();
			ps = ct.prepareStatement(sql);
			// ��ps�ģ���ֵ
			for (int i = 0; i < paras.length; i++) {
				ps.setString(i + 1, paras[i]);
			}
			ps.executeUpdate();// ִ�в��� ���ر仯������
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

	// ͳһ�ر���Դ
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
