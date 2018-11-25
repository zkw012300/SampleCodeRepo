package com.zspirytus.simplemusicplayer.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Music implements Parcelable {

    private String mMusicFilePath;

    public Music(String musicFilePath) {
        mMusicFilePath = musicFilePath;
    }

    private Music(Parcel source) {
        mMusicFilePath = source.readString();
    }

    public String getMusicFilePath() {
        return mMusicFilePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mMusicFilePath);
    }

    public static Parcelable.Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel parcel) {
            return new Music(parcel);
        }

        @Override
        public Music[] newArray(int i) {
            return new Music[0];
        }
    };
}
