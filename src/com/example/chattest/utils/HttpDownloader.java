package com.example.chattest.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 该类主要用于对网络上的资源用Http进行下载
 * 
 * @author 作者 E-mail:
 * @date 创建时间：2016年2月18日 上午10:30:28
 * @version 1.0
 * @parameter
 * @since
 * @return
 */
public class HttpDownloader {
	private URL url = null;

	/**
	 * 下载任意形式的文件 该函数返回整形-1:代表文件出错 0:代表下载成功 1:代表问价已经存在
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
	 * 根据URL得到输入流
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
