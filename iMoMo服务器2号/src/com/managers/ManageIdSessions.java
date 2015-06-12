package com.managers;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;

/**
 * 保存session Id 和 用户Id，因为客户端断意外断开连接时，不会发送通知，
 * 
 * @author Administrator
 *
 */
public class ManageIdSessions {
	public static Map<Long, String> Map = new HashMap<Long, String>();
	
	/**
	 * 添加对应关系
	 * @param sessionId
	 * @param userId
	 */
	public static void addUserId(Long sessionId, String userId){
		Map.put(sessionId, userId);
		System.out.println("添加 ：会话Id = " + sessionId + "对应用户Id = " + userId);
	}
	
	/**
	 * 得到和会话对应的用户Id
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
			System.out.println("删除 : seesionId = " + sessionId);
		}
	}
	
	public static boolean isContainsId(Long sessionId){
		return Map.containsKey(sessionId);
	}
	
}



