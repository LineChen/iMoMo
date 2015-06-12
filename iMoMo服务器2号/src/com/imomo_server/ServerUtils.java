package com.imomo_server;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.text.MaskFormatter;

import org.apache.mina.core.session.IoSession;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.database.SqlModel;
import com.imomo_msg.MsgDb;
import com.imomo_msg.MsgKeys;
import com.imomo_msg.MsgTranceUtil;
import com.imomo_msg.StrangerBean;
import com.imomo_msg.iMoMoMsg;
import com.imomo_msg.iMoMoMsgTypes;
import com.imomo_msg.myLocationBean;
import com.managers.GroupInfo;
import com.managers.ManageAProvinceLoc;
import com.managers.ManageClientSession;
import com.managers.ManageGroups;
import com.managers.ManageIdSessions;
import com.managers.ManageLocMap;
import com.server_utils.FileTools;
import com.server_utils.PasswordUtil;
import com.server_utils.StaticValues;
import com.server_utils.SendEmailToClient;

/**
 * 服务器辅助类
 * 
 * @author Administrator
 *
 */
public class ServerUtils {

	static ServerUtils serverUtils;

	public static ServerUtils getInstance() {
		// if (serverUtils == null) {
		// serverUtils = new ServerUtils();
		// }
		return new ServerUtils();
	}

	/**
	 * 处理注册
	 * 
	 * @param session
	 *            会话
	 * @param moMsg
	 *            消息包
	 */
	public void handleRegister(IoSession session, iMoMoMsg moMoMsg) {
		JSONObject json = JSON.parseObject(moMoMsg.msgJson);
		SqlModel model = new SqlModel();
		String userEmail = json.getString(MsgKeys.userEmail);
		String userId = model.allocateId();// 分配Id
		String userHeadPath = StaticValues.HEAD_P_PATH + userId + ".png";
		FileTools.getInstance().saveMultyFile(userHeadPath, moMoMsg.msgBytes);
		String sql = "insert into imomo_clients values (?,?,?,?,?,?,?,?,?)";
		String[] paras = { userId, userEmail, json.getString(MsgKeys.userName),
				json.getString(MsgKeys.userPasswd), userHeadPath,
				json.getString(MsgKeys.userSex),
				json.getString(MsgKeys.userBirthday), "" , 0+""};
		iMoMoMsg Notify = new iMoMoMsg();
		Notify.symbol = '+';
		JSONObject NotifyJson = new JSONObject();
		if (model.updateDb(sql, paras)) {
			NotifyJson.put(MsgKeys.msgType, iMoMoMsgTypes.REGISTER_SUCCESS);
			System.out.println("注册成功");
		} else {
			NotifyJson.put(MsgKeys.msgType, iMoMoMsgTypes.REGISTER_FAILED);// 注册失败
		}
		Notify.msgJson = NotifyJson.toJSONString();
		session.write(Notify);
	}

	/**
	 * 处理找回密码
	 * 
	 * @param session
	 * @param moMoMsg
	 */
	public void handleFindPasswd(IoSession session, iMoMoMsg moMoMsg) {
		JSONObject json = JSON.parseObject(moMoMsg.msgJson);
		String userEmail = json.getString(MsgKeys.userEmail);// 邮箱地址
		SqlModel model = new SqlModel();
		String userName = model.getUserName(userEmail, true);
		iMoMoMsg Notify = new iMoMoMsg();
		Notify.symbol = '+';
		JSONObject NotifyJson = new JSONObject();
		String newPwd = PasswordUtil.getInstance().createNewPwd();
//		System.out.println("newPwd = " + newPwd);
//		System.out.println("newPwd - MD5 = "
//				+ PasswordUtil.getInstance().toMD5(newPwd));
		if (!userName.equals("null")) {
			String sql = "update imomo_clients set userPasswd = ? where userEmail = ?";
			String[] paras = { PasswordUtil.getInstance().toMD5(newPwd),
					userEmail };
			if (model.updateDb(sql, paras)) {
				// 发送邮件...
				new SendEmailToClient(userEmail, "找回密码", "尊敬的" + userName
						+ ":\n	您好，系统为您随机生成的密码是:" + newPwd + ",登录后请尽快修改密码!");
				NotifyJson.put(MsgKeys.msgType,
						iMoMoMsgTypes.FIND_PASSWD_SUCCESS);
			} else {
				NotifyJson.put(MsgKeys.msgType,
						iMoMoMsgTypes.FIND_PASSWD_FAILED);
			}
		} else {
			System.out.println("没有该用户");
			NotifyJson.put(MsgKeys.msgType, iMoMoMsgTypes.FIND_PASSWD_FAILED);
		}
		Notify.msgJson = NotifyJson.toJSONString();
		session.write(Notify);
	}

	/**
	 * 处理重置密码
	 * 
	 * @param session
	 * @param moMoMsg
	 */
	public void handleResetPasswd(IoSession session, iMoMoMsg moMoMsg) {
		JSONObject json = JSON.parseObject(moMoMsg.msgJson);
		String userId = json.getString(MsgKeys.userId);
		SqlModel model = new SqlModel();
		String sql = "update imomo_clients set userPasswd = ? where userId = ?";
		String[] paras = { json.getString(MsgKeys.userPasswd), userId };
		iMoMoMsg Notify = new iMoMoMsg();
		Notify.symbol = '+';
		JSONObject NotifyJson = new JSONObject();
		if (model.updateDb(sql, paras)) {
			System.out.println("修改密码成功");
			NotifyJson.put(MsgKeys.msgType, iMoMoMsgTypes.RESET_PASSWD_SUCCESS);
		} else {
			System.out.println("修改密码失败");
			NotifyJson.put(MsgKeys.msgType, iMoMoMsgTypes.RESET_PASSWD_FAILED);
		}
		Notify.msgJson = NotifyJson.toJSONString();
		session.write(Notify);
	}

	/**
	 * 处理修改用户名
	 * 
	 * @param moMoMsg
	 */
	public void handleResetUserInfo(iMoMoMsg moMoMsg) {
		JSONObject json = JSON.parseObject(moMoMsg.msgJson);
		String userId = json.getString(MsgKeys.userId);
		int type = json.getIntValue(MsgKeys.msgType);
		SqlModel model = new SqlModel();
		String sql = "";
		String[] paras = new String[2];
		switch (type) {
		case iMoMoMsgTypes.RESET_USERNAME:
			sql = "update imomo_clients set userName = ? where userId = ?";
			paras[0] = json.getString(MsgKeys.userName);
			break;
		case iMoMoMsgTypes.RESET_SEX:
			sql = "update imomo_clients set userSex = ? where userId = ?";
			paras[0] = json.getString(MsgKeys.userSex);
			break;
		case iMoMoMsgTypes.RESET_BIRTHDAY:
			sql = "update imomo_clients set userBirthday = ? where userId = ?";
			paras[0] = json.getString(MsgKeys.userBirthday);
			break;
		case iMoMoMsgTypes.RESET_SIGNATUE:
			sql = "update imomo_clients set personSignature = ? where userId = ?";
			paras[0] = json.getString(MsgKeys.personSignature);
			break;
		}
		paras[1] = userId;
		if (model.updateDb(sql, paras)) {
			System.out.println("修改用信息成功");
		} else {
			System.out.println("修改用户信失败");
		}
		// 不发送通知消息
	}

	/**
	 * 处理修改头像
	 * 
	 * @param session
	 * @param moMoMsg
	 */
	public void handleResetHead(IoSession session, iMoMoMsg moMoMsg) {
		JSONObject json = JSON.parseObject(moMoMsg.msgJson);
		String userId = json.getString(MsgKeys.userId);
		String userHeadPath = StaticValues.HEAD_P_PATH + userId + ".png";
		iMoMoMsg Notify = new iMoMoMsg();
		Notify.symbol = '+';
		JSONObject NotifyJson = new JSONObject();
		try {
			FileTools.getInstance().saveMultyFile(userHeadPath,
					moMoMsg.msgBytes);
			// 修改成功
			System.out.println("修改头像成功");
			NotifyJson.put(MsgKeys.msgType, iMoMoMsgTypes.RESET_HEAD_SUCCESS);

			SqlModel model = new SqlModel();
			String[] friendList = model.getFriendIds(userId);
			iMoMoMsg resetHead = new iMoMoMsg();
			resetHead.symbol = '-';
			JSONObject resetHeadJson = new JSONObject();
			resetHeadJson.put(MsgKeys.msgType, iMoMoMsgTypes.RESET_HEAD);
			resetHeadJson.put(MsgKeys.friendId, userId);// 通知好友是谁改头像的
			resetHeadJson.put(MsgKeys.userId, userId);// 我发的
			resetHead.msgJson = resetHeadJson.toJSONString();
			resetHead.msgBytes = moMoMsg.msgBytes;
			for (String friendId : friendList) {
				// 判断是否在线,在线直接转发，不在线缓存到数据库中
				if (ManageClientSession.isContainsId(friendId)) {
					ManageClientSession.getSession(friendId).write(resetHead);
//					System.out.println("转发成功..");
				} else {
					if (!model.isTableExists("mc_" + friendId))// "mc_" + userId
						model.createCacheTable(friendId);// 创建缓存数据库
					MsgDb msgDb = MsgTranceUtil.getInstance().Trance_Net2Db(
							resetHead);
					if (model.insertCacheMsg(msgDb, friendId)) {
//						System.out.println("缓存成功");
					} else {
//						System.out.println("缓存失败");
					}
				}
			}

		} catch (Exception e) {
			// 修改失败
			NotifyJson.put(MsgKeys.msgType, iMoMoMsgTypes.RESET_HEAD_FAILED);
//			System.out.println("修改头像失败");
		}
		Notify.msgJson = NotifyJson.toJSONString();
		session.write(Notify);
	}

	/**
	 * 处理用户登录
	 * 
	 * @param session
	 * @param moMoMsg
	 */
	public void handleLogin(IoSession session, iMoMoMsg moMoMsg) {
		JSONObject json = JSON.parseObject(moMoMsg.msgJson);
		iMoMoMsg Notify = new iMoMoMsg();
		Notify.symbol = '+';
		JSONObject NotifyJson = new JSONObject();
		if (new SqlModel().checkUser(json.getString(MsgKeys.userEmail),
				json.getString(MsgKeys.userPasswd))) {
//			System.out.println("合法用户");
			// 合法用户
			NotifyJson.put(MsgKeys.msgType, iMoMoMsgTypes.LOGIN_SUCCESS);
		} else {
			// 非法用户
//			System.out.println("非法用户");
			NotifyJson.put(MsgKeys.msgType, iMoMoMsgTypes.LOGIN_FAILED);
		}
		Notify.msgJson = NotifyJson.toJSONString();
		session.write(Notify);
	}


	/**
	 * 处理用户超级登录
	 * 
	 * @param session
	 * @param moMoMsg
	 */
	public void handleLoginSuper(IoSession session, iMoMoMsg moMoMsg) {
		JSONObject json = JSON.parseObject(moMoMsg.msgJson);
		SqlModel model = new SqlModel();
		String userEmail = json.getString(MsgKeys.userEmail);
		int msgtype = json.getIntValue(MsgKeys.msgType);
		JSONObject info = model.getUserInfo(userEmail, true);
		String userId = info.getString(MsgKeys.userId);
		iMoMoMsg Notify = new iMoMoMsg();
		JSONObject NotifyJson = new JSONObject();
		if (info != null) {
			// 获得用户信息成功
			// 管理会话 、Id
			ManageIdSessions.addUserId(session.getId(), userId);
			ManageClientSession.addSession(info.getString(MsgKeys.userId),
					session);
			NotifyJson.put(MsgKeys.userId, userId);
			NotifyJson.put(MsgKeys.userName, info.getString(MsgKeys.userName));
			NotifyJson.put(MsgKeys.userSex, info.getString(MsgKeys.userSex));
			NotifyJson.put(MsgKeys.userBirthday,
					info.getString(MsgKeys.userBirthday));
			NotifyJson.put(MsgKeys.personSignature,
					info.getString(MsgKeys.personSignature));
			NotifyJson.put(MsgKeys.vitalityValue,
					info.getIntValue(MsgKeys.vitalityValue));
			if (msgtype == iMoMoMsgTypes.LOGIN_SUPER_HEAD) {
				Notify.symbol = '+';
				NotifyJson.put(MsgKeys.msgType, iMoMoMsgTypes.LOGIN_SUPER_HEAD);
				System.out.println("用户本地有头像");
			} else if (msgtype == iMoMoMsgTypes.LOGIN_SUPER_NOHEAD) {
				System.out.println("用户本地无头像");
				Notify.symbol = '-';
				NotifyJson.put(MsgKeys.msgType,
						iMoMoMsgTypes.LOGIN_SUPER_NOHEAD);
				Notify.msgBytes = FileTools.getInstance().getMultyFileBytes(
						info.getString(MsgKeys.userHeadPath));
			}
		} else {
			NotifyJson.put(MsgKeys.msgType, iMoMoMsgTypes.LOGIN_SUPER_FAILED);
		}
		Notify.msgJson = NotifyJson.toJSONString();
		session.write(Notify);
		// 判断是否有离线消息，有则转发，
		sendCacheMsg(session, userId, model);
	}

	/**
	 * 转发离线消息
	 * 
	 * @param session
	 * @param userId
	 * @param model
	 */
	private void sendCacheMsg(IoSession session, String userId, SqlModel model) {
		if (model.isTableExists("mc_" + userId)) {
			if (model.getMsgCount(userId) > 0) {
				// 说明有离线消息
				List<MsgDb> list = model.getCacheMsgs(userId);
				for (MsgDb msgDb : list) {
					iMoMoMsg moMsg = MsgTranceUtil.getInstance().Trance_Db2Net(
							msgDb);

					session.write(moMsg);
				}
				// 清空数据库离线文件
				model.clearMsgCache(userId);
			}
		}
	}

	/**
	 * 处理用户位置消息,记录用户的位置
	 * 
	 * @param moMoMsg
	 */
	public void handleLocation(iMoMoMsg moMoMsg) {
		JSONObject loc_json = JSON.parseObject(moMoMsg.msgJson);
		String userId = loc_json.getString(MsgKeys.userId);
		String province = loc_json.getString(MsgKeys.loc_province);
		myLocationBean locationBean = new myLocationBean();
		locationBean.province = province;
		locationBean.longitude = loc_json.getDoubleValue(MsgKeys.loc_Longitude);
		locationBean.latitude = loc_json.getDoubleValue(MsgKeys.loc_Latitude);
		System.out.println(locationBean.toString());
		if (ManageLocMap.isContainsProvince(province)) {
			ManageLocMap.getAProvinceLoc(province).addLocation(userId,
					locationBean);// 添加到所在省
		} else {
			ManageAProvinceLoc AProvinceLoc = new ManageAProvinceLoc(province);
			AProvinceLoc.addLocation(userId, locationBean);
			ManageLocMap.addAProvinceLoc(province, AProvinceLoc);
		}
	}

	/**
	 * 处理请求得到周围陌生人位置
	 * 
	 * @param moMoMsg
	 */
	public void handleGetStrangersLoc(iMoMoMsg moMoMsg) {
		JSONObject json = JSON.parseObject(moMoMsg.msgJson);
		int type = json.getIntValue(MsgKeys.msgType);
		String userId = json.getString(MsgKeys.userId);
		String province = json.getString(MsgKeys.loc_province);
		double longitude = json.getDoubleValue(MsgKeys.loc_Longitude);
		double latitude = json.getDoubleValue(MsgKeys.loc_Latitude);
		// 计算距离，然后获取陌生人信息（姓名，Id，位置）
		iMoMoMsg moMsg = new iMoMoMsg();
		moMsg.symbol = '+';
		JSONObject jsonSend = new JSONObject();

		if (type == iMoMoMsgTypes.GET_STRANGERS_LOC_ONEKM) {
			// 一公里内
			// /** 测试... */
			// List<StrangerBean> list = new ManageAProvinceLoc("sd")
			// .getDisStrangers(true, userId, 0);
			// JSONArray jsonArray = (JSONArray) JSONArray.toJSON(list);
			// jsonSend.put(MsgKeys.msgType,
			// iMoMoMsgTypes.STRANGERS_LIST_ONEKM);
			// jsonSend.put(MsgKeys.strangerList, jsonArray);

			if (ManageLocMap.isContainsProvince(province)) {
				// 当前省有在线用户
				ManageAProvinceLoc aProvinceLoc = ManageLocMap
						.getAProvinceLoc(province);
				if (aProvinceLoc.getCount() > 1) {
					List<StrangerBean> list = aProvinceLoc.getDisStrangers(
							true, userId, 0);
					JSONArray jsonArray = (JSONArray) JSONArray.toJSON(list);
					jsonSend.put(MsgKeys.msgType,
							iMoMoMsgTypes.STRANGERS_LIST_ONEKM);
					jsonSend.put(MsgKeys.strangerList, jsonArray);
				} else {
					jsonSend.put(MsgKeys.msgType, iMoMoMsgTypes.NO_STRANGERS);
				}
			} else {
				jsonSend.put(MsgKeys.msgType, iMoMoMsgTypes.NO_STRANGERS);
			}

		} else if (type == iMoMoMsgTypes.GET_STRANGERS_LOC_MORE) {
			// 大于一公里
			int distRange = json.getIntValue(MsgKeys.distRange);

			// /** 测试... */
			// List<StrangerBean> list = new ManageAProvinceLoc("sd")
			// .getDisStrangers(false, userId, distRange);
			// JSONArray jsonArray = (JSONArray) JSONArray.toJSON(list);
			// jsonSend.put(MsgKeys.msgType, iMoMoMsgTypes.STRANGERS_LIST_MORE);
			// jsonSend.put(MsgKeys.strangerList, jsonArray);

			// System.out.println("大于一公里 ： " + jsonSend.toJSONString());
			if (ManageLocMap.isContainsProvince(province)) {
				// 当前省有在线用户
				ManageAProvinceLoc aProvinceLoc = ManageLocMap
						.getAProvinceLoc(province);
				if (aProvinceLoc.getCount() > 1) {
					List<StrangerBean> list = aProvinceLoc.getDisStrangers(
							false, userId, distRange);
					JSONArray jsonArray = (JSONArray) JSONArray.toJSON(list);
					jsonSend.put(MsgKeys.msgType,
							iMoMoMsgTypes.STRANGERS_LIST_MORE);
					jsonSend.put(MsgKeys.strangerList, jsonArray);
				} else {
					jsonSend.put(MsgKeys.msgType, iMoMoMsgTypes.NO_STRANGERS);
				}
			} else {
				jsonSend.put(MsgKeys.msgType, iMoMoMsgTypes.NO_STRANGERS);
			}
		}

		moMsg.msgJson = jsonSend.toJSONString();
		ManageClientSession.getSession(userId).write(moMsg);
	}

	/**
	 * 处理请求得到一个陌生人的具体信息,(区分本地有头像 无头像)
	 * 
	 * @param moMoMsg
	 */
	public void handleGetFriendInfo(IoSession session, iMoMoMsg moMoMsg) {
		JSONObject json = JSON.parseObject(moMoMsg.msgJson);
		int type = json.getInteger(MsgKeys.msgType);
		String userId = json.getString(MsgKeys.userId);
		String friendId = json.getString(MsgKeys.friendId);
		SqlModel model = new SqlModel();
		JSONObject info = model.getUserInfo(friendId, false);
		iMoMoMsg moMsg = new iMoMoMsg();
		if (type == iMoMoMsgTypes.GETA_FRIEND_INFO_HEAD) {
			moMsg.symbol = '+';
			info.put(MsgKeys.msgType, iMoMoMsgTypes.GETA_FRIEND_INFO_HEAD);
		} else if (type == iMoMoMsgTypes.GETA_FRIEND_INFO_NOHEAD) {
			String headPath = info.getString(MsgKeys.userHeadPath);
			moMsg.msgBytes = FileTools.getInstance()
					.getMultyFileBytes(headPath);
			moMsg.symbol = '-';
			info.put(MsgKeys.msgType, iMoMoMsgTypes.GETA_FRIEND_INFO_NOHEAD);

		} else if (type == iMoMoMsgTypes.GET_STRANGER_INFO) {
			String headPath = info.getString(MsgKeys.userHeadPath);
			moMsg.msgBytes = FileTools.getInstance()
					.getMultyFileBytes(headPath);
			moMsg.symbol = '-';
			info.put(MsgKeys.msgType, iMoMoMsgTypes.GET_STRANGER_INFO);
		}
		info.remove(MsgKeys.userHeadPath);
		moMsg.msgJson = info.toJSONString();
		session.write(moMsg);
		// ManageClientSession.getSession(userId).write(moMsg);
	}

	/**
	 * 处理添加好友
	 * 
	 * @param session
	 * @param moMoMsg
	 */
	public void handleAddFriend(IoSession session, iMoMoMsg moMoMsg) {
//		System.out.println("添加好友---");
		JSONObject json = JSON.parseObject(moMoMsg.msgJson);
		String userId = json.getString(MsgKeys.userId);
		String friendId = json.getString(MsgKeys.friendId);
		iMoMoMsg moMsg = new iMoMoMsg();
		moMsg.symbol = '+';
		JSONObject notify = new JSONObject();
		SqlModel model = new SqlModel();
		if (model.addFriend(userId, friendId)) {
			// 添加好友成功
			notify.put(MsgKeys.msgType, iMoMoMsgTypes.ADD_FRIEND_SUCCESS);

			// 自动添加到对方好友列表中，然后发送提示信息（在线才发，不在线转离线消息）
			model.addFriend(friendId, userId);
			// 判断是否在线,在线直接转发，不在线缓存到数据库中
			// 1.得到添加好友用户的信息
			JSONObject info = model.getUserInfo(userId, false);
			iMoMoMsg addMsg = new iMoMoMsg();
			addMsg.symbol = '-';
			String headPath = info.getString(MsgKeys.userHeadPath);
			addMsg.msgBytes = FileTools.getInstance().getMultyFileBytes(
					headPath);
			info.put(MsgKeys.msgType, iMoMoMsgTypes.ADD_FRIEND);
			info.remove(MsgKeys.userHeadPath);
			// JSONObject notyadd = new JSONObject();
			// notyadd.put(MsgKeys.msgType, iMoMoMsgTypes.ADD_FRIEND);
			info.put(MsgKeys.friendId, userId);
			String friendName = model.getUserName(userId, false);
			info.put(MsgKeys.friendName, friendName);

			addMsg.msgJson = info.toJSONString();
			if (ManageClientSession.isContainsId(friendId)) {
				ManageClientSession.getSession(friendId).write(addMsg);
				System.out.println("转发成功..");
			} else {
				if (!model.isTableExists("mc_" + friendId))// "mc_" + userId
					model.createCacheTable(friendId);// 创建缓存数据库
				MsgDb msgDb = MsgTranceUtil.getInstance().Trance_Net2Db(addMsg);
				if (model.insertCacheMsg(msgDb, friendId)) {
//					System.out.println("缓存成功");
				} else {
//					System.out.println("缓存失败");
				}
			}

		} else {
			notify.put(MsgKeys.msgType, iMoMoMsgTypes.ADD_FRIEND_FAILED);
		}
		moMsg.msgJson = notify.toJSONString();
		session.write(moMsg);
	}

	/**
	 * 处理删除好友
	 * 
	 * @param session
	 * @param moMoMsg
	 */
	public void handleDeleteFriend(IoSession session, iMoMoMsg moMoMsg) {
		JSONObject json = JSON.parseObject(moMoMsg.msgJson);
		String userId = json.getString(MsgKeys.userId);
		String friendId = json.getString(MsgKeys.friendId);
		SqlModel model = new SqlModel();
		model.deleteFriend(userId, friendId);
		model.deleteFriend(friendId, userId);// 解除双方关系
	}

	/**
	 * 处理获取好友Id列表
	 * 
	 * @param session
	 * @param moMoMsg
	 */
	public void handleGetFriendIdList(IoSession session, iMoMoMsg moMoMsg) {
		JSONObject json = JSON.parseObject(moMoMsg.msgJson);
		String userId = json.getString(MsgKeys.userId);
		SqlModel model = new SqlModel();
		String[] ids = model.getFriendIds(userId);
		String IdList = "";
		if (ids.length > 0) {
			for (String string : ids) {
				IdList += string + ",";
			}
//			System.out.println(userId + "的好友列表：" + IdList);
		} else {
			IdList = "none";// 无好友
//			System.out.println(userId + "无好友");
		}
		iMoMoMsg moMsg = new iMoMoMsg();
		moMsg.symbol = '+';
		JSONObject jsonSend = new JSONObject();
		jsonSend.put(MsgKeys.msgType, iMoMoMsgTypes.FRIEND_ID_LIST);
		jsonSend.put(MsgKeys.friendIdList, IdList);
		moMsg.msgJson = jsonSend.toJSONString();
		session.write(moMsg);
	}

	/**
	 * 处理用户反馈信息
	 * 
	 * @param moMoMsg
	 */
	public void handleReback(iMoMoMsg moMoMsg) {
		JSONObject json = JSON.parseObject(moMoMsg.msgJson);
		String userId = json.getString(MsgKeys.userId);
		String reback = json.getString(MsgKeys.msgCotent);
		FileTools.getInstance().saveReback(userId, reback);
	}

	/**
	 * 
	 * @param moMoMsg
	 */
	public void handleVitality(iMoMoMsg moMoMsg) {
		JSONObject json = JSON.parseObject(moMoMsg.msgJson);
		int type = json.getIntValue(MsgKeys.msgType);
		String userId = json.getString(MsgKeys.userId);
		SqlModel model = new SqlModel();
		if(type == iMoMoMsgTypes.PASS_GAME){
			model.UpdateVitality(userId, -1);
		} else if(type == iMoMoMsgTypes.SIGN){
			model.UpdateVitality(userId, 1);
		}
	}

	/**
	 * 创建多人聊天
	 * 
	 * @param session
	 * @param moMoMsg
	 */
	public void handleCreateGroup(IoSession session, iMoMoMsg moMoMsg) {
		JSONObject json = JSON.parseObject(moMoMsg.msgJson);
		String userId = json.getString(MsgKeys.userId);
		String groupName = json.getString(MsgKeys.groupName);
		String groupTopic = json.getString(MsgKeys.groupTopic);
		String province = json.getString(MsgKeys.loc_province);
		SqlModel model = new SqlModel();

		GroupInfo gInfo = new GroupInfo();
		gInfo.creator = model.getUserName(userId, false);
		gInfo.createTime = System.currentTimeMillis();
		gInfo.groupId = "group_" + userId + "_" + gInfo.createTime;
		gInfo.groupName = groupName;
		gInfo.groupTopic = groupTopic;
		
		String groupIconPath = StaticValues.HEAD_P_PATH + gInfo.groupId
				+ ".png";
		FileTools.getInstance().saveMultyFile(groupIconPath, moMoMsg.msgBytes);

		// 返回是否创建成功消息
		if (ManageGroups.addGroup(gInfo.groupId, gInfo)) {
			model.UpdateVitality(userId, -1);
			ManageGroups.getGroup(gInfo.groupId).joinGroup(userId);// 把创建用户添加到群组
			
			if(!ManageGroups.isHaveGroup){
				ManageGroups.keepWatching();//开启守护线程
			}
			
			iMoMoMsg moMsg = new iMoMoMsg();
			moMsg.symbol = '-';
			JSONObject jsonSend = new JSONObject();
			jsonSend.put(MsgKeys.msgType, iMoMoMsgTypes.CREATE_GROUP_SUCCESS);
			jsonSend.put(MsgKeys.groupId, gInfo.groupId);
			jsonSend.put(MsgKeys.groupName, gInfo.groupName);
			jsonSend.put(MsgKeys.groupTopic, gInfo.groupTopic);
			moMsg.msgBytes = FileTools.getInstance().getMultyFileBytes(
					groupIconPath);
			moMsg.msgJson = jsonSend.toJSONString();
			session.write(moMsg);

			if (ManageLocMap.isContainsProvince(province)) {
				// 当前省有在线用户
				ManageAProvinceLoc aProvinceLoc = ManageLocMap
						.getAProvinceLoc(province);
				if (aProvinceLoc.getCount() > 1) {
					List<StrangerBean> list = aProvinceLoc.getDisStrangers(
							true, userId, 0);
					if (list.size() > 0) {
						System.out.println("周围的人 : ");
						iMoMoMsg inviteMsg = new iMoMoMsg();
						inviteMsg.symbol = '-';
						JSONObject inviteJson = new JSONObject();
						inviteJson.put(MsgKeys.msgType,
								iMoMoMsgTypes.INVITE_TO_GROUP);
						inviteJson.put(MsgKeys.groupCreator, gInfo.creator);
						inviteJson.put(MsgKeys.groupId, gInfo.groupId);
						inviteJson.put(MsgKeys.groupName, gInfo.groupName);
						inviteJson.put(MsgKeys.groupTopic, gInfo.groupTopic);
						inviteMsg.msgBytes = FileTools.getInstance()
								.getMultyFileBytes(groupIconPath);
						inviteMsg.msgJson = inviteJson.toJSONString();
						for (StrangerBean bean : list) {
							System.out.println("Id = " + bean.strangerId);
							IoSession ivSession = ManageClientSession
									.getSession(bean.strangerId);
							ivSession.write(inviteMsg);
						}
					}
				}
			}
		} else {
			iMoMoMsg moMsg = new iMoMoMsg();
			moMsg.symbol = '+';
			JSONObject jsonSend = new JSONObject();
			jsonSend.put(MsgKeys.msgType, iMoMoMsgTypes.CREATE_GROUP_FAILED);
			moMsg.msgJson = jsonSend.toJSONString();
			session.write(moMsg);
		}

	}

	/**
	 * 同意加入群组
	 * 
	 * @param moMoMsg
	 */
	public void handleRecieveToGroup(iMoMoMsg moMoMsg) {
		JSONObject json = JSON.parseObject(moMoMsg.msgJson);
		String userId = json.getString(MsgKeys.userId);
		String groupId = json.getString(MsgKeys.groupId);
		GroupInfo group = ManageGroups.getGroup(groupId);
		group.joinGroup(userId);
	}

	/**
	 * 处理发送消息(在线则转发，不在线则缓存)
	 * 
	 * @param session
	 * @param moMoMsg
	 */
	public void handleChatMsg(iMoMoMsg moMoMsg) {
//		System.out.println("处理聊天消息");
		JSONObject json = JSON.parseObject(moMoMsg.msgJson);
		int isGroupMsg = 0;
		if (json.containsKey(MsgKeys.isGroupMsg)) {
			isGroupMsg = json.getInteger(MsgKeys.isGroupMsg);
			System.out.println("isGroupMsg = " + isGroupMsg);
		}
		// 判断是否是群组信息，不是则转发给单独用户
		if (isGroupMsg == 0) {
			String getterId = json.getString(MsgKeys.friendId);
			// 判断是否在线,在线直接转发，不在线缓存到数据库中
			sendMsgToUser(moMoMsg, getterId);
		} else if (isGroupMsg == iMoMoMsgTypes.GROUP_MSG) {
			String userId = json.getString(MsgKeys.userId);// 发送者
			String groupId = json.getString(MsgKeys.friendId);// 接收好友的Id
			String sendTime = json.getString(MsgKeys.sendTime);// 发送时间
			System.out.println("groupId = " + groupId);
			if (ManageGroups.isContainsGroupId(groupId)) {
				Map<String, String> membersMap = ManageGroups.getGroup(groupId).membersMap;
				for (String key : membersMap.keySet()) {
					String getterId = membersMap.get(key);
					if (!userId.equals(getterId)) {
						json.put(MsgKeys.userId, groupId);// 转发群组消息，发送者Id要换成群组Id(客户端把群组当成一个好友处理)
						json.put(MsgKeys.sendTime, userId + "\n" + sendTime);//在时间显示区显示发送者Id，不然其他用户不知道到底是谁发送的
						moMoMsg.msgJson = json.toJSONString();
						sendMsgToUser(moMoMsg, getterId);
					}
				}
			} else {
//				System.out.println("群组不存在..");
			}
		}
	}

	/**
	 * 发送信息给单个用户
	 * 
	 * @param moMoMsg
	 * @param getterId
	 */
	private void sendMsgToUser(iMoMoMsg moMoMsg, String getterId) {
		if (ManageClientSession.isContainsId(getterId)) {
			ManageClientSession.getSession(getterId).write(moMoMsg);
//			System.out.println("转发成功..");
		} else {
			SqlModel model = new SqlModel();
			if (!model.isTableExists("mc_" + getterId))// "mc_" + userId
				model.createCacheTable(getterId);// 创建缓存数据库
			MsgDb msgDb = MsgTranceUtil.getInstance().Trance_Net2Db(moMoMsg);
			if (model.insertCacheMsg(msgDb, getterId)) {
//				System.out.println("缓存成功");
			} else {
//				System.out.println("缓存失败");
			}
		}
	}

	// 获取当前时间
	public String getNowTime() {
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		String date = format.format(new Date());
		return date;
	}

}
