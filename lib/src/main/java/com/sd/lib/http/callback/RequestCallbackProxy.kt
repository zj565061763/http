package com.sd.lib.http.callback;

import com.sd.lib.http.IRequest;
import com.sd.lib.http.IResponse;
import com.sd.lib.http.utils.TransmitParam;

import org.jetbrains.annotations.NotNull;

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
    public void saveRequest$lib_debug(@NotNull IRequest request)
    {
        super.saveRequest$lib_debug(request);
        for (RequestCallback item : getArrCallback())
        {
            if (item != null)
                item.saveRequest$lib_debug(request);
        }
    }

    @Override
    public void saveResponse$lib_debug(@NotNull IResponse response)
    {
        super.saveResponse$lib_debug(response);
        for (RequestCallback item : getArrCallback())
        {
            if (item != null)
                item.saveResponse$lib_debug(response);
        }
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
