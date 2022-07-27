package com.sixe.idp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesUtil {

    private static PreferencesUtil sInstance;
    private static Context sApplication;
    private static final String CONFIG = "idpconfig";
    private static SharedPreferences sp;

    public static PreferencesUtil getInstance() {
        if (sInstance == null) {
            synchronized (PreferencesUtil.class) {
                if (sInstance == null) {
                    sInstance = new PreferencesUtil();
                }
            }
        }
        return sInstance;
    }

    public static void init(Context application) {
        sApplication = application;
    }

    public void putCodeString(String key, String value) {
        if (sp == null) {
            sp = sApplication.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        sp.edit().putString(key, value).apply();
    }

    public String getCodeString(String key, String defValue) {
        if (sp == null) {
            sp = sApplication.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        return sp.getString(key, defValue);
    }

}
