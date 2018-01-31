package com.fanwe.lib.http.callback;

import com.fanwe.lib.http.Request;
import com.fanwe.lib.http.Response;
import com.fanwe.lib.http.utils.TransmitParam;

/**
 * Created by zhengjun on 2017/10/10.
 */

public abstract class RequestCallback
{
    private Request mRequest;
    private Response mResponse;

    public void setRequest(Request request)
    {
        this.mRequest = request;
    }

    public Request getRequest()
    {
        return mRequest;
    }

    public void setResponse(Response response)
    {
        mResponse = response;
    }

    public Response getResponse()
    {
        return mResponse;
    }

    //---------- notify method start ----------

    public void onPrepare(Request request)
    {
    }

    public void onStart()
    {
    }

    public void onSuccessBackground() throws Exception
    {
    }

    public void onSuccessBefore()
    {
    }

    public abstract void onSuccess();

    public void onProgressUpload(TransmitParam param)
    {
    }

    public void onError(Exception e)
    {
    }

    public void onCancel()
    {
    }

    public void onFinish()
    {
    }

    //---------- notify method end ----------
}
