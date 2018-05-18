package com.example.niephox.methophotos.Interfaces;

/**
 * Created by Niephox on 4/20/2018.
 */

public interface iAsyncCallback {
    enum REQUEST_CODE {
        STORAGE,
        DATABASE,
        METADATA
    }

    void RefreshView(REQUEST_CODE rq);
    void RetrieveData(REQUEST_CODE rq);
}
