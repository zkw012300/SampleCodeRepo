package com.zspirytus.simplemusicplayer.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    private static SimpleDateFormat mDataFormat = new SimpleDateFormat("mm:ss", Locale.CHINA);

    private DateUtil() {
        throw new AssertionError();
    }

    public static String getMinutesSeconds(int milliseconds) {
        return mDataFormat.format(new Date(milliseconds));
    }
}
