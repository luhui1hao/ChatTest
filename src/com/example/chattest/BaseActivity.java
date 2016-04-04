package com.example.chattest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import com.example.chattest.bean.MessageForward;
import com.example.chattest.bean.MessageGroup;
import com.example.chattest.bean.MessageResponse;
import com.example.chattest.xml.CreateGroup;
import com.example.chattest.xml.DeleteGroup;
import com.example.chattest.xml.Forward;
import com.handkoo.smartvideophone05.handler.HK_UI_MainMsgHandler;
import com.handkoo.smartvideophone05.utils.HK_Message_Tool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 *
 * @author ly
 *
 */
public class BaseActivity extends Activity {
	private static final String TAG = "BaseActivity";

	private MsgHandler handler = new MsgHandler();

	private String ip = "221.6.106.35";
	private int port = 10905;
	private String imei = "241ab3d06d9209343639c95f3271753e";
//	private String imei = "8661010200145763";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * 开启消息服务器
	 */
	protected void startMsg() {
		HK_Message_Tool.mStartMsgFun(ip, port, imei, handler);
	}

	/**
	 * 关闭消息服务器
	 */
	protected void stopMsg() {
		HK_Message_Tool.mStopMsgFun();
	}

	private void parser(int type, String xml) {
		if (xml == null)
			return;
		Log.i(TAG, "xml : " + xml);
		try {
			switch (type) {
			case 37:// 请求响应
				if (xml.contains("GROUPCREAT")) {
					createGroupResponse(xml);
				} else if (xml.contains("GROUPDEL")) {
					deleteGroupResponse(xml);
				} else if (xml.contains("FORWARD")) {
					forwardResponse(xml);
				}
				break;
			case 38:// 通知
				if (xml.contains("GROUPCREAT")) {
					createGroup(xml);
				} else if (xml.contains("GROUPDEL")) {
					deleteGroup(xml);
				} else if (xml.contains("FORWARD")) {
					forward(xml);
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			Log.e(TAG, "Exception occurred : " + e.getMessage());
		}
	}
	
	/**
	 * 请求建组响应
	 * 
	 * @param xml
	 * @throws Exception
	 */
	private void createGroupResponse(String xml) throws Exception{
		Log.i(TAG, "createGroupResponse");
		InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		MessageResponse msg = CreateGroup.response(is);
		//TODO update database
		// notification UI update
//		updateUI("COM_HANDKOO_CREATE_GROUP_RESPONSE");
	}
	
	/**
	 * 请求删组响应
	 * 
	 * @param xml
	 * @throws Exception
	 */
	private void deleteGroupResponse(String xml) throws Exception{
		Log.i(TAG, "deleteGroupResponse");
		InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		MessageResponse msg = DeleteGroup.response(is);
		//TODO update database
		// notification UI update
//		updateUI("COM_HANDKOO_DELETE_GROUP_RESPONSE");
	}
	
	/**
	 * 请求转发数据响应
	 * 
	 * @param xml
	 * @throws Exception
	 */
	private void forwardResponse(String xml) throws Exception{
		Log.i(TAG, "forwardResponse");
		InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		MessageResponse msg = Forward.response(is);
		//TODO update database
		// notification UI update
//		updateUI("COM_HANDKOO_FORWARD_RESPONSE");
	}

	/**
	 * 服务器通知建组
	 * 
	 * @param xml
	 * @throws Exception
	 */
	private void createGroup(String xml) throws Exception {
		Log.i(TAG, "createGroup");
		InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		MessageGroup msg = CreateGroup.parser(is);
		//TODO update database
		// notification UI update
		updateUI("COM_HANDKOO_CREATE_GROUP", msg);
	}

	/**
	 * 服务器通知删组
	 * 
	 * @param xml
	 * @throws Exception
	 */
	private void deleteGroup(String xml) throws Exception {
		Log.i(TAG, "deleteGroup");
		InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		MessageGroup msg = DeleteGroup.parser(is);
		//TODO update database
		// notification UI update
		updateUI("COM_HANDKOO_DELETE_GROUP", msg);
	}

	/**
	 * 服务器通知转发数据
	 * 
	 * @param xml
	 * @throws Exception
	 */
	private void forward(String xml) throws Exception {
		Log.i(TAG, "forward");
		InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		MessageForward msg = Forward.parser(is);
		//TODO update database
		// notification UI update
		updateUI("COM_HANDKOO_FORWARD", msg);
	}

	private void updateUI(String action, Serializable msg) {
		Intent intent = new Intent(action);
		intent.putExtra("obj", msg);
		sendBroadcast(intent);
	}

	@SuppressLint("HandlerLeak")
	class MsgHandler extends HK_UI_MainMsgHandler {
		
		@Override
		public void mParaGps(int arg0, int arg1, String arg2) {
		}

		@Override
		public void mParaKeepAlive(int arg0, int arg1, String arg2) {
			Log.i(TAG, "keep-alive");
		}

		@Override
		public void mParaOtherMsgInfo(int arg0, int arg1, String arg2) {
			Log.i(TAG, "mParaOtherMsgInfo :  arg0 = " + arg0 + ", arg1 = " + arg1 +", arg2 = " + arg2);
			parser(arg0, arg2);
		}

		@Override
		public void mParaStartAudio(int arg0, int arg1, String arg2) {
		}

		@Override
		public void mParaStartVideo(int arg0, int arg1, String arg2) {
		}

		@Override
		public void mParaStopAudio(int arg0, int arg1, String arg2) {
		}

		@Override
		public void mParaStopVideo(int arg0, int arg1, String arg2) {
		}

		@Override
		public void mParaTranferMsgByAudio(int arg0, int arg1, String arg2) {
		}

		@Override
		public void mParaTranferMsgByDev(int arg0, int arg1, String arg2) {
			Log.i(TAG, "mParaTranferMsgByDev :  arg0 = " + arg0 + ", arg1 = " + arg1 +", arg2 = " + arg2);
		}

		@Override
		public void mParaUserLog(int arg0, int arg1, String arg2) {
		}

		@Override
		public void mParaXml(int arg0, int arg1, String arg2) {
			Log.i(TAG, "mParaXml :  arg0 = " + arg0 + ", arg1 = " + arg1 +", arg2 = " + arg2);
		}
	}
}
