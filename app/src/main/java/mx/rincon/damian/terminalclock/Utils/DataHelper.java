package mx.rincon.damian.terminalclock.Utils;

import android.content.Context;

public class DataHelper {
    public final static String VALUES = "values";
    public final static String USERNAME = "username";
    public final static String HOST = "host";
    public static void putString(Context context, String value, String key){
        context.getSharedPreferences(VALUES,Context.MODE_PRIVATE).edit().putString(key,value).apply();
    }

    public static String getString(Context context, String key){
        return context.getSharedPreferences(VALUES,Context.MODE_PRIVATE).getString(key,"Android");
    }
}
