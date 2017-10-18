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

* 网络或者服务端等外部原因造成的异常：<br>
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

## 文件下载
目前不支持断点下载，只能重头开始下载
```java
Request request = new GetRequest(URL_FILE); //创建请求对象
request.setTag(TAG); //给请求设置tag，可用于取消请求

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
PostRequest request = new PostRequest(URL); //创建请求对象
request.addFile("file", file) //添加File对象
        .param("ctl", "avatar").param("act", "uploadImage"); //添加form参数
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
//设置请求拦截对象，可用于log输出，或者一些需要全局处理的逻辑，注意这边传入的对象如果是和资源相关的对象，需要在资源销毁的时候remove
RequestManager.getInstance().addRequestInterceptor(new IRequestInterceptor()
{
    @Override
    public void beforeExecute(Request request)
    {
        //请求被真正执行之前回调
        Log.i(TAG, "beforeExecute:" + request);
    }

    @Override
    public void afterExecute(Response response)
    {
        //请求被真正执行之后回调
    }
});
```

## 请求标识
在实际开发中有的接口请求如果点击连续请求的话，我们希望只有最后一次有效，那么可以通过给请求设置标识的方式来取消之前发起的请求
1. 实现IRequestIdentifierProvider接口
```java
public class AppRequestIdentifierProvider implements IRequestIdentifierProvider
{
    @Override
    public String provideRequestIdentifier(Request request)
    {
        String identifier = null;

        //此处的clt和act为作者公司服务端标识接口的参数，故用这两个参数组合来生成请求标识
        Object ctl = request.getMapParam().get("ctl");
        Object act = request.getMapParam().get("act");
        if (ctl != null && act != null)
        {
            identifier = ctl + "," + act;
        }

        return identifier;
    }
}
```

2. 设置对象给RequestManager
```java
//设置Request的唯一标识生成对象，注意这边传入的对象不应该是和资源相关的对象，否则资源销毁回调的时候被单例持有会造成内存泄漏
RequestManager.getInstance().setRequestIdentifierProvider(new IRequestIdentifierProvider()
{
    @Override
    public String provideRequestIdentifier(Request request)
    {
        String identifier = null;

        //此处的clt和act为作者公司服务端标识接口的参数，故用这两个参数组合来生成请求标识
        Object ctl = request.getMapParam().get("ctl");
        Object act = request.getMapParam().get("act");
        if (ctl != null && act != null)
        {
            identifier = ctl + "," + act;
        }

        return identifier;
    }
});
```

3. 在异步回调接口的onPrepare方法中取消已经发起的请求
```java
@Override
public void onPrepare(Request request)
{
    super.onPrepare(request);
    RequestManager.getInstance().cancelRequestIdentifier(request);
}
```

## 多个异步回调
在实际开发中，有的接口需要很多地方都调用的时候一般把这个接口封装到一个方法中，然后要求外部传入一个异步回调对象来监听。<br>
但是假如我们封装的方法中需要先做一些统一的处理，然后再通知外部传进来的回调对象，那怎么处理比较方便呢？<br>
<br>
通常的实现办法是先在内部做处理，然后再手动通知外部传进来的回调，如下：
```java
public static void requestCommonInterface(final RequestCallback callback)
{
    new PostRequest(URL).param("ctl", "app").param("act", "init").execute(new RequestCallback()
    {
        @Override
        public void onSuccess()
        {
            //内部事先做一些统一的处理
            Log.i(TAG, "do common logic");
            if (callback != null)
            {
                callback.onSuccess();
            }
        }
    });
}
```

以上方法较为繁琐，每个地方都要写，当然也可以通过代理模式减少一些重复的代码，但也是挺繁琐的，这边介绍一个库中的类可以解决这种尴尬，具体代码如下：
```java
public void requestCommonInterface(final RequestCallback callback)
{
    //公共逻辑回调
    IRequestCallback commonLogicCallback = new RequestCallback()
    {
        @Override
        public void onSuccess()
        {
            Log.i(TAG, "do common logic");
        }
    };

    //根据多个回调对象动态生成一个回调代理对象，这里要注意的是，多个回调对象中的getResponse()返回的都是同一个对象
    IRequestCallback callbackProxy = RequestCallbackProxy.get(commonLogicCallback, callback);

    new PostRequest(URL).param("ctl", "app").param("act", "init").execute(callbackProxy);
}
```
