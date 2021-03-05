package com.sd.lib.http.body;

public interface IRequestBody<T>
{
    String getContentType();

    T getBody();
}
