package com.sd.lib.http.callback;

import com.sd.lib.http.utils.TransmitParam;

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
