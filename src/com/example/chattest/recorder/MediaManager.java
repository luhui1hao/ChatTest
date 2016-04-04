package com.example.chattest.recorder;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;

public class MediaManager {
	private static MediaPlayer mMediaPlayer; 
    private static boolean isPause;
 
    /**
     * ��������
     * @param filePath
     * @param onCompletionListener
     */
    public static void playSound(String filePath,OnCompletionListener onCompletionListener) {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
 
            //����һ��error������
            mMediaPlayer.setOnErrorListener(new OnErrorListener() {
 
                public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        } else {
            mMediaPlayer.reset();
        }
 
        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception e) {
 
        }
    }
 
    /**
     * ��ͣ����
     */
    public static void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) { //���ڲ��ŵ�ʱ��
            mMediaPlayer.pause();
            isPause = true;
        }
    }
 
    /**
     * ��ǰ��isPause״̬
     */
    public static void resume() {
        if (mMediaPlayer != null && isPause) {  
            mMediaPlayer.start();
            isPause = false;
        }
    }
 
    /**
     * �ͷ���Դ
     */
    public static void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
