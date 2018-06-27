package com.fanwe.lib.http.body;

import android.text.TextUtils;

import java.io.File;
import java.net.HttpURLConnection;

/**
 * Created by zhengjun on 2017/10/16.
 */

public class FileRequestBody extends RequestBody
{
    private File mFile;

    public FileRequestBody(File file)
    {
        mFile = file;
    }

    public String getFilename()
    {
        return mFile.getName();
    }

    @Override
    public String getContentType()
    {
        String result = super.getContentType();
        if (TextUtils.isEmpty(result) && mFile != null)
            result = getFileContentType(mFile);

        return result;
    }

    public static String getFileContentType(File file)
    {
        final String filename = file.getName();
        String contentType = HttpURLConnection.guessContentTypeFromName(filename);
        if (TextUtils.isEmpty(contentType))
            contentType = "application/octet-stream";

        return contentType;
    }
}
