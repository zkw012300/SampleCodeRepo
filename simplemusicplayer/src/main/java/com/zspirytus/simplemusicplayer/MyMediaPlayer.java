package com.zspirytus.simplemusicplayer;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

import com.zspirytus.simplemusicplayer.entity.Music;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MyMediaPlayer implements MediaPlayer.OnPreparedListener {

    private MediaPlayer mMediaPlayer;
    private MyTimer mTimer;
    private AssetManager mAssetManager;
    private IProgressListener mIProgressListener;

    private static class SingletonHolder {
        static MyMediaPlayer INSTANCE = new MyMediaPlayer();
    }

    private MyMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mTimer = new MyTimer();
    }

    public static MyMediaPlayer getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        beginToPlay();
    }

    public void setProgressListener(IProgressListener listener) {
        mIProgressListener = listener;
    }

    public void init(AssetManager assetManager) {
        mAssetManager = assetManager;
    }

    public void play(Music music) {
        try {
            AssetFileDescriptor assetFileDescriptor = mAssetManager.openFd(music.getMusicFilePath());
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        mMediaPlayer.pause();
        mTimer.pause();
    }

    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    private void beginToPlay() {
        mMediaPlayer.start();
        mTimer.start();
    }

    public interface IProgressListener {
        void onProgressChange(int currentMilliseconds);
    }

    private class MyTimer {

        private Timer mTimer;
        private TimerTask mTimerTask;

        private boolean hasStart = false;

        public void start() {
            hasStart = true;
            final int SECONDS = 1000;
            mTimer = new Timer();
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    int currentPlayingSeconds = MyMediaPlayer.getInstance().getCurrentPosition();
                    if (mIProgressListener != null)
                        mIProgressListener.onProgressChange(currentPlayingSeconds);
                }
            };
            mTimer.schedule(mTimerTask, 0, SECONDS);
        }

        public void pause() {
            if (hasStart) {
                mTimer.cancel();
                mTimer = null;
                mTimerTask.cancel();
                mTimerTask = null;
                hasStart = false;
            }
        }
    }

}
