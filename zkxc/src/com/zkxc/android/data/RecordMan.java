package com.zkxc.android.data;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import android.content.Context;

import com.zkxc.android.act.AppZkxc;
import com.zkxc.android.common.CmnListAdapter.ListItemName;
import com.zkxc.android.common.Debug;
import com.zkxc.android.map.ContentFile;
import com.zkxc.android.map.FileUtils;
import com.zkxc.android.map.Service;
import com.zkxc.android.map.Setting;
import com.zkxc.android.table.GridLayout;
import com.zkxc.android.table.controller.LocationView;
import com.zkxc.android.table.controller.MediaPicker;

public class RecordMan {

	private static final String RECORDS_FILE = "records.txt";

	public static final String FILE_NAME    = "fileName";
	public static final String RECORD_TIME  = "record_time";
	public static final String PENDING      = "pending";
	public static final String RECORD_USER  = "record_user";

	static String GetTableRecordFolder(boolean pending)
	{
		String folder = DataMan.GetDataFolder() + AppZkxc.mUserInfo.userId + "/record/";
		
		if (pending)
			return folder += "pending/";
		else
			return folder;
	}
	
	static String GetLocationFilePath()
	{
		String path = GetTableRecordFolder(true) + "location/";
		
		File file = new File(path);
		if (!file.exists())
			file.mkdirs();
			
		return path;
	}
	
	static String GetSyncedRecordFilePath(String tableId)
	{
		return GetTableRecordFolder(false) + tableId + "/";
	}

	public Vector<Map<String, Object>> getRecords(String tabId)
	{
		Vector<Map<String, Object>> records = new Vector<Map<String, Object>>();
		
		// 查找已经同步的记录record目录下
		ReadSyncedRecord(records, GetSyncedRecordFilePath(tabId) + RECORDS_FILE);
		// 查找未同步的记录record/pending目录下
		FindPendingRecord(records, GetTableRecordFolder(true) + tabId + "/");

		return records;
	}
	
	static void ReadRecordLine(Map<String, Object> record, String[] lines)
	{		
		if (lines == null || lines.length == 0)
			return;
		
		for (String line : lines)
		{
			//Debug.Log("ReadRecordLine, line = " + line);
			String[] token = line.split("=");
			if (token != null && token.length >= 2)
			{
				record.put(token[0], token[1]); // filed,value
				
				// 提取子单元格数据（放心，这个只是初始化用，保存时另外保存，没有子单元格数据）
				String[] children = token[1].split(GridLayout.SEP_RECORD);
				
				if (children != null)
				{
					for (String child : children)
					{
						String[] childToken = child.split(GridLayout.SEP_CHILD_RECORD);
						// 通过冒汗查找是否有子单元格数据
						if (childToken != null && childToken.length == 2)
						{
							record.put(childToken[0], childToken[1]); // filed,value
							//Debug.Log("子单元格：" + child);
						}
					}
				}
			}
		}
	}
	
	static void ReadSyncedRecord(Vector<Map<String, Object>> records, String filePathName)
	{
		String[] recordLines = DataMan.ReadLines(filePathName);
		
		if (recordLines == null || recordLines.length == 0)
			return;
		
		for (String recordLine : recordLines)
		{
			//Debug.Log("ReadSyncedRecord, recordLine = " + recordLine);
			
			Map<String, Object> record = new HashMap<String, Object>();
		
			ReadRecordLine(record, recordLine.split(","));
		
			// 已经同步 的标志
			record.put(PENDING, "0");
			
			// 记录采集的用户
			//record.put(RECORD_USER, "TODO");

			records.add(record);
		}
	}

	public static int PostRecords(String tableIds) {
		String pendingFileDir = GetTableRecordFolder(true);
		File recordFolder = new File(pendingFileDir);
		
		if (tableIds == null)
		{
			Debug.Log("PostRecords tableIds is null");
			return 0;
		}
		
		String[] tableIdsArray = tableIds.split(",");
		if (tableIdsArray == null || tableIdsArray.length == 0)
		{
			Debug.Log("PostRecords tableIds is null");
			return 0;
		}
		
		//  检查是否有record文件夹
		Debug.Log("搜索文件夹：" + pendingFileDir);
		if (!recordFolder.exists())
			return 0;
			
		// 是否有记录，文件夹名为tableId
		File[] tableFolders = recordFolder.listFiles();
		if (tableFolders == null || tableFolders.length == 0) 
			return 0;
				
		// tableId文件夹下是否有记录
		//for (File tableFolder : tableFolders) {
		for (String tableIdInList : tableIdsArray)
		{
			// 从列表中查询采集表id，然后查找相应的文件夹，而不是从已经保存的文件夹中查找（可能改采集表在服务器已经不存在）
			File tableFolder = new File(pendingFileDir + "/" + tableIdInList);
			
			if (!tableFolder.isDirectory())
				continue;
				
			String tableId = tableFolder.getName();
			Debug.Log("搜索文件夹：" + tableId);
			
			// 是否有记录，文件夹名为采集日期
			File[] recordFiles = tableFolder.listFiles();
			if (recordFiles == null || recordFiles.length == 0) 
				 continue;
				
			// tableId文件夹下是否有记录
			for (File recordFile : recordFiles) {
				if (!recordFile.isFile())
					continue;
				
				// getPath() 返回全路径
				String recorFileName = recordFile.getName();
				Debug.Log("查找到文件，上传：" + recorFileName);

				if (PostRecord(tableId, recordFile.getParent() + "/", recorFileName)) {
				}
				else
				{
					Debug.Log("上传失败：" + recorFileName);
					//return -1;
					// 一个记录上传失败后继续上传其他记录
					continue;
				}
			}
		}
			
		return 0;
	}

	private void FindPendingRecord(Vector<Map<String, Object>> records, String recordFileDir) {
		
		// 文件夹是否存在
		File recordFile = new File(recordFileDir);
		if (!recordFile.exists()) 
			return;

		// 是否有记录
		File[] files = recordFile.listFiles();
		if (files == null || files.length == 0) 
			return;

		for (File file : files) {
			Map<String, Object> record = new HashMap<String, Object>();

			// 是否已经同步
			record.put(PENDING, "1");
			// 删除修改用
			record.put(FILE_NAME, file.getName());
			// 记录日期
			record.put(RECORD_TIME, file.getName());

			ReadRecordLine(record, DataMan.ReadLines(file.getPath()));
			
			records.add(record);
		}
	}

	public static boolean SaveRecord(ListItemName table, Map<String, Object> collectionData, Vector<String> locationRecords, String fileName)
	{
		String recordFileDir = GetTableRecordFolder(true) + table.id + "/";
		
		if (FileUtils.IsFileExist(recordFileDir) || FileUtils.MakeDirs(recordFileDir))
		{
			try {
				// 新建采集表记录和修改采集表记录
				if (fileName == null)
					fileName = DataMan.GetCurDate();
				else
				{
					// 如果已经同步，删除同步目录下的记录文件
					String recordFileDirSynced = GetTableRecordFolder(false) + table.id + "/";
					if (FileUtils.IsFileExist(recordFileDirSynced + fileName))
					{
						FileUtils.DeleteFile(recordFileDirSynced + fileName);
					}
				}
				
				DataOutputStream dos = new DataOutputStream(new FileOutputStream(recordFileDir + fileName));
				
				// 保存采集数据
				for (String key : collectionData.keySet())
				{
					Object value = collectionData.get(key);
					if (value != null)
					{
						String buffer = key + "=" + value.toString() + "\n";
						dos.write(buffer.getBytes());
					}
				} 
				
				dos.close();
				
				//  保存位置信息
				String content =  "采集：" + table.name + ", " + DataMan.GetCurHumanDate() + "(" + AppZkxc.mUserInfo.userId + ")";
				SaveLocationRecord(locationRecords, content, fileName);
				
			} catch (IOException e) {
				Debug.Log("ERROR = " + e.getMessage());
				e.printStackTrace();
			}
			
			return true;
		}
		
		return false;
	}

	public static boolean PostOrDeleteRecord(boolean post, String tableId, Map<String, Object> record) {
		String fileName = record.get(FILE_NAME).toString();
		boolean pending = (record.get(PENDING).toString() == "1");

		String filePath = GetTableRecordFolder(pending) + tableId + "/";

		if (post)
			return PostRecord(tableId, filePath, fileName);
		else
			return FileUtils.DeleteFile(filePath + fileName);
	}

	static boolean PostRecord(String tableId, String filePath, String fileName)
	{
		String[] lines = DataMan.ReadLines(filePath + fileName);

		//Debug.Log("PostRecord, file = " + filePath + fileName + ", lines = " + ((lines == null) ? 0 : lines.length));

		if (lines == null || lines.length == 0)
			return false;

		StringBuilder sb = new StringBuilder();

		for (String line : lines) {
			Debug.Log("PostRecord, line = " + line);

			String[] token = line.split("=");
			if (InvalidRecordLine(token))
				continue; // ZZZ 错误处理

			String key = token[0];
			String value = token[1];

			if (sb.length() == 0)
				sb.append(key + "=" + value);
			else
			    sb.append("," + key + "=" + value);
		}

		// 上传本地媒体到服务器
		ProcessMediaInRecord(lines);
		
		// 上传位置信息
		File recordFolder = new File(GetLocationFilePath() + fileName);
		if (recordFolder.exists() && recordFolder.isDirectory())
		{
			File[] files = recordFolder.listFiles();
			if (files != null && files.length > 0)
			{
				for (File recordFile : files)
				{
					Service.UploadContentFile(recordFile);
				}
			}
		}

		// TODO AAA 同步时弹出等待进度条
		String ret = Downloader.PostUrl(DataMan.URL_PUT_RECORD, "tableid=" + tableId + "&" + 
							RECORD_TIME + "=" + fileName + "&" + 
							RECORD_USER + "=" + AppZkxc.mUserInfo.userId +
							"&filed=" + sb.toString());

		if (ret.equals("0")) {
			String recordFileDir = GetTableRecordFolder(false);
			RecordMan.GetRecordsFromServer(tableId);
			boolean retValue = FileUtils.MoveFile(filePath + fileName, recordFileDir + tableId + "/", fileName);
			Debug.Log("移动文件：" + filePath + fileName + ", 到：" + recordFileDir + tableId + "/" + fileName + ", " + retValue);
		}

		return (ret.equals("0"));
	}

	private static boolean InvalidRecordLine(String[] token) {
		return (token == null || token.length < 2);
	}

	public static boolean GetRecordsFromServer(String tableId) {
	    String path = GetSyncedRecordFilePath(tableId);
	    String fileName = RECORDS_FILE;
	    
		boolean ret = (Downloader.DownFile(DataMan.URL_GET_RECORD + "?tableid=" + tableId, path, fileName) == 0);
		
		// 下载媒体文件到本地
		ProcessMediaInSyncedRecord(path + "/" + fileName);
		
		return ret;
	}

	static void ProcessMediaInRecord(String[] recordLines)
	{
		if (recordLines == null || recordLines.length == 0)
			return;

		for (String recordLine : recordLines)
		{
			// filed_04x05=[PICTURE],2012_1204_150113,/mnt/sdcard/zkxc,protection_zone_tip.htm
			// *** TODO AAA 数据关联的才上传？ filed_标本号_03x00=filed_04x00:[PICTURE],time,name,path;filed_04x01:[PICTURE],time,name,path
			String[] token = recordLine.split("=");
			if (InvalidRecordLine(token))
				continue; // ZZZ 错误处理

			String value = token[1];
			//[PICTURE],2012_1204_150113,/mnt/sdcard/zkxc,protection_zone_tip.htm
			
			/*

			
			// filed_04x00:[PICTURE],time,name,path
			// filed_04x01:[PICTURE],time,name,path
			// ...
			for (String recordPair : recordPairs)
			*/
			{
				String recordPairs[] = value.split(";");
				
				if (recordPairs == null || recordPairs.length == 0)
				{
					continue;
				}
				
				for (String recordPair : recordPairs)
				{
				
					String recordValue[] = recordPair.split(":");
					
					// filed_04x00
	                // [PICTURE],time,name,path
					if (recordValue == null || recordValue.length < 2)
						continue;
					
				// [PICTURE],time,name,path
				String record[] = recordValue[1].split(MediaPicker.HALFEN);
				
				if (MediaPicker.IsContainsMedia(record))
				{
					//valueToken[0], 类型
					//valueToken[1], 文件路径名
					
					Debug.Log("media type : " + record[0]);
					
					String type = record[0];
					String time = record[1];
					
					// 文件名
					String mediaFileName = record[3];
					
					// 文件名为空
					if (mediaFileName != null && mediaFileName.length() > 0)
					{
						// 文件路径
						String mediaFilePath = record[2] + "/";
						
						/*
						Date date = new Date(file.lastModified());
						String time = DataMan.GetFormatedDate(date);
						*/
						
						Debug.Log("Server.PostFile:" + mediaFilePath + mediaFileName);
						String ret = Server.PostFile(type, time, mediaFileName, mediaFilePath);
						if (ret != null)
						{
							Debug.Log("Server.PostFile Error:" + ret);
							continue;
						}
						
						Debug.Log("Server.PostFile:O" + mediaFilePath + mediaFileName);
					}
				}
				}
			}
		}
	}
	
	static void ProcessMediaInSyncedRecord(String filePathName)
	{
		String[] recordLines = DataMan.ReadLines(filePathName);
		
		if (recordLines == null || recordLines.length == 0)
			return;

		for (String recordLine : recordLines)
		{
			// filed_04x05=[PICTURE],2012_1204_150113,/mnt/sdcard/zkxc,protection_zone_tip.htm
			// *** TODO AAA 数据关联的才上传？ filed_标本号_03x00=filed_04x00:[PICTURE],time,name,path;filed_04x01:[PICTURE],time,name,path
			String[] token = recordLine.split("=");
			if (InvalidRecordLine(token))
				continue; // ZZZ 错误处理

			for (String value : token)
			{
				String recordPairs[] = value.split(";");
				
				if (recordPairs == null || recordPairs.length == 0)
				{
					Debug.Log("recordPairs : continue");
					continue;
				}
				
				for (String recordPair : recordPairs)
				{
					String recordValue[] = recordPair.split(":");
					
					// filed_04x00
	                // [PICTURE],time,name,path
					if (recordValue == null || recordValue.length < 2)
						continue;
					
				// [PICTURE],time,name,path
				String record[] = recordValue[1].split(MediaPicker.HALFEN);
				
				if (MediaPicker.IsContainsMedia(record))
				{
					//valueToken[0], 类型
					//valueToken[1], 文件路径名
					
					Debug.Log("media type : " + record[0]);
					
					String type = record[0];
					String time = record[1];
					
					// 文件名
					String mediaFileName = record[3];
					
					// 文件名为空
					if (mediaFileName != null && mediaFileName.length() > 0)
					{
						// 媒体文件保存路径
						String mediaFilePath = MediaPicker.GetMediaDir();

						Debug.Log("Server.GetFile A:" + mediaFileName + mediaFilePath);
						int ret = Server.GetFile(type, time, mediaFileName, mediaFilePath);
						if (ret < 0)
						{
							Debug.Log("Server.GetFile Error:" + ret);
							continue;
						}
						
						Debug.Log("Server.GetFile Error:" + ret);
					}
				}
				}
			}
		}
	}

	public static void UploadLocationFile(Context context, File recordFile) {
		
		/*
		File[] recordFiles = new File(GetLocationRecordPath()).listFiles();
		
		if (recordFiles == null || recordFiles.length == 0) return;
		
		for (File recordFile : recordFiles)
		{*/
	    
	}
	
	static String GetLocationRecordPath()
	{
		return DataMan.GetDataForlder() + "/location";
	}
	
	public static void DeleteFolder(String filepath)
	{
		try {
			File f = new File(filepath);// 定义文件路径
			if (f.exists() && f.isDirectory()) {// 判断是文件还是目录
				if (f.listFiles().length == 0) {// 若目录下没有文件则直接删除
					f.delete();
				} else {// 若有则把文件放进数组，并判断是否有下级目录
					File delFile[] = f.listFiles();
					int i = f.listFiles().length;
					for (int j = 0; j < i; j++) {
						if (delFile[j].isDirectory()) {
							DeleteFolder(delFile[j].getAbsolutePath());// 递归调用DeleteFolder方法并取得子目录路径
						}
						delFile[j].delete();// 删除文件
					}
				}
			}
		}
		catch (Exception e)
		{
			Debug.Log("删除文件夹失败：" + e.getMessage());
		}
	}
	
	/* 保存轨迹记录 */
	public static void SaveLocationRecord(Vector<String> records, String content, String fileName) {
		
		// 从采集表记录文件名格式转换为map下记录文件的格式
		long time = DataMan.GetLongDate(fileName);
		//String locationFileName = Setting.FILE_DATE_FORMAT.format(time);
		String path = GetLocationFilePath() + fileName;
		
		// 保存的路径是文件夹
		DeleteFolder(path);
		
		if (records == null || records.size() == 0)
		{
			return;
		}
		
		File folder = new File(path);
		if (!folder.exists() && !folder.mkdirs())
		{
			Debug.Log("创建文件夹失败：" + path);
			return;
		}
		
		// 创建记录文件
		Date addTime = new Date(time);
		
		long now = System.currentTimeMillis();
		
		// 位置信息计数
		//int locationCnt = 1;
		// 保存记录文件
		for (String location : records)
		{
			String[] token = location.split(LocationView.LOCATION_VALUE_TOKEN);
			
			try {
				double lat = Double.parseDouble(token[0]);
				double lng = Double.parseDouble(token[1]);
				
				// 采集表GPS保存格式，数据库可以搜索字符串查找位置信息
				ContentFile.WriteCoolectionActionFile(new File(path + "/" + Setting.FILE_DATE_FORMAT.format(now) + ".txt"), lat, lng, addTime, content + "[" + lat + "," + lng + "]");
				//locationCnt++;
				now += 1000; // 加1秒
				// 以秒为最小单位有问题，如果一个采集记录有60个位置信息，则一分钟之内再保存位置信息会有覆盖
			}
			catch (Exception exp)
			{
				Debug.Log("位置解析错误：" + location);
			}
		}
	}
}
