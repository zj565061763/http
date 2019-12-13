package com.sd.lib.http.callback;

import com.sd.lib.http.IRequest;
import com.sd.lib.http.IResponse;
import com.sd.lib.http.utils.TransmitParam;

public class RequestCallbackProxy extends RequestCallback
{
    private RequestCallback[] mArrCallback;

    protected RequestCallbackProxy(RequestCallback... callbacks)
    {
        mArrCallback = callbacks;
    }

    /**
     * 返回回调代理对象
     *
     * @param callbacks
     * @return
     */
    public static RequestCallback get(RequestCallback... callbacks)
    {
        return new RequestCallbackProxy(callbacks);
    }

    public RequestCallback[] getArrCallback()
    {
        if (mArrCallback == null)
            mArrCallback = new RequestCallback[0];
        return mArrCallback;
    }

    @Override
    public void setRequest(IRequest request)
    {
        for (RequestCallback item : getArrCallback())
        {
            if (item != null)
                item.setRequest(request);
        }
    }

    @Override
    public IRequest getRequest()
    {
        final int length = getArrCallback().length;
        if (length > 0)
            return getArrCallback()[length - 1].getRequest();

        return null;
    }

    @Override
    public void setResponse(IResponse response)
    {
        for (RequestCallback item : getArrCallback())
        {
            if (item != null)
                item.setResponse(response);
        }
    }

    @Override
    public IResponse getResponse()
    {
        final int length = getArrCallback().length;
        if (length > 0)
            return getArrCallback()[length - 1].getResponse();

        return null;
    }

    @Override
    public void onPrepare(IRequest request)
    {
        for (RequestCallback item : getArrCallback())
        {
            if (item != null)
                item.onPrepare(request);
        }
    }

    @Override
    public void onStart()
    {
        for (RequestCallback item : getArrCallback())
        {
            if (item != null)
                item.onStart();
        }
    }

    @Override
    public void onSuccessBackground() throws Exception
    {
        for (RequestCallback item : getArrCallback())
        {
            if (item != null)
                item.onSuccessBackground();
        }
    }

    @Override
    public void onSuccessBefore()
    {
        for (RequestCallback item : getArrCallback())
        {
            if (item != null)
                item.onSuccessBefore();
        }
    }

    @Override
    public void onSuccess()
    {
        for (RequestCallback item : getArrCallback())
        {
            if (item != null)
                item.onSuccess();
        }
    }

    @Override
    public void onError(Exception e)
    {
        for (RequestCallback item : getArrCallback())
        {
            if (item != null)
                item.onError(e);
        }
    }

    @Override
    public void onCancel()
    {
        for (RequestCallback item : getArrCallback())
        {
            if (item != null)
                item.onCancel();
        }
    }

    @Override
    public void onFinish()
    {
        for (RequestCallback item : getArrCallback())
        {
            if (item != null)
                item.onFinish();
        }
    }

    @Override
    public void onProgressUpload(TransmitParam param)
    {
        for (RequestCallback item : getArrCallback())
        {
            if (item != null)
                item.onProgressUpload(param);
        }
    }
}
