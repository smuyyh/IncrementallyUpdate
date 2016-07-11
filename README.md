# IncrementallyUpdate
Android 实现应用的增量更新和升级

### 原理
服务端通过新版本APK和旧版本APK生成patch补丁（也成为差分包），客户端更新的时候只需要下载差分包到本地，然后从system/app取出旧版本APK，通过差分包来合成新版本的APK，这个过程实际上就是打补丁。

1、copy resource：拷贝旧版本APK以及新版本APK到SD卡。为了后面进行生成差分包。

2、install old apk：安装旧版本的APK。

3、bsdiff：生成差分包。这个实际上应该是在服务端完成，目前服务端工具我还没制作出来，后面补上。

4、bspatch：通过差分包及旧版本APK生成新版本APK。

5、install new apk：安装生成的新版本APK。

```java
String srcDir = Environment.getExternalStorageDirectory().toString() + "/DaemonProcess-1.apk";
String destDir1 = Environment.getExternalStorageDirectory().toString() + "/DaemonProcess-2.apk";
String destDir2 = Environment.getExternalStorageDirectory().toString() + "/DaemonProcess-3.apk";
String patchDir = Environment.getExternalStorageDirectory().toString() + "/DaemonProcess.patch";
```
首先来看看这四个文件的作用

srcDir：旧版本apk路径。也就是已安装的旧版应用的APK地址。为了便于演示，这边直接写死路径。若想真正获取旧版apk地址，可通过下面代码实现：
```java
String appDir = getPackageManager().getApplicationInfo(packageName, 0).sourceDir;
```
destDir1：新版本的apk路径。

destDir2：新版本的apk路径。通过差分包+旧版本APK合成新版本APK。

patchDir：差分包。通过旧版本APK+新版本APK生成差分包。

### 拷贝资源
两个版本的APK文件放置于Assets文件夹下，进行拆分和合成之前要先点击“copy resource”拷贝到SD卡。

### NDK配置
下载NDK，并local.properties下配置自己ndk路径
```xml
ndk.dir=/Users/yuyuhang/Documents/Android/android-ndk-r10c
```
若不想编译jni资源，也可直接使用项目提供的so库。在build.gradle配置so库路径
```java
sourceSets {
        main {
            // jni.srcDirs = ['src/main/jni', 'src/main/jni/']
            jniLibs.srcDirs = ['libs'] // 若不想编译jni代码，可直接引用so库，ndk编译相关脚本注释掉
        }
    }
```

### 使用
调用生成差分包及合成APK的native方法。
```java
package com.yyh.lib.bsdiff;

public class DiffUtils {

	static DiffUtils instance;

	public static DiffUtils getInstance() {
		if (instance == null)
			instance = new DiffUtils();
		return instance;
	}

	static {
		System.loadLibrary("ApkPatchLibrary");
	}

	/**
	 * native方法 比较路径为oldPath的apk与newPath的apk之间差异，并生成patch包，存储于patchPath
	 * 
	 * 返回：0，说明操作成功
	 * 
	 * @param oldApkPath
	 *            示例:/sdcard/old.apk
	 * @param newApkPath
	 *            示例:/sdcard/new.apk
	 * @param patchPath
	 *            示例:/sdcard/xx.patch
	 * @return
	 */
	public native int genDiff(String oldApkPath, String newApkPath, String patchPath);
}
```
```java
package com.yyh.lib.bsdiff;

public class PatchUtils {

	static PatchUtils instance;

	public static PatchUtils getInstance() {
		if (instance == null)
			instance = new PatchUtils();
		return instance;
	}

	static {
		System.loadLibrary("ApkPatchLibrary");
	}

	/**
	 * native方法 使用路径为oldApkPath的apk与路径为patchPath的补丁包，合成新的apk，并存储于newApkPath
	 * 
	 * 返回：0，说明操作成功
	 * 
	 * @param oldApkPath
	 *            示例:/sdcard/old.apk
	 * @param newApkPath
	 *            示例:/sdcard/new.apk
	 * @param patchPath
	 *            示例:/sdcard/xx.patch
	 * @return
	 */
	public native int patch(String oldApkPath, String newApkPath, String patchPath);
}
```

### 新版本APK安装
![image description](https://github.com/smuyyh/IncrementallyUpdate/blob/master/screenshot/2.png)