package com.sd.lib.http.callback;

import com.sd.lib.http.task.FTask;
import com.sd.lib.http.utils.HttpIOUtil;
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
        mFile = file;
        checkFile();
    }

    public final File getFile()
    {
        return mFile;
    }

    public final TransmitParam getTransmitParam()
    {
        return mTransmitParam;
    }

    private void checkFile()
    {
        final File file = getFile();
        if (file == null)
            throw new NullPointerException("file is null");

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

        final long total = getResponse().getContentLength();
        final InputStream input = getResponse().getInputStream();
        final OutputStream output = new FileOutputStream(getFile());

        try
        {
            HttpIOUtil.copy(input, output, new HttpIOUtil.ProgressCallback()
            {
                @Override
                public void onProgress(long count)
                {
                    if (getTransmitParam().transmit(total, count))
                    {
                        FTask.runOnUiThread(mUpdateProgressRunnable);
                    }
                }
            });
        } finally
        {
            FTask.runOnUiThread(mUpdateProgressRunnable);
            HttpIOUtil.closeQuietly(input);
            HttpIOUtil.closeQuietly(output);
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
        FTask.removeCallbacks(mUpdateProgressRunnable);
    }
}
