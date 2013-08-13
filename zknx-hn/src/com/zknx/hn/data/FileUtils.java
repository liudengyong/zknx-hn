package com.zknx.hn.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

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
     * �����ļ�
     * @param fileName   
     * @return   
     * @throws IOException   
     */   
    public static File createSDFile(String path, String fileName) throws IOException
    {
    	createSDDir(path);
    	
        File file = new File(path + fileName);

        String realPath = file.getParent();
        if (!FileUtils.IsFileExist(realPath)) {
        	Debug.Log("����Ŀ¼��" + realPath);
        	FileUtils.MakeDirs(realPath);
        }

        file.createNewFile();
        return file;   
    }
    
    /**   
     * �����ļ�
     * @param fileName   
     * @return   
     * @throws IOException   
     */   
    public static File CreateFile(String fileName) throws IOException
    {
    	int end = fileName.lastIndexOf('/');
    	String path = fileName.substring(0, end);
        File file = new File(fileName);

        // ����Ŀ¼
        File dir = new File(path);
        if (dir.exists() || dir.mkdirs())
        	file.createNewFile();

        return file;   
    } 
       
    /**   
     * �ݹ鴴��Ŀ¼
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
     * �ж��ļ��Ƿ����
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
        	Debug.Log("ɾ���ļ�:" + fileName);
        	return file.delete();
        }
        
        Debug.Log("ɾ���ļ�, û�ҵ���" + fileName);
        
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
     * ?????InputStream????????��??SD????   
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
            err = "д�ļ�����";   
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
     * Ĭ�ϱ��루UTF8��д�ļ�
     * @param path
     * @param fileName
     * @param text
     * @return
     */
    public static String WriteText(String path, String fileName, String text) {
    	return WriteText(path, fileName, text, null);
    }
    
    /**
     * д�ļ�
     * @param fileName
     * @param text
     * @return
     * @throws IOException
     */
    public static String WriteText(String fileName, String text)
    {
    	try {
	    	File file = CreateFile(fileName);
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
	        writer.write(text);
	        writer.flush();
	        
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			Debug.Log("д�ļ�����" + fileName);
		}

        return null;
    }
    
    /**
     * Ĭ�ϱ��루UTF8��д�ļ�
     * @param path
     * @param fileName
     * @param text
     * @return
     */
    public static String WriteGB2312Text(boolean rootPath, String fileName, String text) {
    	String path = DataMan.DataFile("", rootPath);

    	return WriteText(path, fileName, text, "GB2312");
    }
    
    /**
     * д�ļ���SD����
     * @param path
     * @param fileName
     * @param text
     * @return
     */
    private static String WriteText(String path, String fileName, String text, String encoding)
    {
    	String err = null;
        OutputStream outputStream = null;
        OutputStreamWriter writer = null;
        
        try {
        	Debug.Log("WriteText:" + path + ", " + fileName);
            File file = createSDFile(path, fileName);
            
            outputStream = new FileOutputStream(file);
            if (encoding != null)
            	writer = new OutputStreamWriter(outputStream, "GB2312");
            else
            	writer = new OutputStreamWriter(outputStream);
            writer.write(text);
            writer.flush();
        }    
        catch (Exception e) {   
            e.printStackTrace();
            err = "д�ļ�����";
            Debug.Log("WriteText��" + e.getMessage());
        }   
        finally{   
            try {
            	if (outputStream != null)
            		outputStream.close();
            	if (writer != null)
            		writer.close();
            } catch (IOException e) {   
                e.printStackTrace();   
            }   
        }
        
        return err;
    }
    
    /**
     * д�ļ�
     * @param fileName
     * @param content
     */
    public static boolean WriteFile(String fileName, byte[] content) {
		try {
			FileOutputStream file = new FileOutputStream(fileName);
			file.write(content);
			file.close();
		} catch (IOException e) {
			Debug.Log("д�ļ�����" + e.getMessage());
			return false;
		}

		return true;
	}
    
    /**
     * ����һ������
     * @param path
     * @param fileName
     * @param line
     * @return
     */
    public static String AppendLine(String fileName, String line) {
    	FileWriter writer = null;  
        try {
            // ��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�
        	if (!FileUtils.IsFileExist(fileName))
        		WriteText(fileName, line);
        	else {
	            writer = new FileWriter(fileName, true);
	            writer.write("\n" + line);
        	}
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
