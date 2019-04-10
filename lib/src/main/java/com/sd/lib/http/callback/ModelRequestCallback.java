package com.sd.lib.http.callback;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class ModelRequestCallback<T> extends StringRequestCallback
{
    private T mActModel;

    @Override
    public void onSuccessBackground() throws Exception
    {
        super.onSuccessBackground();

        final Type type = getModelType();
        mActModel = parseToModel(getResult(), type);
    }

    protected Type getModelType()
    {
        final ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        final Type[] types = parameterizedType.getActualTypeArguments();
        if (types != null && types.length > 0)
        {
            return types[0];
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
     * @param type
     * @return
     */
    protected abstract T parseToModel(String content, Type type);
}
