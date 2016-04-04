package com.example.chattest.recorder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import android.media.MediaRecorder;

public class AudioManager {
	private MediaRecorder mMediaRecorder;// 这个类要去看一下书的
	private String mDir;
	private String mCurrentFilePath;
	private static AudioManager mInstance;
	private boolean isPrepare;//判断是否prepare好的变量

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
     * 使用接口 用于回调
     */
	public interface AudioStateListener {
		void wellPrepared();
	}
	
	public AudioStateListener mListener;
	
	/**
     * 回调方法
     */
    public void setOnAudioStateListener(AudioStateListener listener) {
        mListener = listener;
    }

	public void prepareAudio() {// 准备
		try {
			isPrepare = false;
			File dir = new File(mDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			//文件名在这里获取
			String fileName = generateFileName();
			File file = new File(dir, fileName);

			mCurrentFilePath = file.getAbsolutePath();

			mMediaRecorder = new MediaRecorder();
			// 设置输出文件
			mMediaRecorder.setOutputFile(mCurrentFilePath);
			// 设置MediaRecorder的音频源为麦克风
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			// 设置音频格式
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
			// 设置音频编码
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			// 准备录音
			mMediaRecorder.prepare();
			// 开始
			mMediaRecorder.start();
			// 准备结束
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
     * 随机生成文件的名称
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
            	//这里直接把异常捕获是为了防止，因为getMaxAmplitude获取失败而导致程序挂掉
            }
        }
        return 1;
    }

	public void release() {// 释放
		mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder = null;
	}

	public void cancel() {// 取消
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
