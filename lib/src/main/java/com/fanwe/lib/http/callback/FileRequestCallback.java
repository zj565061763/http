package com.fanwe.lib.http.callback;

import com.fanwe.lib.http.utils.IOUtil;
import com.fanwe.lib.http.utils.TransmitParam;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by zhengjun on 2017/10/13.
 */

public abstract class FileRequestCallback extends RequestCallback
{
    private File mFile;
    private TransmitParam mTransmitParam;

    public FileRequestCallback(File file)
    {
        mFile = file;
    }

    public File getFile()
    {
        return mFile;
    }

    public void setFile(File file)
    {
        if (mFile == null)
        {
            mFile = file;
        }
    }

    public TransmitParam getTransmitParam()
    {
        if (mTransmitParam == null)
        {
            mTransmitParam = new TransmitParam();
        }
        return mTransmitParam;
    }

    private void checkFile()
    {
        File file = getFile();
        if (file == null)
        {
            throw new NullPointerException("file is null");
        }
        if (!file.exists())
        {
            try
            {
                file.createNewFile();
            } catch (Exception e)
            {
                onError(e);
            }
        }
    }

    @Override
    public void onSuccessBackground() throws Exception
    {
        super.onSuccessBackground();
        checkFile();

        final long total = getResponse().getContentLength();
        InputStream input = getResponse().getInputStream();
        OutputStream ouput = new FileOutputStream(getFile());

        try
        {
            IOUtil.copy(input, ouput, new IOUtil.ProgressCallback()
            {
                @Override
                public void onProgress(long count)
                {
                    getTransmitParam().transmit(count, total);
                    onProgressBackground(getTransmitParam());
                }
            });
        } finally
        {
            IOUtil.closeQuietly(input);
            IOUtil.closeQuietly(ouput);
        }
    }

    protected abstract void onProgressBackground(TransmitParam param);


}
