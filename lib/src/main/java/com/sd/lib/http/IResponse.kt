package com.sd.lib.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface IResponse
{
    /**
     * http返回码
     *
     * @return
     */
    int getCode();

    int getContentLength();

    Map<String, List<String>> getHeaders();

    String getCharset();

    InputStream getInputStream();

    /**
     * 读取输入流的内容，并以字符串返回(必须在非UI线程调用)
     *
     * @return
     * @throws IOException
     */
    String getAsString() throws IOException;
}
