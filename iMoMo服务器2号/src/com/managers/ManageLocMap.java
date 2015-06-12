package com.managers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.imomo_msg.myLocationBean;

/**
 * ��ʡ�������û�λ��
 * 
 * @author Administrator
 *
 */
public class ManageLocMap {
	public static Map<String, ManageAProvinceLoc> map = new HashMap<String, ManageAProvinceLoc>();

	/**
	 * �õ�һ��ʡ��λ�ù�����
	 * 
	 * @param province
	 * @return
	 */
	public static ManageAProvinceLoc getAProvinceLoc(String province) {
		if (isContainsProvince(province)) {
//			System.out.println("getʡ:" + province);
			return (ManageAProvinceLoc) map.get(province);
		}
		return null;
	}

	/**
	 * ���һ��ʡλ�ù�����
	 * 
	 * @param province
	 * @param AProvinceLoc
	 */
	public static void addAProvinceLoc(String province,
			ManageAProvinceLoc AProvinceLoc) {
//		System.out.println("���ʡ��--" + province);
		map.put(province, AProvinceLoc);
	}

	/**
	 * ɾ��һ��ʡ��λ�ù�����
	 * 
	 * @param province
	 */
	public static void deleteAProvinceLoc(String province) {
//		System.out.println("ɾ��ʡ:--" + province);
		map.remove(province);
	}

	/**
	 * �ж��Ƿ����һ��ʡλ�ù�����
	 * 
	 * @param province
	 * @return
	 */
	public static boolean isContainsProvince(String province) {
		return map.containsKey(province);
	}
	
	/**
	 * �û����ߣ���ʱɾ��λ����Ϣ
	 * @param userId
	 */
	public static void deleteOneUser(String userId){
		Iterator<ManageAProvinceLoc> iterator = (Iterator<ManageAProvinceLoc>) map.values().iterator();
		while(iterator.hasNext()){
			ManageAProvinceLoc aProvinceLoc = iterator.next();
			if(aProvinceLoc.isContainsId(userId)){
				aProvinceLoc.deleteLocation(userId);
				if(aProvinceLoc.getCount() == 0){
					deleteAProvinceLoc(aProvinceLoc.province);
				}
			}
		}
	}
}



