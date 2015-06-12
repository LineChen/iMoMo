package com.database;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.imomo_msg.MsgDb;
import com.imomo_msg.MsgKeys;
import com.server_utils.FileTools;

public class SqlModel {
	/** 新增用户、修改信息等操作 */
	public boolean updateDb(String sql, String[] paras) {
		// 创建SqlHelper(如果程序并发性不考虑，可以吧SqlHelper做成静态的)
		SqlHelper sqlHelper = new SqlHelper();
		return sqlHelper.updExecute(sql, paras);
	}

	/** 登录验证 */
	public boolean checkUser(String userEmail, String userPasswd) {
		SqlHelper sp = null;
		boolean isLegal = false;
		try {
			// 阻止SQL语句 和参数列表
			String sql = "select userPasswd from imomo_clients where userEmail = ?";
			String paras[] = { userEmail };
			sp = new SqlHelper();
			ResultSet rs = sp.query(sql, paras);
			if (rs.next()) {
				if (rs.getString(1).equals(userPasswd)) {
					isLegal = true;
				}
			}
		} catch (Exception e) {
			isLegal = false;
			e.printStackTrace();
		} finally {
			sp.close();
		}
		return isLegal;
	}

	/**
	 * 根据Id返回用户名
	 * 
	 * @param userId
	 * @return 返回null表示不存在该用户
	 */
	public String getUserName(String userEmail, boolean isEmail) {
		SqlHelper sp = null;
		String userName = "null";
		try {
			String sql = null;
			if(isEmail) {
				  sql = "select userName from imomo_clients where userEmail = ?";
			} else {
				  sql = "select userName from imomo_clients where userId = ?";
			}
			String paras[] = {userEmail};
			sp = new SqlHelper();
			ResultSet rs = sp.query(sql, paras);
			if (rs.next()) {
				userName = rs.getString(1);
			}
		} catch (Exception e) {
			userName = "null";
			e.printStackTrace();
		} finally {
			sp.close();
		}
		return userName;
	}

	/**
	 * 
	 * @param userEmail
	 *            用户注册邮箱 或用户Id
	 * @param isEmail
	 *            是邮箱还是用户Id标识 true - 邮箱 false - 用户Id
	 * @return
	 */
	public JSONObject getUserInfo(String userEmail, boolean isEmail) {
		JSONObject userInfo = new JSONObject();
		SqlHelper sp = null;
		String sql = null;
		try {
			if (isEmail) {
				sql = "select userId, userName, userHeadPath, userSex, userBirthday, personSignature, vitalityValue from imomo_clients where userEmail = ?";
			} else {
				sql = "select userId, userName, userHeadPath, userSex, userBirthday, personSignature, vitalityValue from imomo_clients where userId = ?";
			}
			String paras[] = { userEmail };

			sp = new SqlHelper();
			ResultSet rs = sp.query(sql, paras);
			if (rs.next()) {
				userInfo.put(MsgKeys.userId, rs.getString(1));
				userInfo.put(MsgKeys.userName, rs.getString(2));
				userInfo.put(MsgKeys.userHeadPath, rs.getString(3));
				userInfo.put(MsgKeys.userSex, rs.getString(4));
				userInfo.put(MsgKeys.userBirthday, rs.getString(5));
				userInfo.put(MsgKeys.personSignature, rs.getString(6));
				userInfo.put(MsgKeys.vitalityValue, rs.getInt(7));
			}
		} catch (Exception e) {
			userInfo = null;
			e.printStackTrace();
		} finally {
			sp.close();
		}
		return userInfo;
	}

	/**
	 * 判断一个表是否存在
	 * 
	 * @param userId
	 * @return
	 */
	public boolean isTableExists(String tableName) {
		boolean isExists = true;
		String sql = "SHOW TABLES like ?";
		String paras[] = { tableName };
		SqlHelper sp = null;
		String tempTable = null;
		try {
			sp = new SqlHelper();
			ResultSet rs = sp.query(sql, paras);
			if (!rs.next()) {
				isExists = false;
			}
		} catch (Exception e) {
			isExists = false;
			e.printStackTrace();
		} finally {
			sp.close();
		}
		return isExists;
	}

	/**
	 * 分配Id
	 * 
	 * @param userEmail
	 * @return
	 */
	public String allocateId() {
		SqlHelper sp = null;
		int newId = 0;
		try {
			String sql = "select allocate_id from allocation_id where flag = ?";
			String paras[] = { "0" };
			sp = new SqlHelper();
			ResultSet rs = sp.query(sql, paras);
			if (rs.next()) {
				newId = rs.getInt(1);
				sp.updExecute(
						"update allocation_id set flag = ? where flag = ?",
						new String[] { "1", "0" });
				sp.updExecute("insert into allocation_id values(?,?)",
						new String[] { (newId + 1) + "", "0" });
			} else {
				return "null";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "null";
		} finally {
			sp.close();
		}
		return newId + "";
	}

	/**
	 * 插入一条离线消息
	 * 
	 * @param msgDb
	 * @return
	 */
	public boolean insertCacheMsg(MsgDb msgDb, String userId) {
		String sql = "insert into " + "mc_" + userId + " values (?,?)";
		String[] paras = { msgDb.msgType + "", msgDb.msgJson };
		return updateDb(sql, paras);
	}

	/** 根据表名，清空表中的信息缓存 */
	public boolean clearMsgCache(String userId) {
		String sql = "delete from " + "mc_" + userId + " where 1 = ?";
		String[] paras = { "1" };
		SqlHelper sqlHelper = new SqlHelper();
		return sqlHelper.updExecute(sql, paras);
	}

	/**
	 * 创建用户消息缓冲表（当接收消息的用户未上线，暂存消息，上线之后立即发送）
	 * 
	 * @param userId
	 *            接收信息的用户Id
	 * @return 返回true表示成功，反之失败
	 */
	public boolean createCacheTable(String userId) {
		String tableName = "mc_" + userId;// 表明尽量短
		String sql = "create table " + tableName
				+ " (msgType int(4), msgJson text)";
		SqlHelper sqlHelper = new SqlHelper();
		return sqlHelper.create(sql);
	}

	/**
	 * 得到离线消息记录数
	 * 
	 * @param userId
	 * @return
	 */
	public int getMsgCount(String userId) {
		int count = 0;// "SELECT count(*) FROM sqlite_master WHERE type='table' AND name= ? ";
		String tableName = "mc_" + userId;
		String sql = "select count(*) from " + tableName + " where 1 = ?";
		String paras[] = { "1" };
		SqlHelper sp = null;
		try {
			sp = new SqlHelper();
			ResultSet rs = sp.query(sql, paras);
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (Exception e) {
			count = 0;
			e.printStackTrace();
		} finally {
			sp.close();
		}
		return count;
	}

	/**
	 * 从数据库得到离线消息list
	 * 
	 * @param userId
	 * @return
	 */
	public List<MsgDb> getCacheMsgs(String userId) {
		String tableName = "mc_" + userId;
		List<MsgDb> list = new ArrayList<MsgDb>();
		String sql = "select * from " + tableName + " where 1 = ?";
		String paras[] = { "1" };
		SqlHelper sp = null;
		try {
			sp = new SqlHelper();
			ResultSet rs = sp.query(sql, paras);
			while (rs.next()) {
				MsgDb msgDb = new MsgDb();
				msgDb.msgType = rs.getInt("msgType");
				msgDb.msgJson = rs.getString("msgJson");
				list.add(msgDb);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sp.close();
		}

		return list;
	}

	/**
	 * 添加好友
	 * 
	 * @param userId
	 *            用户Id
	 * @param friendId
	 *            待添加的好友Id
	 * @return
	 */
	public boolean addFriend(String userId, String friendId) {
		SqlHelper sp = null;
		String sql2 = "select userId from friend_list  where userId = ?";
		String[] pp = { userId };
		try {
			sp = new SqlHelper();
			ResultSet rs = sp.query(sql2, pp);
			if (!rs.next()) {
				System.out.println("rs.next() " + rs.next());
				String sql3 = "insert into friend_list(userId) values(?)";
				String paras[] = { userId };
				updateDb(sql3, paras);
			}
			String sql1 = "select friendList from friend_list  where userId = ?";
			String paras1[] = { userId };
			String freindListStr = "";
			sp = new SqlHelper();
			ResultSet rs2 = sp.query(sql1, paras1);
			if (rs2.next()) {
				freindListStr = rs2.getString(1);
			}
			if (freindListStr == null) {
				freindListStr = friendId;
			} else {
				String[] friends = getFriendIds(userId);
				boolean isExists = false;
				for (String string : friends) {
					if (string.equals(friendId)) {
						isExists = true;
						break;
					}
				}
				if (!isExists)
					freindListStr += "," + friendId;
			}

			String sql = "update friend_list set friendList = ? where userId = ?";
			String paras[] = { freindListStr, userId };
			return updateDb(sql, paras);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sp.close();
		}
		return false;

	}

	/**
	 * 删除好友
	 * 
	 * @param userId
	 * @param friendId
	 * @return
	 */
	public boolean deleteFriend(String userId, String friendId) {
		String[] oldFriend = this.getFriendIds(userId);
		String newFriendStr = "";
		for (String str : oldFriend) {
			if (!str.equals(friendId)) {
				newFriendStr += str + ",";
			}
		}
		String sql = "update friend_list set friendList = ? where userId = ?";
		String paras[] = { newFriendStr.substring(0, newFriendStr.length() - 1), userId };
		return updateDb(sql, paras);
	}

	/**
	 * 获取好友列表
	 * 
	 * @param userId
	 * @return list
	 */
	public String[] getFriendIds(String userId) {
		String[] friendList = null;
		String sql = "select friendList from friend_list  where userId = ?";
		String paras[] = { userId };
		SqlHelper sp = null;
		try {
			sp = new SqlHelper();
			ResultSet rs = sp.query(sql, paras);
			if (rs.next()) {
				friendList = rs.getString(1).split(",");
			}
		} catch (Exception e) {
//			e.printStackTrace();
		} finally {
			sp.close();
		}
		return friendList;
	}
	
	/**
	 * 更新活力值
	 * @param userId
	 * @param type
	 * @return
	 */
	public boolean UpdateVitality(String userId, int type){
		SqlHelper sp = null;
		try {
			String sql1 = "select vitalityValue from imomo_clients where userId = ?";
			String[] paras1 = {userId};
			sp = new SqlHelper();
			ResultSet rs = sp.query(sql1, paras1);
			int vitalityValue = 0;
			if (rs.next()) {
				vitalityValue = rs.getInt(1);
			}
			String sql = "update imomo_clients set vitalityValue = ? where userId = ?";
			String[] paras = new String[2];
			if(type == -1){
				paras[0]= (vitalityValue - 10) + "";
			} else if(type == 1){
				paras[0]= (vitalityValue + 15) + "";
			}
			paras[1]=userId;
			return sp.updExecute(sql, paras);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sp.close();
		}
		return false;
	}

}




