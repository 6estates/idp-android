package com.sixe.idp.network;

import com.sixe.idp.bean.BaseResponse;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Network request interface
 */
public interface INetworkRequest {

    /**
     * PDF file upload interface with parameters
     */
    @Multipart
    @POST("api/extraction/fields/async/{projectId}")
    Observable<ResponseBody> imageFileUpload(@Path("projectId") String projectId, @Part List<MultipartBody.Part> partList);

    /**
     * Multi picture upload interface with parameters
     */
    @Multipart
    @POST("api/extraction/fields/async/multi_image/{projectId}")
    Observable<ResponseBody> multiFileUpload(@Path("projectId") String projectId, @Part List<MultipartBody.Part> partList);

    /**
     * Get task results
     */
    @GET("api/extraction/field/async/result/{taskId}")
    Observable<ResponseBody> getTaskResult(@Path("taskId") String taskId);

    /**
     * Download Excel
     */
    @GET("api/extraction/fields/excel/{projectId}/{taskId}")
    Observable<ResponseBody> getExcel(@Path("projectId") String projectId, @Path("taskId") String taskId);

    /**
     * Email sharing interface
     */
    @Multipart
    @POST("api/extraction/share/email")
    Observable<BaseResponse> emailShare(@Part List<MultipartBody.Part> partList);

    /**
     * Download Source PDF
     */
    @GET("api/extraction/asyntask/file_content/{taskId}")
    Observable<ResponseBody> getPdf(@Path("taskId") String taskId);

    /**
     * task list
     */
    @GET("api/extraction/history/self_list")
    Observable<ResponseBody> getTaskList(@QueryMap Map<String, Object> param);

    /**
     * Get IDP Authorization
     */
    @POST("oauth/token?grant_type=client_bind")
    Observable<ResponseBody> getIdpAuthorization();

    /**
     * Get project id
     */
    @GET("node/project/id")
    Observable<ResponseBody> getProjectId();

}
