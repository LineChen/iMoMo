package com.imomo_server;

import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Date;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.imomo_msg.MsgKeys;
import com.imomo_msg.iMoMoMsg;
import com.imomo_msg.iMoMoMsgTypes;
import com.managers.ManageClientSession;
import com.managers.ManageIdSessions;
import com.managers.ManageLocMap;

public class iMomoServerHandler extends IoHandlerAdapter {

	private IoSession session;

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		super.exceptionCaught(session, cause);
		// cause.printStackTrace();
	}

	@Override
	public void inputClosed(IoSession session) throws Exception {
		super.inputClosed(session);
	}

	public void messageReceived(IoSession session, Object message)
			throws Exception {
		super.messageReceived(session, message);
		this.session = session;
		iMoMoMsg moMoMsg = (iMoMoMsg) message;
		JSONObject msgJson = JSON.parseObject(moMoMsg.msgJson);
//		System.out.println("���յ���Ϣ");
		// ManageClientSession.addSession("9091", session);
//		System.out.println("����������:" + moMoMsg.toString());
		if (moMoMsg.symbol == '+') {
			switch (msgJson.getInteger(MsgKeys.msgType)) {
			case iMoMoMsgTypes.FIND_PASSWD:
				// �һ�����:
				ServerUtils.getInstance().handleFindPasswd(session, moMoMsg);
				break;

			// ----------------�޸ĸ�����Ϣ--------------------------------//
			case iMoMoMsgTypes.RESET_PASSWD:
				// ��������:
				ServerUtils.getInstance().handleResetPasswd(session, moMoMsg);
				break;

			case iMoMoMsgTypes.RESET_USERNAME:
				// �޸��û���:
				ServerUtils.getInstance().handleResetUserInfo(moMoMsg);
				break;
			case iMoMoMsgTypes.RESET_SEX:
				// �޸��Ա�:
				ServerUtils.getInstance().handleResetUserInfo(moMoMsg);
				break;
			case iMoMoMsgTypes.RESET_BIRTHDAY:
				// �޸�����:
				ServerUtils.getInstance().handleResetUserInfo(moMoMsg);
				break;
			case iMoMoMsgTypes.RESET_SIGNATUE:
				// �޸ĸ���ǩ��:
				ServerUtils.getInstance().handleResetUserInfo(moMoMsg);
				break;
			// ----------------�޸ĸ�����Ϣ--------------------------------//

			// ------------------��¼---------------------------//
			case iMoMoMsgTypes.LOGIN:
				// �û���¼:
				ServerUtils.getInstance().handleLogin(session, moMoMsg);
				break;
			case iMoMoMsgTypes.LOGIN_SUPER_HEAD:
				// �û�������¼��ͷ��:
				ServerUtils.getInstance().handleLoginSuper(session, moMoMsg);
				break;
			case iMoMoMsgTypes.LOGIN_SUPER_NOHEAD:
				// �û�������¼��ͷ��:
				ServerUtils.getInstance().handleLoginSuper(session, moMoMsg);
				break;
			// ------------------��¼---------------------------//

			case iMoMoMsgTypes.CHATING_TEXT_MSG:
				// �ı���Ϣ:
				ServerUtils.getInstance().handleChatMsg(moMoMsg);
				break;

			// ------------------λ����ط���-----------------------//
			case iMoMoMsgTypes.LOCATION:
				// �û�����λ����Ϣ
				ServerUtils.getInstance().handleLocation(moMoMsg);
				break;
			case iMoMoMsgTypes.GET_STRANGERS_LOC_ONEKM:
				// ����õ���Χһ����İ����λ��
				ServerUtils.getInstance().handleGetStrangersLoc(moMoMsg);
				break;
				
			case iMoMoMsgTypes.GET_STRANGERS_LOC_MORE:
				// ����õ���Χ����һ������İ����λ��
				ServerUtils.getInstance().handleGetStrangersLoc(moMoMsg);
				break;
				
			case iMoMoMsgTypes.GET_STRANGER_INFO:
				// ����õ���Χ����һ������İ����λ��
				ServerUtils.getInstance().handleGetFriendInfo(session, moMoMsg);
				break;
			// ------------------λ����ط���-----------------------//
				
				
				
			case iMoMoMsgTypes.GETA_FRIEND_INFO_HEAD:
				// ����õ�һ��İ���˵ľ�����Ϣ,���÷�ͷ��
				ServerUtils.getInstance().handleGetFriendInfo(session, moMoMsg);
				break;
			case iMoMoMsgTypes.GETA_FRIEND_INFO_NOHEAD:
				// ����õ�һ��İ���˵ľ�����Ϣ��Ҫ��ͷ��
				ServerUtils.getInstance().handleGetFriendInfo(session, moMoMsg);
				break;
				
			case iMoMoMsgTypes.ADD_FRIEND:
				// ��Ӻ���
				ServerUtils.getInstance().handleAddFriend(session, moMoMsg);
				break;
			case iMoMoMsgTypes.DELETE_FRIEND:
				// ɾ������
				ServerUtils.getInstance().handleDeleteFriend(session, moMoMsg);
				break;
				
			case iMoMoMsgTypes.GET_FRIEND_ID_LIST:
				// ��ú���Id�б�
				ServerUtils.getInstance().handleGetFriendIdList(session, moMoMsg);
				break;	
				
			case iMoMoMsgTypes.REBACK:
				// �û�����
				ServerUtils.getInstance().handleReback(moMoMsg);
				break;	
				
			case iMoMoMsgTypes.SIGN:
				// �û�ǩ��
				ServerUtils.getInstance().handleVitality(moMoMsg);
				break;	
			case iMoMoMsgTypes.PASS_GAME:
				// �û�ǩ��
				ServerUtils.getInstance().handleVitality(moMoMsg);
				break;
				
			case iMoMoMsgTypes.AGREEE_TO_GROUP:
				ServerUtils.getInstance().handleRecieveToGroup(moMoMsg);
				break;
				
			}

		} else if (moMoMsg.symbol == '-') {
			switch (msgJson.getInteger(MsgKeys.msgType)) {
			case iMoMoMsgTypes.REGISTER:
				// ע��:
				ServerUtils.getInstance().handleRegister(session, moMoMsg);
				break;
			case iMoMoMsgTypes.RESET_HEAD:
				// �޸�ͷ��:
				ServerUtils.getInstance().handleResetHead(session, moMoMsg);
				break;
			case iMoMoMsgTypes.CHATING_IMAGE_MSG:
				// ͼƬ��Ϣ:
				ServerUtils.getInstance().handleChatMsg(moMoMsg);
				break;
			case iMoMoMsgTypes.CHATING_VOICE_MSG:
				// ������Ϣ:
				ServerUtils.getInstance().handleChatMsg(moMoMsg);
				break;
				
			case iMoMoMsgTypes.CREATE_GROUP:
				// ע��:
				ServerUtils.getInstance().handleCreateGroup(session, moMoMsg);
				break;
			}

		}

	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		super.messageSent(session, message);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		super.sessionClosed(session);
		long sessionId = session.getId();
		if (ManageIdSessions.isContainsId(sessionId)) {
			// ˵���ǳ����ӵ�Id,��Ҫ��ʱɾ������ĻỰ
			String userId = ManageIdSessions.getUserId(sessionId);
			ManageIdSessions.deleteUserId(sessionId);
			ManageClientSession.deleteSession(userId);
			// ɾ��λ��
			ManageLocMap.deleteOneUser(userId);
		}
//		System.out.println("�ر�" + session);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		super.sessionIdle(session, status);
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		super.sessionOpened(session);
//		System.out.println("����+" + session);
	}

}
