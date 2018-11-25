package com.zspirytus.simplemusicplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zspirytus.simplemusicplayer.entity.Music;
import com.zspirytus.simplemusicplayer.factory.MusicFactory;
import com.zspirytus.simplemusicplayer.utils.DateUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PLAY_CONTROL_BINDER = 1;
    private static final int PLAY_PROGRESS_REGISTER_BINDER = 2;
    private IOnProgressChangeImpl mProgressObserver = new IOnProgressChangeImpl();

    private IBinderPool mBinder;
    private IPlayControl mPlayControl;
    private ServiceConnection conn;

    private TextView mProgressText;

    private class IOnProgressChangeImpl extends IOnProgressChange.Stub {
        @Override
        public void onProgressChange(final int currentMilliseconds) throws RemoteException {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressText.setText(DateUtil.getMinutesSeconds(currentMilliseconds));
                }
            });
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initView();
        bindService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play:
                play();
                break;
            case R.id.pause:
                pause();
                break;
        }
    }

    private void initData() {
        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                mBinder = IBinderPool.Stub.asInterface(iBinder);
                register();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mBinder = null;
                unregister();
            }
        };
    }

    private void initView() {
        Button mPlayButton = findViewById(R.id.play);
        Button mPauseButton = findViewById(R.id.pause);
        mProgressText = findViewById(R.id.progress);
        mPlayButton.setOnClickListener(this);
        mPauseButton.setOnClickListener(this);
    }

    private void bindService() {
        Intent startServiceIntent = new Intent(MainActivity.this, PlayMusicService.class);
        bindService(startServiceIntent, conn, BIND_AUTO_CREATE);
    }

    private void unbindService() {
        unbindService(conn);
    }

    private void play() {
        Music sampleMusic = MusicFactory.getSampleMusic();
        try {
            if (mPlayControl == null) {
                IBinder iBinder = mBinder.getBinder(PLAY_CONTROL_BINDER);
                mPlayControl = IPlayControl.Stub.asInterface(iBinder);
            }
            mPlayControl.play(sampleMusic);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void pause() {
        try {
            if (mPlayControl == null) {
                IBinder iBinder = mBinder.getBinder(PLAY_CONTROL_BINDER);
                mPlayControl = IPlayControl.Stub.asInterface(iBinder);
            }
            mPlayControl.pause();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void register() {
        try {
            IBinder iBinder = mBinder.getBinder(PLAY_PROGRESS_REGISTER_BINDER);
            IPlayProgressRegister playProgressRegister = IPlayProgressRegister.Stub.asInterface(iBinder);
            playProgressRegister.registerProgressObserver(mProgressObserver);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void unregister() {
        try {
            IBinder iBinder = mBinder.getBinder(PLAY_PROGRESS_REGISTER_BINDER);
            IPlayProgressRegister playProgressRegister = IPlayProgressRegister.Stub.asInterface(iBinder);
            playProgressRegister.unregisterProgressObserver(mProgressObserver);
            playProgressRegister = null;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
