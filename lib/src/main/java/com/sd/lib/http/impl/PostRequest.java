package com.sd.lib.http.impl;

import android.text.TextUtils;

import com.sd.lib.http.ContentType;
import com.sd.lib.http.IPostRequest;
import com.sd.lib.http.IResponse;
import com.sd.lib.http.body.BytesBody;
import com.sd.lib.http.body.FileBody;
import com.sd.lib.http.body.IRequestBody;
import com.sd.lib.http.body.StringBody;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PostRequest extends BaseRequest implements IPostRequest
{
    private List<FilePart> mListFile;
    private IRequestBody mBody;

    private List<FilePart> getListFile()
    {
        if (mListFile == null)
            mListFile = new ArrayList<>();
        return mListFile;
    }

    @Override
    public PostRequest addPart(String name, File file)
    {
        addPart(name, null, null, file);
        return this;
    }

    @Override
    public PostRequest addPart(String name, String filename, String contentType, File file)
    {
        final FilePart filePart = new FilePart(name, filename, contentType, file);
        getListFile().add(filePart);
        return this;
    }

    @Override
    protected IResponse doExecute() throws Exception
    {
        final HttpRequest request = newHttpRequest(getUrl(), HttpRequest.METHOD_POST);

        if (mBody != null)
        {
            request.contentType(mBody.getContentType());
            if (mBody instanceof StringBody)
            {
                final String body = ((StringBody) mBody).getBody();
                request.send(body);
            } else if (mBody instanceof FileBody)
            {
                final File body = ((FileBody) mBody).getBody();
                request.send(body);
            } else if (mBody instanceof BytesBody)
            {
                final byte[] body = ((BytesBody) mBody).getBody();
                request.send(body);
            }
        } else
        {
            final Map<String, Object> params = getParams().toMap();
            if (mListFile != null && !mListFile.isEmpty())
            {
                for (Map.Entry<String, Object> item : params.entrySet())
                {
                    request.part(item.getKey(), String.valueOf(item.getValue()));
                }

                for (FilePart item : mListFile)
                {
                    request.part(item.name, item.filename, item.contentType, item.file);
                }

            } else
            {
                request.form(params);
            }
        }

        final Response response = new Response(request);
        response.getCodeOrThrow();

        return response;
    }

    @Override
    public PostRequest setBody(IRequestBody body)
    {
        mBody = body;
        return this;
    }

    private static final class FilePart
    {
        public final String name;
        public final String filename;
        public final String contentType;
        public final File file;

        public FilePart(String name, String filename, String contentType, File file)
        {
            if (TextUtils.isEmpty(contentType))
            {
                contentType = HttpURLConnection.guessContentTypeFromName(file.getName());
                if (TextUtils.isEmpty(contentType))
                    contentType = ContentType.STREAM;
            }

            if (TextUtils.isEmpty(filename))
                filename = file.getName();

            this.name = name;
            this.filename = filename;
            this.contentType = contentType;
            this.file = file;
        }
    }
}
