package com.imomo_msg;
/**
 * symbol = '+' : �����ļ�
 * symbol = '-' : �Ǵ����ļ�
 * @author Administrator
 */
public class iMoMoMsg {
	/** symbol = '+' : �����ļ� symbol = '-' : �Ǵ����ļ� */
	public char symbol;// �ж��Ƿ��Ǵ��ı��ļ�
	/** �������ı���Ϣ����Ľ��� */
	public String msgJson;
	/** ���ı���Ϣ */
	public byte[] msgBytes;
	
	@Override
	public String toString() {
		return "iMoMoMsg [symbol=" + symbol + ", msgJson=" + msgJson + "]";
	}

	
}
