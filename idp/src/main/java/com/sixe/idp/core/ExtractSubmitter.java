package com.sixe.idp.core;

import android.os.Build;
import android.os.Environment;

import com.sixe.idp.bean.BaseResponse;
import com.sixe.idp.bean.TaskInfo;
import com.sixe.idp.network.BaseObserver;
import com.sixe.idp.network.INetworkObserver;
import com.sixe.idp.network.INetworkRequest;
import com.sixe.idp.network.MainRequestApi;
import com.sixe.idp.network.exception.ExceptionHandle;
import com.sixe.idp.utils.PreferencesUtil;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Use to submit
 */
public class ExtractSubmitter {

    /**
     * Submit images
     *
     * @param taskInfo     Requested object
     * @param taskCallback Callback of response
     */
    public static void submitImages(TaskInfo taskInfo, TaskCallback taskCallback) {

        // For Android 11 and above, you need to apply for all file permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                && !Environment.isExternalStorageManager()) {
            taskCallback.failure("Need all files access permission.");
            return;
        }

        // Request parameters
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("fileType", taskInfo.fileType());
        builder.addFormDataPart("hitl", taskInfo.hitl() + "");
        for (int i = 0; i < taskInfo.imagePaths().size(); i++) {
            File file = new File(taskInfo.imagePaths().get(i));
            builder.addFormDataPart("images[" + i + "]", file.getName(),
                    RequestBody.create(MediaType.parse("image/*"), file));
        }
        List<MultipartBody.Part> parts = builder.build().parts();
        String projectId = PreferencesUtil.getInstance().getCodeString("projectId", "40");
        MainRequestApi.getService(INetworkRequest.class).multiFileUpload(projectId, parts)
                .compose(MainRequestApi.getInstance().applySchedulers(new BaseObserver<>(new INetworkObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse baseResponse) {
                        taskCallback.success(baseResponse.getData());
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        taskCallback.failure(((ExceptionHandle.ResponseThrowable) e).message);
                    }
                })));
    }

    /**
     * Submit file of PDF
     *
     * @param taskInfo     Requested object
     * @param taskCallback Callback of response
     */
    public static void submitPdf(TaskInfo taskInfo, TaskCallback taskCallback) {
        // For Android 11 and above, you need to apply for all file permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                && !Environment.isExternalStorageManager()) {
            taskCallback.failure("Need all files access permission.");
            return;
        }
        // Request parameters
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("fileType", taskInfo.fileType());
        builder.addFormDataPart("hitl", taskInfo.hitl() + "");
        File file = new File(taskInfo.filePath());
        RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        builder.addFormDataPart("file", file.getName(), body);
        List<MultipartBody.Part> parts = builder.build().parts();
        String projectId = PreferencesUtil.getInstance().getCodeString("projectId", "40");
        MainRequestApi.getService(INetworkRequest.class).imageFileUpload(projectId, parts)
                .compose(MainRequestApi.getInstance().applySchedulers(new BaseObserver<>(new INetworkObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse baseResponse) {
                        taskCallback.success(baseResponse.getData());
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        taskCallback.failure(((ExceptionHandle.ResponseThrowable) e).message);
                    }
                })));
    }

}
