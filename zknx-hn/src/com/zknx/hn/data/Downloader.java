package com.zknx.hn.data;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import android.os.StrictMode;

import com.zknx.hn.App;
import com.zknx.hn.common.Debug;

public class Downloader {
	
	static void FixNetworkException() {
		/* TODO FixNetworkException
		String strVer = "4.0"; // GetVersion.GetSystemVersion();
		strVer = strVer.substring(0, 3).trim();
		float fv = Float.valueOf(strVer);
		if (fv > 2.3) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
			.detectDiskReads()
			.detectDiskWrites()
			.detectNetwork() // 这里可以替换为detectAll() 就包括了磁盘读写和网络I/O
			.penaltyLog() //打印logcat，当然也可以定位到dropbox，通过文件保存相应的log
			.build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
			.detectLeakedSqlLiteObjects() //探测SQLite数据库操作
			.penaltyLog() //打印logcat
			.penaltyDeath()
			.build()); 
		}
		*/
	}

	static HttpURLConnection GetConnection(String strUrl) throws IOException
	{
		int timeout = 1000 * App.GetTimeout();
		URL url = new URL(strUrl);
		
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        
		conn.setConnectTimeout(timeout);
		conn.setReadTimeout(timeout);
        
        return conn;
	}
	
	/**   
     * @param _interface
     * 接口地址（server url后的地址）   
     * @param path
     * 文件存放路径
     * @param fileName
     * 文件名
     * @return    
     *      -1: 下载失败
     *       0: 下载成功
     *       1: 文件已经存在
     */   
    public static int DownFile(String _interface, String path, String fileName)
    {    	
        try {
        	String tmpFileName = fileName + ".tmp";
        	
        	FixNetworkException();
        	
        	String urlStr = DataMan.URL_SERVER + _interface;
        	
            HttpURLConnection urlConn = GetConnection(urlStr);
            
            Debug.Log("URL = " + urlStr + ", FileName = " + path + fileName + ", Timeout = " + urlConn.getConnectTimeout());
            
            InputStream inputStream = urlConn.getInputStream();
            
            FileUtils.DeleteFile(path + tmpFileName);
            
            if (FileUtils.Write2SDFromInput(path, tmpFileName, inputStream) != null)
            {   
                return -1;   
            }
            
            File tmpFile = new File(path + tmpFileName);
            
            if (tmpFile.exists())
            {
            	if (tmpFile.length() > 0)
            		FileUtils.RenameFile(path + tmpFileName, path + fileName);
            	else
            		FileUtils.DeleteFile(path + tmpFileName);
            	
            	Debug.Log("DownFile, OK, url = " + urlStr);
            }

            inputStream.close();
            
            return 0;
            
        } catch (MalformedURLException e) {   
        	Debug.Log("下载失败,DownFile,MalformedURLException： " + e.getMessage());
        } catch (FileNotFoundException e) {
        	Debug.Log("下载失败,DownFile,文件没找到 ： " + e.getMessage());
        } catch (IOException e) {   
        	Debug.Log("下载失败,DownFile,IOException： " + e.getMessage());
        } catch (Exception e) {
        	e.printStackTrace();
        	Debug.Log("下载失败,DownFile,Exception： " + e.getMessage());
        }

        return -1;   
    }
    
    /** 下载url到临时文件中
     * @param urlStr
     * 		要下载的URL地址
     * @return    
     *      -1: 下载失败
     *       0: 下载成功
     */   
    public static int DownFile(String urlStr)
    {
        try {   
        	FileUtils.DeleteFile(DataMan.DataFile(DataMan.FILE_NAME_TMP));
        	
        	HttpURLConnection urlConn = GetConnection(urlStr);
            
            InputStream inputStream = urlConn.getInputStream();
            
            if (FileUtils.Write2SDFromInput(DataMan.DataFile(""), DataMan.FILE_NAME_TMP, inputStream) != null)
            {   
                return -1;   
            }
            
            inputStream.close();
            
            return 0;
            
        } catch (MalformedURLException e) {   
            e.printStackTrace();   
        } catch (IOException e) {   
            e.printStackTrace();   
        } catch (Exception e) {   
            e.printStackTrace();   
            return -1;   
        }

        return 0;   
    }

    /**
     * 通过参数post
     * 返回http post返回的字符串
     */
	public static String PostUrl(String strUrl, String strParams)
	{
		StringBuilder sb = new StringBuilder();
		
		try {
			
			byte[] data = strParams.getBytes();
			//byte[] data = URLEncoder.encode(strParams, "gb2312").getBytes();
			// 输出完整URL
			Debug.Log(strUrl + "?" + strParams);

			HttpURLConnection conn = GetConnection(strUrl);

			conn.setRequestMethod("POST");
			conn.setDoOutput(true);//如果通过post提交数据，必须设置允许对外输出数据
			conn.setRequestProperty("user-agent"," kSOAP/2.0");    
			//conn.setRequestProperty("soapaction"," http://tempuri.org/IAvatarApplicationService/" + methename);    
			conn.setRequestProperty("content-type"," application/x-www-form-urlencoded");    
			conn.setRequestProperty("connection"," close");    
			conn.setRequestProperty("Accept"," *, */*");  
			conn.setRequestProperty("Host"," 218.106.254.101:8045");
			conn.setRequestProperty("Content-Length", String.valueOf(data.length));

			OutputStream outStream = conn.getOutputStream();

			outStream.write(data);
			outStream.flush();
			outStream.close();
	
			int responseCode = conn.getResponseCode();
			if (responseCode == 200){
				
				BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));  
	
				String text;
				while((text = r.readLine()) != null)
				{
					if (sb.length() == 0)
						sb.append(text);
					else
						sb.append("\n" + text);
				}

				r.close();
			}
			else
			{
				Debug.Log("ResponseCode " + responseCode);
			}
		} catch (IOException e) {
			e.printStackTrace();
			sb.append("IO错误");
		} catch (Exception e) {
			e.printStackTrace();
			sb.append("错误");
		}

		Debug.Log("Return:" + sb.toString());

		return sb.toString();
	}
	
	
	/**
	 * 上传文件
	 * 
	 * @param file
	 * @return
	 */
	public static String HttpPost(String strUrl, List<NameValuePair> params, String filePathName)
	{
		String ret = null;
		
		//String uriAPI = "http://192.168.1.100:8080/test/test.jsp"; //这是我测试的本地,大家可以随意改
        /*建立HTTPost对象*/
        org.apache.http.client.methods.HttpPost httpRequest = new org.apache.http.client.methods.HttpPost(strUrl); 

        try 
        { 
          /* 添加请求参数到请求对象*/
          httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8)); 
          /*发送请求并等待响应*/
          HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest); 
          /*若状态码为200 ok*/
          if(httpResponse.getStatusLine().getStatusCode() == 200)  
          { 
            /*读返回数据*/
            //String strResult = EntityUtils.toString(httpResponse.getEntity()); 
            //mTextView1.setText(strResult); 
          } 
          else 
          { 
            //mTextView1.setText("Error Response: "+httpResponse.getStatusLine().toString()); 
          } 
        } 
        catch (ClientProtocolException e) 
        {  
          e.printStackTrace();
          ret = e.toString();
        } 
        catch (IOException e) 
        {  
          e.printStackTrace();
          ret = e.toString();
        } 
        catch (Exception e) 
        {
          e.printStackTrace();
          ret = e.toString();
        }
        
        return ret;
	}
	
	/**
	 * 上传文件
	 * 
	 * @param file
	 * @return
	 */
	public static String PostFile(String strUrl, List<NameValuePair> params, String filePathName)
	{
		File file = new File(filePathName);
		
		if (!file.exists())
			return ("文件不存在：" + filePathName);
		
		if (params == null)
			return "参数为空";
		
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "******UPLOAD|||" + System.currentTimeMillis();
		String srcPath = file.getAbsolutePath();
		
		try {
			URL url = new URL(strUrl);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			// 设置每次传输的流大小，可以有效防止手机因为内存不足崩溃
			// 此方法用于在预先不知道内容长度时启用没有进行内部缓冲的 HTTP 请求正文的流。
			httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K
			// 允许输入输出流
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			// 使用POST方法
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			httpURLConnection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			
			httpURLConnection.setConnectTimeout(1000 * App.GetTimeout());

			DataOutputStream dos = new DataOutputStream(
					httpURLConnection.getOutputStream());
			
			for (NameValuePair nameValuePair : params)
			{
				// 遍历参数
				dos.writeBytes(twoHyphens + boundary + end);
				dos.writeBytes("Content-Disposition: form-data; name=\"" + nameValuePair.getName() + "\""
						+ end);
				dos.writeBytes(end);
				dos.writeBytes(nameValuePair.getValue() + end);
			}

			Debug.Log("Data=" + srcPath.substring(srcPath.lastIndexOf("/") + 1));
			
			// 文件
			dos.writeBytes(twoHyphens + boundary + end);
			dos.writeBytes("Content-Disposition: form-data; name=\"Data\"; filename=\""
					+ srcPath.substring(srcPath.lastIndexOf("/") + 1)
					+ "\""
					+ end);
			dos.writeBytes(end);

			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[8192]; // 8k
			int count = 0;
			while ((count = fis.read(buffer)) != -1) {
				dos.write(buffer, 0, count);
			}
			fis.close();

			dos.writeBytes(end);
			
			// 结束
			dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
			dos.flush();

			InputStream is = httpURLConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			StringBuilder result = new StringBuilder();
			String line = null;
			while (null != (line = br.readLine())) {
				if (result.length() > 0) {
					result.append('\n');
				}
				result.append(line);
			}
			dos.close();
			is.close();
			Debug.Log("SERVICE： " + result.toString());

		} catch (Exception e) {
			e.printStackTrace();
			Debug.Log("ERROR：" + e.getMessage());
			return e.getMessage();
		}
		
		return null;
	}
}
