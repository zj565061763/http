package com.sd.lib.http.callback;

import com.sd.lib.http.utils.TransmitParam;

public interface IUploadProgressCallback
{
    void onProgressUpload(TransmitParam param);
}
