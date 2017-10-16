package com.fanwe.lib.http.callback;

import com.fanwe.lib.http.utils.TransmitParam;

/**
 * Created by zhengjun on 2017/10/16.
 */

public interface UploadProgressCallback
{
    UploadProgressCallback EMPTY_PROGRESS_CALLBACK = new UploadProgressCallback()
    {
        @Override
        public void onProgressUpload(TransmitParam param)
        {
        }
    };


    void onProgressUpload(TransmitParam param);
}
