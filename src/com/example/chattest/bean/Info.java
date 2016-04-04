package com.example.chattest.bean;

import java.io.Serializable;

import android.graphics.Bitmap;

/**
 *
 * @author ly
 *
 */
public class Info implements Serializable{
	private String fileType;
	private String sendType;
	private int groupId;
	private String identify;
	private String detail;
	private String time;

	// 新添加的变量
	private int sendOrRecvFlag;// 发送接收标识符
	private String localFileName;
	private float seconds;// 音频的时长
	private Bitmap bm;
	//新添加的常量
	public static final int READ = 1;//已读
	public static final int UNREAD = 0;//未读
	public static final int TYPE_RECEIVED = 0;//接收
	public static final int TYPE_SENT = 1;//发送
	public static final String TEXT_TYPE = "text";
	public static final String IMAGE_TYPE = "pic";
	public static final String AUDIO_TYPE = "audio";
	public static final String ONLY = "only";
	public static final String GROUP = "group";

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getSendType() {
		return sendType;
	}

	public void setSendType(String sendType) {
		this.sendType = sendType;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getIdentify() {
		return identify;
	}

	public void setIdentify(String identify) {
		this.identify = identify;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	//
	public void setSendOrRecvFlag(int sendOrRecvFlag) {
		this.sendOrRecvFlag = sendOrRecvFlag;
	}

	public int getSendOrRecvFlag() {
		return sendOrRecvFlag;
	}

	public String getLocalFileName() {
		return localFileName;
	}

	public void setLocalFileName(String localFileName) {
		this.localFileName = localFileName;
	}

	public float getSeconds() {
		return seconds;
	}

	public void setSeconds(float seconds) {
		this.seconds = seconds;
	}

	public void setBitmap(Bitmap bm) {
		this.bm = bm;
	}

	public Bitmap getBitmap() {
		return bm;
	}
}
