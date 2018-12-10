package com.sd.lib.http.body;

import android.text.TextUtils;

import java.io.File;
import java.net.HttpURLConnection;

public class FileBody extends BaseBody<File>
{
    private final File mFile;

    public FileBody(File file)
    {
        if (file == null)
            throw new NullPointerException("file is null");
        mFile = file;
    }

    @Override
    public final String getContentType()
    {
        final String contentType = HttpURLConnection.guessContentTypeFromName(mFile.getName());
        if (!TextUtils.isEmpty(contentType))
            return contentType;

        return super.getContentType();
    }

    @Override
    public final File getBody()
    {
        return mFile;
    }
}
