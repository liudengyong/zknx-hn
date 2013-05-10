package com.zknx.hn.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.zknx.hn.common.Debug;

public class FileUtils {

	public static void copyFile(File sourceFile, File targetFile)  throws IOException
	{    
        FileInputStream input = new FileInputStream(sourceFile);    
        BufferedInputStream inBuff=new BufferedInputStream(input);    
   
        FileOutputStream output = new FileOutputStream(targetFile);    
        BufferedOutputStream outBuff=new BufferedOutputStream(output);    
            
        byte[] b = new byte[8192];    
        int len;    
        while ((len =inBuff.read(b)) != -1) {    
            outBuff.write(b, 0, len);    
        }    
        outBuff.flush();    
            
        inBuff.close();    
        outBuff.close();    
        output.close();    
        input.close();    
	}
    
    private static int FILE_BUFF_SIZE = 4 * 1024;    

    /**   
     * 创建文件
     * @param fileName   
     * @return   
     * @throws IOException   
     */   
    public static File createSDFile(String path, String fileName) throws IOException
    {
    	createSDDir(path);
    	
        File file = new File(path + fileName);   
        file.createNewFile();
        return file;   
    }   
       
    /**   
     * 递归创建目录
     * @param dirName   
     * @return   
     */   
    public static File createSDDir(String dirName)
    {   
        File dir = new File(dirName);   
        dir.mkdirs();
        return dir;   
    }   
       
    /**   
     * 判断文件是否存在
     * @param fileName   
     * @return   
     */   
    public static boolean IsFileExist(String fileName)
    {   
        File file = new File(fileName);   
        return file.exists();   
    }
    
    public static boolean DeleteFile(String fileName)
    {   
        File file = new File(fileName);
        
        if (file.exists()) {
        	Debug.Log("删除文件:" + fileName);
        	return file.delete();
        }
        
        Debug.Log("删除文件, 没找到：" + fileName);
        
        return false;
    }
    
    public static boolean RenameFile(String fileName, String newFileName)
    {   
        File file    = new File(fileName);
        File newFile = new File(newFileName);
        
        if (file.exists()) {
        	return file.renameTo(newFile);
        }
        
        return false;
    }
    
    public static boolean MoveFile(String fileName, String newPath, String newFileName)
    {   
        File file    = new File(fileName);
        File newPathFile = new File(newPath);
        
        if (file.exists()) {
        	if (newPathFile.exists() || newPathFile.mkdirs()) {
        		File newFile = new File(newPath + newFileName);
        		return file.renameTo(newFile);
        	}
        }
        
        return false;
    }

	public static boolean MakeDirs(String fileDir) {
		File file = new File(fileDir);   
        return file.mkdirs();  
	}  
       
    /**   
     * ?????InputStream????????д??SD????   
     * @param path   
     * @param fileName   
     * @param input   
     * @return   
     */   
    public static String Write2SDFromInput(String path, String fileName, InputStream input)
    {
    	String err = null;
        File file = null;   
        OutputStream outputStream = null;
        
        try {
        	
        	int len = 0;
        	int totalLen = 0;
            byte[] buffer = new byte[FILE_BUFF_SIZE];
            
            file = createSDFile(path, fileName);
            
            outputStream = new FileOutputStream(file);
            
            while ((len = input.read(buffer)) != -1)
            {      
            	outputStream.write(buffer, 0, len);
            	totalLen += len;
            }
            
            Debug.Log("totalLen = " + (float)totalLen/1024 + " K");
  
            outputStream.flush();
        }    
        catch (Exception e) {   
            e.printStackTrace();
            err = "写文件错误";   
        }   
        finally{   
            try {
            	if (outputStream != null)
            		outputStream.close();   
            } catch (IOException e) {   
                e.printStackTrace();   
            }   
        }
        
        return err;
    }
    
    public static String WriteText(String path, String fileName, String text)
    {
    	String err = null;
        OutputStream outputStream = null;
        
        try {
        	
            File file = createSDFile(path, fileName);
            
            outputStream = new FileOutputStream(file);
            
            outputStream.write(text.getBytes());
  
            outputStream.flush();
        }    
        catch (Exception e) {   
            e.printStackTrace();
            err = "写文件错误";   
        }   
        finally{   
            try {
            	if (outputStream != null)
            		outputStream.close();   
            } catch (IOException e) {   
                e.printStackTrace();   
            }   
        }
        
        return err;
    }
    
    /**
     * 写文件
     * @param fileName
     * @param content
     */
    public static boolean WriteFile(String fileName, byte[] content) {
		try {
			FileOutputStream file = new FileOutputStream(fileName);
			file.write(content);
			file.close();
		} catch (IOException e) {
			Debug.Log("写文件错误：" + e.getMessage());
			return false;
		}

		return true;
	}
}
