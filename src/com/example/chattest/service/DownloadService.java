package com.example.chattest.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.example.chattest.MainActivity;
import com.example.chattest.bean.Info;
import com.example.chattest.sqlite.MyDatabaseHelper;
import com.example.chattest.utils.FileUtils;
import com.example.chattest.utils.HttpDownloader;

/**
 * @author  ���� E-mail:
 * @date ����ʱ�䣺2016��2��18�� ����1:12:20
 * @version 1.0
 * @parameter
 * @since 
 * @return 
 */
public class DownloadService extends Service {
	public static final String TAG = "DownloadService";
	MyDatabaseHelper helper;
	HttpDownloader downloader;
	FileUtils utils;
	Info info;
	String url;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		helper = MyDatabaseHelper.getInstance(this);
		downloader = new HttpDownloader();
		utils = new FileUtils();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		info = (Info) intent.getSerializableExtra("info");
		url = info.getDetail();//���ص�ַ
		//�����߳̽������غʹ������ݿ����
		new Thread(new Runnable(){

			@Override
			public void run() {
				//�ж�����Ƶ����ͼƬ�ļ�
				if(info.getFileType() == Info.IMAGE_TYPE){
					String fileName = utils.generatePicName();
					int flag = downloader.downFile(url, utils.getPicDirName()+ "/", fileName);
					Log.e(TAG, "flag is " + flag);
					if(flag == 0){//�ļ�������سɹ����ʹ������ݿ�
						info.setLocalFileName(utils.getPicDirAdd() + "/" +  fileName);
						info.setBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeFile(info.getLocalFileName()),200, 200, true));
						helper.insertInfo(info);
						//����UI���½���
						Message msg = MainActivity.uiHandler.obtainMessage();
						Bundle data = new Bundle();
						data.putSerializable("info", info);
						msg.setData(data);
						MainActivity.uiHandler.sendMessage(msg);
					}
				}else if(info.getFileType() == Info.AUDIO_TYPE){
					String fileName = utils.generateAudioName();
					int flag = downloader.downFile(url, utils.getAudioDirName()+"/", fileName);
					if(flag == 0){//�ļ�������سɹ����ʹ������ݿ�
						info.setLocalFileName(utils.getAudioDirAdd() + "/" +  fileName);
						//TODO info������Ƶʱ��
						helper.insertInfo(info);
						//����UI���½���
						Message msg = MainActivity.uiHandler.obtainMessage();
						Bundle data = new Bundle();
						data.putSerializable("info", info);
						msg.setData(data);
						MainActivity.uiHandler.sendMessage(msg);
					}
				}
			}
			
		}).start();
		
		return super.onStartCommand(intent, flags, startId);
	}
}
