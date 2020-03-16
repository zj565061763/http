package com.sd.lib.http;

import java.io.File;

public interface IPostRequest extends IRequest, IBodyRequest
{
    IPostRequest addPart(String name, File file);

    IPostRequest addPart(String name, String filename, String contentType, File file);

    IPostRequest setParamsType(ParamsType type);

    enum ParamsType
    {
        Default,
        Json
    }
}
