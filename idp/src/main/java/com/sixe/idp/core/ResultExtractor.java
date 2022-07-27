package com.sixe.idp.core;

import android.content.Context;

import com.sixe.idp.bean.BaseResponse;
import com.sixe.idp.network.BaseObserver;
import com.sixe.idp.network.INetworkObserver;
import com.sixe.idp.network.INetworkRequest;
import com.sixe.idp.network.MainRequestApi;
import com.sixe.idp.network.exception.ExceptionHandle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Get task result
 * Send email
 */
public class ResultExtractor {

    /**
     * Get task result by task id
     *
     * @param taskId         task id
     * @param resultCallback callback of response
     */
    public static void extractResultByTaskId(String taskId, ResultCallback resultCallback) {
        MainRequestApi.getService(INetworkRequest.class).getTaskResult(taskId)
                .compose(MainRequestApi.getInstance().applySchedulers(new BaseObserver<>(new INetworkObserver<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody taskResultResponse) {

                        try {
                            String result = taskResultResponse.string();
                            JSONObject jsonObject = new JSONObject(result);
                            JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                            String status = jsonObjectData.getString("status");
                            if ("Done".equals(status)) {
                                // 已完成
                                resultCallback.success(jsonObjectData.toString());
                            } else {
                                resultCallback.failure("Task is not done");
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            resultCallback.failure("Task is not done");
                        }


                    }

                    @Override
                    public void onFailure(Throwable e) {
                        resultCallback.failure(((ExceptionHandle.ResponseThrowable) e).message);
                    }
                })));
    }

    /**
     * Send excel file to mailbox
     *
     * @param context        context
     * @param id             task ID
     * @param title          email title
     * @param emailAddress   email address
     * @param resultCallback callback of response
     */
    public static void sendEmail(Context context, String id, String title,
                                 String emailAddress, ResultCallback resultCallback) {
        // Judge whether excel has been downloaded. If not, download it
        String excelName = id + "_result" + ".xlsx";
        String filePath = context.getExternalFilesDir(null) + File.separator + excelName;
        File excelFile = new File(filePath);
        if (!excelFile.exists()) {
            downExcel(excelFile, id, title, emailAddress, resultCallback);
        } else {
            realSendEmail(excelFile, title, emailAddress, resultCallback);
        }
    }

    /**
     * Get excel of task
     *
     * @param context        context
     * @param id             task ID
     * @param resultCallback callback of response
     */
    public static void getTaskExcel(Context context, String id, ResultCallback resultCallback) {
        // excel file name
        String excelName = id + "_result" + ".xlsx";
        String filePath = context.getExternalFilesDir(null) + File.separator + excelName;
        File excelFile = new File(filePath);
        if (!excelFile.exists()) {
            // If the file does not exist, download it
            MainRequestApi.getService(INetworkRequest.class).getExcel(40, id)
                    .compose(MainRequestApi.getInstance().applySchedulers(new BaseObserver(new INetworkObserver<ResponseBody>() {
                        @Override
                        public void onSuccess(ResponseBody responseBody) {
                            InputStream inputStream = responseBody.byteStream();
                            try {
                                FileOutputStream fos = new FileOutputStream(excelFile);
                                byte buf[] = new byte[1024];
                                while (true) {
                                    int numread = inputStream.read(buf);
                                    if (numread <= 0) {
                                        // download complete
                                        break;
                                    }
                                    fos.write(buf, 0, numread);
                                }
                                fos.close();
                                inputStream.close();
                                // Download complete, return file path
                                resultCallback.success(filePath);
                            } catch (IOException e) {
                                e.printStackTrace();
                                resultCallback.failure(e.getMessage());

                            }

                        }

                        @Override
                        public void onFailure(Throwable e) {
                            resultCallback.failure(((ExceptionHandle.ResponseThrowable) e).message);

                        }
                    })));
        } else {
            resultCallback.success(filePath);
        }
    }

    /**
     * Download excel
     *
     * @param excelFile      excel file
     * @param id             task ID
     * @param title          email title
     * @param emailAddress   email address
     * @param resultCallback callback of response
     */
    private static void downExcel(File excelFile, String id, String title,
                                  String emailAddress, ResultCallback resultCallback) {
        MainRequestApi.getService(INetworkRequest.class).getExcel(40, id)
                .compose(MainRequestApi.getInstance().applySchedulers(new BaseObserver(new INetworkObserver<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody responseBody) {
                        InputStream inputStream = responseBody.byteStream();

                        try {
                            FileOutputStream fos = new FileOutputStream(excelFile);
                            byte buf[] = new byte[1024];
                            while (true) {
                                int numread = inputStream.read(buf);
                                if (numread <= 0) {
                                    // download complete
                                    break;
                                }
                                fos.write(buf, 0, numread);
                            }
                            fos.close();
                            inputStream.close();
                            realSendEmail(excelFile, title, emailAddress, resultCallback);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Throwable e) {
                        resultCallback.failure(((ExceptionHandle.ResponseThrowable) e).message);

                    }
                })));
    }

    /**
     * Call the interface to send mail
     *
     * @param excelFile      excel file
     * @param title          email title
     * @param emailAddress   email address
     * @param resultCallback callback of response
     */
    private static void realSendEmail(File excelFile, String title,
                                      String emailAddress, ResultCallback resultCallback) {

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), excelFile);
        builder.addFormDataPart("title", title);
        builder.addFormDataPart("content", "");
        builder.addFormDataPart("emailAddress", emailAddress);
        builder.addFormDataPart("attachments", excelFile.getName(), body);
        List<MultipartBody.Part> parts = builder.build().parts();
        MainRequestApi.getService(INetworkRequest.class).emailShare(parts)
                .compose(MainRequestApi.getInstance().applySchedulers(new BaseObserver<>(new INetworkObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        resultCallback.success("success");
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        resultCallback.failure(((ExceptionHandle.ResponseThrowable) e).message);

                    }
                })));

    }
}
