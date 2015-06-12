package com.client_test;

import java.net.InetSocketAddress;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.imomo_codecfactory.iMoMoCodecFactory;

public class iMoMoClientTest {
	public static void main(String args[])
	{
		NioSocketConnector conn=new NioSocketConnector();
		conn.getFilterChain().addLast("code",new ProtocolCodecFilter(new iMoMoCodecFactory()));
		conn.setHandler(new clientHander());
		conn.connect(new InetSocketAddress("119.167.70.210",9090));//10.50.44.130
	}

}
