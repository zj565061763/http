## Gradle
[![](https://jitpack.io/v/zj565061763/http.svg)](https://jitpack.io/#zj565061763/http)

## 同步请求
```java
new Thread(new Runnable()
{
    @Override
    public void run()
    {
        try
        {
            Request request = new PostRequest(URL); //创建请求对象
            request.param("ctl", "app").param("act", "init"); //创建要提交的form数据
            Response response = request.execute(); //发起请求，得到Response对象

            InputStream inputStream = response.getInputStream(); //结果以输入流返回
            int code = response.getCode(); //返回码
            String result = response.getBody(); //结果以字符串返回

            Log.i(TAG, result);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}).start();
```

## 异步请求

* 正常回调：<br>
`onPrepare(execute调用线程)->onStart(UI线程)->onSuccessBackground(非UI线程)->onSuccessBefore(UI线程)->onSuccess(UI线程)->onFinish(UI线程)`

* 网络异常：<br>
`onPrepare(execute调用线程)->onStart(UI线程)->onError(UI线程)->onFinish(UI线程)`

* onSuccessBackground中代码逻辑造成的异常：<br>
`onPrepare(execute调用线程)->onStart(UI线程)->onSuccessBackground(非UI线程)->onError(UI线程)->onFinish(UI线程)`

```java
Request request = new PostRequest(URL) //创建请求对象
        .param("ctl", "app").param("act", "init") //创建要提交的form数据
        .setTag(TAG); //设置请求对应的tag，可用于取消请求对象

RequestHandler requestHandler = request.execute(new ModelRequestCallback<InitActModel>()
{
    @Override
    public void onPrepare(Request request)
    {
        super.onPrepare(request);
        //异步请求在被执行之前的准备回调，execute调用线程
    }

    @Override
    public void onStart()
    {
        super.onStart();
        //异步请求开始执行，ui线程
    }

    @Override
    public void onSuccessBackground() throws Exception
    {
        super.onSuccessBackground();
        //成功回调，super里面回调了parseToModel方法把返回的内容转为实体，非ui线程
    }

    @Override
    public void onSuccess()
    {
        //成功回调，ui线程
        InitActModel model = getActModel(); //获得解析好的实体
        Log.i(TAG, "onSuccess:" + model.getCity());
    }

    @Override
    protected InitActModel parseToModel(String content, Class<InitActModel> clazz)
    {
        //把返回的内容转实体，非ui线程
        return new Gson().fromJson(content, clazz);
    }

    @Override
    public void onError(Exception e)
    {
        super.onError(e);
        //异常回调，ui线程
    }

    @Override
    public void onCancel()
    {
        super.onCancel();
        //异步请求被取消回调，ui线程
    }

    @Override
    public void onFinish()
    {
        super.onFinish();
        //结束回调，ui线程
    }
});

requestHandler.cancel(); //根据异步请求关联的对象取消请求
RequestManager.getInstance().cancelTag(TAG); //根据tag取消请求
```
