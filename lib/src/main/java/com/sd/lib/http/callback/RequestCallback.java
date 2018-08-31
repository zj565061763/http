package com.sd.lib.http.callback;

import com.sd.lib.http.IRequest;
import com.sd.lib.http.IResponse;
import com.sd.lib.http.utils.TransmitParam;

public abstract class RequestCallback
{
    private IRequest mRequest;
    private IResponse mResponse;

    public void setRequest(IRequest request)
    {
        this.mRequest = request;
    }

    public IRequest getRequest()
    {
        return mRequest;
    }

    public void setResponse(IResponse response)
    {
        mResponse = response;
    }

    public IResponse getResponse()
    {
        return mResponse;
    }

    //---------- notify method start ----------

    public void onPrepare(IRequest request)
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
