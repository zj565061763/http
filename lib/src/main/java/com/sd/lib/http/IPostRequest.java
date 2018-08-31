package com.sd.lib.http;

import java.io.File;

public interface IPostRequest extends IRequest
{
    IPostRequest addFile(String name, File file);

    IPostRequest addFile(String name, String filename, String contentType, File file);
}
