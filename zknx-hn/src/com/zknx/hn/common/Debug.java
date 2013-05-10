package com.zknx.hn.common;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

import com.zknx.hn.App;

public class Debug {
	
	/**
	 * 输出调试信息
	 * @param log
	 */
	public static void Log(String log) {
		if (App.mDebug)
			android.util.Log.d("农信", log);
	}
	
	/**
	 * 拷贝并解压测试数据文件到sd卡
	 * @param context

	public static void ExtractTestDataFile(Context context) {
		String stampFileName = DataMan.DataFile("test.zip");
		File file = new File(stampFileName);
		
		if (!file.exists()) {
			SaveAssetToSdcard(context, "test.zip", DataMan.DataFile(""));
			if (!Ziper.UnZip(stampFileName, DataMan.DataFile(""))) {
				file.delete();
				Log("解压测试文件失败：" + stampFileName);
			}
		}
	}
	*/
	
	/**
	 * 保存asset文件到sd卡
	 * @param context
	 * @param assetName
	 * @param path
	 */
	static boolean SaveAssetToSdcard(Context context, String assetName, String path)
	{
		try {
			InputStream is = context.getAssets().open(assetName);
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(path + "//" + assetName));
			
			int len;
			byte[] buffer = new byte[1024];
			while ((len = is.read(buffer)) > 0)
			{
				dos.write(buffer, 0, len);
			}
			
			dos.close();
			is.close();
			
			return true;
		} catch (IOException e) {
			Log("保存文件错误：" + e.getMessage());
		}
		
		return false;
	}
}
