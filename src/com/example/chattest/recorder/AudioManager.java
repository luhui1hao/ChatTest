package com.example.chattest.recorder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import android.media.MediaRecorder;

public class AudioManager {
	private MediaRecorder mMediaRecorder;// �����Ҫȥ��һ�����
	private String mDir;
	private String mCurrentFilePath;
	private static AudioManager mInstance;
	private boolean isPrepare;//�ж��Ƿ�prepare�õı���

	private AudioManager(String dir) {
		mDir = dir;
	}

	public static AudioManager getInstance(String dir) {
		synchronized (AudioManager.class) {
			if (mInstance == null) {
				mInstance = new AudioManager(dir);
			}
			return mInstance;
		}
	}

	/**
     * ʹ�ýӿ� ���ڻص�
     */
	public interface AudioStateListener {
		void wellPrepared();
	}
	
	public AudioStateListener mListener;
	
	/**
     * �ص�����
     */
    public void setOnAudioStateListener(AudioStateListener listener) {
        mListener = listener;
    }

	public void prepareAudio() {// ׼��
		try {
			isPrepare = false;
			File dir = new File(mDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			//�ļ����������ȡ
			String fileName = generateFileName();
			File file = new File(dir, fileName);

			mCurrentFilePath = file.getAbsolutePath();

			mMediaRecorder = new MediaRecorder();
			// ��������ļ�
			mMediaRecorder.setOutputFile(mCurrentFilePath);
			// ����MediaRecorder����ƵԴΪ��˷�
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			// ������Ƶ��ʽ
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
			// ������Ƶ����
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			// ׼��¼��
			mMediaRecorder.prepare();
			// ��ʼ
			mMediaRecorder.start();
			// ׼������
			isPrepare = true;
			if (mListener != null) {
				mListener.wellPrepared();
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	 /**
     * ��������ļ�������
     */
    private String generateFileName() {
        return UUID.randomUUID().toString() + ".amr";
    }
 
    public int getVoiceLevel(int maxlevel) {
        if (isPrepare) {
            try {
                // mMediaRecorder.getMaxAmplitude() 1~32767
                return maxlevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
            } catch (Exception e) {
            	//����ֱ�Ӱ��쳣������Ϊ�˷�ֹ����ΪgetMaxAmplitude��ȡʧ�ܶ����³���ҵ�
            }
        }
        return 1;
    }

	public void release() {// �ͷ�
		mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder = null;
	}

	public void cancel() {// ȡ��
		release();
        if (mCurrentFilePath != null) {
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }
	}

	public String getCurrentFilePath() {
		return mCurrentFilePath;
	}
}
