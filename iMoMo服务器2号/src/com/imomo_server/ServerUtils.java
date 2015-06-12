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
 * ������������
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
	 * ����ע��
	 * 
	 * @param session
	 *            �Ự
	 * @param moMsg
	 *            ��Ϣ��
	 */
	public void handleRegister(IoSession session, iMoMoMsg moMoMsg) {
		JSONObject json = JSON.parseObject(moMoMsg.msgJson);
		SqlModel model = new SqlModel();
		String userEmail = json.getString(MsgKeys.userEmail);
		String userId = model.allocateId();// ����Id
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
			System.out.println("ע��ɹ�");
		} else {
			NotifyJson.put(MsgKeys.msgType, iMoMoMsgTypes.REGISTER_FAILED);// ע��ʧ��
		}
		Notify.msgJson = NotifyJson.toJSONString();
		session.write(Notify);
	}

	/**
	 * �����һ�����
	 * 
	 * @param session
	 * @param moMoMsg
	 */
	public void handleFindPasswd(IoSession session, iMoMoMsg moMoMsg) {
		JSONObject json = JSON.parseObject(moMoMsg.msgJson);
		String userEmail = json.getString(MsgKeys.userEmail);// �����ַ
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
				// �����ʼ�...
				new SendEmailToClient(userEmail, "�һ�����", "�𾴵�" + userName
						+ ":\n	���ã�ϵͳΪ��������ɵ�������:" + newPwd + ",��¼���뾡���޸�����!");
				NotifyJson.put(MsgKeys.msgType,
						iMoMoMsgTypes.FIND_PASSWD_SUCCESS);
			} else {
				NotifyJson.put(MsgKeys.msgType,
						iMoMoMsgTypes.FIND_PASSWD_FAILED);
			}
		} else {
			System.out.println("û�и��û�");
			NotifyJson.put(MsgKeys.msgType, iMoMoMsgTypes.FIND_PASSWD_FAILED);
		}
		Notify.msgJson = NotifyJson.toJSONString();
		session.write(Notify);
	}

	/**
	 * ������������
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
			System.out.println("�޸�����ɹ�");
			NotifyJson.put(MsgKeys.msgType, iMoMoMsgTypes.RESET_PASSWD_SUCCESS);
		} else {
			System.out.println("�޸�����ʧ��");
			NotifyJson.put(MsgKeys.msgType, iMoMoMsgTypes.RESET_PASSWD_FAILED);
		}
		Notify.msgJson = NotifyJson.toJSONString();
		session.write(Notify);
	}

	/**
	 * �����޸��û���
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
			System.out.println("�޸�����Ϣ�ɹ�");
		} else {
			System.out.println("�޸��û���ʧ��");
		}
		// ������֪ͨ��Ϣ
	}

	/**
	 * �����޸�ͷ��
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
			// �޸ĳɹ�
			System.out.println("�޸�ͷ��ɹ�");
			NotifyJson.put(MsgKeys.msgType, iMoMoMsgTypes.RESET_HEAD_SUCCESS);

			SqlModel model = new SqlModel();
			String[] friendList = model.getFriendIds(userId);
			iMoMoMsg resetHead = new iMoMoMsg();
			resetHead.symbol = '-';
			JSONObject resetHeadJson = new JSONObject();
			resetHeadJson.put(MsgKeys.msgType, iMoMoMsgTypes.RESET_HEAD);
			resetHeadJson.put(MsgKeys.friendId, userId);// ֪ͨ������˭��ͷ���
			resetHeadJson.put(MsgKeys.userId, userId);// �ҷ���
			resetHead.msgJson = resetHeadJson.toJSONString();
			resetHead.msgBytes = moMoMsg.msgBytes;
			for (String friendId : friendList) {
				// �ж��Ƿ�����,����ֱ��ת���������߻��浽���ݿ���
				if (ManageClientSession.isContainsId(friendId)) {
					ManageClientSession.getSession(friendId).write(resetHead);
//					System.out.println("ת���ɹ�..");
				} else {
					if (!model.isTableExists("mc_" + friendId))// "mc_" + userId
						model.createCacheTable(friendId);// �����������ݿ�
					MsgDb msgDb = MsgTranceUtil.getInstance().Trance_Net2Db(
							resetHead);
					if (model.insertCacheMsg(msgDb, friendId)) {
//						System.out.println("����ɹ�");
					} else {
//						System.out.println("����ʧ��");
					}
				}
			}

		} catch (Exception e) {
			// �޸�ʧ��
			NotifyJson.put(MsgKeys.msgType, iMoMoMsgTypes.RESET_HEAD_FAILED);
//			System.out.println("�޸�ͷ��ʧ��");
		}
		Notify.msgJson = NotifyJson.toJSONString();
		session.write(Notify);
	}

	/**
	 * �����û���¼
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
//			System.out.println("�Ϸ��û�");
			// �Ϸ��û�
			NotifyJson.put(MsgKeys.msgType, iMoMoMsgTypes.LOGIN_SUCCESS);
		} else {
			// �Ƿ��û�
//			System.out.println("�Ƿ��û�");
			NotifyJson.put(MsgKeys.msgType, iMoMoMsgTypes.LOGIN_FAILED);
		}
		Notify.msgJson = NotifyJson.toJSONString();
		session.write(Notify);
	}


	/**
	 * �����û�������¼
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
			// ����û���Ϣ�ɹ�
			// ����Ự ��Id
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
				System.out.println("�û�������ͷ��");
			} else if (msgtype == iMoMoMsgTypes.LOGIN_SUPER_NOHEAD) {
				System.out.println("�û�������ͷ��");
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
		// �ж��Ƿ���������Ϣ������ת����
		sendCacheMsg(session, userId, model);
	}

	/**
	 * ת��������Ϣ
	 * 
	 * @param session
	 * @param userId
	 * @param model
	 */
	private void sendCacheMsg(IoSession session, String userId, SqlModel model) {
		if (model.isTableExists("mc_" + userId)) {
			if (model.getMsgCount(userId) > 0) {
				// ˵����������Ϣ
				List<MsgDb> list = model.getCacheMsgs(userId);
				for (MsgDb msgDb : list) {
					iMoMoMsg moMsg = MsgTranceUtil.getInstance().Trance_Db2Net(
							msgDb);

					session.write(moMsg);
				}
				// ������ݿ������ļ�
				model.clearMsgCache(userId);
			}
		}
	}

	/**
	 * �����û�λ����Ϣ,��¼�û���λ��
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
					locationBean);// ��ӵ�����ʡ
		} else {
			ManageAProvinceLoc AProvinceLoc = new ManageAProvinceLoc(province);
			AProvinceLoc.addLocation(userId, locationBean);
			ManageLocMap.addAProvinceLoc(province, AProvinceLoc);
		}
	}

	/**
	 * ��������õ���Χİ����λ��
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
		// ������룬Ȼ���ȡİ������Ϣ��������Id��λ�ã�
		iMoMoMsg moMsg = new iMoMoMsg();
		moMsg.symbol = '+';
		JSONObject jsonSend = new JSONObject();

		if (type == iMoMoMsgTypes.GET_STRANGERS_LOC_ONEKM) {
			// һ������
			// /** ����... */
			// List<StrangerBean> list = new ManageAProvinceLoc("sd")
			// .getDisStrangers(true, userId, 0);
			// JSONArray jsonArray = (JSONArray) JSONArray.toJSON(list);
			// jsonSend.put(MsgKeys.msgType,
			// iMoMoMsgTypes.STRANGERS_LIST_ONEKM);
			// jsonSend.put(MsgKeys.strangerList, jsonArray);

			if (ManageLocMap.isContainsProvince(province)) {
				// ��ǰʡ�������û�
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
			// ����һ����
			int distRange = json.getIntValue(MsgKeys.distRange);

			// /** ����... */
			// List<StrangerBean> list = new ManageAProvinceLoc("sd")
			// .getDisStrangers(false, userId, distRange);
			// JSONArray jsonArray = (JSONArray) JSONArray.toJSON(list);
			// jsonSend.put(MsgKeys.msgType, iMoMoMsgTypes.STRANGERS_LIST_MORE);
			// jsonSend.put(MsgKeys.strangerList, jsonArray);

			// System.out.println("����һ���� �� " + jsonSend.toJSONString());
			if (ManageLocMap.isContainsProvince(province)) {
				// ��ǰʡ�������û�
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
	 * ��������õ�һ��İ���˵ľ�����Ϣ,(���ֱ�����ͷ�� ��ͷ��)
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
	 * ������Ӻ���
	 * 
	 * @param session
	 * @param moMoMsg
	 */
	public void handleAddFriend(IoSession session, iMoMoMsg moMoMsg) {
//		System.out.println("��Ӻ���---");
		JSONObject json = JSON.parseObject(moMoMsg.msgJson);
		String userId = json.getString(MsgKeys.userId);
		String friendId = json.getString(MsgKeys.friendId);
		iMoMoMsg moMsg = new iMoMoMsg();
		moMsg.symbol = '+';
		JSONObject notify = new JSONObject();
		SqlModel model = new SqlModel();
		if (model.addFriend(userId, friendId)) {
			// ��Ӻ��ѳɹ�
			notify.put(MsgKeys.msgType, iMoMoMsgTypes.ADD_FRIEND_SUCCESS);

			// �Զ���ӵ��Է������б��У�Ȼ������ʾ��Ϣ�����߲ŷ���������ת������Ϣ��
			model.addFriend(friendId, userId);
			// �ж��Ƿ�����,����ֱ��ת���������߻��浽���ݿ���
			// 1.�õ���Ӻ����û�����Ϣ
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
				System.out.println("ת���ɹ�..");
			} else {
				if (!model.isTableExists("mc_" + friendId))// "mc_" + userId
					model.createCacheTable(friendId);// �����������ݿ�
				MsgDb msgDb = MsgTranceUtil.getInstance().Trance_Net2Db(addMsg);
				if (model.insertCacheMsg(msgDb, friendId)) {
//					System.out.println("����ɹ�");
				} else {
//					System.out.println("����ʧ��");
				}
			}

		} else {
			notify.put(MsgKeys.msgType, iMoMoMsgTypes.ADD_FRIEND_FAILED);
		}
		moMsg.msgJson = notify.toJSONString();
		session.write(moMsg);
	}

	/**
	 * ����ɾ������
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
		model.deleteFriend(friendId, userId);// ���˫����ϵ
	}

	/**
	 * �����ȡ����Id�б�
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
//			System.out.println(userId + "�ĺ����б�" + IdList);
		} else {
			IdList = "none";// �޺���
//			System.out.println(userId + "�޺���");
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
	 * �����û�������Ϣ
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
	 * ������������
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

		// �����Ƿ񴴽��ɹ���Ϣ
		if (ManageGroups.addGroup(gInfo.groupId, gInfo)) {
			model.UpdateVitality(userId, -1);
			ManageGroups.getGroup(gInfo.groupId).joinGroup(userId);// �Ѵ����û���ӵ�Ⱥ��
			
			if(!ManageGroups.isHaveGroup){
				ManageGroups.keepWatching();//�����ػ��߳�
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
				// ��ǰʡ�������û�
				ManageAProvinceLoc aProvinceLoc = ManageLocMap
						.getAProvinceLoc(province);
				if (aProvinceLoc.getCount() > 1) {
					List<StrangerBean> list = aProvinceLoc.getDisStrangers(
							true, userId, 0);
					if (list.size() > 0) {
						System.out.println("��Χ���� : ");
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
	 * ͬ�����Ⱥ��
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
	 * ��������Ϣ(������ת�����������򻺴�)
	 * 
	 * @param session
	 * @param moMoMsg
	 */
	public void handleChatMsg(iMoMoMsg moMoMsg) {
//		System.out.println("����������Ϣ");
		JSONObject json = JSON.parseObject(moMoMsg.msgJson);
		int isGroupMsg = 0;
		if (json.containsKey(MsgKeys.isGroupMsg)) {
			isGroupMsg = json.getInteger(MsgKeys.isGroupMsg);
			System.out.println("isGroupMsg = " + isGroupMsg);
		}
		// �ж��Ƿ���Ⱥ����Ϣ��������ת���������û�
		if (isGroupMsg == 0) {
			String getterId = json.getString(MsgKeys.friendId);
			// �ж��Ƿ�����,����ֱ��ת���������߻��浽���ݿ���
			sendMsgToUser(moMoMsg, getterId);
		} else if (isGroupMsg == iMoMoMsgTypes.GROUP_MSG) {
			String userId = json.getString(MsgKeys.userId);// ������
			String groupId = json.getString(MsgKeys.friendId);// ���պ��ѵ�Id
			String sendTime = json.getString(MsgKeys.sendTime);// ����ʱ��
			System.out.println("groupId = " + groupId);
			if (ManageGroups.isContainsGroupId(groupId)) {
				Map<String, String> membersMap = ManageGroups.getGroup(groupId).membersMap;
				for (String key : membersMap.keySet()) {
					String getterId = membersMap.get(key);
					if (!userId.equals(getterId)) {
						json.put(MsgKeys.userId, groupId);// ת��Ⱥ����Ϣ��������IdҪ����Ⱥ��Id(�ͻ��˰�Ⱥ�鵱��һ�����Ѵ���)
						json.put(MsgKeys.sendTime, userId + "\n" + sendTime);//��ʱ����ʾ����ʾ������Id����Ȼ�����û���֪��������˭���͵�
						moMoMsg.msgJson = json.toJSONString();
						sendMsgToUser(moMoMsg, getterId);
					}
				}
			} else {
//				System.out.println("Ⱥ�鲻����..");
			}
		}
	}

	/**
	 * ������Ϣ�������û�
	 * 
	 * @param moMoMsg
	 * @param getterId
	 */
	private void sendMsgToUser(iMoMoMsg moMoMsg, String getterId) {
		if (ManageClientSession.isContainsId(getterId)) {
			ManageClientSession.getSession(getterId).write(moMoMsg);
//			System.out.println("ת���ɹ�..");
		} else {
			SqlModel model = new SqlModel();
			if (!model.isTableExists("mc_" + getterId))// "mc_" + userId
				model.createCacheTable(getterId);// �����������ݿ�
			MsgDb msgDb = MsgTranceUtil.getInstance().Trance_Net2Db(moMoMsg);
			if (model.insertCacheMsg(msgDb, getterId)) {
//				System.out.println("����ɹ�");
			} else {
//				System.out.println("����ʧ��");
			}
		}
	}

	// ��ȡ��ǰʱ��
	public String getNowTime() {
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		String date = format.format(new Date());
		return date;
	}

}
