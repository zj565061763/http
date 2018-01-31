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

        final Type[] types = getGenericTypes(getClass());
        if (types != null && types.length > 0)
        {
            if (types[0] instanceof Class)
            {
                mActModel = parseToModel(getResult(), (Class<T>) types[0]);
            }
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

    private Type[] getGenericTypes(Class<?> clazz)
    {
        ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericSuperclass();
        return parameterizedType.getActualTypeArguments();
    }
}
