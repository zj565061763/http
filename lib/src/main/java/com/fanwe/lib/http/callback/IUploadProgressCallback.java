package com.fanwe.lib.http.callback;

import com.fanwe.lib.http.utils.TransmitParam;

/**
 * Created by zhengjun on 2017/10/16.
 */

public interface IUploadProgressCallback
{
    IUploadProgressCallback DEFAULT = new IUploadProgressCallback()
    {
        @Override
        public void onProgressUpload(TransmitParam param)
        {
        }
    };


    void onProgressUpload(TransmitParam param);
}
