package com.example.tomato;

import android.content.Context;
import android.content.SharedPreferences;

public class UserInfoConfig {

    public UserInfoConfig() {
    }

    //設定檔儲存
    public static void setConfig(Context context, String name, String key,
                                 String value)
    {
        SharedPreferences settings =context.getSharedPreferences(name,0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putString(key, value);
        PE.apply();
    }

    //設定檔讀取
    public static String getConfig(Context context , String name , String
            key , String def)
    {
        SharedPreferences settings =context.getSharedPreferences(name,0);
        return settings.getString(key, def);
    }
}
