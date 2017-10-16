package com.fanwe.lib.http;

import com.fanwe.lib.http.body.FileRequestBody;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public PostRequest addPart(String name, String filename, File part)
    {
        addPart(name, filename, null, part);
        return this;
    }

    public PostRequest addPart(String name, String filename, String contentType, File part)
    {
        FileRequestBody body = new FileRequestBody();
        body.setName(name);
        body.setFilename(filename);
        body.setContentType(contentType);
        body.setFile(part);

        getListFilePart().add(body);
        return this;
    }

    @Override
    protected void doExecute(Response response) throws Exception
    {
        HttpRequest request = newHttpRequest(getUrl(), HttpRequest.METHOD_POST);

        if (mListFilePart != null && !mListFilePart.isEmpty())
        {
            for (Map.Entry<String, Object> item : getMapParam().entrySet())
            {
                request.part(item.getKey(), String.valueOf(item.getValue()));
            }
            for (FileRequestBody item : mListFilePart)
            {
                request.part(item.getName(), item.getFilename(), item.getContentType(), item.getFile());
            }
        } else
        {
            request.form(getMapParam());
        }

        response.fillValue(request);
    }
}
