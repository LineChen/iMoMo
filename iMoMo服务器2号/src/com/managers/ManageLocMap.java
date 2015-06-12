package com.managers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.imomo_msg.myLocationBean;

/**
 * 按省来管理用户位置
 * 
 * @author Administrator
 *
 */
public class ManageLocMap {
	public static Map<String, ManageAProvinceLoc> map = new HashMap<String, ManageAProvinceLoc>();

	/**
	 * 得到一个省的位置管理类
	 * 
	 * @param province
	 * @return
	 */
	public static ManageAProvinceLoc getAProvinceLoc(String province) {
		if (isContainsProvince(province)) {
//			System.out.println("get省:" + province);
			return (ManageAProvinceLoc) map.get(province);
		}
		return null;
	}

	/**
	 * 添加一个省位置管理类
	 * 
	 * @param province
	 * @param AProvinceLoc
	 */
	public static void addAProvinceLoc(String province,
			ManageAProvinceLoc AProvinceLoc) {
//		System.out.println("添加省：--" + province);
		map.put(province, AProvinceLoc);
	}

	/**
	 * 删除一个省的位置管理类
	 * 
	 * @param province
	 */
	public static void deleteAProvinceLoc(String province) {
//		System.out.println("删除省:--" + province);
		map.remove(province);
	}

	/**
	 * 判断是否存在一个省位置管理类
	 * 
	 * @param province
	 * @return
	 */
	public static boolean isContainsProvince(String province) {
		return map.containsKey(province);
	}
	
	/**
	 * 用户下线，即时删掉位置信息
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



