package com.zspirytus.simplemusicplayer.factory;

import com.zspirytus.simplemusicplayer.entity.Music;

public class MusicFactory {

    private MusicFactory() {
        throw new AssertionError();
    }

    public static Music getSampleMusic() {
        return new Music("generic_xperia.ogg");
    }
}
