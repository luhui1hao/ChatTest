package com.example.chattest;

import java.util.List;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.chattest.bean.Info;

public class MsgAdapter extends BaseAdapter {
	public static final String TAG = "MsgAdapter";
	private List<Info> msgList;
	private Context context;
	private RelativeLayout leftLayout, rightLayout;
	private TextView leftMsgTV, rightMsgTV;
	private ImageView leftImg, rightImg;
	private View view;
	private int mMinItemWith;//最小宽度
	private int mMaxItemWith;//最大宽度
	private RelativeLayout rLeftLayout, rRightLayout;
	private FrameLayout leftLength, rightLength;
	private TextView leftTV, rightTV;

	public MsgAdapter(Context context, List<Info> msgList) {
		this.context = context;
		this.msgList = msgList;
		
		//下面的操作是为音频准备的
		//获取屏幕的宽度 
		WindowManager wManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wManager.getDefaultDisplay().getMetrics(outMetrics);
		mMaxItemWith = (int) (outMetrics.widthPixels * 0.7f);
		mMinItemWith = (int) (outMetrics.widthPixels * 0.15f);
	}

	@Override
	public int getCount() {
		return msgList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Info info = msgList.get(position);
		// *************************找到控件***********************
		// 判断要发送的数据的类型，加载不同的布局
		if (info.getFileType().equals(Info.TEXT_TYPE)) {// 发送文本
			view = LayoutInflater.from(context)
					.inflate(R.layout.text_msg_item, null);
			leftLayout = (RelativeLayout) view.findViewById(R.id.left_layout);
			rightLayout = (RelativeLayout) view.findViewById(R.id.right_layout);
			leftMsgTV = (TextView) view.findViewById(R.id.left_msg);
			rightMsgTV = (TextView) view.findViewById(R.id.right_msg);
		} else if (info.getFileType().equals(Info.IMAGE_TYPE)) {// 发送图片
			view = LayoutInflater.from(context).inflate(
					R.layout.image_msg_item, null);
			leftLayout = (RelativeLayout) view
					.findViewById(R.id.image_left_layout);
			rightLayout = (RelativeLayout) view
					.findViewById(R.id.image_right_layout);
			leftImg = (ImageView) view.findViewById(R.id.left_img);
			rightImg = (ImageView) view.findViewById(R.id.right_img);
		}else if(info.getFileType().equals(Info.AUDIO_TYPE)){//发送音频
			//给View填充布局并找到每个控件的对象
			view = LayoutInflater.from(context).inflate(R.layout.recorder_msg_item, null);
			//左右的布局
			rLeftLayout = (RelativeLayout) view.findViewById(R.id.recorder_left_layout);
			rRightLayout = (RelativeLayout) view.findViewById(R.id.recorder_right_layout);
			//框框背景
			leftLength = (FrameLayout)view.findViewById(R.id.recorder_length_left);
			rightLength = (FrameLayout)view.findViewById(R.id.recorder_length);
			//音频时间TextView
			leftTV = (TextView)view.findViewById(R.id.recorder_time_left);
			rightTV = (TextView)view.findViewById(R.id.recorder_time);
		}

		// **********************设置控件内容**********************
		// 判断消息类型从而选择设置控件内容
		switch (info.getFileType()) {
		case Info.TEXT_TYPE:
			// 判断消息是发送的还是接收的
			if (info.getSendOrRecvFlag() == Info.TYPE_SENT) {
				// 如果是发出的消息，则显示右边的消息布局，将左边的消息布局隐藏
				rightLayout.setVisibility(View.VISIBLE);
				leftLayout.setVisibility(View.GONE);
				rightMsgTV.setText(info.getDetail());
			} else if(info.getSendOrRecvFlag() == Info.TYPE_RECEIVED){
				// 如果是收到的消息，则显示左边的消息布局，将右边的消息布局隐藏
				leftLayout.setVisibility(View.VISIBLE);
				rightLayout.setVisibility(View.GONE);
				leftMsgTV.setText(info.getDetail());
			}
			break;
		case Info.IMAGE_TYPE:
			// 判断消息是发送的还是接收的
			if (info.getSendOrRecvFlag() == Info.TYPE_SENT) {// 发送
				// 如果是发出的消息，则显示右边的消息布局，将左边的消息布局隐藏
				rightLayout.setVisibility(View.VISIBLE);
				leftLayout.setVisibility(View.GONE);
				//设置缩略图
				rightImg.setImageBitmap(info.getBitmap());
				
			} else if(info.getSendOrRecvFlag() == Info.TYPE_RECEIVED){// 接收
				// 如果是收到的消息，则显示左边的消息布局，将右边的消息布局隐藏
				leftLayout.setVisibility(View.VISIBLE);
				rightLayout.setVisibility(View.GONE);
				//设置缩略图
				rightImg.setImageBitmap(info.getBitmap());
			}
			break;
		case Info.AUDIO_TYPE:
			//判断消息是发送的还是接收的
			if(info.getSendOrRecvFlag() == Info.TYPE_SENT){
				// 如果是发出的消息，则显示右边的消息布局，将左边的消息布局隐藏
				rRightLayout.setVisibility(View.VISIBLE);
				rLeftLayout.setVisibility(View.GONE);
				//设置时间文本
				rightTV.setText(Math.round(info.getSeconds())+"\"");
				//设置音频框框长度
				ViewGroup.LayoutParams lParams=rightLength.getLayoutParams();
				lParams.width=(int) (mMinItemWith+mMaxItemWith/60f*info.getSeconds());
				rightLength.setLayoutParams(lParams);
			}else if(info.getSendOrRecvFlag() == Info.TYPE_RECEIVED){
				// 如果是收到的消息，则显示左边的消息布局，将右边的消息布局隐藏
				rRightLayout.setVisibility(View.GONE);
				rLeftLayout.setVisibility(View.VISIBLE);
				//设置时间文本
				leftTV.setText(Math.round(info.getSeconds())+"\"");
				//设置音频框框长度
				ViewGroup.LayoutParams lParams=leftLength.getLayoutParams();
				lParams.width=(int) (mMinItemWith+mMaxItemWith/60f*info.getSeconds());
				leftLength.setLayoutParams(lParams);
			}
		}

		return view;
	}
	
}
