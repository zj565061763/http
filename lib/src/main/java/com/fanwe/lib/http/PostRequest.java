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

    public PostRequest(String url)
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

    public PostRequest addFile(String name, File file)
    {
        addFile(name, null, null, file);
        return this;
    }

    public PostRequest addFile(String name, String filename, File file)
    {
        addFile(name, filename, null, file);
        return this;
    }

    public PostRequest addFile(String name, String filename, String contentType, File file)
    {
        FileRequestBody body = new FileRequestBody();
        body.setName(name);
        body.setFilename(filename);
        body.setContentType(contentType);
        body.setFile(file);

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
