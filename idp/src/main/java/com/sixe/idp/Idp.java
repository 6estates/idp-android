package com.sixe.idp;

import android.content.Context;

import com.sixe.idp.utils.PreferencesUtil;

import me.pqpo.smartcropperlib.SmartCropper;

public class Idp {

    public static final String IMAGE_PATH = "imagePath";
    public static final String CROP_IMAGE_PATH = "cropImagePath";
    public static final String CROP_ERROR = "cropError";

    /**
     * Initialize the Idp environment
     *
     * @param context context
     * @param token   auth token for the account
     */
    public static synchronized void init(Context context, String token) {
        SmartCropper.buildImageDetector(context);
        PreferencesUtil.init(context);
        PreferencesUtil.getInstance().putCodeString("token", token);
    }
}
