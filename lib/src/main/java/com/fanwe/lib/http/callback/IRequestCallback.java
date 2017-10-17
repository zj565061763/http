package com.fanwe.lib.http.callback;

import com.fanwe.lib.http.Request;
import com.fanwe.lib.http.Response;
import com.fanwe.lib.http.utils.TransmitParam;

/**
 * Created by zhengjun on 2017/10/10.
 */
public interface IRequestCallback extends UploadProgressCallback
{
    void setResponse(Response response);

    Response getResponse();

    void onPrepare(Request request);

    void onStart();

    void onSuccessBackground() throws Exception;

    void onSuccessBefore();

    void onSuccess();

    void onError(Exception e);

    void onCancel();

    void onFinish();

    IRequestCallback DEFAULT = new IRequestCallback()
    {
        @Override
        public void onProgressUpload(TransmitParam param)
        {
        }

        @Override
        public void setResponse(Response response)
        {
        }

        @Override
        public Response getResponse()
        {
            return null;
        }

        @Override
        public void onPrepare(Request request)
        {
        }

        @Override
        public void onStart()
        {
        }

        @Override
        public void onSuccessBackground() throws Exception
        {
        }

        @Override
        public void onSuccessBefore()
        {
        }

        @Override
        public void onSuccess()
        {
        }

        @Override
        public void onError(Exception e)
        {
        }

        @Override
        public void onCancel()
        {
        }

        @Override
        public void onFinish()
        {
        }
    };
}
