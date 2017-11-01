package com.fanwe.www.http;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengjun on 2017/11/1.
 */
public class InterfaceDispatcher<T> implements InvocationHandler
{
    private Class<T> mClazz;
    private T mProxy;
    private List<T> mListSubscriber;

    public InterfaceDispatcher(Class<T> clazz)
    {
        mClazz = clazz;

        if (clazz == null)
        {
            throw new NullPointerException("clazz must not be null");
        }
        if (!clazz.isInterface())
        {
            throw new NullPointerException("clazz must be an interface class");
        }
    }

    public T getProxy()
    {
        if (mProxy == null)
        {
            mProxy = (T) Proxy.newProxyInstance(mClazz.getClassLoader(), new Class<?>[]{mClazz}, this);
        }
        return mProxy;
    }

    private List<T> getListSubscriber()
    {
        if (mListSubscriber == null)
        {
            mListSubscriber = new ArrayList<>();
        }
        return mListSubscriber;
    }

    public void addSubscriber(T subscriber)
    {
        if (subscriber == null)
        {
            return;
        }
        if (!getListSubscriber().contains(subscriber))
        {
            getListSubscriber().add(subscriber);
        }
    }

    public void removeSubscriber(T subscriber)
    {
        if (subscriber == null || mListSubscriber == null)
        {
            return;
        }
        getListSubscriber().remove(subscriber);
    }

    public void clearSubscriber()
    {
        if (mListSubscriber == null)
        {
            return;
        }
        mListSubscriber.clear();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        Object result = null;
        if (mListSubscriber != null)
        {
            for (T item : mListSubscriber)
            {
                result = method.invoke(item, args);

                if (item instanceof IMethodResultProvider)
                {
                    IMethodResultProvider provider = (IMethodResultProvider) item;
                    if (provider.provideMethodResult(method, args, result))
                    {
                        break;
                    }
                }
            }
        }

        return result;
    }

    public interface IMethodResultProvider
    {
        boolean provideMethodResult(Method method, Object[] args, Object result);
    }
}
