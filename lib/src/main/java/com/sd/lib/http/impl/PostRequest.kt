package com.sd.lib.http.impl;

import android.text.TextUtils;

import com.sd.lib.http.ContentType;
import com.sd.lib.http.IPostRequest;
import com.sd.lib.http.IResponse;
import com.sd.lib.http.body.BytesBody;
import com.sd.lib.http.body.FileBody;
import com.sd.lib.http.body.IRequestBody;
import com.sd.lib.http.body.JsonBody;
import com.sd.lib.http.body.StringBody;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PostRequest extends BaseRequestImpl implements IPostRequest
{
    private List<FilePart> mListFile;
    private IRequestBody mBody;

    private ParamsType mParamsType = ParamsType.Default;

    private List<FilePart> getListFile()
    {
        if (mListFile == null)
            mListFile = new ArrayList<>();
        return mListFile;
    }

    @Override
    public void addPart(@NotNull String name, @NotNull File file)
    {
        addPart(name, file, null, null);
    }

    @Override
    public void addPart(@NotNull String name, @NotNull File file, @Nullable String filename, @Nullable String contentType)
    {
        final FilePart filePart = new FilePart(name, file, filename, contentType);
        getListFile().add(filePart);
    }

    @Override
    public void setBody(IRequestBody body)
    {
        mBody = body;
    }

    @Override
    public void setParamsType(ParamsType type)
    {
        if (type == null)
            throw new NullPointerException("type is null");

        mParamsType = type;
    }

    @Override
    protected IResponse doExecute() throws Exception
    {
        final HttpRequest request = newHttpRequest(getUrl(), HttpRequest.METHOD_POST);

        if (mBody != null)
        {
            executeBody(request);
        } else
        {
            if (mParamsType == ParamsType.Default)
            {
                executeDefault(request);
            } else if (mParamsType == ParamsType.Json)
            {
                executeJson(request);
            }
        }

        final Response response = new Response(request);
        response.getCodeOrThrow();

        return response;
    }

    private void executeBody(HttpRequest request)
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
    }

    private void executeDefault(HttpRequest request)
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

    private void executeJson(HttpRequest request) throws JSONException
    {
        final JSONObject jsonObject = new JSONObject();
        final Map<String, Object> params = getParams().toMap();
        for (Map.Entry<String, Object> item : params.entrySet())
        {
            jsonObject.put(item.getKey(), item.getValue());
        }

        final String json = jsonObject.toString();
        setBody(new JsonBody(json));
        executeBody(request);
    }


    private static final class FilePart
    {
        public final String name;
        public final String filename;
        public final String contentType;
        public final File file;

        public FilePart(String name, File file, String filename, String contentType)
        {
            if (TextUtils.isEmpty(name))
                throw new IllegalArgumentException("name is empty");

            if (file == null)
                throw new IllegalArgumentException("file is null");

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
