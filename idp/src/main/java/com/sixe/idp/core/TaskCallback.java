package com.sixe.idp.core;

/**
 * Callback of submit task
 */
public interface TaskCallback {
    /**
     * Submit success
     * @param response response
     */
    void success(String response);

    /**
     * Submit fail
     * @param error failure reason
     */
    void failure(String error);
}
