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

        final Class<T> modelClass = getModelClass();
        mActModel = parseToModel(getResult(), modelClass);
    }

    protected Class<T> getModelClass()
    {
        final ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        final Type[] types = parameterizedType.getActualTypeArguments();
        if (types != null && types.length > 0)
        {
            return (Class<T>) types[0];
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
}
