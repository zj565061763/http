package com.fanwe.lib.http.callback;

import com.fanwe.lib.http.Request;
import com.fanwe.lib.http.Response;
import com.fanwe.lib.http.utils.TransmitParam;

/**
 * Created by zhengjun on 2017/10/10.
 */
public interface IRequestCallback extends IUploadProgressCallback
{
    void setRequest(Request request);

    /**
     * 返回请求对象
     *
     * @return
     */
    Request getRequest();

    void setResponse(Response response);

    /**
     * 返回请求结果对象
     *
     * @return
     */
    Response getResponse();

    /**
     * 异步请求在被执行之前的准备回调
     *
     * @param request
     */
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
        public void setRequest(Request request)
        {
        }

        @Override
        public Request getRequest()
        {
            return null;
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
