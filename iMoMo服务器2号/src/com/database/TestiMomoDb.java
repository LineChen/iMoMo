package com.database;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.digest.Md5Crypt;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi.MD5;
import org.bouncycastle.util.encoders.Base64Encoder;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.Base64;
import com.imomo_msg.MsgDb;

public class TestiMomoDb {

	public static void main(String[] args) throws Exception {
		SqlModel model = new SqlModel();
//		System.out.println(model.clearMsgCache("client"));
//		String sql = "insert into client values(?)";
//		String parm[] = {"1"};
//		model.updateDb(sql, parm);
		
//		System.out.println("新增缓存表 = "+model.createCacheTable("110@qq.com"));;
//		String ss = "15764230067@163.com";
//		System.out.println();
//		System.out.println("密码 = " + model.findPasswd("15764230067@163.com"));
//		System.out.println("存在？" + model.isTableExists("ui8s"));
		
//		System.out.println("分配："+model.allocateId("15764230067@163.com"));
//		System.out.println(model.getUserCacheTableId("15764230067@163.com"));
		
//		System.out.println(model.getUserName("15764230067@163.com"));
//		JSONObject info = model.getUserInfo("15764230067@163.com", true);
//		JSONObject info = model.getUserInfo("9090", false);
//		System.out.println(info);
		
//		System.out.println(model.checkUser("15764230067@163.com","99e86f0e663826199b519869651dd5c2"));
//		List<MsgDb> list = model.getCacheMsgs("9090");
//		for(MsgDb msgDb : list){
//			System.out.println(msgDb.toString());
//		}
//		MsgDb msgDb = new MsgDb();
		
//		System.out.println(model.insertCacheMsg(msgDb, "9090"));
		
//		System.out.println(model.getMsgCount("9090"));
		
//		model.createFriendTable("9090");deleteFriend
		System.out.println("添加ok？ :"+model.addFriend("9099", "9098"));
//		System.out.println("删除ok？ :"+model.deleteFriend("9099", "9092"));
		String[] friendList = model.getFriendIds("9099");
		for (int i = 0; i < friendList.length; i++) {
			System.out.println(friendList[i]);
		}
		
//		System.out.println(model.UpdateVitality("9091", 1));
		
	}
}












