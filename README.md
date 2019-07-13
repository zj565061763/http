# About
封装了一层Http的接口，默认实现用的是：[http-request](https://github.com/kevinsawicki/http-request)

# Gradle
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
            IRequest request = new GetRequest();
            //设置请求地址
            request.setBaseUrl("https://www.baidu.com/");
            //发起请求，得到Response对象
            IResponse response = request.execute();
            //请求结果以字符串返回
            final String result = response.getAsString();

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    mTextView.setText(result);
                }
            });
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}).start();
```

## 异步请求

* 正常回调：<br>
`onPrepare(请求发起所在线程)->onStart(UI线程)->onSuccessBackground(非UI线程)->onSuccessBefore(UI线程)->onSuccess(UI线程)->onFinish(UI线程)`

```java
IRequest request = new GetRequest();
// 设置请求地址
request.setBaseUrl(URL);
// 设置请求参数
request.getParams().put("aaa", "aaa").put("bbb", "bbb");
// 设置该请求的tag，可用于取消请求
request.setTag(TAG);

// 发起异步请求
RequestHandler requestHandler = request.execute(new ModelRequestCallback<WeatherModel>()
{
    @Override
    public void onPrepare(IRequest request)
    {
        super.onPrepare(request);
        // 请求在被执行之前的准备回调(发起请求被调用的线程)
        Log.i(TAG, "onPrepare");
    }

    @Override
    public void onStart()
    {
        super.onStart();
        // 请求开始执行(UI线程)
        Log.i(TAG, "onStart");
    }

    @Override
    public void onSuccessBackground() throws Exception
    {
        super.onSuccessBackground();
        // 成功回调，super里面回调了parseToModel方法把返回的内容转为实体，(非UI线程)
        Log.i(TAG, "onSuccessBackground");
    }

    @Override
    protected WeatherModel parseToModel(String content, Class<WeatherModel> clazz)
    {
        // 把返回的内容转实体(非UI线程)
        return new Gson().fromJson(content, clazz);
    }

    @Override
    public void onSuccess()
    {
        // 成功回调(UI线程)

        // 获得返回结果对象
        IResponse response = getResponse();
        // 获得接口对应的实体
        WeatherModel model = getActModel();
        Log.i(TAG, "onSuccess:" + model.weatherinfo.city);
    }

    @Override
    public void onError(Exception e)
    {
        super.onError(e);
        // 异常回调，请求异常或者转实体出错(UI线程)
        Log.i(TAG, "onError:" + e);
    }

    @Override
    public void onCancel()
    {
        super.onCancel();
        // 请求被取消回调(UI线程)
        Log.i(TAG, "onCancel");
    }

    @Override
    public void onFinish()
    {
        super.onFinish();
        // 结束回调(UI线程)
        Log.i(TAG, "onFinish");
    }
});

requestHandler.cancel(); //根据异步请求关联的对象取消请求
RequestManager.getInstance().cancelTag(TAG); //根据tag取消请求
```

## 文件下载
目前不支持断点下载，只能重头开始下载
```java
IRequest request = new GetRequest();
request.setBaseUrl(URL); //设置下载地址

File file = new File(getExternalCacheDir(), "download.apk"); //设置下载文件要保存的File
request.execute(new FileRequestCallback(file)
{
    @Override
    protected void onProgressDownload(TransmitParam param)
    {
        mProgressBar.setProgress(param.getProgress()); //下载进度
        mTvProgress.setText(param.getProgress() + "%"); //下载百分比
        mTvSpeed.setText(param.getSpeedKBps() + "KB/秒"); //下载速度
        if (param.isFinish())
        {
            //下载完成
            mTvSpeed.setText("");
            Log.i(TAG, "download finish");
        }
    }

    @Override
    public void onSuccess()
    {

    }
});
```

## 文件上传
```java
IPostRequest request = new PostRequest();
request.setBaseUrl(URL);
// 添加File对象
request.addFile("file", file)
        .param("aaa", "aaa").param("bbb", "bbb");
request.execute(new RequestCallback()
{
    @Override
    public void onProgressUpload(TransmitParam param)
    {
        super.onProgressUpload(param);
        mProgressBar.setProgress(param.getProgress()); //上传进度
        mTvProgress.setText(param.getProgress() + "%"); //上传百分比
        mTvSpeed.setText(param.getSpeedKBps() + "KB/秒"); //上传速度
        if (param.isFinish())
        {
            //上传完成
            mTvSpeed.setText("");
            Log.i(TAG, "download finish");
        }
    }

    @Override
    public void onSuccess()
    {

    }
});
```

## Cookie管理
库中已经内置了一个SerializableCookieStore来管理cookie，如果用户要自己实现的话，可以实现ICookieStore接口
```java
//设置cookie管理对象
RequestManager.getInstance().setCookieStore(new SerializableCookieStore(this));
```

## 请求拦截
在实际开发中，有些业务需要全局处理，比如发起请求之前对参数加密，打印每次请求的地址等。可以实现IRequestInterceptor接口，把对象设置给RequestManager
```java
//设置请求拦截对象，可用于log输出，或者一些需要全局处理的逻辑
RequestManager.getInstance().addRequestInterceptor(new IRequestInterceptor()
{
    @Override
    public IResponse beforeExecute(IRequest request)
    {
        //请求被真正执行之前回调
        Log.i(TAG, "beforeExecute:" + request);
        return null;
    }

    @Override
    public IResponse afterExecute(IRequest request, IResponse response);
    {
        //请求被真正执行之后回调
        return null;
    }
});
```

## 请求标识
在实际开发中有的接口请求如果点击连续请求的话，我们希望只有最后一次有效，那么可以通过给请求设置标识的方式来取消之前发起的请求
1. 实现IRequestIdentifierProvider接口，并设置给RequestManager
```java
//设置Request的唯一标识生成对象，注意这边传入的对象不应该是和资源相关的对象，否则资源销毁回调的时候被单例持有会造成内存泄漏
RequestManager.getInstance().setRequestIdentifierProvider(new IRequestIdentifierProvider()
{
    @Override
    public String provideRequestIdentifier(IRequest request)
    {
        String identifier = null;

        //此处的act为作者公司服务端标识接口的参数，故用这个参数组合来生成请求标识
        Object act = request.getParam("act");
        if (act != null)
        {
            identifier = String.valueOf(act);
        }

        return identifier;
    }
});
```

2. 在异步回调接口的onPrepare方法中取消已经发起的请求
```java
@Override
public void onPrepare(IRequest request)
{
    super.onPrepare(request);
    RequestManager.getInstance().cancelRequestIdentifier(request);
}
```

## 多个异步回调
在实际开发中，有的接口需要很多地方都调用的时候一般把这个接口封装到一个方法中，然后要求外部传入一个异步回调对象来监听。<br>
但是假如我们封装的方法中需要先做一些统一的处理，然后再通知外部传进来的回调对象，可以通过以下方法较为方便的实现<br>
```java
public void requestCommonInterface(final RequestCallback callback)
{
    IPostRequest request = new PostRequest();
    request.setBaseUrl(URL);
    request.getParams().put("aaa", "aaa");
    request.execute(RequestCallbackProxy.get(new RequestCallback()
    {
        @Override
        public void onSuccess()
        {
            Log.i(TAG, "do common logic");
        }
    }, callback));
}
```