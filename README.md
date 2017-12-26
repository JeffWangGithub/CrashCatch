### 应用自定义捕获异常，弹出崩溃是 UI 对话框，代替洗他那个的崩溃弹窗

#### 原理

 - 1. 获取系统默认的DefaultUncaughtExceptionHandler，暂时保存；
 - 2. 设置自定义的 UncaughtExceptionHandler，处理异常
 - 3. 处理崩溃是 UI，开启新线程处理
 - 4. 将异常信息再次传递给系统DefaultUncaughtExceptionHandler
      注意：如果不进行传递给系统处理，则会产生 ANR 的状况，原因不必解释了吧
      
      
#### 使用

    ```
        GLExceptionHandler.getInstance().init(Application application);
    
    ```
    注意：如果有使用其他第三方 crash 统计工具(如 Fabric crashly)，要放到第三方初始化之后；