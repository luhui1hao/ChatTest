package com.example.chattest.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.os.Environment;
import android.util.Log;

import com.example.chattest.bean.Info;

/**
 * ������Ҫ���ڶ��ļ����ļ��еĲ���
 * 
 * @author ���� E-mail:
 * @date ����ʱ�䣺2016��2��18�� ����10:35:06
 * @version 1.0
 * @parameter
 * @since
 * @return
 */
public class FileUtils {
	private static final String SDCARD_ROOT = Environment
			.getExternalStorageDirectory().getAbsolutePath() + File.separator;
	private static final String PIC_DIR_NAME = "aMyPic";
	private static final String AUDIO_DIR_NAME = "aMyRecorder";
	private static final String ROOT_DIR_ADD = SDCARD_ROOT + "aTianAnCaiXian";
	private static final String PIC_DIR_ADD = ROOT_DIR_ADD + "/" + PIC_DIR_NAME;
	private static final String AUDIO_DIR_ADD = ROOT_DIR_ADD + "/"
			+ AUDIO_DIR_NAME;

	/**
	 * ��ȡ��ǰ�ⲿ�洢�豸��Ŀ¼
	 */
	public static String getSDCARD_ROOT() {
		return SDCARD_ROOT;
	}

	/**
	 * ��ȡ��Ŀ����ĸ��ļ���
	 * 
	 * @return
	 */
	public static String getRootDirName() {
		return ROOT_DIR_ADD;
	}

	public static String getPicDirName() {
		return PIC_DIR_NAME;
	}

	public static String getAudioDirName() {
		return AUDIO_DIR_NAME;
	}

	/**
	 * ��ȡͼƬ�����ļ���·��
	 * 
	 * @return
	 */
	public static String getPicDirAdd() {
		return PIC_DIR_ADD;
	}

	/**
	 * ��ȡ��Ƶ�����ļ���·��
	 * 
	 * @return
	 */
	public static String getAudioDirAdd() {
		return AUDIO_DIR_ADD;
	}

	/**
	 * ��SD���ϴ���Ŀ¼
	 */
	public File createSDDir(String dirName) {
		File dir = new File(SDCARD_ROOT + dirName);
		dir.mkdir();
		return dir;
	}

	/**
	 * ��SD���ϴ����ļ�
	 */
	public File creatSDFile(String fileName) throws IOException {
		File file = new File(SDCARD_ROOT + fileName);
		file.createNewFile();
		return file;
	}

	/**
	 * �ж�SD���ϵ��ļ����Ƿ����
	 */
	public boolean isFileExist(String dirName, String fileName) {
		Log.d("mars.download.FileUtils", "isFileExist has run");
		File file = new File(SDCARD_ROOT + dirName + fileName);
		return file.exists();
	}

	/**
	 * �ж�SD���ϵ��ļ����Ƿ����
	 */
	public boolean isFileExist2(String path) {
		File file = new File(path);
		return file.exists();
	}

	/**
	 * ��һ��InputStream���������д�뵽SD����
	 */
	public File write2SDFromInput(String dirName, String fileName,
			InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			createSDDir(dirName);
			file = creatSDFile(dirName + fileName);
			output = new FileOutputStream(file);
			byte[] buffer = new byte[4 * 1024];
			int length;
			while ((length = input.read(buffer)) != -1) {
				output.write(buffer, 0, length);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return file;
	}

	/**
	 * ɾ���ļ�
	 */
	public boolean deleteFile(String path) throws Exception {
		File file = new File(path);
		return file.delete();
	}

	/**
	 * �������ͼƬ������
	 */
	public static String generatePicName() {
		return UUID.randomUUID().toString() + ".jpeg";
	}

	/**
	 * ���������Ƶ������
	 */
	public static String generateAudioName() {
		return UUID.randomUUID().toString() + ".amr";
	}

	/**
	 * ��ȡ�ļ����ֽ�����
	 * 
	 * @param info
	 * @return
	 * @throws Exception
	 */
	public static byte[] getData(String path) throws Exception {
		File file = new File(path);
		byte[] buffer = null;
		FileInputStream fis = new FileInputStream(file);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int n;
		while ((n = fis.read(b)) != -1) {
			bos.write(b, 0, n);
		}
		fis.close();
		bos.close();
		buffer = bos.toByteArray();
		return buffer;
	}
}
