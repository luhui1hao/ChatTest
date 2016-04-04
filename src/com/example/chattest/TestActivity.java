package com.example.chattest;

import java.util.ArrayList;
import java.util.List;

import com.example.chattest.bean.Group;
import com.example.chattest.bean.Info;
import com.example.chattest.bean.Member;
import com.example.chattest.bean.MessageForward;
import com.example.chattest.bean.MessageGroup;
import com.example.chattest.xml.CreateGroup;
import com.example.chattest.xml.DeleteGroup;
import com.example.chattest.xml.Forward;
import com.handkoo.smartvideophone05.utils.HK_Message_Tool;
import com.handkoo.smartvideophone05.utils.HK_Message_XS_Util;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

/** 
 *
 * @author ly
 *
 */
public class TestActivity extends BaseActivity{
	private static final String TAG = "TestActivity";
	private Handler handler =new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		
		startMsg();
		//TODO create
//		handler.postDelayed(new Runnable() {
//
//			@Override
//			public void run() {
//				Group group = new Group();
//				group.setGroupNum("Test");
//				List<Member> members = new ArrayList<Member>();
//				for (int i = 0; i < 5; i++) {
//					Member member = new Member();
//					member.setName("member" + i);
//					members.add(member);
//				}
//				group.setMembers(members);
//				List<Group> groups = new ArrayList<Group>();
//				groups.add(group);
//				MessageGroup msg = new MessageGroup();
//				msg.setGroups(groups);
//
//				String xml = CreateGroup.pack(msg, 1);
//				Log.i(TAG, xml);
//
//				byte[] mData = HK_Message_XS_Util.getInstance().mGetByteFromPara((byte) 37, xml);
//				HK_Message_Tool.mSendMsgPkg(mData);
//			}
//		}, 5 * 1000);
		
		//TODO delete
//		handler.postDelayed(new Runnable() {
//			
//			@Override
//			public void run() {
//				Group group = new Group();
//				group.setGroupId(10218);
//				List<Group> groups = new ArrayList<Group>();
//				groups.add(group);
//				MessageGroup msg = new MessageGroup();
//				msg.setGroups(groups);
//				
//				String xml = DeleteGroup.pack(msg, 1);
//				Log.i(TAG, xml);
//				
//				byte[] mData = HK_Message_XS_Util.getInstance().mGetByteFromPara((byte) 37, xml);
//				HK_Message_Tool.mSendMsgPkg(mData);
//			}
//		}, 5 * 1000);
		
		//TODO forward
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Info info = new  Info();
				info.setFileType("text");
				info.setSendType("group");
				info.setIdentify("100220");
				info.setDetail("detail");
				
				List<Info> infos = new ArrayList<Info>();
				infos.add(info);
				MessageForward msg = new MessageForward();
				msg.setInfos(infos);
				
				String xml = Forward.pack(msg, 1);
				Log.i(TAG, xml);
				
				byte[] mData = HK_Message_XS_Util.getInstance().mGetByteFromPara((byte) 37, xml);
				HK_Message_Tool.mSendMsgPkg(mData);
			}
		}, 5 * 1000);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopMsg();
	}
}
