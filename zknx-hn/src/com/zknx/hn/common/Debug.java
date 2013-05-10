package com.zknx.hn.common;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

import com.zknx.hn.App;

public class Debug {
	
	/**
	 * ���������Ϣ
	 * @param log
	 */
	public static void Log(String log) {
		if (App.mDebug)
			android.util.Log.d("ũ��", log);
	}
	
	/**
	 * ��������ѹ���������ļ���sd��
	 * @param context

	public static void ExtractTestDataFile(Context context) {
		String stampFileName = DataMan.DataFile("test.zip");
		File file = new File(stampFileName);
		
		if (!file.exists()) {
			SaveAssetToSdcard(context, "test.zip", DataMan.DataFile(""));
			if (!Ziper.UnZip(stampFileName, DataMan.DataFile(""))) {
				file.delete();
				Log("��ѹ�����ļ�ʧ�ܣ�" + stampFileName);
			}
		}
	}
	*/
	
	/**
	 * ����asset�ļ���sd��
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
			Log("�����ļ�����" + e.getMessage());
		}
		
		return false;
	}
}
