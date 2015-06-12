package com.imomo_msg;

/**
 * 用户位置类
 * 
 * @author Administrator
 *
 */
public class myLocationBean {
	// 省份、经纬度
	public String province;
	public double longitude;
	public double latitude;

	@Override
	public String toString() {
		return "myLocationBean [province=" + province + ", longitude="
				+ longitude + ", latitude=" + latitude + "]";
	}

}
