package com.managers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.mina.core.session.IoSession;

/**
 * �����û��Ự
 * 
 * @author Administrator
 *
 */
public class ManageClientSession {
	public static Map<String, IoSession> clientMap = new HashMap<String, IoSession>();

	public static IoSession getSession(String userId) {
		if (isContainsId(userId)) {
			System.out.println("getSession--userId::" + userId);
			return (IoSession) clientMap.get(userId);
		}
		return null;
	}

	public static void addSession(String userId, IoSession ioSession) {
		System.out.println("���session--" + userId + " " + ioSession);
		clientMap.put(userId, ioSession);
	}

	public static void deleteSession(String userId) {
		System.out.println("ɾ��session--" + userId);
		if (isContainsId(userId)) {
			clientMap.remove(userId);
		}
	}

	public static boolean isContainsId(String userId) {
		return clientMap.containsKey(userId);
	}

}
