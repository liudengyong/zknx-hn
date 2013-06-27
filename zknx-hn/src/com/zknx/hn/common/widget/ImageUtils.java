package com.zknx.hn.common.widget;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
	
	/**
	* ���ر���ͼƬ
	* http://bbs.3gstdy.com
	* @param url
	* @return
	*/
	public static Bitmap GetLoacalBitmap(String fileName) {
	     try {
	          FileInputStream fis = new FileInputStream(fileName);
	          return BitmapFactory.decodeStream(fis);
	     } catch (FileNotFoundException e) {
	          e.printStackTrace();
	          Debug.Log("���ر���ͼƬʧ�ܣ�û�ҵ��ļ���" + e.getMessage());
	     } catch (Exception e) {
	    	 Debug.Log("���ر���ͼƬʧ�ܣ�" + e.getMessage());
	     }
	     
	     return null;
	}
}
