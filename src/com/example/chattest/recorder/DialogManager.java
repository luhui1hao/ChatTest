package com.example.chattest.recorder;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chattest.R;


public class DialogManager {
	private Dialog mDialog;
	private Context mContext;
	private ImageView mIcon, mVoice;
	private TextView mLabel;

	public DialogManager(Context context) {
		mContext = context;
	}

	public void showRecordingDialog() {
		mDialog = new Dialog(mContext, R.style.Theme_AudioDialog);
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.dialog_recorder, null);
		mDialog.setContentView(view);

		mIcon = (ImageView) mDialog.findViewById(R.id.id_recorder_dialog_icon);
		mVoice = (ImageView) mDialog
				.findViewById(R.id.id_recorder_dialog_voice);
		mLabel = (TextView) mDialog.findViewById(R.id.id_recorder_dialog_label);

		mDialog.show();
	}

	public void recording(){
		if(mDialog != null && mDialog.isShowing()){ //显示状态
			mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.VISIBLE);
            mLabel.setVisibility(View.VISIBLE);
 
            mIcon.setImageResource(R.drawable.recorder);
            mLabel.setText(R.string.str_dialog_recording);
        }
	}

	public void wantToCancel() {
		if(mDialog != null && mDialog.isShowing()){ //显示状态
			mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLabel.setVisibility(View.VISIBLE);
 
            mIcon.setImageResource(R.drawable.cancel);
            mLabel.setText(R.string.str_recorder_want_cancel);
        }
	}

	public void tooShort() {
		if(mDialog != null && mDialog.isShowing()){ //显示状态
			mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLabel.setVisibility(View.VISIBLE);
 
            mIcon.setImageResource(R.drawable.voice_to_short);
            mLabel.setText(R.string.str_too_short);
        }
	}

	/**
	 * 移除对话框
	 */
	public void dismissDialog() {
		if(mDialog != null && mDialog.isShowing()){ //显示状态
			mDialog.dismiss();
			mDialog = null;
        }
	}
	
	/**
	 * 通过level去更新voice上的图片
	 * @param level 1-7
	 */
	public void updateVoiceLevel(int level){
		if(mDialog != null && mDialog.isShowing()){ //显示状态
//			mIcon.setVisibility(View.VISIBLE);
//			mVoice.setVisibility(View.VISIBLE);
//			mLabel.setVisibility(View.VISIBLE);
 
            //设置图片的id
            int resId = mContext.getResources().getIdentifier("v" + level, "drawable", mContext.getPackageName());
            mVoice.setImageResource(resId);
        }
	}
}
