package com.sd.lib.http.body;

import java.nio.charset.Charset;

public interface IRequestBody<T>
{
    String getContentType();



    T getBody();
}
