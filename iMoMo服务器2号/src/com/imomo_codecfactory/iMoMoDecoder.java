package com.imomo_codecfactory;

import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Date;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

import com.imomo_msg.iMoMoMsg;
import com.mysql.jdbc.log.Log;
import com.security.PBE;

public class iMoMoDecoder extends CumulativeProtocolDecoder {
//	CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		iMoMoMsg moMsg = new iMoMoMsg();
		int pos = in.position();
		int remaining = in.remaining();
		try {
			// 判断长度,开头symbol 是char型
			if (remaining < 2) {
				in.position(pos);
				return false;
			}
			char symbol = in.getChar();// 相当于消费掉字节流
			moMsg.symbol = symbol;
			if (symbol == '+') {
				int msgJsonLen = 0;// msgJson长度
				// 判断是否够解析出的长度
				msgJsonLen = in.getInt();
				if (remaining - 2 < msgJsonLen || msgJsonLen < 0) {
					in.position(pos);
					return false;
				}
				byte[] temp = new byte[msgJsonLen];
				in.get(temp);//得到加密后byte数组
				moMsg.msgJson = new String(PBE.deCiphering(temp), "utf-8");//统一utf-8编码
//				System.out.println("服务器解码："+moMsg.toString());
				out.write(moMsg);
			} else if (symbol == '-') {
				// 接收文本信息
				int msgJsonLen = 0;// msgJson长度
				int msgBytesLen = 0;// msgBytes长度
				msgJsonLen = in.getInt();
				if (remaining - 2 < msgJsonLen || msgJsonLen < 0) {
					in.position(pos);
					return false;
				}
				byte[] temp1 = new byte[msgJsonLen];
				in.get(temp1);//得到加密后byte数组
				moMsg.msgJson = new String(PBE.deCiphering(temp1), "utf-8");
				// 接收图片信息
				msgBytesLen = in.getInt();
				if (remaining - 2 - 4 - 4 - msgJsonLen < msgBytesLen
						|| msgBytesLen < 0) {
					in.position(pos);
					return false;
				}
				byte[] temp2 = new byte[msgBytesLen];
				in.get(temp2);//得到加密后byte数组
				moMsg.msgBytes = PBE.deCiphering(temp2);
				out.write(moMsg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			in.position(pos);
			return false;
		}
		return true;
	}
}
