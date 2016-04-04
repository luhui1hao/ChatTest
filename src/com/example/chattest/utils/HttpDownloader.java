package com.example.chattest.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * ������Ҫ���ڶ������ϵ���Դ��Http��������
 * 
 * @author ���� E-mail:
 * @date ����ʱ�䣺2016��2��18�� ����10:30:28
 * @version 1.0
 * @parameter
 * @since
 * @return
 */
public class HttpDownloader {
	private URL url = null;

	/**
	 * ����������ʽ���ļ� �ú�����������-1:�����ļ����� 0:�������سɹ� 1:�����ʼ��Ѿ�����
	 */
	public int downFile(String urlStr, String dirName, String fileName) {
		InputStream inputStream = null;
		try {
			FileUtils fileUtils = new FileUtils();

			if (fileUtils.isFileExist(dirName, fileName)) {
				return 1;
			} else {
				inputStream = getInputStreamFromUrl(urlStr);
				File resultFile = fileUtils.write2SDFromInput(dirName,
						fileName, inputStream);
				if (resultFile == null) {
					return -1;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	/**
	 * ����URL�õ�������
	 */
	public InputStream getInputStreamFromUrl(String urlStr)
			throws MalformedURLException, IOException {
		HttpURLConnection urlConn = null;
		URL url = new URL(urlStr);
		urlConn = (HttpURLConnection) url.openConnection();
		urlConn.setRequestMethod("GET");
		urlConn.setConnectTimeout(3000);
		urlConn.setReadTimeout(3000);
		InputStream inputStream = urlConn.getInputStream();
		return inputStream;
	}
}
