package com.sixe.idp.network;

import com.sixe.idp.bean.BaseResponse;
import com.sixe.idp.network.exception.ExceptionHandle;
import com.sixe.idp.utils.PreferencesUtil;

import java.io.IOException;

import io.reactivex.rxjava3.functions.Function;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * oauth client api
 */
public class OauthRequestApi extends BaseNetworkRequestApi {

    private static OauthRequestApi mInstance;

    public static OauthRequestApi getInstance() {
        if (mInstance == null) {
            synchronized (OauthRequestApi.class) {
                if (mInstance == null) {
                    mInstance = new OauthRequestApi();
                }
            }
        }
        return mInstance;
    }

    /**
     * Get interface services
     *
     * @param service Interface class
     * @param <T>     Interface class
     * @return Interface class
     */
    public static <T> T getService(Class<T> service) {
        return getInstance().getRetrofit(service).create(service);
    }

    @Override
    protected Interceptor getInterceptor() {

        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request.Builder builder = chain.request().newBuilder();

                builder.addHeader("Authorization", "Basic " +
                        PreferencesUtil.getInstance().getCodeString("oauthAuthorization", ""));

                return chain.proceed(builder.build());
            }
        };
    }

    @Override
    protected <T> Function<T, T> getAppErrorHandler() {
        return new Function<T, T>() {
            @Override
            public T apply(T response) throws Exception {
                if (response instanceof BaseResponse && ((BaseResponse) response).getStatus() != 200) {
                    ExceptionHandle.ServerException exception = new ExceptionHandle.ServerException();
                    exception.code = ((BaseResponse) response).getStatus();
                    exception.message = ((BaseResponse) response).getMessage() != null ? ((BaseResponse) response).getMessage() : "";
                    throw exception;
                }
                return response;
            }
        };
    }

    @Override
    protected String getUrl() {
        return "https://oauth-sea.6estates.com";
    }

}
