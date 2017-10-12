package com.fanwe.lib.http;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by zhengjun on 2017/10/12.
 */
public abstract class ModelRequestCallback<T> extends StringRequestCallback
{
    private T mModel;

    @Override
    public void onSuccessBackground() throws Exception
    {
        super.onSuccessBackground();

        Type type = getType(getClass(), 0);
        if (type instanceof Class)
        {
            mModel = parseToModel(getResult(), (Class<T>) type);
        }
    }

    public T getModel()
    {
        return mModel;
    }

    /**
     * 将字符串解析为实体
     *
     * @param content
     * @param clazz
     * @return
     */
    protected abstract T parseToModel(String content, Class<T> clazz);

    private Type getType(Class<?> clazz, int index)
    {
        Type type = null;
        Type[] types = this.getType(clazz);
        if (types != null && index >= 0 && types.length > index)
        {
            type = types[index];
        }
        return type;
    }

    private Type[] getType(Class<?> clazz)
    {
        Type[] types = null;
        if (clazz != null)
        {
            Type type = clazz.getGenericSuperclass();
            ParameterizedType parameterizedType = (ParameterizedType) type;
            types = parameterizedType.getActualTypeArguments();
        }

        return types;
    }
}
