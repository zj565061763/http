package com.fanwe.lib.http.callback;

import android.os.CountDownTimer;

import com.fanwe.lib.http.utils.IOUtil;
import com.fanwe.lib.http.utils.TransmitParam;
import com.fanwe.lib.task.FTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
            } catch (IOException e)
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
            stopTimer();
            FTask.runOnUiThread(mUpdateProgressRunnable);
            IOUtil.closeQuietly(input);
            IOUtil.closeQuietly(ouput);
        }
    }

    private synchronized void startTimer()
    {
        if (mTimer != null)
        {
            return;
        }

        FTask.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                synchronized (FileRequestCallback.this)
                {
                    if (mTimer == null)
                    {
                        mTimer = new CountDownTimer(Long.MAX_VALUE, 1000)
                        {
                            @Override
                            public void onTick(long millisUntilFinished)
                            {
                                mUpdateProgressRunnable.run();
                            }

                            @Override
                            public void onFinish()
                            {
                            }
                        };
                        mTimer.start();
                    }
                }
            }
        });
    }

    private synchronized void stopTimer()
    {
        if (mTimer != null)
        {
            mTimer.cancel();
            mTimer = null;
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
