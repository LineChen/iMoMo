package com.server_utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.imomo_server.ServerUtils;

/**
 * ���ǲ�����,
 * 
 * @author Administrator
 *
 */
public class FileTools {
	static FileTools fileTools;

	public static void main(String[] args) {
		byte[] fff = FileTools.getInstance().getMultyFileBytes(
				StaticValues.MSG_CACHE_IMA_P_PATH + "fff.png");
		FileTools.getInstance().saveMultyFile(
				StaticValues.MSG_CACHE_IMA_P_PATH + "90joj.jpg", fff);
	}

	public static FileTools getInstance() {
		// if(fileTools == null){
		// fileTools = new FileTools();
		// }
		return new FileTools();
	}

	/**
	 * @category �洢�û�ͼƬ��������Ϣ
	 * @param msgBytes
	 *            ͼƬ������byte����
	 */
	public void saveMultyFile(String filepath, byte[] msgBytes) {
		if (msgBytes.length > 0) {
			File file = new File(filepath);
			FileOutputStream fileout = null;
			FileChannel fc = null;
			try {
				fileout = new FileOutputStream(file);
				fc = fileout.getChannel();
				fileout.write(msgBytes);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					fc.close();
					fileout.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		} else {
			System.out.println("���棺�ļ�Ϊ�գ��޷�����!!");
		}
	}

	/**
	 * @param filepath
	 *            �ļ�·��
	 * @return ͼƬ������byte����
	 */
	public byte[] getMultyFileBytes(String filepath) {
		File file = new File(filepath);
		ByteBuffer bytebuffer = null;
		FileInputStream fileInputStream = null;
		FileChannel channel = null;
		try {
			if (!file.exists()) {
				System.err.println("���ļ�������...");
			} else {
				fileInputStream = new FileInputStream(file);
				channel = fileInputStream.getChannel();
				bytebuffer = ByteBuffer.allocate((int) channel.size());
				bytebuffer.clear();
				channel.read(bytebuffer);
				return bytebuffer.array();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				channel.close();
				fileInputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * ɾ���ļ�
	 * 
	 * @param filePath
	 *            �ļ�·��
	 */
	public void deleteFile(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * �����û�������Ϣ
	 * 
	 * @param userId
	 * @param reback
	 */
	public void saveReback(String userId, String reback) {
		File file = new File(StaticValues.REBACK_PATH);
		BufferedWriter out = null;
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file, true)));
			out.write(userId + ":" + reback );//+ "\r\n\r\n"
			out.write("\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
