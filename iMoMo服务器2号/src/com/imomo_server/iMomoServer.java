package com.imomo_server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.imomo_codecfactory.iMoMoCodecFactory;
import com.server_utils.StaticValues;

public class iMomoServer {
	public static void main(String[] args) {
		NioSocketAcceptor acceptor = new NioSocketAcceptor();
		acceptor.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new iMoMoCodecFactory()));
		acceptor.setHandler(new iMomoServerHandler());
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 60);//一分钟为通话，进入空闲状态
		try {
			acceptor.bind(new InetSocketAddress(StaticValues.SERVER_PORT));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
