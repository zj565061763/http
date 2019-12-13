package com.sd.lib.http.callback;

import com.sd.lib.http.IRequest;
import com.sd.lib.http.IResponse;
import com.sd.lib.http.exception.HttpExceptionResponseCode;
import com.sd.lib.http.utils.TransmitParam;

public abstract class RequestCallback implements IUploadProgressCallback
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
        processResponseCode(getResponse().getCode());
    }

    /**
     * 处理返回码
     *
     * @param code
     * @throws Exception
     */
    protected void processResponseCode(int code) throws Exception
    {
        final HttpExceptionResponseCode codeException = HttpExceptionResponseCode.from(code);
        if (codeException != null)
            throw codeException;
    }

    public void onSuccessBefore()
    {
    }

    public abstract void onSuccess();

    @Override
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
