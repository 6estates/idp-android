package com.sixe.idp.network;

import com.sixe.idp.network.exception.ExceptionHandle;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * Rxjava
 * @param <T>
 */
public class BaseObserver<T> implements Observer<T> {

    private INetworkObserver<T> mINetworkObserver;

    public BaseObserver(INetworkObserver<T> iNetworkObserver) {
        mINetworkObserver = iNetworkObserver;
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }

    @Override
    public void onNext(T t) {
        mINetworkObserver.onSuccess(t);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        if (e instanceof ExceptionHandle.ResponseThrowable) {
            mINetworkObserver.onFailure(e);
        } else {
            mINetworkObserver.onFailure(new ExceptionHandle.ResponseThrowable(e, ExceptionHandle.ERROR.UNKNOWN));
        }

    }

    @Override
    public void onComplete() {

    }
}
