// IPlayProgressRegister.aidl
package com.zspirytus.simplemusicplayer;

import com.zspirytus.simplemusicplayer.IOnProgressChange;

interface IPlayProgressRegister {
    void registerProgressObserver(IOnProgressChange observer);
    void unregisterProgressObserver(IOnProgressChange observer);
}
