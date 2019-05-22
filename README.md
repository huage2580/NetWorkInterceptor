# NetWorkInterceptor
实时拦截，浮窗显示网络请求；android端内进行网络请求的日志分析

## 使用 （sample
```java
okhttpClient.addInterceptor(new HttpInterceptor());

// check permission

 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)){
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 100);
            }
        }
        
//start service
startService(new Intent(MainActivity.this, SpyXService.class))
```
出现悬浮窗后开始记录网络请求，点击悬浮窗查看（Start recording the network request after the floating window appears; click on the floating window to view  
![截图1](https://github.com/huage2580/NetWorkInterceptor/blob/master/png1.png?raw=true)  
详情 (detail
![截图2](https://github.com/huage2580/NetWorkInterceptor/blob/master/png2.png?raw=true)

## ❗请注意（warning  
appcompat & recyclerView used androidX supprot package!

