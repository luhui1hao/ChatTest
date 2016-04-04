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
	private int mMinItemWith;//��С���
	private int mMaxItemWith;//�����
	private RelativeLayout rLeftLayout, rRightLayout;
	private FrameLayout leftLength, rightLength;
	private TextView leftTV, rightTV;

	public MsgAdapter(Context context, List<Info> msgList) {
		this.context = context;
		this.msgList = msgList;
		
		//����Ĳ�����Ϊ��Ƶ׼����
		//��ȡ��Ļ�Ŀ�� 
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
		// *************************�ҵ��ؼ�***********************
		// �ж�Ҫ���͵����ݵ����ͣ����ز�ͬ�Ĳ���
		if (info.getFileType().equals(Info.TEXT_TYPE)) {// �����ı�
			view = LayoutInflater.from(context)
					.inflate(R.layout.text_msg_item, null);
			leftLayout = (RelativeLayout) view.findViewById(R.id.left_layout);
			rightLayout = (RelativeLayout) view.findViewById(R.id.right_layout);
			leftMsgTV = (TextView) view.findViewById(R.id.left_msg);
			rightMsgTV = (TextView) view.findViewById(R.id.right_msg);
		} else if (info.getFileType().equals(Info.IMAGE_TYPE)) {// ����ͼƬ
			view = LayoutInflater.from(context).inflate(
					R.layout.image_msg_item, null);
			leftLayout = (RelativeLayout) view
					.findViewById(R.id.image_left_layout);
			rightLayout = (RelativeLayout) view
					.findViewById(R.id.image_right_layout);
			leftImg = (ImageView) view.findViewById(R.id.left_img);
			rightImg = (ImageView) view.findViewById(R.id.right_img);
		}else if(info.getFileType().equals(Info.AUDIO_TYPE)){//������Ƶ
			//��View��䲼�ֲ��ҵ�ÿ���ؼ��Ķ���
			view = LayoutInflater.from(context).inflate(R.layout.recorder_msg_item, null);
			//���ҵĲ���
			rLeftLayout = (RelativeLayout) view.findViewById(R.id.recorder_left_layout);
			rRightLayout = (RelativeLayout) view.findViewById(R.id.recorder_right_layout);
			//��򱳾�
			leftLength = (FrameLayout)view.findViewById(R.id.recorder_length_left);
			rightLength = (FrameLayout)view.findViewById(R.id.recorder_length);
			//��Ƶʱ��TextView
			leftTV = (TextView)view.findViewById(R.id.recorder_time_left);
			rightTV = (TextView)view.findViewById(R.id.recorder_time);
		}

		// **********************���ÿؼ�����**********************
		// �ж���Ϣ���ʹӶ�ѡ�����ÿؼ�����
		switch (info.getFileType()) {
		case Info.TEXT_TYPE:
			// �ж���Ϣ�Ƿ��͵Ļ��ǽ��յ�
			if (info.getSendOrRecvFlag() == Info.TYPE_SENT) {
				// ����Ƿ�������Ϣ������ʾ�ұߵ���Ϣ���֣�����ߵ���Ϣ��������
				rightLayout.setVisibility(View.VISIBLE);
				leftLayout.setVisibility(View.GONE);
				rightMsgTV.setText(info.getDetail());
			} else if(info.getSendOrRecvFlag() == Info.TYPE_RECEIVED){
				// ������յ�����Ϣ������ʾ��ߵ���Ϣ���֣����ұߵ���Ϣ��������
				leftLayout.setVisibility(View.VISIBLE);
				rightLayout.setVisibility(View.GONE);
				leftMsgTV.setText(info.getDetail());
			}
			break;
		case Info.IMAGE_TYPE:
			// �ж���Ϣ�Ƿ��͵Ļ��ǽ��յ�
			if (info.getSendOrRecvFlag() == Info.TYPE_SENT) {// ����
				// ����Ƿ�������Ϣ������ʾ�ұߵ���Ϣ���֣�����ߵ���Ϣ��������
				rightLayout.setVisibility(View.VISIBLE);
				leftLayout.setVisibility(View.GONE);
				//��������ͼ
				rightImg.setImageBitmap(info.getBitmap());
				
			} else if(info.getSendOrRecvFlag() == Info.TYPE_RECEIVED){// ����
				// ������յ�����Ϣ������ʾ��ߵ���Ϣ���֣����ұߵ���Ϣ��������
				leftLayout.setVisibility(View.VISIBLE);
				rightLayout.setVisibility(View.GONE);
				//��������ͼ
				rightImg.setImageBitmap(info.getBitmap());
			}
			break;
		case Info.AUDIO_TYPE:
			//�ж���Ϣ�Ƿ��͵Ļ��ǽ��յ�
			if(info.getSendOrRecvFlag() == Info.TYPE_SENT){
				// ����Ƿ�������Ϣ������ʾ�ұߵ���Ϣ���֣�����ߵ���Ϣ��������
				rRightLayout.setVisibility(View.VISIBLE);
				rLeftLayout.setVisibility(View.GONE);
				//����ʱ���ı�
				rightTV.setText(Math.round(info.getSeconds())+"\"");
				//������Ƶ��򳤶�
				ViewGroup.LayoutParams lParams=rightLength.getLayoutParams();
				lParams.width=(int) (mMinItemWith+mMaxItemWith/60f*info.getSeconds());
				rightLength.setLayoutParams(lParams);
			}else if(info.getSendOrRecvFlag() == Info.TYPE_RECEIVED){
				// ������յ�����Ϣ������ʾ��ߵ���Ϣ���֣����ұߵ���Ϣ��������
				rRightLayout.setVisibility(View.GONE);
				rLeftLayout.setVisibility(View.VISIBLE);
				//����ʱ���ı�
				leftTV.setText(Math.round(info.getSeconds())+"\"");
				//������Ƶ��򳤶�
				ViewGroup.LayoutParams lParams=leftLength.getLayoutParams();
				lParams.width=(int) (mMinItemWith+mMaxItemWith/60f*info.getSeconds());
				leftLength.setLayoutParams(lParams);
			}
		}

		return view;
	}
	
}
