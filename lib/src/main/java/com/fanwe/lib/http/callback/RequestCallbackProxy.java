package com.fanwe.lib.http.callback;

import com.fanwe.lib.http.Request;
import com.fanwe.lib.http.Response;
import com.fanwe.lib.http.utils.TransmitParam;

/**
 * Created by zhengjun on 2017/10/12.
 */
public class RequestCallbackProxy implements IRequestCallback
{
    private IRequestCallback[] mArrCallback;

    protected RequestCallbackProxy(IRequestCallback... callbacks)
    {
        mArrCallback = callbacks;
    }

    public static IRequestCallback get(final IRequestCallback... callbacks)
    {
        return new RequestCallbackProxy(callbacks);
    }

    public IRequestCallback[] getArrCallback()
    {
        if (mArrCallback == null)
        {
            mArrCallback = new IRequestCallback[0];
        }
        return mArrCallback;
    }

    @Override
    public void setRequest(Request request)
    {
        for (IRequestCallback item : getArrCallback())
        {
            if (item != null)
            {
                item.setRequest(request);
            }
        }
    }

    @Override
    public Request getRequest()
    {
        int length = getArrCallback().length;
        if (length > 0)
        {
            return getArrCallback()[length - 1].getRequest();
        }
        return null;
    }

    @Override
    public void setResponse(Response response)
    {
        for (IRequestCallback item : getArrCallback())
        {
            if (item != null)
            {
                item.setResponse(response);
            }
        }
    }

    @Override
    public Response getResponse()
    {
        int length = getArrCallback().length;
        if (length > 0)
        {
            return getArrCallback()[length - 1].getResponse();
        }
        return null;
    }

    @Override
    public void onPrepare(Request request)
    {
        for (IRequestCallback item : getArrCallback())
        {
            if (item != null)
            {
                item.onPrepare(request);
            }
        }
    }

    @Override
    public void onStart()
    {
        for (IRequestCallback item : getArrCallback())
        {
            if (item != null)
            {
                item.onStart();
            }
        }
    }

    @Override
    public void onSuccessBackground() throws Exception
    {
        for (IRequestCallback item : getArrCallback())
        {
            if (item != null)
            {
                item.onSuccessBackground();
            }
        }
    }

    @Override
    public void onSuccessBefore()
    {
        for (IRequestCallback item : getArrCallback())
        {
            if (item != null)
            {
                item.onSuccessBefore();
            }
        }
    }

    @Override
    public void onSuccess()
    {
        for (IRequestCallback item : getArrCallback())
        {
            if (item != null)
            {
                item.onSuccess();
            }
        }
    }

    @Override
    public void onError(Exception e)
    {
        for (IRequestCallback item : getArrCallback())
        {
            if (item != null)
            {
                item.onError(e);
            }
        }
    }

    @Override
    public void onCancel()
    {
        for (IRequestCallback item : getArrCallback())
        {
            if (item != null)
            {
                item.onCancel();
            }
        }
    }

    @Override
    public void onFinish()
    {
        for (IRequestCallback item : getArrCallback())
        {
            if (item != null)
            {
                item.onFinish();
            }
        }
    }

    @Override
    public void onProgressUpload(TransmitParam param)
    {
        for (IRequestCallback item : getArrCallback())
        {
            if (item != null)
            {
                item.onProgressUpload(param);
            }
        }
    }
}
