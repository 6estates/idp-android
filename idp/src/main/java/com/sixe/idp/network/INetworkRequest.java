package com.sixe.idp.network;

import com.sixe.idp.bean.BaseResponse;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Network request interface
 */
public interface INetworkRequest {

    /**
     * PDF file upload interface with parameters
     */
    @Multipart
    @POST("api/extraction/fields/async/40")
    Observable<BaseResponse> imageFileUpload(@Part List<MultipartBody.Part> partList);

    /**
     * Multi picture upload interface with parameters
     */
    @Multipart
    @POST("api/extraction/fields/async/multi_image/40")
    Observable<BaseResponse> multiFileUpload(@Part List<MultipartBody.Part> partList);

    /**
     * Get task results
     */
    @GET("api/extraction/field/async/result/{taskId}")
    Observable<ResponseBody> getTaskResult(@Path("taskId") String taskId);

    /**
     * Download Excel
     */
    @GET("api/extraction/fields/excel/{projectId}/{taskId}")
    Observable<ResponseBody> getExcel(@Path("projectId") int projectId, @Path("taskId") String taskId);

    /**
     * Email sharing interface
     */
    @Multipart
    @POST("api/extraction/share/email")
    Observable<BaseResponse> emailShare(@Part List<MultipartBody.Part> partList);

}
