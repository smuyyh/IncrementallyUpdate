# IncrementallyUpdate
Android 实现应用的增量更新和升级

```java
String srcDir = Environment.getExternalStorageDirectory().toString() + "/DaemonProcess-1.apk";
String destDir1 = Environment.getExternalStorageDirectory().toString() + "/DaemonProcess-2.apk";
String destDir2 = Environment.getExternalStorageDirectory().toString() + "/DaemonProcess-3.apk";
String patchDir = Environment.getExternalStorageDirectory().toString() + "/DaemonProcess.patch";
```
首先来看看这四个文件的作用

srcDir：