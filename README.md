# IncrementallyUpdate
Android 实现应用的增量更新和升级

### 原理
服务端通过新版本APK和旧版本APK生成patch补丁（也成为差分包），客户端更新的时候只需要下载差分包到本地，然后从system/app取出旧版本APK，通过差分包来合成新版本的APK，这个过程实际上就是打补丁。

步骤 | 内容
---|---
拷贝资源 | 拷贝旧版本APK以及新版本APK到SD卡。为了后面进行生成差分包
安装旧版本APK | 安装旧版本的APK
生成补丁 | 生成差分包。这个实际上应该是在服务端完成
打补丁 | 通过差分包及旧版本APK生成新版本APK
安装新版本APK | 安装生成的新版本APK
获取某个应用的APK安装文件 | 在真正的增量更新过程中，旧版本Apk应该从/data/app底下获取，拷贝到SD卡，进行打补丁。当然，也可以不拷贝，直接使用该路径。


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

### NDK配置
若需自己编译jni代码，则下载NDK，并在local.properties下配置自己ndk路径
```xml
ndk.dir=/Users/yuyuhang/Documents/Android/android-ndk-r10c
```
build.gradle加入以下内容：
```java
android {
    defaultConfig {
        ndk{
            moduleName "ApkPatchLibrary"
            abiFilters "armeabi", "armeabi-v7a", "x86"
        }
    }
    sourceSets {
        main {
            jni.srcDirs = ['src/main/jni', 'src/main/jni/']
        }
    }
}
```
若不想编译jni资源，也可直接使用项目提供的so库。在build.gradle配置so库路径,去掉jni编译相关脚本，sync now...
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
	 * 比较oldapk与newapk之间差异，并生成patch包，存储于patchPath
	 * 
	 * @param oldApkPath 示例:/sdcard/old.apk
	 * @param newApkPath 示例:/sdcard/new.apk
	 * @param patchPath 示例:/sdcard/xx.patch
	 * @return 0:成功 非0:失败
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
	 * 使用oldApk与patch补丁包，合成新的apk，存储于newApkPath
	 * 
	 * @param oldApkPath 示例:/sdcard/old.apk
	 * @param newApkPath 示例:/sdcard/new.apk
	 * @param patchPath 示例:/sdcard/xx.patch
	 * @return 0:成功 非0:失败
	 */
	public native int patch(String oldApkPath, String newApkPath, String patchPath);
}
```

### 服务端
服务端工具以及源码位于Server目录下。目前只在Linux64位的系统下编译，其他系统大家可自行编译。Linux下的可直接修改makefile，windows下可用VC编译。

###### Diff工具：生成差分包
```xml
<!--命令             oldApk              newApk              patch-->
./linux-x86_64/Diff DaemonProcess-1.apk DaemonProcess-2.apk dp.patch
```
###### Patch工具：合并

```xml
<!--命令              oldApk              newApk              patch-->
./linux-x86_64/Patch DaemonProcess-1.apk DaemonProcess-3.apk dp.patch
```


### 项目截图

<img src="https://github.com/smuyyh/IncrementallyUpdate/blob/master/screenshot/1.png?raw=true" width="280"/>
<img src="https://github.com/smuyyh/IncrementallyUpdate/blob/master/screenshot/3.png?raw=true" width="280"/>
<img src="https://github.com/smuyyh/IncrementallyUpdate/blob/master/screenshot/2.png?raw=true" width="280"/>