package com.sd.lib.http.body;

import android.text.TextUtils;

import com.sd.lib.http.ContentType;

import java.io.File;
import java.net.HttpURLConnection;

public class FileBody implements IRequestBody<File>
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
        String contentType = HttpURLConnection.guessContentTypeFromName(mFile.getName());
        if (TextUtils.isEmpty(contentType))
            contentType = ContentType.STREAM;

        return contentType;
    }

    @Override
    public final File getBody()
    {
        return mFile;
    }
}
