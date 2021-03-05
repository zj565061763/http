package com.sd.lib.http.utils;

import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

public class HttpIOUtil
{
    private HttpIOUtil()
    {
    }

    public static String readString(InputStream inputStream, String charset) throws IOException
    {
        if (TextUtils.isEmpty(charset))
            charset = "UTF-8";

        if (!(inputStream instanceof BufferedInputStream))
            inputStream = new BufferedInputStream(inputStream);

        final Reader reader = new InputStreamReader(inputStream, charset);
        final StringBuilder sb = new StringBuilder();
        final char[] buffer = new char[1024];
        int len;
        while ((len = reader.read(buffer)) >= 0)
        {
            sb.append(buffer, 0, len);
        }
        return sb.toString();
    }

    public static void writeString(OutputStream outputStream, String content, String charset) throws IOException
    {
        if (TextUtils.isEmpty(charset))
            charset = "UTF-8";

        final Writer writer = new OutputStreamWriter(outputStream, charset);
        writer.write(content);
        writer.flush();
    }

    public static void copy(InputStream inputStream, OutputStream outputStream, ProgressCallback callback) throws IOException
    {
        if (!(inputStream instanceof BufferedInputStream))
            inputStream = new BufferedInputStream(inputStream);

        if (!(outputStream instanceof BufferedOutputStream))
            outputStream = new BufferedOutputStream(outputStream);

        long count = 0;
        int len = 0;
        final byte[] buffer = new byte[1024];
        while ((len = inputStream.read(buffer)) != -1)
        {
            outputStream.write(buffer, 0, len);
            count += len;

            if (callback != null)
                callback.onProgress(count);
        }
        outputStream.flush();
    }

    public static void closeQuietly(Closeable closeable)
    {
        if (closeable != null)
        {
            try
            {
                closeable.close();
            } catch (Throwable ignored)
            {
            }
        }
    }

    public interface ProgressCallback
    {
        void onProgress(long count);
    }
}
