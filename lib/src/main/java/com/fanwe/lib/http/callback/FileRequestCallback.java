package com.fanwe.lib.http.callback;

import com.fanwe.lib.http.utils.HttpIOUtil;
import com.fanwe.lib.http.utils.TransmitParam;
import com.fanwe.lib.task.FTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by zhengjun on 2017/10/13.
 */

public abstract class FileRequestCallback extends RequestCallback
{
    private final File mFile;
    private TransmitParam mTransmitParam = new TransmitParam();

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

        final long total = getResponse().getContentLength();
        final InputStream input = getResponse().getInputStream();
        final OutputStream ouput = new FileOutputStream(getFile());

        try
        {
            HttpIOUtil.copy(input, ouput, new HttpIOUtil.ProgressCallback()
            {
                private int lastProgress;

                @Override
                public void onProgress(long count)
                {
                    getTransmitParam().transmit(count, total);

                    final int newProgress = getTransmitParam().getProgress();
                    if (newProgress != lastProgress)
                    {
                        FTask.runOnUiThread(mUpdateProgressRunnable);
                        lastProgress = newProgress;
                    }
                }
            });
        } finally
        {
            FTask.runOnUiThread(mUpdateProgressRunnable);
            HttpIOUtil.closeQuietly(input);
            HttpIOUtil.closeQuietly(ouput);
        }
    }

    private Runnable mUpdateProgressRunnable = new Runnable()
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
        FTask.MAIN_HANDLER.removeCallbacks(mUpdateProgressRunnable);
    }
}
