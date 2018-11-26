package com.zspirytus.simpleaidltest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

public class MyService extends Service {

    private MyBinder mBinder;
    private ICallback mCallback;

    @Override
    public IBinder onBind(Intent intent) {
        if (mBinder == null)
            mBinder = new MyBinder();
        return mBinder;
    }

    private class MyBinder extends IAIDLTest.Stub {
        @Override
        public void testMethod(int a) throws RemoteException {
            SystemClock.sleep(5000);
            Log.e(this.getClass().getSimpleName(), "MyService Receive Msg: " + a + "\t at Thread: " + Thread.currentThread().getName());
            if (mCallback != null)
                mCallback.callback(a);
        }

        @Override
        public void setCallback(ICallback callback) throws RemoteException {
            mCallback = callback;
        }
    }
}
