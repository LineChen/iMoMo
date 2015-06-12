package com.server_utils;

/**
 * 根据一对经纬度得到两点距离
 * @author Administrator
 *
 */
public class DistanceUtil {

	public static void main(String[] args) {
		double dis = getDistance(120.133356, 36.013802, 120.133525, 36.013665);
		System.out.println("两点距离 = " + dis);
	}

	/**
	 * 
	 * @param long1
	 *            经度1
	 * @param lat1
	 *            纬度1
	 * @param long2
	 *            经度2
	 * @param lat2
	 *            纬度2
	 * @return 单位公里
	 */
	public static double getDistance(double long1, double lat1, double long2,
			double lat2) {
		double a, b, R;
		R = 6378137; // 地球半径(米)
		lat1 = lat1 * Math.PI / 180.0;
		lat2 = lat2 * Math.PI / 180.0;
		a = lat1 - lat2;
		b = (long1 - long2) * Math.PI / 180.0;
		double d;
		double sa2, sb2;
		sa2 = Math.sin(a / 2.0);
		sb2 = Math.sin(b / 2.0);
		d = 2
				* R
				* Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)
						* Math.cos(lat2) * sb2 * sb2));
		return d/1000;
	}

}
