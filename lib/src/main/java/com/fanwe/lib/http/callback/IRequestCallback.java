package com.fanwe.lib.http.callback;

import com.fanwe.lib.http.Response;

/**
 * Created by zhengjun on 2017/10/10.
 */
public interface IRequestCallback
{
    void setResponse(Response response);

    Response getResponse();

    void onStart();

    void onSuccessBackground() throws Exception;

    void onSuccess();

    void onError(Exception e);

    void onCancel();

    void onFinish();

    IRequestCallback EMPTY_CALLBACK = new IRequestCallback()
    {
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
        public void onStart()
        {
        }

        @Override
        public void onSuccessBackground() throws Exception
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
