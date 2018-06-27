package com.fanwe.lib.http.callback;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by zhengjun on 2017/10/12.
 */
public abstract class ModelRequestCallback<T> extends StringRequestCallback
{
    private T mActModel;

    @Override
    public void onSuccessBackground() throws Exception
    {
        super.onSuccessBackground();

        final Type[] types = getGenericTypes(getClass(), ModelRequestCallback.class);
        if (types != null && types.length > 0)
        {
            mActModel = parseToModel(getResult(), (Class<T>) types[0]);
        } else
        {
            throw new RuntimeException("generic type not found");
        }
    }

    public final T getActModel()
    {
        return mActModel;
    }

    /**
     * 将字符串解析为实体
     *
     * @param content
     * @param clazz
     * @return
     */
    protected abstract T parseToModel(String content, Class<T> clazz);

    private static Type[] getGenericTypes(Class<?> clazz, Class<?> superClass)
    {
        if (clazz == null || superClass == null)
            throw new NullPointerException("params must not be null");

        while (true)
        {
            if (clazz.getSuperclass() == superClass)
                break;
            else
                clazz = clazz.getSuperclass();
        }

        final ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericSuperclass();
        return parameterizedType.getActualTypeArguments();
    }
}
