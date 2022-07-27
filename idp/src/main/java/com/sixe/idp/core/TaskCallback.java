package com.sixe.idp.core;

/**
 * Callback of submit task
 */
public interface TaskCallback {
    /**
     * Submit success
     * @param id task id
     */
    void success(int id);

    /**
     * Submit fail
     * @param error failure reason
     */
    void failure(String error);
}
