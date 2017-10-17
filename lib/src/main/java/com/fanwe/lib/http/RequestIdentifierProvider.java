package com.fanwe.lib.http;

/**
 * 用来给Request生成唯一标识
 * Created by zhengjun on 2017/10/17.
 */
public interface RequestIdentifierProvider
{
    RequestIdentifierProvider DEFAULT = new RequestIdentifierProvider()
    {
        @Override
        public String provideRequestIdentifier(Request request)
        {
            return null;
        }
    };

    /**
     * 根据Request生成它的唯一标识
     *
     * @param request
     * @return
     */
    String provideRequestIdentifier(Request request);
}
