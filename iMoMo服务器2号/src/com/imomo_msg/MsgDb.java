package com.imomo_msg;

/**
 * 存在数据库的离线消息类
 * 
 * @author Administrator
 *
 */
public class MsgDb {
	public int msgType;
	public String msgJson;

	@Override
	public String toString() {
		return "MsgDb [msgType=" + msgType + ", msgJson=" + msgJson + "]";
	}

}
