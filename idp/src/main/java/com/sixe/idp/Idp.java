package com.sixe.idp;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;

import com.sixe.idp.network.BaseObserver;
import com.sixe.idp.network.INetworkObserver;
import com.sixe.idp.network.INetworkRequest;
import com.sixe.idp.network.MainRequestApi;
import com.sixe.idp.network.OauthRequestApi;
import com.sixe.idp.utils.PreferencesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import me.pqpo.smartcropperlib.SmartCropper;
import okhttp3.ResponseBody;

public class Idp {

    public static final String IMAGE_PATH = "imagePath";
    public static final String CROP_IMAGE_PATH = "cropImagePath";
    public static final String CROP_ERROR = "cropError";

    /**
     * Initialize the Idp environment
     *
     * @param context context
     */
    public static synchronized void init(Context context) {
        SmartCropper.buildImageDetector(context);
        PreferencesUtil.init(context);
        try {
            // get id and secret from AndroidManifest.xml
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle metaData = applicationInfo.metaData;
            if (metaData != null) {
                String id = applicationInfo.metaData.getString("com.sixestates.client.id");
                String secret = applicationInfo.metaData.getString("com.sixestates.client.secret");
                String combination = id + ":" + secret;
                // get oauth authorization
                String oauthAuthorization = Base64.encodeToString(combination.getBytes(), Base64.DEFAULT).replaceAll("\r|\n", "");
                PreferencesUtil.getInstance().putCodeString("oauthAuthorization", oauthAuthorization);
                // get idp authorization
                getIdpAuthorization();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get IDP Authorization
     */
    private static void getIdpAuthorization() {
        OauthRequestApi.getService(INetworkRequest.class).getIdpAuthorization()
                .compose(OauthRequestApi.getInstance().applySchedulers(new BaseObserver<>(new INetworkObserver<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody response) {
                        try {
                            String result = response.string();
                            JSONObject jsonObject = new JSONObject(result);
                            JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                            String value = jsonObjectData.getString("value");
                            // save to SP
                            PreferencesUtil.getInstance().putCodeString("idpAuthorization", value);
                            getProjectId();
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Throwable e) {
                        e.printStackTrace();
                    }
                })));
    }

    /**
     * Get Project ID
     */
    private static void getProjectId() {
        MainRequestApi.getService(INetworkRequest.class).getProjectId()
                .compose(MainRequestApi.getInstance().applySchedulers(new BaseObserver<>(new INetworkObserver<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody responseBody) {
                        try {
                            String result = responseBody.string();
                            JSONObject jsonObject = new JSONObject(result);
                            String projectId = jsonObject.getString("data");
                            // save project id to SP
                            PreferencesUtil.getInstance().putCodeString("projectId", projectId);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Throwable e) {
                        e.printStackTrace();
                    }
                })));
    }

}
