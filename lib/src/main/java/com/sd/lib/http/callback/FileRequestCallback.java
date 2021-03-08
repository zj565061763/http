package com.sd.lib.http.callback;

import com.sd.lib.http.IResponse;
import com.sd.lib.http.utils.HttpIOUtils;
import com.sd.lib.http.utils.HttpUtils;
import com.sd.lib.http.utils.TransmitParam;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class FileRequestCallback extends RequestCallback
{
    private final File mFile;
    private final TransmitParam mTransmitParam = new TransmitParam();

    public FileRequestCallback(File file)
    {
        if (file == null)
            throw new IllegalArgumentException("file is null");
        mFile = file;
    }

    public final File getFile()
    {
        return mFile;
    }

    public final TransmitParam getTransmitParam()
    {
        return mTransmitParam;
    }

    @Override
    public void onSuccessBackground() throws Exception
    {
        super.onSuccessBackground();

        final IResponse response = getResponse();
        final long total = response.getContentLength();
        final InputStream input = response.getInputStream();
        final OutputStream output = new FileOutputStream(getFile());
        try
        {
            HttpIOUtils.copy(input, output, new HttpIOUtils.ProgressCallback()
            {
                @Override
                public void onProgress(long count)
                {
                    if (getTransmitParam().transmit(total, count))
                        HttpUtils.runOnUiThread(mUpdateProgressRunnable);
                }
            });
        } finally
        {
            HttpUtils.runOnUiThread(mUpdateProgressRunnable);
            HttpIOUtils.closeQuietly(input);
            HttpIOUtils.closeQuietly(output);
        }
    }

    private final Runnable mUpdateProgressRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            onProgressDownload(getTransmitParam());
        }
    };

    protected abstract void onProgressDownload(TransmitParam param);

    @Override
    public void onCancel()
    {
        super.onCancel();
        HttpUtils.removeCallbacks(mUpdateProgressRunnable);
    }
}
