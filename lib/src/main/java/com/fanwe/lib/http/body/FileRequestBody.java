package com.fanwe.lib.http.body;

import android.text.TextUtils;

import java.io.File;
import java.net.HttpURLConnection;

/**
 * Created by zhengjun on 2017/10/16.
 */

public class FileRequestBody extends RequestBody
{
    private File file;

    public File getFile()
    {
        return file;
    }

    public void setFile(File file)
    {
        this.file = file;
    }

    @Override
    public String getFilename()
    {
        String result = super.getFilename();
        if (TextUtils.isEmpty(result) && getFile() != null)
        {
            result = getFile().getName();
        }
        return result;
    }

    @Override
    public String getContentType()
    {
        String result = super.getContentType();
        if (TextUtils.isEmpty(result))
        {
            result = getFileContentType(getFile());
        }
        return result;
    }

    public static String getFileContentType(File file)
    {
        String filename = file.getName();
        String contentType = HttpURLConnection.guessContentTypeFromName(filename);
        if (TextUtils.isEmpty(contentType))
        {
            contentType = "application/octet-stream";
        }
        return contentType;
    }
}
