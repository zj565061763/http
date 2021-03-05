package com.sd.lib.http;

/**
 * 用来给Request生成标识
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
