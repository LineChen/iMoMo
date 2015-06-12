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
			properties.setProperty("mail.transport.protocol", "smtp");//�����ʼ�Э��
			properties.setProperty("mail.smtp.auth", "true");//��Ҫ��֤
			// properties.setProperty("mail.debug", "true");//����debugģʽ��̨����ʼ����͵Ĺ���
			session = Session.getInstance(properties);
			session.setDebug(false);//debugģʽ->����̨����ʾ�����ʼ��ľ���ϸ��
			
			//�ʼ���Ϣ
			messgae = new MimeMessage(session);
			messgae.setFrom(new InternetAddress("15764230067@163.com"));//���÷�����
			messgae.setText(content);//�����ʼ�����
			messgae.setSubject(subject);//�����ʼ�����
			
			//�����ʼ�
			tran = session.getTransport();	
			tran.connect("smtp.163.com", 25, "15764230067@163.com", "BigBad670067");//���ӵ��������������
			// tran.connect("smtp.qq.com", 25, "Michael8@qq.vip.com", "xxxx");//���ӵ�QQ���������
			tran.sendMessage(messgae, new Address[]{ new InternetAddress(clinetmailAddress)});//�����ʼ�������
			tran.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}	
	}
	
}
