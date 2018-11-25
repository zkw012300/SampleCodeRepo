// IPlayControl.aidl
package com.zspirytus.simplemusicplayer;

import com.zspirytus.simplemusicplayer.entity.Music;

interface IPlayControl {
    void play(in Music music);
    void pause();
}
