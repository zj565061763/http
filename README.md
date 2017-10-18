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
