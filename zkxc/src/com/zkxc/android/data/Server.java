package com.zkxc.android.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.zkxc.android.common.Debug;

public class Server {

	public static String PostFile(String type, String time, String fileName, String filePath)
	{
	    File file = new File(filePath + fileName);
	    if (!file.exists())
	    {
	    	return "文件不存在：" + filePath + fileName;
	    }
	    
        /*
         * NameValuePair实现请求参数的封装
        */
        List <NameValuePair> params = new ArrayList <NameValuePair>(); 
        params.add(new BasicNameValuePair("filename", fileName)); 
        params.add(new BasicNameValuePair("type", type));
        params.add(new BasicNameValuePair("time", time));

	    Debug.Log(DataMan.URL_PUT_FILE + "?filename=" + fileName + "&type=" + type + "&time=" + time + ", file=" + filePath + fileName);

		return Downloader.PostFile(DataMan.URL_PUT_FILE, params, filePath + fileName);
	}

	public static int GetFile(String type, String time, String fileName, String localFilePath)
	{
		File file = new File(localFilePath + fileName);
	    if (file.exists())
	    {
	    	Debug.Log("文件已经存在：" + localFilePath + fileName);
	    	return 1;
	    }

		Debug.Log(DataMan.URL_GET_FILE + "?filename=" + fileName + "&type=" + type + "&time=" + time + ", local=" + localFilePath + "/" + fileName);

		return Downloader.DownFile(DataMan.URL_GET_FILE + "?filename=" + fileName + "&type=" + type + "&time=" + time, localFilePath, fileName);
	}
}
