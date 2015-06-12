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
 * 考虑并发性,
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
	 * @category 存储用户图片或语音消息
	 * @param msgBytes
	 *            图片或语音byte数组
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
			System.out.println("警告：文件为空，无法保存!!");
		}
	}

	/**
	 * @param filepath
	 *            文件路径
	 * @return 图片或语音byte数组
	 */
	public byte[] getMultyFileBytes(String filepath) {
		File file = new File(filepath);
		ByteBuffer bytebuffer = null;
		FileInputStream fileInputStream = null;
		FileChannel channel = null;
		try {
			if (!file.exists()) {
				System.err.println("该文件不存在...");
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
	 * 删除文件
	 * 
	 * @param filePath
	 *            文件路径
	 */
	public void deleteFile(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 保存用户反馈信息
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
