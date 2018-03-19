package com.fanwe.lib.http;

/**
 * 用来给Request生成标识
 * Created by zhengjun on 2017/10/17.
 */
public interface IRequestIdentifierProvider
{
    /**
     * 根据Request生成它的唯一标识
     *
     * @param request
     * @return
     */
    String provideRequestIdentifier(IRequest request);
}
