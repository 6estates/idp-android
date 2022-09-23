package com.sixe.idp.core;

import android.content.Context;

import com.sixe.idp.bean.BaseResponse;
import com.sixe.idp.network.BaseObserver;
import com.sixe.idp.network.INetworkObserver;
import com.sixe.idp.network.INetworkRequest;
import com.sixe.idp.network.MainRequestApi;
import com.sixe.idp.network.exception.ExceptionHandle;
import com.sixe.idp.utils.PreferencesUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
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
                            resultCallback.success(result);
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
            resultCallback.failure("Excel not downloaded, please download first.");
        } else {
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
            String projectId = PreferencesUtil.getInstance().getCodeString("projectId", "40");
            MainRequestApi.getService(INetworkRequest.class).getExcel(projectId, id)
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
     * Download PDF
     *
     * @param id             task ID
     * @param resultCallback callback of response
     */
    public static void downPdf(Context context, String id, ResultCallback resultCallback) {
        String excelName = "merge_" + id + ".pdf";
        String filePath = context.getExternalFilesDir(null) + File.separator + excelName;
        File pdfFile = new File(filePath);
        if (!pdfFile.exists()) {

            MainRequestApi.getService(INetworkRequest.class).getPdf(id)
                    .compose(MainRequestApi.getInstance().applySchedulers(new BaseObserver(new INetworkObserver<ResponseBody>() {
                        @Override
                        public void onSuccess(ResponseBody responseBody) {
                            InputStream inputStream = responseBody.byteStream();

                            try {
                                FileOutputStream fos = new FileOutputStream(pdfFile);
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
                                resultCallback.success(filePath);

                            } catch (IOException e) {
                                e.printStackTrace();
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
     * Get Task List
     *
     * @param pageSize       page size
     * @param pageIndex      page index, start at 1
     * @param resultCallback callback of response
     */
    public static void loadTaskList(int pageSize, int pageIndex, ResultCallback resultCallback) {
        String projectId = PreferencesUtil.getInstance().getCodeString("projectId", "40");

        HashMap<String, Object> param = new HashMap<>();
        param.put("limit", pageSize);
        param.put("page", pageIndex);
        param.put("search", "{\"projectId\":\"" + projectId + "\",\"fileName\":\"\"}");
        param.put("sort", "{\"prop\":\"createTime\",\"order\":\"descending\"}");

        MainRequestApi.getService(INetworkRequest.class).getTaskList(param)
                .compose(MainRequestApi.getInstance().applySchedulers(new BaseObserver<>(new INetworkObserver<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody responseBody) {
                        try {
                            String result = responseBody.string();
                            resultCallback.success(result);
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

    }

}
