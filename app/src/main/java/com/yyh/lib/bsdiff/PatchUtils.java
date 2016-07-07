package com.yyh.lib.bsdiff;

/**
 * APK Patch工具类
 * 
 * @author yuyuhang
 * @date 2016-1-26 下午1:10:40
 */
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