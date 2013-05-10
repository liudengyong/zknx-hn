package com.zknx.hn.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

	protected static MessageDigest mMessageDigest = null;

    static {  
        try {
            mMessageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
        	Debug.Log("MD5校验初始化失败，MessageDigest不支持MD5：" + e.getMessage());
        }
    }

	 /** 
     * 生成文件的md5校验值 
     * @param file 
     * @return 
     * 如果，MessageDigest不支持MD5，返回空
     */  
    public static String GetFileMD5String(File file) {
    	
    	// 如果，MessageDigest不支持MD5，返回空
    	if (mMessageDigest == null)
    		return null;

    	try {
	        InputStream in = new FileInputStream(file);
	        byte[] buffer = new byte[1024];
	        int numRead = 0;
	        while ((numRead = in.read(buffer)) > 0) {
	            mMessageDigest.update(buffer, 0, numRead);
	        }
	        
	        in.close();

	        return BufferToHex(mMessageDigest.digest());
    	}
    	catch (IOException e) {
    		Debug.Log("MD5校验失败：" + e.getMessage());
    	}

    	return null;
    }

    private static String BufferToHex(byte bytes[]) {
        return BufferToHex(bytes, 0, bytes.length);
    }

    private static String BufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            AppendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    /** 
     * 默认的密码字符串组合，用来将字节转换成 16 进制表示的字符,apache校验下载的文件的正确性用的就是默认的这个组合
     */  
    protected static char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6',  
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };  

    private static void AppendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = HEX_DIGITS[(bt & 0xf0) >> 4]; // 取字节中高 4 位的数字转换, >>> 为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同
        char c1 = HEX_DIGITS[bt & 0xf]; // 取字节中低 4 位的数字转换
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }
}
