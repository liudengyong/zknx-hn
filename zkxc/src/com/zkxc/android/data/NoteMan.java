package com.zkxc.android.data;

import java.io.File;

import com.zkxc.android.common.Debug;
import com.zkxc.android.map.FileUtils;

public class NoteMan {
	
	static String NOTE_FORDER_NAME = "notes";
	static String NOTE_FILE_NAME = "note.txt";
	
	static String GetNoteFolder(String tabId)
	{
		String folder = DataMan.GetDataFolder() + "/" + NOTE_FORDER_NAME + "/";
		
		if (makeDir(folder))
		{
			folder += tabId + "/";
			
			if (makeDir(folder))
				return folder;
		}

		Debug.Log("错误！创建文件夹失败：" + folder);
		return null;
	}
	
	public static String GetTableNote(String tableId)
	{
		String filePath = GetNoteFolder(tableId);
		String fileName = NOTE_FILE_NAME;
		
		String[] lines = DataMan.ReadLines(filePath + fileName);
		
		if (lines == null || lines.length == 0)
			return null;
		
		StringBuilder sb = new StringBuilder();
		
		for (String line : lines)
		{
			if (sb.length() == 0)
				sb.append(line);
			else
				sb.append("\n" + line);
		}
		
		return sb.toString();
	}
	
	public static boolean SaveTableNote(String tableId, String note)
	{
		String filePath = GetNoteFolder(tableId);
		String fileName = NOTE_FILE_NAME;
		
        String err = FileUtils.WriteText(filePath, fileName, note);
        
        if (err != null)
        {
        	//MessageBox.Show(ActTable.this, err);
        	return false;
        }
        
        return true;
	}
	
	/*
	
	public static String GetNoteLocal(String tableId) {
	    String path = GetNoteFolder(tableId);
	    
	    if (path == null)
	    {
	    	return null;
	    }
	    
		String ret[] = DataMan.ReadLines(path + "/" + NOTE_FILE_NAME);
		
		if (ret == null)
			return null;
		
		return ret[0];
	}
	
	public static boolean SaveNoteLocal(String tableId, String note) {
	    String path = GetNoteFolder(tableId);
	    
	    if (path == null)
	    {
	    	return false;
	    }
	    
		return DataMan.Write(path + "/" + NOTE_FILE_NAME, note);
	}
	*/
	
	public static boolean GetNoteFromServer(String tableId) {

		String filePath = GetNoteFolder(tableId);
		String fileName = NOTE_FILE_NAME;
	    
		Debug.Log(DataMan.URL_GET_NOTE + "?tableid=" + tableId);
		
		boolean ret = (Downloader.DownFile(DataMan.URL_GET_NOTE + "?tableid=" + tableId, filePath, fileName) == 0);
		
		Debug.Log("" + ret);
		
		return ret;
	}
	
	public static boolean PutNoteToServer(String tableId) {
	    
	    String note = GetTableNote(tableId);
	    
	    if (note == null || note.length() == 0)
	    	return false;
	    
	    Debug.Log(DataMan.URL_PUT_NOTE + "?tableid=" + tableId + "&note=" + note);
	    
		boolean ret = (Downloader.DownFile(DataMan.URL_PUT_NOTE + "?tableid=" + tableId + "&note=" + note, DataMan.GetDataFolder() + "/", "tmp.txt") == 0);
		
		Debug.Log("" + ret);
		
		return ret;
	}
	
	static boolean makeDir(String dir) {
	    File folderData = new File(dir);
        if (!folderData.exists() && !folderData.mkdirs())
        {
            //throw new Exception("Failed to create folder : " + dir);
            return false;
        }
        
        return true;
	}
}
