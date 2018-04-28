package com.example.niephox.methophotos.Interfaces;

/**
 * Created by Niephox on 4/20/2018.
 */

public interface iAsyncCallback {
    // REQUEST CODE STORAGE = 1
    // REQUEST CODE DATABASE = 2
    // METADATA = 3
    void RefreshView(int RequestCode);
    void RetrieveData(int RequestCode);
}
