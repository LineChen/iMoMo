package com.imomo_msg;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.server_utils.FileTools;
import com.server_utils.StaticValues;

/**
 * 消息包转换工具
 * 
 * @author Administrator
 *
 */
public class MsgTranceUtil {

	public static MsgTranceUtil getInstance() {
		return new MsgTranceUtil();
	}

	/**
	 * 数据库存数消息转化为网络传输消息
	 * 
	 * @param msgDb
	 * @return
	 */
	public iMoMoMsg Trance_Db2Net(MsgDb msgDb) {
		iMoMoMsg moMsg = new iMoMoMsg();
		switch (msgDb.msgType) {
		case iMoMoMsgTypes.CHATING_TEXT_MSG:
			moMsg.symbol = '+';
			moMsg.msgJson = msgDb.msgJson;
			break;
		case iMoMoMsgTypes.CHATING_IMAGE_MSG:
			JSONObject json = JSON.parseObject(msgDb.msgJson);
			moMsg.symbol = '-';
			String imagePath = json.getString(MsgKeys.imagePath);
			moMsg.msgBytes = FileTools.getInstance().getMultyFileBytes(
					imagePath);
			json.remove(MsgKeys.imagePath);
			moMsg.msgJson = json.toJSONString();
			//删除本地缓存图片
			FileTools.getInstance().deleteFile(imagePath);
			break;
		case iMoMoMsgTypes.CHATING_VOICE_MSG:
			moMsg.symbol = '-';
			JSONObject json2 = JSON.parseObject(msgDb.msgJson);
			String voicePath = json2.getString(MsgKeys.voicePath);
			moMsg.msgBytes = FileTools.getInstance().getMultyFileBytes(
					voicePath);
			json2.remove(MsgKeys.voicePath);
			moMsg.msgJson = json2.toJSONString();
			FileTools.getInstance().deleteFile(voicePath);
			break;
			
		case iMoMoMsgTypes.ADD_FRIEND:
			moMsg.symbol = '+';
			moMsg.msgJson = msgDb.msgJson;
			break;
			
		case iMoMoMsgTypes.RESET_HEAD:
			JSONObject resetHeadjson = JSON.parseObject(msgDb.msgJson);
			moMsg.symbol = '-';
			String headPath = resetHeadjson.getString(MsgKeys.imagePath);
			moMsg.msgBytes = FileTools.getInstance().getMultyFileBytes(
					headPath);
			resetHeadjson.remove(MsgKeys.imagePath);
			moMsg.msgJson = resetHeadjson.toJSONString();
			//删除本地缓存图片
			FileTools.getInstance().deleteFile(headPath);
			break;
			
		}
		return moMsg;
	}

	/**
	 * 网络消息转化为数据库存储消息
	 * 
	 * @param moMsg
	 * @return
	 */
	public MsgDb Trance_Net2Db(iMoMoMsg moMsg) {
		MsgDb msgDb = new MsgDb();
		JSONObject json = JSON.parseObject(moMsg.msgJson);
		int msgtype = json.getIntValue(MsgKeys.msgType);
		switch (msgtype) {
		case iMoMoMsgTypes.CHATING_TEXT_MSG:
			break;
		case iMoMoMsgTypes.CHATING_IMAGE_MSG:
			String imagePath = StaticValues.MSG_CACHE_IMA_P_PATH
					+ json.getString(MsgKeys.userId) + "_"
					+ System.currentTimeMillis() + ".png";
			FileTools.getInstance().saveMultyFile(imagePath, moMsg.msgBytes);
			json.put(MsgKeys.imagePath, imagePath);
			break;
		case iMoMoMsgTypes.CHATING_VOICE_MSG:
			String voicePath = StaticValues.MSG_CACHE_VOI_P_PATH
					+ json.getString(MsgKeys.userId) + "_"
					+ System.currentTimeMillis() + ".amr";
			FileTools.getInstance().saveMultyFile(voicePath, moMsg.msgBytes);
			json.put(MsgKeys.voicePath, voicePath);
			break;
		case iMoMoMsgTypes.ADD_FRIEND:
//			String headPath = StaticValues.HEAD_P_PATH
//			+ json.getString(MsgKeys.userId) + ".png";
//			json.put(MsgKeys.imagePath, headPath);
			break;
			
		case iMoMoMsgTypes.RESET_HEAD:
			String headPath = StaticValues.MSG_CACHE_IMA_P_PATH
			+ json.getString(MsgKeys.userId) + "_"
			+ System.currentTimeMillis() + ".png";
			FileTools.getInstance().saveMultyFile(headPath, moMsg.msgBytes);
			json.put(MsgKeys.imagePath, headPath);
			break;
			
		}
		msgDb.msgType = msgtype;
		msgDb.msgJson = json.toJSONString();
		return msgDb;
	}

}
