// IBinderPool.aidl
package com.zspirytus.simplemusicplayer;

interface IBinderPool {
    IBinder getBinder(int binderCode);
}
