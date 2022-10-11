package com.sixe.idp.network;

import com.sixe.idp.network.exception.HttpErrorHandler;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Basic class of network request interface
 */
public abstract class BaseNetworkRequestApi {

    private String mBaseUrl;
    private OkHttpClient mOkHttpClient;

    public BaseNetworkRequestApi() {
        mBaseUrl = getUrl();
    }

    /**
     * Get retrofit object
     *
     * @param service Interface service corresponding to network request
     * @return Return instance
     */
    protected Retrofit getRetrofit(Class service) {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(mBaseUrl);
        builder.client(getOkHttpClient());
        builder.addConverterFactory(GsonConverterFactory.create());
        builder.addCallAdapterFactory(RxJava3CallAdapterFactory.create());
        return builder.build();
    }

    /**
     * Customize okhttpclient
     *
     * @return Return instance
     */
    private OkHttpClient getOkHttpClient() {
        if (mOkHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            if (getInterceptor() != null) {
                // Interceptors can be added before requests to add related request headers, etc
                builder.addInterceptor(getInterceptor());
            }

            mOkHttpClient = builder.build();

        }
        return mOkHttpClient;
    }

    /**
     * Responsive rxjava processing of requests
     */
    public <T> ObservableTransformer<T, T> applySchedulers(final Observer<T> observer) {
        return new ObservableTransformer<T, T>() {
            @Override
            public @NonNull ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                @NonNull Observable<T> observable = (Observable<T>) upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(getAppErrorHandler())
                        .onErrorResumeNext(new HttpErrorHandler<T>());
                observable.subscribe(observer);
                return observable;
            }
        };
    }

    /**
     * Custom interceptor
     */
    protected abstract Interceptor getInterceptor();

    /**
     * Error message
     */
    protected abstract <T> Function<T, T> getAppErrorHandler();

    /**
     * Request url
     */
    protected abstract String getUrl();

}
