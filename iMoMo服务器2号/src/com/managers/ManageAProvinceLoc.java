package com.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.database.SqlModel;
import com.imomo_msg.StrangerBean;
import com.imomo_msg.myLocationBean;
import com.server_utils.DistanceUtil;

/**
 * 管理用户位置(一个省)
 * 
 * @author Administrator
 *
 */
public class ManageAProvinceLoc {

	Map<String, myLocationBean> locationMap;
	public String province;

	public ManageAProvinceLoc(String province) {
		this.province = province;
		locationMap = new TreeMap<String, myLocationBean>();
	}

	public myLocationBean getLocation(String userId) {
		if (isContainsId(userId)) {
//			System.out.println("getLocation--userId::" + userId);
			return (myLocationBean) locationMap.get(userId);
		}
		return null;
	}

	public void addLocation(String userId, myLocationBean LocationBean) {
//		System.out.println("添加myLocationBean--" + userId + " " + LocationBean);
		locationMap.put(userId, LocationBean);
	}

	public void deleteLocation(String userId) {
//		System.out.println("删除myLocationBean--" + userId);
		if (isContainsId(userId))
			locationMap.remove(userId);
	}

	public int getCount() {
		return locationMap.size();
	}

	public boolean isContainsId(String userId) {
		return locationMap.containsKey(userId);
	}

	/**
	 * 得到指定距离内的陌生人相关信息
	 * @param isOnekm 是否是周围一公里
	 * @param userId
	 * @param range
	 * @return
	 */
	public List<StrangerBean> getDisStrangers(boolean isOnekm, String userId, int range) {
		int NUM = 0;
		int MAX = 100;
		List<StrangerBean> list = new ArrayList<StrangerBean>();
//		//测试用....
//			StrangerBean bean1 = new StrangerBean();
//			bean1.strangerId = "9090";
//			bean1.strangerName = "妖姬";
//			bean1.Longitude = 120.143350;
//			bean1.Latitude = 36.015562;
//			bean1.strangerLoc = 1 + "公里以内";
//			list.add(bean1);
//			bean1 = new StrangerBean();
//			bean1.strangerId = "9091";
//			bean1.strangerName = "女神";
//			bean1.Longitude = 120.133340;
//			bean1.Latitude = 36.013557;
//			bean1.strangerLoc = 1 + "公里以内";
//			list.add(bean1); 
			
		myLocationBean loc1 = this.getLocation(userId);
		Iterator<String> iter = locationMap.keySet().iterator();
		while (iter.hasNext()) {
			String strangerId = iter.next();
			if (!strangerId.equals(userId)) {
				myLocationBean loc2 = this.getLocation(strangerId);
				double dist = DistanceUtil
						.getDistance(loc1.longitude, loc1.latitude, loc2.longitude, loc2.latitude);
				SqlModel model = 	new SqlModel();
				if(isOnekm){
					if(dist > 0 && dist <= 1){
						StrangerBean bean = new StrangerBean();
						bean.strangerId = strangerId;
						bean.strangerName = model.getUserName(strangerId, false);
						bean.Longitude = loc2.longitude;
						bean.Latitude = loc2.latitude;
						bean.strangerLoc = 1 + "公里以内";
						list.add(bean); 
						NUM ++;
						if(NUM == MAX){
							break;
						}
					}
				} else {
					if(dist > range - 1 && dist <= range){
						StrangerBean bean = new StrangerBean();
						bean.strangerId = strangerId;
						bean.strangerName = model.getUserName(strangerId, false);
						bean.Longitude = loc2.longitude;
						bean.Latitude = loc2.latitude;
						bean.strangerLoc = range + "公里以内";
						list.add(bean); 
						if(NUM == MAX){
							break;
						}
					}
				}
			}
		}
		return list;
	}
}
