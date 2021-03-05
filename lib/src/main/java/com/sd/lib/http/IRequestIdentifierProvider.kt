package com.sd.lib.http

interface IRequestIdentifierProvider {
    /**
     * 根据请求对象生成它的唯一标识
     */
    fun provideRequestIdentifier(request: IRequest): String?
}