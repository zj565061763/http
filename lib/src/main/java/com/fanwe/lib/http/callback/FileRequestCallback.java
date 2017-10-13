package com.fanwe.lib.http.callback;

import android.os.CountDownTimer;

import com.fanwe.lib.http.utils.IOUtil;
import com.fanwe.lib.http.utils.TransmitParam;
import com.fanwe.lib.task.SDTask;

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
    private TransmitParam mTransmitParam = new TransmitParam();
    private CountDownTimer mTimer;

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

    private void startTimer()
    {
        SDTask.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (mTimer == null)
                {
                    mTimer = new CountDownTimer(Long.MAX_VALUE, 1000)
                    {
                        @Override
                        public void onTick(long millisUntilFinished)
                        {
                            onProgress(getTransmitParam());
                        }

                        @Override
                        public void onFinish()
                        {
                        }
                    };
                    mTimer.start();
                }
            }
        });
    }

    private void stopTimer()
    {
        SDTask.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (mTimer != null)
                {
                    mTimer.cancel();
                    mTimer = null;
                }
            }
        });
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
            startTimer();
            IOUtil.copy(input, ouput, new IOUtil.ProgressCallback()
            {
                @Override
                public void onProgress(long count)
                {
                    getTransmitParam().transmit(count, total);
                }
            });
        } finally
        {
            SDTask.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    onProgress(getTransmitParam());
                }
            });
            stopTimer();
            IOUtil.closeQuietly(input);
            IOUtil.closeQuietly(ouput);
        }
    }

    protected abstract void onProgress(TransmitParam param);

}
