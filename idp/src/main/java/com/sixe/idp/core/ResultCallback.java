package com.sixe.idp.core;

/**
 * Callback of task result or send email
 */
public interface ResultCallback {

    /**
     * Task result or success to send email
     *
     * @param data result
     */
    void success(String data);

    /**
     * Query result failed or fail to send email
     *
     * @param error error message
     */
    void failure(String error);
}
