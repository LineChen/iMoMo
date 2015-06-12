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
//		System.out.println("接收到消息");
		// ManageClientSession.addSession("9091", session);
//		System.out.println("服务器接收:" + moMoMsg.toString());
		if (moMoMsg.symbol == '+') {
			switch (msgJson.getInteger(MsgKeys.msgType)) {
			case iMoMoMsgTypes.FIND_PASSWD:
				// 找回密码:
				ServerUtils.getInstance().handleFindPasswd(session, moMoMsg);
				break;

			// ----------------修改个人信息--------------------------------//
			case iMoMoMsgTypes.RESET_PASSWD:
				// 重置密码:
				ServerUtils.getInstance().handleResetPasswd(session, moMoMsg);
				break;

			case iMoMoMsgTypes.RESET_USERNAME:
				// 修改用户名:
				ServerUtils.getInstance().handleResetUserInfo(moMoMsg);
				break;
			case iMoMoMsgTypes.RESET_SEX:
				// 修改性别:
				ServerUtils.getInstance().handleResetUserInfo(moMoMsg);
				break;
			case iMoMoMsgTypes.RESET_BIRTHDAY:
				// 修改生日:
				ServerUtils.getInstance().handleResetUserInfo(moMoMsg);
				break;
			case iMoMoMsgTypes.RESET_SIGNATUE:
				// 修改个人签名:
				ServerUtils.getInstance().handleResetUserInfo(moMoMsg);
				break;
			// ----------------修改个人信息--------------------------------//

			// ------------------登录---------------------------//
			case iMoMoMsgTypes.LOGIN:
				// 用户登录:
				ServerUtils.getInstance().handleLogin(session, moMoMsg);
				break;
			case iMoMoMsgTypes.LOGIN_SUPER_HEAD:
				// 用户超级登录有头像:
				ServerUtils.getInstance().handleLoginSuper(session, moMoMsg);
				break;
			case iMoMoMsgTypes.LOGIN_SUPER_NOHEAD:
				// 用户超级登录无头像:
				ServerUtils.getInstance().handleLoginSuper(session, moMoMsg);
				break;
			// ------------------登录---------------------------//

			case iMoMoMsgTypes.CHATING_TEXT_MSG:
				// 文本信息:
				ServerUtils.getInstance().handleChatMsg(moMoMsg);
				break;

			// ------------------位置相关服务-----------------------//
			case iMoMoMsgTypes.LOCATION:
				// 用户地理位置消息
				ServerUtils.getInstance().handleLocation(moMoMsg);
				break;
			case iMoMoMsgTypes.GET_STRANGERS_LOC_ONEKM:
				// 请求得到周围一公里陌生人位置
				ServerUtils.getInstance().handleGetStrangersLoc(moMoMsg);
				break;
				
			case iMoMoMsgTypes.GET_STRANGERS_LOC_MORE:
				// 请求得到周围大于一公里内陌生人位置
				ServerUtils.getInstance().handleGetStrangersLoc(moMoMsg);
				break;
				
			case iMoMoMsgTypes.GET_STRANGER_INFO:
				// 请求得到周围大于一公里内陌生人位置
				ServerUtils.getInstance().handleGetFriendInfo(session, moMoMsg);
				break;
			// ------------------位置相关服务-----------------------//
				
				
				
			case iMoMoMsgTypes.GETA_FRIEND_INFO_HEAD:
				// 请求得到一个陌生人的具体信息,不用发头像
				ServerUtils.getInstance().handleGetFriendInfo(session, moMoMsg);
				break;
			case iMoMoMsgTypes.GETA_FRIEND_INFO_NOHEAD:
				// 请求得到一个陌生人的具体信息，要发头像
				ServerUtils.getInstance().handleGetFriendInfo(session, moMoMsg);
				break;
				
			case iMoMoMsgTypes.ADD_FRIEND:
				// 添加好友
				ServerUtils.getInstance().handleAddFriend(session, moMoMsg);
				break;
			case iMoMoMsgTypes.DELETE_FRIEND:
				// 删除好友
				ServerUtils.getInstance().handleDeleteFriend(session, moMoMsg);
				break;
				
			case iMoMoMsgTypes.GET_FRIEND_ID_LIST:
				// 获得好友Id列表
				ServerUtils.getInstance().handleGetFriendIdList(session, moMoMsg);
				break;	
				
			case iMoMoMsgTypes.REBACK:
				// 用户反馈
				ServerUtils.getInstance().handleReback(moMoMsg);
				break;	
				
			case iMoMoMsgTypes.SIGN:
				// 用户签到
				ServerUtils.getInstance().handleVitality(moMoMsg);
				break;	
			case iMoMoMsgTypes.PASS_GAME:
				// 用户签到
				ServerUtils.getInstance().handleVitality(moMoMsg);
				break;
				
			case iMoMoMsgTypes.AGREEE_TO_GROUP:
				ServerUtils.getInstance().handleRecieveToGroup(moMoMsg);
				break;
				
			}

		} else if (moMoMsg.symbol == '-') {
			switch (msgJson.getInteger(MsgKeys.msgType)) {
			case iMoMoMsgTypes.REGISTER:
				// 注册:
				ServerUtils.getInstance().handleRegister(session, moMoMsg);
				break;
			case iMoMoMsgTypes.RESET_HEAD:
				// 修改头像:
				ServerUtils.getInstance().handleResetHead(session, moMoMsg);
				break;
			case iMoMoMsgTypes.CHATING_IMAGE_MSG:
				// 图片消息:
				ServerUtils.getInstance().handleChatMsg(moMoMsg);
				break;
			case iMoMoMsgTypes.CHATING_VOICE_MSG:
				// 语音消息:
				ServerUtils.getInstance().handleChatMsg(moMoMsg);
				break;
				
			case iMoMoMsgTypes.CREATE_GROUP:
				// 注册:
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
			// 说明是长连接的Id,需要及时删除保存的会话
			String userId = ManageIdSessions.getUserId(sessionId);
			ManageIdSessions.deleteUserId(sessionId);
			ManageClientSession.deleteSession(userId);
			// 删除位置
			ManageLocMap.deleteOneUser(userId);
		}
//		System.out.println("关闭" + session);
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
//		System.out.println("连接+" + session);
	}

}
