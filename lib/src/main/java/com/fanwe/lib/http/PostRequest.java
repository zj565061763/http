package com.fanwe.lib.http;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengjun on 2017/10/11.
 */

public class PostRequest extends HttpRequestImpl
{
    private List<FileRequestBody> mListFilePart;

    protected PostRequest(String url)
    {
        super(url);
    }

    public List<FileRequestBody> getListFilePart()
    {
        if (mListFilePart == null)
        {
            mListFilePart = new ArrayList<>();
        }
        return mListFilePart;
    }

    public PostRequest addPart(String name, File part)
    {
        addPart(name, null, null, part);
        return this;
    }

    public PostRequest addPart(String name, String filename, String contentType, File part)
    {
        FileRequestBody body = new FileRequestBody();
        body.name = name;
        body.filename = filename;
        body.contentType = contentType;
        body.part = part;
        getListFilePart().add(body);
        return this;
    }


    @Override
    protected void doExecute(Response response) throws Exception
    {
        HttpRequest request = newHttpRequest(getUrl(), HttpRequest.METHOD_POST);

        request.form(getMapParam());
        if (mListFilePart != null && !mListFilePart.isEmpty())
        {
            for (FileRequestBody item : mListFilePart)
            {
                request.part(item.name, item.filename, item.contentType, item.part);
            }
        }

        response.fillValue(request);
    }

    private static class RequestBody
    {
        public String name;
        public String filename;
        public String contentType;
    }

    private static class FileRequestBody extends RequestBody
    {
        public File part;
    }
}
