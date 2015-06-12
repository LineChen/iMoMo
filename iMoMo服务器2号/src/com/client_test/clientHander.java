package com.client_test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.imomo_msg.MsgKeys;
import com.imomo_msg.iMoMoMsg;
import com.imomo_msg.iMoMoMsgTypes;
import com.imomo_server.ServerUtils;
import com.server_utils.FileTools;

public class clientHander extends IoHandlerAdapter {

	public void exceptionCaught(IoSession arg0, Throwable arg1)
			throws Exception {
		arg1.printStackTrace();
	}

	public void messageReceived(IoSession session, Object message)
			throws Exception {
		iMoMoMsg moMoMsg = (iMoMoMsg) message;
		System.out.println("�ͻ��˽��գ�"+moMoMsg.toString());
		JSONObject msgJson = JSON.parseObject(moMoMsg.msgJson);
		if (moMoMsg.symbol == '+') {
			switch (msgJson.getInteger(MsgKeys.msgType)) {
			case iMoMoMsgTypes.FIND_PASSWD_SUCCESS:
				System.out.println("��鿴ע������");
				break;
			case iMoMoMsgTypes.FIND_PASSWD_FAILED:
				System.out.println("�һ�����ʧ��,��ȷ�����������������");
				break;
				
			case iMoMoMsgTypes.RESET_PASSWD_SUCCESS:
				System.out.println("�����޸ĳɹ�");
				break;
				
			case iMoMoMsgTypes.RESET_PASSWD_FAILED:
				System.out.println("�����޸�ʧ��");
				break;
				
			case iMoMoMsgTypes.RESET_HEAD_SUCCESS:
				System.out.println("ͷ���޸ĳɹ�");
				break;
			case iMoMoMsgTypes.RESET_HEAD_FAILED:
				System.out.println("ͷ���޸�ʧ��");
				break;
				
			case iMoMoMsgTypes.LOGIN_SUCCESS:
				System.out.println("��¼�ɹ�");
				break;
			case iMoMoMsgTypes.LOGIN_FAILED:
				System.out.println("��¼ʧ��");
				break;
			}

		} else if (moMoMsg.symbol == '-') {
			
		}
	}
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		super.sessionClosed(session);
	}
	

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		iMoMoMsg moMoMsg = new iMoMoMsg();
		JSONObject Json = new JSONObject();
//		// �һ��������(ok)
//		 Json.put(MsgKeys.msgType, iMoMoMsgTypes.FIND_PASSWD);
//		 Json.put(MsgKeys.userEmail, "15764230067@163.com");
//		 moMoMsg.msgJson = Json.toJSONString();
//		 moMoMsg.symbol = '+';
//		 session.write(moMoMsg);

		// //�����������(ok)
		// Json.put(MsgKeys.msgType, iMoMoMsgTypes.RESET_PASSWD);
		// Json.put(MsgKeys.senderId, "15764230067@163.com");
		// Json.put(MsgKeys.userPasswd, "1998");
		// moMoMsg.msgJson = Json.toJSONString();
		// moMoMsg.symbol = '+';
		// session.write(moMoMsg);

//		// �����û�������(ok)
//		Json.put(MsgKeys.msgType, iMoMoMsgTypes.RESET_SIGNATUE);
//		Json.put(MsgKeys.userId, "9091");
//		Json.put(MsgKeys.personSignature, "will be better");
//		moMoMsg.msgJson = Json.toJSONString();
//		moMoMsg.symbol = '+';
//		session.write(moMoMsg);
		
		// �޸�ͷ�����(ok)
//		Json.put(MsgKeys.msgType, iMoMoMsgTypes.RESET_HEAD);
//		Json.put(MsgKeys.senderId, "15764230067@163.com");
//		moMoMsg.msgJson = Json.toJSONString();
//		moMoMsg.msgBytes = FileTools.getInstance().getMultyFileBytes("E:\\iMoMoServer\\ClientsHead\\fff.png");
//		moMoMsg.symbol = '-';
//		session.write(moMoMsg);
		
		//��¼����(ok)
//		Json.put(MsgKeys.msgType, iMoMoMsgTypes.LOGIN_SUPER_HEAD);
////		Json.put(MsgKeys.userEmail, "1101587382@qq.com");
//		Json.put(MsgKeys.userEmail, "15764230067@163.com");
//		Json.put(MsgKeys.userPasswd, "f460cf88343668720ed4191fadeae605");
//		moMoMsg.msgJson = Json.toJSONString();
//		moMoMsg.symbol = '+';
//		session.write(moMoMsg);
		
//		//�����ı���Ϣ(ok)
//		Json.put(MsgKeys.msgType, iMoMoMsgTypes.CHATING_TEXT_MSG);
//		Json.put(MsgKeys.userId, "9090");
//		Json.put(MsgKeys.getterId, "9092");
//		Json.put(MsgKeys.msgCotent, "today is today....");
//		moMoMsg.msgJson = Json.toJSONString();
//		moMoMsg.symbol = '+';
//		session.write(moMoMsg);
		
//		//���Է��Ͷ�ý���ļ�(ok)
//		Json.put(MsgKeys.msgType, iMoMoMsgTypes.CHATING_VOICE_MSG);
//		Json.put(MsgKeys.userId, "9090");
//		Json.put(MsgKeys.getterId, "9091");
//		moMoMsg.msgJson = Json.toJSONString();
//		moMoMsg.msgBytes = FileTools.getInstance().getMultyFileBytes("E:\\iMoMoServer\\ClientsHead\\fff.png");
//		moMoMsg.symbol = '-';
//		session.write(moMoMsg);
//		
//		//����ע��
//		Json.put(MsgKeys.msgType, iMoMoMsgTypes.REGISTER);
//		Json.put(MsgKeys.userEmail, "15764230067@163.com");
//		Json.put(MsgKeys.userName, "isRunning");
//		Json.put(MsgKeys.userPasswd, "run_run");
//		Json.put(MsgKeys.userBirthday, "1993.3.5");
//		Json.put(MsgKeys.userSex, "male");
//		moMoMsg.msgJson = Json.toJSONString();
//		moMoMsg.msgBytes = FileTools.getInstance().getMultyFileBytes("E:\\iMoMoServer\\ClientsHead\\fff.png");
//		moMoMsg.symbol = '-';
//		session.write(moMoMsg);
		
		//���Ի�ú�����Ϣ(ok)
//		Json.put(MsgKeys.msgType, iMoMoMsgTypes.GETA_FRIEND_INFO_NOHEAD);
//		Json.put(MsgKeys.userId, "9091");
//		Json.put(MsgKeys.friendId, "9090");
//		moMoMsg.msgJson = Json.toJSONString();
//		moMoMsg.symbol = '+';
//		session.write(moMoMsg);
		
		Json.put(MsgKeys.msgType, iMoMoMsgTypes.REBACK);
		Json.put(MsgKeys.userId, "9091");
		Json.put(MsgKeys.msgCotent, "its very good....");
		moMoMsg.msgJson = Json.toJSONString();
		moMoMsg.symbol = '+';
		session.write(moMoMsg);
		
	}

}




