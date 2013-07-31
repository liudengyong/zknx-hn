package com.zkxc.android.data;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.zkxc.android.act.AppZkxc;
import com.zkxc.android.common.CmnListAdapter;
import com.zkxc.android.common.Debug;
import com.zkxc.android.common.CmnListAdapter.ListItemName;
import com.zkxc.android.map.FileUtils;
import com.zkxc.android.zipper.Ziper;

import android.content.Context;
import android.os.Environment;
import android.os.StrictMode;

public class DataMan {
	
	public final static boolean DEBUG = true;

	static String ZKXC_SERVER = "http://218.106.254.101:8045/";
	static String ZKXC_SERVER_FILE = "http://218.106.254.101:8099/";
	
	static String URL_GET_COLLECTION_TAB_LIST = ZKXC_SERVER + "get_collect_table_list.aspx?userid=";
	static String URL_GET_COLLECTION_TAB      = ZKXC_SERVER + "get_collection_table.aspx?id=";
	
	static String URL_GET_PA_INFO             = ZKXC_SERVER + "get_pa_info_a.aspx"; // TODO AAA 实现按地址下载保护区信息
	static String URL_PA_INFO_FILE            = ZKXC_SERVER_FILE + "PA_info/";

	public static String URL_PUT_RECORD = ZKXC_SERVER + "put_record.aspx";
	public static String URL_GET_RECORD = ZKXC_SERVER + "get_record.aspx";
	
	public static String URL_PUT_FILE = ZKXC_SERVER + "put_file.aspx";
	public static String URL_GET_FILE = ZKXC_SERVER + "get_file.aspx";
	
	public static String URL_PUT_NOTE = ZKXC_SERVER + "set_note.aspx";
	public static String URL_GET_NOTE = ZKXC_SERVER + "get_note.aspx";

	public static String DATA_FOLDER_NAME = "/zkxc/";
	public static String DATA_FOLDER = "";
	
	public static String TMP_FILENAME = "tmp.tmp";
	
	public static String TABLE_LIST_FILE_NAME = "list.txt";
	
	public static String GetDataForlder()
	{
		return Environment.getExternalStorageDirectory().getPath() + DATA_FOLDER_NAME;
	}
	
	public static boolean InitDataFolder()
    {
		/* 初始化3.2系统的安全策略 */
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		.detectDiskReads()
		.detectDiskWrites()
		.detectNetwork()
		.penaltyLog()
		.build());

		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		.detectLeakedSqlLiteObjects()
		.penaltyLog()
		.penaltyDeath()
		.build());

    	String status = Environment.getExternalStorageState();

		if (status.equals(Environment.MEDIA_MOUNTED)) 
		{
			String dataFolder = GetDataForlder();

			File forder = new File(dataFolder);

			if (forder.exists() || forder.mkdirs()) 
			{
				DataMan.DATA_FOLDER = dataFolder;
	    		
				return true;
			}
		}
		
		return false;
	}
	
	public static void Debug(Context context)
    {
    	try {
    		SaveAssetToSdcard(context, "protection_zone_tip.htm");
    		SaveAssetToSdcard(context, "protection_zone_err.htm");
    		//SaveAssetToSdcard(context, "123.zkxc");
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	static void SaveAssetToSdcard(Context context, String assetName) throws IOException
	{
		InputStream is = context.getAssets().open(assetName);
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(DATA_FOLDER + assetName));
		
		int len;
		byte[] buffer = new byte[1024];
		while ((len = is.read(buffer)) > 0)
		{
			dos.write(buffer, 0, len);
		}
		
		dos.close();
		is.close();
	}
	
	static final String DATE_FORMAT = "yyyy_MMdd_HHmmss";
	static final String DATE_FORMAT_HUMAN = "yyyy年MM月dd日HH:mm";
	
	// 2012-9-22 22:27:21
	// static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static String GetCurDate()
	{
		String datetime = new SimpleDateFormat(DATE_FORMAT).format(new Date(System.currentTimeMillis()));
		return datetime;
	}
	
	public static String GetCurHumanDate()
	{
		String datetime = new SimpleDateFormat(DATE_FORMAT_HUMAN).format(new Date(System.currentTimeMillis()));
		return datetime;
	}
	
	public static String GetFormatedDate(Date date)
	{
		return new SimpleDateFormat(DATE_FORMAT).format(date);
	}
	
	public static long GetLongDate(String date)
	{
		try {
			return new SimpleDateFormat(DATE_FORMAT).parse(date).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return 0;
	}

	public static String GetTabFilePathByUserId() {
		return (DATA_FOLDER + "table_list/" + AppZkxc.mUserInfo.userId + "/");
	}
	
	public static String GetTabFileName(String tableId) {
		return (tableId + ".xml");
	}
	
	public static String GetNoteFileName(String tableId) {
		return (tableId + ".note");
	}

	public static CmnListAdapter GetProtectionZone(Context context, String province)
	{
		if (0 <= Downloader.DownFile(URL_GET_PA_INFO, DATA_FOLDER, "protection_info.txt"))
		{
			CmnListAdapter adapter = new CmnListAdapter(context);
	    	
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						new FileInputStream(DATA_FOLDER + "protection_info.txt")));
			
				String line;
			
				while((line = br.readLine()) != null)
				{ 
					String token[] = line.split(",");
					
					if (token == null || token.length < 8) continue;
					//list.add(new NormalData(token[0], token[1]));
					
					adapter.addItem(new ListItemName(token[4], token[1], null));
				}
				
				br.close();
			
			} catch (IOException exp) {
				exp.printStackTrace();
			}
	    	//19131,PA_B_19131-14,2004-2-1 0:00:00,2012-6-8 10:01:45,������Ϻ,�м�,333,Ұ����,http://218.106.254.101:8099/PA_info/PA_B_19131-14/1.zip

	    	adapter.sortByName(true);

	    	return adapter;
		}
		
		return null;
	}
	
	public static CmnListAdapter GetTables(Context context, String addressId)
	{
		String tableId;
		String tableName;
		String[] token;
		CmnListAdapter adapter = new CmnListAdapter(context);

		String[] lines = ReadLines(GetTableListFilePath() + TABLE_LIST_FILE_NAME);
		
		if (lines != null) {
			for (String line : lines) {
				
				token = line.split(",");
				
				//21,PA_B_19131-15,1,采集表名字,2012-7-18 21:48:23,xxxxx
				if (token == null || token.length < 6)
					continue;
				
				tableId   = token[0];
				tableName = token[3];
				
				adapter.addItem(new ListItemName(tableName, tableId, null));
			}
		}

    	adapter.sortByName(true);

    	return adapter;
	}
	
	public static String GetProtectionZoneFilePath(String protectionId)
	{
		// �������ļ���
		String protectionPath  = DATA_FOLDER + protectionId + "/";
		
		// htm ���ڵĻ�ֱ�ӷ���
		String filePath = protectionPath + "1.htm";
		Debug.Log("GetProtectionZoneFilePath:filePath=" + filePath);
		
        if (FileUtils.IsFileExist(filePath)) return filePath;
        
        // zip ���ڵĻ�ֱ�ӽ�ѹ���ɹ��󷵻�
        String zipFilePath = protectionPath + "1.zip";
        if (FileUtils.IsFileExist(zipFilePath) && Ziper.UnZip(zipFilePath, protectionPath))
        {
        	Debug.Log("GetProtectionZoneFilePath_OK:filePath=" + filePath);
        	return filePath;
        }
        
        // zip�����ڻ��߽�ѹ���ɹ�������
		if (0 <= Downloader.DownFile(URL_PA_INFO_FILE + protectionId + "/1.zip", protectionPath, "1.zip")
			&& Ziper.UnZip(zipFilePath, protectionPath))
		{
			Debug.Log("GetProtectionZoneFilePath_DownFile_OK:filePath=" + filePath);
			return filePath;
		}

		return DATA_FOLDER + "protection_zone_err.htm";
	}

	public static String GetDataFolder() {
		return DATA_FOLDER;
	}
	
	// 采集表列表的路径
	public static String GetTableListFilePath()
	{
		if (AppZkxc.mUserInfo == null)
			return null;
		
		return (GetDataForlder() + "table_list/" + AppZkxc.mUserInfo.userId + "/");
	}
	
	// 解析路径
	public static String[] ParsePath(String filePathName)
	{
		// TODO AAA 路径反斜线
		String FILEPATH_TOKEN = "/";
		
		// /m/a.zip
		// pos = 2
		// len = 8
		int pos = filePathName.lastIndexOf(FILEPATH_TOKEN);
		
		if (pos <= 0 || pos >= filePathName.length())
		{
			Debug.Log("Server.PostFile Error:" + filePathName);
			return null;
		}
		
		String ret[] = new String[2];
		
		// path
		ret[0] = filePathName.substring(0, pos);
		// name
		ret[1] = filePathName.substring(pos + 1);
		
		return ret;
	}
	
	// 采集表列表的url
	static String GetTableListUrl()
	{
		return (URL_GET_COLLECTION_TAB_LIST + AppZkxc.mUserInfo.userId);
	}

	public static String[] ReadLines(String fileName) {
		StringBuilder sb = new StringBuilder();
		
		try {
			String line;
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName)));
			
			while((line = br.readLine()) != null) {
				if (sb.length() == 0)
					sb.append(line);
				else
					sb.append("\n" + line);
			}
			
			br.close();
			
			return sb.toString().split("\n");
			
		} catch (FileNotFoundException e) {
			Debug.Log("文件没找到 ： " + fileName);
		} catch (IOException e) {
			Debug.Log("文件读错误 ： " + fileName);
		}
		
		return null;
	}
	
	public static boolean Write(String fileName, String line) {
		
		try {
			File file = new File(fileName);
			
			if (file.exists())
				file.delete();
			
			FileWriter fw = new FileWriter(file);
			fw.write(line);
			fw.flush();
			fw.close();
			
			return true;
			
		} catch (FileNotFoundException e) {
			Debug.Log("文件没找到 ： " + fileName);
		} catch (IOException e) {
			Debug.Log("文件读错误 ： " + fileName);
		}
		
		return false;
	}
	
	public static int DownloadTableList() {
		String url  = GetTableListUrl();
		String path = GetTableListFilePath();
		String fileName = TABLE_LIST_FILE_NAME;

		// 下载采集表列表
		return Downloader.DownFile(url, path, fileName);
	}

	public static int DownloadTableById(String tableId) {
		String filePath = GetTabFilePathByUserId();
		String fileName = GetTabFileName(tableId);
		
		return Downloader.DownFile(URL_GET_COLLECTION_TAB + tableId, filePath, fileName);
	}
}
