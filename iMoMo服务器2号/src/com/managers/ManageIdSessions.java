package com.managers;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;

/**
 * ����session Id �� �û�Id����Ϊ�ͻ��˶�����Ͽ�����ʱ�����ᷢ��֪ͨ��
 * 
 * @author Administrator
 *
 */
public class ManageIdSessions {
	public static Map<Long, String> Map = new HashMap<Long, String>();
	
	/**
	 * ��Ӷ�Ӧ��ϵ
	 * @param sessionId
	 * @param userId
	 */
	public static void addUserId(Long sessionId, String userId){
		Map.put(sessionId, userId);
		System.out.println("��� ���ỰId = " + sessionId + "��Ӧ�û�Id = " + userId);
	}
	
	/**
	 * �õ��ͻỰ��Ӧ���û�Id
	 * @param seesionId
	 * @return
	 */
	public static String getUserId(Long sessionId){
		if(isContainsId(sessionId)){
			return (String)Map.get(sessionId);
		}
		return null;
	}
	
	public static void deleteUserId(Long sessionId){
		if(isContainsId(sessionId)){
			Map.remove(sessionId);
			System.out.println("ɾ�� : seesionId = " + sessionId);
		}
	}
	
	public static boolean isContainsId(Long sessionId){
		return Map.containsKey(sessionId);
	}
	
}



