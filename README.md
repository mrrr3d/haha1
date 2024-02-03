# haha1

#### Introduction
with Amap locate api, get the mobile's location every second, and it can send the loation to the server. And try the best to keep the app alive.


the project doesn't use the WakeLock(唤醒锁). it uses the foregroundservice, and the class OpenNotificationsUtil is copied from [link](https://zhuanlan.zhihu.com/p/592117184). Meanwhile, it will try to request OS ignore BatteryOptimizations.

When it runs on the Harmonyos 3.0, allow 自启动.
