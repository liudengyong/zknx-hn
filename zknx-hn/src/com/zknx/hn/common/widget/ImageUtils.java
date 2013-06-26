package com.zknx.hn.common.widget;

import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.zknx.hn.common.Debug;
import com.zknx.hn.data.FileUtils;

public class ImageUtils {
	/**
	 * ����ͼƬ
	 * @param
	 * data : BitmapͼƬ����
	 */
	public static boolean SaveBitmap(byte[] data, String fileName) {
		if (data != null) {
			try {
				if (FileUtils.IsFileExist(fileName))
					FileUtils.DeleteFile(fileName);

				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				FileOutputStream out = new FileOutputStream(fileName);
				bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
				
				return true;
			} catch (Exception e) {
				Debug.Log("���ش��󣺲���ѹ��ͼƬ��" + e.getMessage());
			} catch (Throwable e) {
				Debug.Log("���ش����ڴ治�㣬setAisImage");
			}
		}
		
		return false;
	}
}
