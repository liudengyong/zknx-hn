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
        	Debug.Log("MD5У���ʼ��ʧ�ܣ�MessageDigest��֧��MD5��" + e.getMessage());
        }
    }

	 /** 
     * �����ļ���md5У��ֵ 
     * @param file 
     * @return 
     * �����MessageDigest��֧��MD5�����ؿ�
     */  
    public static String GetFileMD5String(File file) {
    	
    	// �����MessageDigest��֧��MD5�����ؿ�
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
    		Debug.Log("MD5У��ʧ�ܣ�" + e.getMessage());
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
     * Ĭ�ϵ������ַ�����ϣ��������ֽ�ת���� 16 ���Ʊ�ʾ���ַ�,apacheУ�����ص��ļ�����ȷ���õľ���Ĭ�ϵ�������
     */  
    protected static char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6',  
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };  

    private static void AppendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = HEX_DIGITS[(bt & 0xf0) >> 4]; // ȡ�ֽ��и� 4 λ������ת��, >>> Ϊ�߼����ƣ�������λһ������,�˴�δ�������ַ����кβ�ͬ
        char c1 = HEX_DIGITS[bt & 0xf]; // ȡ�ֽ��е� 4 λ������ת��
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }
}
