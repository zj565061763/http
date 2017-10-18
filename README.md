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

## cookie管理
库中已经内置了一个SerializableCookieStore来管理cookie，如果用户要自己实现的话，可以实现ICookieStore接口
```java
//设置cookie管理对象
RequestManager.getInstance().setCookieStore(new SerializableCookieStore(this));
```