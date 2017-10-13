package com.fanwe.lib.http.callback;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by zhengjun on 2017/10/12.
 */
public final class RequestCallbackProxy
{
    private RequestCallbackProxy()
    {
    }

    public static IRequestCallback get(final IRequestCallback... callbacks)
    {
        if (callbacks == null || callbacks.length <= 0)
        {
            return null;
        }
        Object object = Proxy.newProxyInstance(IRequestCallback.class.getClassLoader(),
                new Class[]{IRequestCallback.class},
                new InvocationHandler()
                {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
                    {
                        Object result = null;
                        for (int i = 0; i < callbacks.length; i++)
                        {
                            IRequestCallback item = callbacks[i];
                            result = method.invoke(item, args);
                        }
                        return result;
                    }
                });

        return (IRequestCallback) object;
    }
}
