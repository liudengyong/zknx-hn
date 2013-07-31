package com.zkxc.android.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.zkxc.android.common.Debug;

import android.util.Log;

public class ZxDataMan {
	
	public final static String DATA_DIR  = DataMan.GetDataForlder() + "data/";
	public final static String INDEX_DIR = DataMan.GetDataForlder() + "index/";
	
	private static final String BASE_TABLE_VERSION = "version.dat";
	private static final String ADDRESS_LIST_FILE_NAME   = "address.list";
	
	private static final String ACOUNTS_FILE_NAME = "acounts.list";
	
	private static final String ZX_URL_DOMAIN = "http://218.106.254.101:8045/";
	private static final String ZX_XML_HEADER = "?data=<?xml%20version=\"1.0\"%20encoding=\"gb2312\"?>";
	
	static void getHttpData(String sz_filename, String sz_xmlcontent, String outputFileName, String folder) throws Exception {

		URL url = new URL(ZX_URL_DOMAIN + sz_filename + ZX_XML_HEADER + sz_xmlcontent);
		
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();

		conn.setRequestMethod("GET");
		conn.setConnectTimeout(10 * 1000);
		conn.setReadTimeout(10 * 1000);
		conn.setDoOutput(true); // ���ͨ��post�ύ��ݣ����������������������

		int retCode = conn.getResponseCode();
		
		if (retCode == 200) {

		    makeDir(DATA_DIR);
		    makeDir(INDEX_DIR);

			InputStream is = conn.getInputStream();
			FileOutputStream o = new FileOutputStream(new File(folder, outputFileName));
			
			int len;
			byte buf[] = new byte[1024];  
			  
			while ((len = is.read(buf)) != -1) {  
				o.write(buf, 0, len);  
			}

			o.close();
			is.close();
			
			return;
		}
		
		throw new Exception("HttpURLConnection response code : " + retCode);
	}
	
	static void getHttpVideoData(String sz_filename, String sz_xmlcontent, String outputFileName, String folder) throws Exception {

		URL url = new URL(sz_filename + ZX_XML_HEADER + sz_xmlcontent);
		
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();

		conn.setRequestMethod("GET");
		conn.setConnectTimeout(10 * 1000);
		conn.setReadTimeout(10 * 1000);
		conn.setDoOutput(true); // ���ͨ��post�ύ��ݣ����������������������

		int retCode = conn.getResponseCode();
		
		if (retCode == 200) {

		    makeDir(folder);

			InputStream is = conn.getInputStream();
			FileOutputStream o = new FileOutputStream(new File(folder, outputFileName));
			
			int len;
			byte buf[] = new byte[1024];  
			  
			while ((len = is.read(buf)) != -1) {  
				o.write(buf, 0, len);  
			}

			o.close();
			is.close();
			
			return;
		}
		
		throw new Exception("HttpURLConnection response code : " + retCode);
	}
	
	static float getVersion(String line)
	{
		float ret = 0;
		int start, end;
		start = line.indexOf("Ver=");
		end   = line.indexOf("\n", start + 1);
		if (start > 0 && end > start)
			ret = Float.parseFloat(line.substring(start + 4, end));
		return ret;
	}
	
	
    public static String save_base_table(String xml_content,String page_name,String save_filename) throws Exception {

		//String xml = "data=" + xml_content;
		String xml = xml_content;
		//String path = "http://www.163.com" + page_name;
		String path = page_name;
		byte[] data = xml.getBytes();
		URL url = new URL(path);
		
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();

		conn.setRequestMethod("POST");
		conn.setConnectTimeout(10 * 1000);
		conn.setReadTimeout(10 * 1000);
		conn.setDoOutput(true);//���ͨ��post�ύ��ݣ����������������������
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

		String returnString = "";
		if(conn.getResponseCode()==200){
			
			
			BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));  
			OutputStreamWriter osw = null;
			
			/*
		    File folderData  = new File(ZxDataMan.DATA_DIR);
		    if (!folderData.exists() && !folderData.mkdirs())
		        throw new Exception("Failed to create folder : " + ZxDataMan.DATA_DIR);

			BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream(), "gb2312"));  
			OutputStreamWriter osw = null;
			
			returnString = r.readLine();
			
			if (returnString.equals("Result=TRUE"))
				osw = new OutputStreamWriter(new FileOutputStream(ZxDataMan.DATA_DIR + save_filename), "gb2312"); 
			*/

			String text;

			while((text = r.readLine()) != null)
			{
				returnString += text;

				if (osw != null)
					osw.write(text + "\n");
			}
			//log.e("login", returnString+"");
			
			r.close(); 
		}
		return returnString;
	}

    //ȡ�û��汾��Ϣ
	public List<Base_Table_Version> get_Base_Table_Version(InputStream content_String) throws Exception, Exception{
		
		
		List<Base_Table_Version>  base_table_version_list = new ArrayList<Base_Table_Version>();
		Base_Table_Version base_table_version = null;
		
		for(int i=0;i<10;i++){
			
			base_table_version = new Base_Table_Version();  
			
			
			base_table_version.setBaseTableName("");
			base_table_version.setBaseTableRemark("");
			base_table_version.setVersion(1);
			
			
			base_table_version_list.add(base_table_version);
			
		}
		
		return base_table_version_list;
	}
	
	static List<Base_Table_Version> get_base_table_version(String sz_filename, String outputFileName) throws Exception {

		
		List<Base_Table_Version>  base_table_version_list = new ArrayList<Base_Table_Version>();

				String xml = "" ;
		
				//String path = "http://www.163.com" + page_name;
			
				byte[] data = xml.getBytes();
				URL url = new URL(ZX_URL_DOMAIN + sz_filename);
				
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();

				conn.setRequestMethod("POST");
				conn.setConnectTimeout(10 * 1000);
				conn.setReadTimeout(10 * 1000);
				conn.setDoOutput(true);//���ͨ��post�ύ��ݣ����������������������
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

				if(conn.getResponseCode()==200){
					
				    File folderData  = new File(ZxDataMan.DATA_DIR);
				    if (!folderData.exists() && !folderData.mkdirs())
				        throw new Exception("Failed to create folder : " + ZxDataMan.DATA_DIR);

					BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));  
					OutputStreamWriter osw = null;
					
					//returnString = r.readLine();
					Debug.Log("login, " + ZxDataMan.DATA_DIR + outputFileName);
					//if (returnString.equals("Result=TRUE"))
					osw = new OutputStreamWriter(new FileOutputStream(ZxDataMan.DATA_DIR + outputFileName), "utf-8"); 

					String text;
					
					Base_Table_Version base_table_version = new Base_Table_Version();

					while((text = r.readLine()) != null)
					{
						//base_table_version = new Base_Table_Version();  
						
						//returnString += text;

						Debug.Log("login, " + text);
						String array[] = text.split(",");
						Debug.Log("login, " + array[0]);
						Debug.Log("login, " + array[1]);
						Debug.Log("login, " + array[2]);
						base_table_version.setBaseTableName(array[0].toString());
						Debug.Log("login, " + array[0]);
						//base_table_version.setVersion(Integer.parseInt(array[1]));
						//log.w("login", array[1]);
						//base_table_version.setBaseTableRemark(array[2]);
						//log.w("login", array[2]);
						
						base_table_version_list.add(base_table_version);
						
						if (osw != null)
							osw.write(text + "\n");
					}
					
					
					//log.e("login", returnString+"");
					
					
					if (osw != null)
						osw.close();  
					r.close(); 

				}
		
		return base_table_version_list;
		
		//throw new Exception(url.toString() + "\nHttpURLConnection response code : " + retCode);
	}
    
	static boolean chkVerAndGetHttpData(String sz_filename, String outputFileName) throws Exception {

		URL url = new URL(ZX_URL_DOMAIN + sz_filename + ZX_XML_HEADER);
		
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();

		conn.setRequestMethod("GET");
		conn.setConnectTimeout(10 * 1000);
		conn.setReadTimeout(10 * 1000);
		conn.setDoOutput(true); // ���ͨ��post�ύ��ݣ����������������������

		int retCode = conn.getResponseCode();
		
		if (retCode == 200) {

		    makeDir(DATA_DIR);
		    makeDir(INDEX_DIR);

			int len;
			byte buf[] = new byte[1024];  

			InputStream is = conn.getInputStream();

			float verCur = 0, verServer = 0;
			
			try {
				FileInputStream i = new FileInputStream(new File(DATA_DIR, outputFileName));
	
				if ((len = i.read(buf)) == 1024)
					verCur = getVersion(new String(buf));
				
				i.close();
			} catch (Exception e) {
			}

			if ((len = is.read(buf)) == 1024)
				verServer = getVersion(new String(buf));

			if (verServer <= verCur) {
				is.close();
				return false;
			}

			FileOutputStream o = new FileOutputStream(new File(DATA_DIR, outputFileName));

			o.write(buf, 0, len);

			while ((len = is.read(buf)) != -1)
				o.write(buf, 0, len);

			o.close();
			is.close();
			
			return true;
		}
		
		throw new Exception(url.toString() + "\nHttpURLConnection response code : " + retCode);
	}
    
    
    
	static void postXmlData(String sz_filename, String sz_xmlcontent) throws Exception {

		String xmlData = ZX_XML_HEADER + sz_xmlcontent;
        URL url = new URL(ZX_URL_DOMAIN + sz_filename + xmlData);
        
		byte[] data = xmlData.getBytes();
		
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();

		conn.setRequestMethod("POST");
		conn.setConnectTimeout(10 * 1000);
		conn.setReadTimeout(10 * 1000);
		conn.setDoOutput(true);
		conn.setRequestProperty("user-agent"," kSOAP/2.0");    
		//conn.setRequestProperty("soapaction"," http://tempuri.org/IAvatarApplicationService/" + methename);    
		conn.setRequestProperty("content-type"," application/x-www-form-urlencoded");    
		conn.setRequestProperty("connection"," close");    
		conn.setRequestProperty("Accept"," *, */*");  
		conn.setRequestProperty("Host"," www.yczjxt.com.cn");
		conn.setRequestProperty("Content-Length", String.valueOf(data.length));

		OutputStream outStream = conn.getOutputStream();
		outStream.write(data);
		outStream.flush();
		outStream.close();

        int retCode = conn.getResponseCode();
        
        if (retCode == 200) {

            StringBuilder sb = new StringBuilder();
            InputStream is = conn.getInputStream();

            byte buf[] = new byte[1024];  

            while (is.read(buf) != -1)
                sb.append(buf);

            is.close();

            return;
        }
        
        throw new Exception("HttpURLConnection response code : " + retCode);
    }

	static class SD {
		String id;
		boolean supply;
		String topic;
		String addrId;
		String productId;
		
		String dockingId[];
		
		SD (boolean _supply, String _id, String _topic, String _addrId, String _productId) {
			supply = _supply;
			id = _id;
			topic = _topic;
			addrId = _addrId;
			productId = _productId;
		}
		
		public void setDockingId(String token[]) {
			dockingId = token;
		}

		public String[] getDockingId() {
			return dockingId;
		}
	}
	
	static void makeDir(String dir) throws Exception {
	    File folderData = new File(dir);
        if (!folderData.exists() && !folderData.mkdirs())
            throw new Exception("Failed to create folder : " + dir);
	}
	
	public static List<Base_Table_Version> Downlad_Base_Table_Ver() throws Exception {
		//Log.e("login", "Downlad_Base_Table_Ver");
		//chkVerAndGetHttpData("Get_base_table_version.aspx", MARKET_LIST_FILE_NAME);
		return get_base_table_version("Get_base_table_version.aspx", BASE_TABLE_VERSION);
	}

	public static boolean DownloadAddressList() throws Exception {
		//Log.e("login", "aaaaa");
		return chkVerAndGetHttpData("Get_Address_base.aspx", ADDRESS_LIST_FILE_NAME);
	}
	
    static Log log;
    
	public static class AcountList {
		static class U {
			String acount;
			String password;
			
			U(String _acount, String _password) {
				acount = _acount;
				password = _password;
			}
			
			@Override
			public String toString() {
				return acount;
			}
		}
		
		public List<U> data;
		
		AcountList(List<U> l) {
			data = l;
		}

		public String getAcount(int pos) {
			if (pos > 0)
				return data.get(pos).acount;
			else
				return data.get(0).acount;
		}
		
		public String getPassword(int pos) {
			if (pos > 0)
				return data.get(pos).password;
			else
				return data.get(0).password;
		}
		
		public boolean isPasswordSaved(int pos) {
			if (pos > 0)
				return data.get(pos).password != null;
			else
				return data.get(0).password != null;
		}

		public void remove(int pos) {
			data.remove(pos);
		}
	}
	
	public static AcountList LoadSavedAcountList() {
		List<AcountList.U> m = new ArrayList<AcountList.U>();
        
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(DATA_DIR + ACOUNTS_FILE_NAME)));
	
			String line;
			
			while((line = br.readLine()) != null){ 
				String token[] = line.split(",");

				String p = null;
				if (token.length > 1)
					p = token[1];
				
				m.add(new AcountList.U(token[0], p));
			}
			
			br.close();

		} catch (Exception exp) {
			exp.printStackTrace();
			return null;
		}
        
		if (m.size() > 0)
			return new AcountList(m);
		else
			return null;
	}
	
	public static String SaveAcountInfo(boolean save, String acount, String password) {
        try {
            makeDir(DATA_DIR);

            BufferedReader b = null;
            
            try {
				InputStream in = new FileInputStream(DATA_DIR + ACOUNTS_FILE_NAME);
		        b = new BufferedReader(new InputStreamReader(in));
            } catch (FileNotFoundException fe) {
            }
	               
	        PrintWriter pw = new PrintWriter(new FileOutputStream(DATA_DIR + ACOUNTS_FILE_NAME + ".new"));

        	if (save)
        		pw.println(acount + "," + password);
        	else
        		pw.println(acount);
        	
        	if (b != null) {
		        String s;
		        while ((s = b.readLine()) != null) {
		        	String t[] = s.split(",");
		        	
		        	if (t.length > 0 && t[0].equals(acount))
			        	continue;
		        	
		        	pw.println(s);
		         }
	
		         b.close();
        	 }
        	
	         pw.flush();
	         pw.close();
	         
	         File f = new File(DATA_DIR + ACOUNTS_FILE_NAME);
	         f.delete();
	         
	         new File(DATA_DIR + ACOUNTS_FILE_NAME + ".new").renameTo(f);

        } catch (Exception e) {
        	e.printStackTrace();
            return "�����ʺ�ʧ�ܣ�\n" + e.toString();
        }
        
		return null;
	}
	
	public static String RemoveAcountInfo(String acount) {
		try {
			InputStream in = new FileInputStream(DATA_DIR + ACOUNTS_FILE_NAME);
	        BufferedReader br = new BufferedReader(new InputStreamReader(in));
	               
	        OutputStream out = new FileOutputStream(DATA_DIR + ACOUNTS_FILE_NAME + ".new");
	        PrintWriter pw = new PrintWriter(out);

	        String s;
	        while ((s = br.readLine()) != null) {
	        	String t[] = s.split(",");
	        	
	        	if (t.length > 0 && t[0].equals(acount))
	        		continue;

	        	pw.println(s);
	         }

	         pw.flush();
	         
	         File f = new File(DATA_DIR + ACOUNTS_FILE_NAME);
	         f.delete();
	         
	         new File(DATA_DIR + ACOUNTS_FILE_NAME + ".new").renameTo(f);
		} catch (Exception e) {
			e.printStackTrace();
			return "ɾ��ʧ�ܣ�\n" + e.toString();
		}
		
		return null;
	}
}


