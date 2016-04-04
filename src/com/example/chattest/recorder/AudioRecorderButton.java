package com.example.chattest.recorder;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.example.chattest.recorder.AudioManager.AudioStateListener;
import com.example.chattest.utils.FileUtils;

public class AudioRecorderButton extends Button {
	private static final int DISTANCE_Y_CANCEL = 50;
	private static final int STATE_NORMAL = 1;// 默认的状态
    private static final int STATE_RECORDING = 2;// 正在录音
    private static final int STATE_WANT_TO_CANCEL = 3;// 希望取消
    private int mCurrentState = STATE_NORMAL; // 当前的状态
    private boolean isRecording = false;
    private DialogManager mDialogManager;
    private AudioManager mAudioManager;
    //用于记录录音时长
    private float mTime;
    // 是否触发longClick
    private boolean mReady;
    
	public AudioRecorderButton(Context context) {
		this(context,null);
	}

	/**
	 * 两个参数的构造方法
	 * @param context
	 * @param attrs
	 */
	public AudioRecorderButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		//在初始化的时候就创建一个DialogManager
		mDialogManager = new DialogManager(getContext());
		
		String dir = FileUtils.getAudioDirAdd();
		mAudioManager = AudioManager.getInstance(dir);
        mAudioManager.setOnAudioStateListener(new AudioStateListener() {
 
            public void wellPrepared() {
                mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
            }
        });
		
		// 由于这个类是button所以在构造方法中添加监听事件
        setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View v) {
            	 mReady = true;
                 mAudioManager.prepareAudio();
                return false;
            }
        });
	}
	
	private static final int MSG_AUDIO_PREPARED = 0x110;
	private static final int MSG_VOICE_CHANGED = 0x111;
	private static final int MSG_DIALOG_DIMISS = 0x112;
	
	/**
     * 录音完成后的回调
     */
    public interface AudioFinishRecorderListener {
        void onFinish(float seconds, String filePath);
    }
 
    private AudioFinishRecorderListener mListener;
 
    public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener) {
        mListener = listener;
    }
	
	/**
     * 获取音量大小的线程
     */
    private Runnable mGetVoiceLevelRunnable = new Runnable() {
 
        public void run() {
            while (isRecording) {
                try {
                    Thread.sleep(100);
                    mTime += 0.1f;
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
	
	//用来处理三个事件
	private Handler mHandler = new Handler() {
		 
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_AUDIO_PREPARED:
                // 显示對話框在开始录音以后
                mDialogManager.showRecordingDialog();
                isRecording = true;
                // 开启一个线程
                new Thread(mGetVoiceLevelRunnable).start();
                break;
 
            case MSG_VOICE_CHANGED:
                mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(7));
                break;
 
            case MSG_DIALOG_DIMISS:
                mDialogManager.dismissDialog();
                break;
 
            }
 
            super.handleMessage(msg);
        }
    };

    /**
     * 屏幕的触摸事件
     */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int x = (int)event.getX();//获得x坐标
		int y = (int)event.getY();//获得y坐标
		
		switch(action){
		case MotionEvent.ACTION_DOWN://按下去的时候触发
			isRecording = true;
			changeState(STATE_RECORDING);//改变当前状态
			break;
		case MotionEvent.ACTION_MOVE://移动的时候触发
			if(isRecording){//判断是否已经开始录音了
				// 根据x,y的坐标，判断是否要取消
				if (wantToCancel(x, y)) {
					changeState(STATE_WANT_TO_CANCEL);
				} else {
					changeState(STATE_RECORDING);
				}
			}
			break;
		case MotionEvent.ACTION_UP://松开的时候触发
			 if (!mReady) {
	                reset();
	                return super.onTouchEvent(event);
	            }
	            if (!isRecording || mTime < 0.6f) {
	                mDialogManager.tooShort();
	                mAudioManager.cancel();
	                mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 1000);// 延迟显示对话框
	            } else if (mCurrentState == STATE_RECORDING) { // 正在录音的时候，结束
	                mDialogManager.dismissDialog();
	                mAudioManager.release();
	 
	                if (mListener != null) {
	                    mListener.onFinish(mTime,mAudioManager.getCurrentFilePath());
	                }
	 
	            } else if (mCurrentState == STATE_WANT_TO_CANCEL) { // 想要取消
	                mDialogManager.dismissDialog();
	                mAudioManager.cancel();
	            }
	            reset();
	            break;
		}
		
		return super.onTouchEvent(event);
	}

	/*
	 * 恢复状态及标志位
	 */
	private void reset() {
		isRecording = false;
		mTime = 0;
		mReady = false;
		changeState(STATE_NORMAL);
	}

	private boolean wantToCancel(int x, int y) {
		if(x<0 || x>getWidth()){
			return true;
		}
		if(y<-DISTANCE_Y_CANCEL || y>getHeight()+DISTANCE_Y_CANCEL){
			return true;
		}
		return false;
	}

	/*
	 * 主要是根据不同的状态来改变按钮的文本和背景,以及Dialog的图片和文字
	 */
	private void changeState(int stateRecording) {
		if(stateRecording != mCurrentState){
			mCurrentState = stateRecording;
			switch(stateRecording){
			case STATE_NORMAL:
//				setBackgroundResource(R.drawable.btn_recorder_normal);
//				setText(R.string.str_recorder_normal);
				break;
			case STATE_RECORDING:
				//下面两句改变的是按钮的状态
//				setBackgroundResource(R.drawable.btn_recorder_recording);
//				setText(R.string.str_recorder_recording);
				//下面改变的是Dialog的状态
				if(isRecording){
					mDialogManager.recording();
				}
				break;
			case STATE_WANT_TO_CANCEL:
//				setBackgroundResource(R.drawable.btn_recorder_recording);
//				setText(R.string.str_recorder_want_cancel);
				mDialogManager.wantToCancel();
				break;
			}
		}
	}
}
