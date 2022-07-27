package com.sixe.idp.network.exception;


import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Function;

/**
 * Handles the following two types of network errors
 * 1. HTTP request related errors, such as 404403, socket timeout, etc
 * 2. Errors in application data will throw runtimeException,
 * and finally come to this function for unified processing
 */
public class HttpErrorHandler<T> implements Function<Throwable, Observable<T>> {
    @Override
    public Observable<T> apply(Throwable throwable) throws Exception {
        return Observable.error(ExceptionHandle.handleException(throwable));
    }
}
