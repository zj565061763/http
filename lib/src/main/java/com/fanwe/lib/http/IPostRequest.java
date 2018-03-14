package com.fanwe.lib.http;

import java.io.File;

/**
 * Created by zhengjun on 2018/3/13.
 */
public interface IPostRequest extends IRequest
{
    IPostRequest addFile(String name, File file);

    IPostRequest addFile(String name, String filename, String contentType, File file);
}
