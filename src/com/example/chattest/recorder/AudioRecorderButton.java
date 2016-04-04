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
	private static final int STATE_NORMAL = 1;// Ĭ�ϵ�״̬
    private static final int STATE_RECORDING = 2;// ����¼��
    private static final int STATE_WANT_TO_CANCEL = 3;// ϣ��ȡ��
    private int mCurrentState = STATE_NORMAL; // ��ǰ��״̬
    private boolean isRecording = false;
    private DialogManager mDialogManager;
    private AudioManager mAudioManager;
    //���ڼ�¼¼��ʱ��
    private float mTime;
    // �Ƿ񴥷�longClick
    private boolean mReady;
    
	public AudioRecorderButton(Context context) {
		this(context,null);
	}

	/**
	 * ���������Ĺ��췽��
	 * @param context
	 * @param attrs
	 */
	public AudioRecorderButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		//�ڳ�ʼ����ʱ��ʹ���һ��DialogManager
		mDialogManager = new DialogManager(getContext());
		
		String dir = FileUtils.getAudioDirAdd();
		mAudioManager = AudioManager.getInstance(dir);
        mAudioManager.setOnAudioStateListener(new AudioStateListener() {
 
            public void wellPrepared() {
                mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
            }
        });
		
		// �����������button�����ڹ��췽������Ӽ����¼�
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
     * ¼����ɺ�Ļص�
     */
    public interface AudioFinishRecorderListener {
        void onFinish(float seconds, String filePath);
    }
 
    private AudioFinishRecorderListener mListener;
 
    public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener) {
        mListener = listener;
    }
	
	/**
     * ��ȡ������С���߳�
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
	
	//�������������¼�
	private Handler mHandler = new Handler() {
		 
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_AUDIO_PREPARED:
                // ��ʾ��Ԓ���ڿ�ʼ¼���Ժ�
                mDialogManager.showRecordingDialog();
                isRecording = true;
                // ����һ���߳�
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
     * ��Ļ�Ĵ����¼�
     */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int x = (int)event.getX();//���x����
		int y = (int)event.getY();//���y����
		
		switch(action){
		case MotionEvent.ACTION_DOWN://����ȥ��ʱ�򴥷�
			isRecording = true;
			changeState(STATE_RECORDING);//�ı䵱ǰ״̬
			break;
		case MotionEvent.ACTION_MOVE://�ƶ���ʱ�򴥷�
			if(isRecording){//�ж��Ƿ��Ѿ���ʼ¼����
				// ����x,y�����꣬�ж��Ƿ�Ҫȡ��
				if (wantToCancel(x, y)) {
					changeState(STATE_WANT_TO_CANCEL);
				} else {
					changeState(STATE_RECORDING);
				}
			}
			break;
		case MotionEvent.ACTION_UP://�ɿ���ʱ�򴥷�
			 if (!mReady) {
	                reset();
	                return super.onTouchEvent(event);
	            }
	            if (!isRecording || mTime < 0.6f) {
	                mDialogManager.tooShort();
	                mAudioManager.cancel();
	                mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 1000);// �ӳ���ʾ�Ի���
	            } else if (mCurrentState == STATE_RECORDING) { // ����¼����ʱ�򣬽���
	                mDialogManager.dismissDialog();
	                mAudioManager.release();
	 
	                if (mListener != null) {
	                    mListener.onFinish(mTime,mAudioManager.getCurrentFilePath());
	                }
	 
	            } else if (mCurrentState == STATE_WANT_TO_CANCEL) { // ��Ҫȡ��
	                mDialogManager.dismissDialog();
	                mAudioManager.cancel();
	            }
	            reset();
	            break;
		}
		
		return super.onTouchEvent(event);
	}

	/*
	 * �ָ�״̬����־λ
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
	 * ��Ҫ�Ǹ��ݲ�ͬ��״̬���ı䰴ť���ı��ͱ���,�Լ�Dialog��ͼƬ������
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
				//��������ı���ǰ�ť��״̬
//				setBackgroundResource(R.drawable.btn_recorder_recording);
//				setText(R.string.str_recorder_recording);
				//����ı����Dialog��״̬
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
