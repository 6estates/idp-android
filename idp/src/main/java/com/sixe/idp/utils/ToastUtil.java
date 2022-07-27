package com.sixe.idp.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;


public class ToastUtil {
    private static Toast mToast;

    public static void show(Context context, String msg) {
        if (context != null && !TextUtils.isEmpty(msg)) {
            if (mToast != null) {
                mToast.setText(msg);
            } else {
                mToast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
            }
            mToast.show();
        }
    }
}
