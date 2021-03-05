package com.sd.lib.http;

import java.io.File;

public interface IPostRequest extends IRequest, IBodyRequest
{
    void addPart(String name, File file);

    void addPart(String name, String filename, String contentType, File file);

    void setParamsType(ParamsType type);

    enum ParamsType
    {
        Default,
        Json
    }
}
