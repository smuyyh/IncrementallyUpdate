package com.yyh.lib.bsdiff;

/**
 * APK Diff工具类
 * 
 * @author yuyuhang
 * @date 2016-1-26 下午1:10:18
 */
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