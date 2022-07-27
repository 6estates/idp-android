package com.sixe.idp.network;

/**
 * Callback after network request
 * @param <T>
 */
public interface INetworkObserver<T> {
    void onSuccess(T t);
    void onFailure(Throwable e);
}
