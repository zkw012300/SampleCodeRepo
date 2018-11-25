package com.zspirytus.simplemusicplayer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.zspirytus.simplemusicplayer.entity.Music;

public class PlayMusicService extends Service {

    private static final int PLAY_CONTROL_BINDER = 1;
    private static final int PLAY_PROGRESS_REGISTER_BINDER = 2;

    private BinderPool mBinder;
    private PlayControl mPlayControl;
    private PlayProgressRegister mPlayProgressRegister;

    private RemoteCallbackList<IOnProgressChange> mIOnProgressChangeList = new RemoteCallbackList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        MyMediaPlayer.getInstance().init(getAssets());
        MyMediaPlayer.getInstance().setProgressListener(new MyMediaPlayer.IProgressListener() {
            @Override
            public void onProgressChange(int currentSeconds) {
                notifyAllObserverProgressChange(currentSeconds);
            }
        });

    }

    @Override
    public IBinder onBind(Intent intent) {
        if (mBinder == null)
            mBinder = new BinderPool();
        return mBinder;
    }

    private void notifyAllObserverProgressChange(int currentMilliseconds) {
        int size = mIOnProgressChangeList.beginBroadcast();
        for (int i = 0; i < size; i++) {
            IOnProgressChange observer = mIOnProgressChangeList.getBroadcastItem(i);
            try {
                observer.onProgressChange(currentMilliseconds);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mIOnProgressChangeList.finishBroadcast();
    }

    public class BinderPool extends IBinderPool.Stub {
        @Override
        public IBinder getBinder(int binderCode) throws RemoteException {
            switch (binderCode) {
                case PLAY_CONTROL_BINDER:
                    if (mPlayControl == null)
                        mPlayControl = new PlayControl();
                    return mPlayControl;
                case PLAY_PROGRESS_REGISTER_BINDER:
                    if (mPlayProgressRegister == null)
                        mPlayProgressRegister = new PlayProgressRegister();
                    return mPlayProgressRegister;
                default:
                    return null;
            }
        }
    }

    private class PlayControl extends IPlayControl.Stub {
        @Override
        public void play(Music music) throws RemoteException {
            MyMediaPlayer.getInstance().play(music);
        }

        @Override
        public void pause() throws RemoteException {
            MyMediaPlayer.getInstance().pause();
        }
    }

    private class PlayProgressRegister extends IPlayProgressRegister.Stub {
        @Override
        public void registerProgressObserver(IOnProgressChange observer) throws RemoteException {
            mIOnProgressChangeList.register(observer);
        }

        @Override
        public void unregisterProgressObserver(IOnProgressChange observer) throws RemoteException {
            mIOnProgressChangeList.unregister(observer);
        }
    }

}
