package com.zspirytus.simpleaidltest;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static int A = 1;

    private TextView mLocalText;
    private TextView mRemoteMsg;

    private IAIDLTest mBinder;
    private ICallbackImpl mCallback;
    private ServiceConnection conn;

    private class ICallbackImpl extends ICallback.Stub {
        @Override
        public void callback(final int a) throws RemoteException {
            mRemoteMsg.post(new Runnable() {
                @Override
                public void run() {
                    switch (a) {
                        case 1:
                            a1();
                            break;
                        case 2:
                            a2();
                            break;
                    }
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

    private void initData() {
        mCallback = new ICallbackImpl();
        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                mBinder = IAIDLTest.Stub.asInterface(iBinder);
                Log.e(this.getClass().getSimpleName(), "MyService Connect.");
                try {
                    mBinder.setCallback(mCallback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.e(this.getClass().getSimpleName(), "MyService Disconnect. Receive From Thread: " + Thread.currentThread().getName());
                bindService();
            }
        };
    }

    private void initView() {
        mLocalText = findViewById(R.id.local_a_text);
        mLocalText.setText("Local A = 1");
        mRemoteMsg = findViewById(R.id.service_msg_text);
        mRemoteMsg.setText("Receive From Remote Service: Default");
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start();
            }
        });
        findViewById(R.id.change_a).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (A == 1) {
                    A = 2;
                    mLocalText.setText("Local A = 2");
                } else {
                    A = 1;
                    mLocalText.setText("Local A = 1");
                }
            }
        });
    }

    private void bindService() {
        Intent startServiceIntent = new Intent(MainActivity.this, MyService.class);
        bindService(startServiceIntent, conn, BIND_AUTO_CREATE);
    }

    private void unbindService() {
        unbindService(conn);
    }

    private void start() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mBinder.testMethod(A);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setName("myThread");
        thread.start();
    }

    private void a1() {
        mRemoteMsg.setText("Receive From Remote Service: a = 1");
    }

    private void a2() {
        mRemoteMsg.setText("Receive From Remote Service: a = 2");
    }
}
