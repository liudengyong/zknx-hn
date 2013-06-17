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
			.detectNetwork() // ��������滻ΪdetectAll() �Ͱ����˴��̶�д������I/O
			.penaltyLog() //��ӡlogcat����ȻҲ���Զ�λ��dropbox��ͨ���ļ�������Ӧ��log
			.build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
			.detectLeakedSqlLiteObjects() //̽��SQLite���ݿ����
			.penaltyLog() //��ӡlogcat
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
     * �ӿڵ�ַ��server url��ĵ�ַ��   
     * @param path
     * �ļ����·��
     * @param fileName
     * �ļ���
     * @return    
     *      -1: ����ʧ��
     *       0: ���سɹ�
     *       1: �ļ��Ѿ�����
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
        	Debug.Log("����ʧ��,DownFile,MalformedURLException�� " + e.getMessage());
        } catch (FileNotFoundException e) {
        	Debug.Log("����ʧ��,DownFile,�ļ�û�ҵ� �� " + e.getMessage());
        } catch (IOException e) {   
        	Debug.Log("����ʧ��,DownFile,IOException�� " + e.getMessage());
        } catch (Exception e) {
        	e.printStackTrace();
        	Debug.Log("����ʧ��,DownFile,Exception�� " + e.getMessage());
        }

        return -1;   
    }
    
    /** ����url����ʱ�ļ���
     * @param urlStr
     * 		Ҫ���ص�URL��ַ
     * @return    
     *      -1: ����ʧ��
     *       0: ���سɹ�
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
     * ͨ������post
     * ����http post���ص��ַ���
     */
	public static String PostUrl(String strUrl, String strParams)
	{
		StringBuilder sb = new StringBuilder();
		
		try {
			
			byte[] data = strParams.getBytes();
			//byte[] data = URLEncoder.encode(strParams, "gb2312").getBytes();
			// �������URL
			Debug.Log(strUrl + "?" + strParams);

			HttpURLConnection conn = GetConnection(strUrl);

			conn.setRequestMethod("POST");
			conn.setDoOutput(true);//���ͨ��post�ύ���ݣ�����������������������
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
			sb.append("IO����");
		} catch (Exception e) {
			e.printStackTrace();
			sb.append("����");
		}

		Debug.Log("Return:" + sb.toString());

		return sb.toString();
	}
	
	
	/**
	 * �ϴ��ļ�
	 * 
	 * @param file
	 * @return
	 */
	public static String HttpPost(String strUrl, List<NameValuePair> params, String filePathName)
	{
		String ret = null;
		
		//String uriAPI = "http://192.168.1.100:8080/test/test.jsp"; //�����Ҳ��Եı���,��ҿ��������
        /*����HTTPost����*/
        org.apache.http.client.methods.HttpPost httpRequest = new org.apache.http.client.methods.HttpPost(strUrl); 

        try 
        { 
          /* �������������������*/
          httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8)); 
          /*�������󲢵ȴ���Ӧ*/
          HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest); 
          /*��״̬��Ϊ200 ok*/
          if(httpResponse.getStatusLine().getStatusCode() == 200)  
          { 
            /*����������*/
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
	 * �ϴ��ļ�
	 * 
	 * @param file
	 * @return
	 */
	public static String PostFile(String strUrl, List<NameValuePair> params, String filePathName)
	{
		File file = new File(filePathName);
		
		if (!file.exists())
			return ("�ļ������ڣ�" + filePathName);
		
		if (params == null)
			return "����Ϊ��";
		
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "******UPLOAD|||" + System.currentTimeMillis();
		String srcPath = file.getAbsolutePath();
		
		try {
			URL url = new URL(strUrl);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			// ����ÿ�δ��������С��������Ч��ֹ�ֻ���Ϊ�ڴ治�����
			// �˷���������Ԥ�Ȳ�֪�����ݳ���ʱ����û�н����ڲ������ HTTP �������ĵ�����
			httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K
			// �������������
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			// ʹ��POST����
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
				// ��������
				dos.writeBytes(twoHyphens + boundary + end);
				dos.writeBytes("Content-Disposition: form-data; name=\"" + nameValuePair.getName() + "\""
						+ end);
				dos.writeBytes(end);
				dos.writeBytes(nameValuePair.getValue() + end);
			}

			Debug.Log("Data=" + srcPath.substring(srcPath.lastIndexOf("/") + 1));
			
			// �ļ�
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
			
			// ����
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
			Debug.Log("SERVICE�� " + result.toString());

		} catch (Exception e) {
			e.printStackTrace();
			Debug.Log("ERROR��" + e.getMessage());
			return e.getMessage();
		}
		
		return null;
	}
}
