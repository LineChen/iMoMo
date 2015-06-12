package com.managers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ManageGroups {
	
	public static Map<String, GroupInfo> groupMap = new HashMap<String, GroupInfo>();

	public static boolean isHaveGroup = false;
	
	static long FIVE_MINUTES = 300000;//�����
	static long ONE_DAY = 86400000;//һ��
	
	public static GroupInfo getGroup(String groupId) {
		if (isContainsGroupId(groupId)) {
//			System.out.println("getGroupMembers--groupId::" + groupId);
			return (GroupInfo) (groupMap.get(groupId));
		}
		return null;
	}

	public static boolean addGroup(String groupId,GroupInfo gInfo) {
//		System.out.println("���Group--" + groupId);
		gInfo.membersMap = new HashMap<String, String>();
		groupMap.put(groupId, gInfo);
		return true;
	}

	public static void deleteGroup(String groupId) {
//		System.out.println("ɾ��Group--" + groupId);
		if (isContainsGroupId(groupId)) {
			groupMap.remove(groupId);
		}
	}
	
	public static boolean isContainsGroupId(String groupId) {
		return groupMap.containsKey(groupId);
	}
	
	/**
	 * �����ػ����̣��鿴Ⱥ������
	 */
	public static void keepWatching(){
		isHaveGroup = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while(isHaveGroup){
						Thread.sleep(FIVE_MINUTES);//ÿ����Ӳ鿴һ��
						Iterator<GroupInfo> iter = groupMap.values().iterator();
						while(iter.hasNext()){
							GroupInfo group = (GroupInfo)iter.next();
							if(System.currentTimeMillis() - group.createTime > ONE_DAY){
								deleteGroup(group.groupId);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	
}
