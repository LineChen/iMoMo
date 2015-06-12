package com.server_utils;
//40
import java.util.*;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmailToClient {
	
	Properties properties=null;
	Session session=null;
	Message messgae=null;
	Transport tran=null;
	
	public SendEmailToClient(String clinetmailAddress,String subject,String content)
	{
		try {
			properties=new Properties();
			properties.setProperty("mail.transport.protocol", "smtp");//发送邮件协议
			properties.setProperty("mail.smtp.auth", "true");//需要验证
			// properties.setProperty("mail.debug", "true");//设置debug模式后台输出邮件发送的过程
			session = Session.getInstance(properties);
			session.setDebug(false);//debug模式->控制台会显示发送邮件的具体细节
			
			//邮件信息
			messgae = new MimeMessage(session);
			messgae.setFrom(new InternetAddress("15764230067@163.com"));//设置发送人
			messgae.setText(content);//设置邮件内容
			messgae.setSubject(subject);//设置邮件主题
			
			//发送邮件
			tran = session.getTransport();	
			tran.connect("smtp.163.com", 25, "15764230067@163.com", "BigBad670067");//连接到新浪邮箱服务器
			// tran.connect("smtp.qq.com", 25, "Michael8@qq.vip.com", "xxxx");//连接到QQ邮箱服务器
			tran.sendMessage(messgae, new Address[]{ new InternetAddress(clinetmailAddress)});//设置邮件接收人
			tran.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}	
	}
	
}
