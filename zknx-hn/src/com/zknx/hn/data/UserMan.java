package com.zknx.hn.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import com.zknx.hn.common.Debug;

public class UserMan {
	
	// �����û���Ϣ���ļ���
	private static final String USER_INFO_FILE_NMAE = "userinfo";
	
	/**
	 * �û���Ϣ�ṹ
	 * @author Dengyong
	 *
	 */
	private static class UserInfo {
		String userId; // �û�id
		String userName; // �û�ע������
		String major; // �û�ע��רҵ
		String address; // �û�ע���ַ
		String phone; // �绰
	}
	
	// ��ǰ�û���Ϣ
	public static UserInfo mUserInfo;

	/**
	 * ��ȡ��ǰ�û�id
	 * @return
	 * ����û��Ѿ���¼���򷵻��û�id
	 */
	public static String GetUserId() {
		return (mUserInfo != null) ? mUserInfo.userId : null;
	}
	
	/**
	 * ��ȡ��ǰ�û�����
	 * @return
	 * ����û��Ѿ���¼���򷵻��û�����
	 */
	public static String GetUserName() {
		return (mUserInfo != null) ? mUserInfo.userName : null;
	}
	
	/**
	 * ��ȡ��ǰ�û���ַid
	 * @return
	 * ����û��Ѿ���¼���򷵻��û���ַid
	 */
	public static String GetUserMajor() {
		return (mUserInfo != null) ? mUserInfo.major : null;
	}

	/**
	 * ��ȡ��ǰ�û���ַ
	 * @return
	 * ����û��Ѿ���¼���򷵻��û���ַ
	 */
	public static String GetUserAddress() {
		return (mUserInfo != null) ? mUserInfo.address : null;
	}
	
	/**
	 * ��ȡ��ǰ�û��绰
	 * @return
	 * ����û��Ѿ���¼���򷵻��û��绰
	 */
	public static String GetUserPhone() {
		return (mUserInfo != null) ? mUserInfo.phone : null;
	}
	/**
	 * ���õ�ǰ�û���Ϣ
	 * @param userId
	 * @param addrId
	 * @param addressId
	 * @param phone
	 */
	public static void SetUserInfo(String userId, String userName, String major, String address, String phone) {
		if (mUserInfo == null)
			mUserInfo = new UserInfo();
		
		mUserInfo.userId = userId;
		mUserInfo.userName = userName;
		mUserInfo.major = major;
		mUserInfo.address = address;
		mUserInfo.phone= phone;
		
		/*
		 * �����û���Ϣ
		*/
	}

	/**
	 * �û���¼
	 */
	public static String Login(String user, String passwd) {

		String ret = null;
		
		try {
			String param = "?userid=" + user + "&password=" + passwd;

			String value = SaveUserInfo(DataMan.URL_LOGIN + param, USER_INFO_FILE_NMAE + "." + user);

			String token[] = value.split(DataMan.COMMON_TOKEN); 

			if (token != null && token.length == 5) {
				
				if (!ParseUserInfo(user)) {
					// ��¼ʧ��
					return "��¼����" + ret;
				}
				
				return null;
			} else {
				return "�û������������";
			}
			
		} catch (UnknownHostException e) {
			ret = "����ʧ�ܣ���������״̬��";
		} catch (Exception e) {
			ret = "���������������״̬��";
		}
		
		return ret;
	}

	/**
	 * �����û���Ϣ
	 */
	private static String SaveUserInfo(String url, String fileName) throws Exception {

		Debug.Log(url);
		
		HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
		
		String returnString = "";
		
		if (conn.getResponseCode() == 200) {
			
			BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream(), "gb2312"));  
			OutputStreamWriter osw = null;
			
			returnString = r.readLine();
			
			Debug.Log("fileName=" + fileName);

			String token[] = returnString.split(DataMan.COMMON_TOKEN);
			if (token != null && token.length == 5/*returnString.equals("Result=TRUE")*/) {
				osw = new OutputStreamWriter(new FileOutputStream(DataMan.DataFile(fileName)), "gb2312"); 
			
				if (osw != null)
					osw.write(returnString);

				if (osw != null)
					osw.close(); 
			}

			r.close(); 
		}
		
		return returnString;
	}
	
	/**
	 * ��ȡ�û���Ϣ
	 * @return
	 */
	private static boolean ParseUserInfo(String user)
    {
		String fileName = DataMan.DataFile(USER_INFO_FILE_NMAE + "." + user);
		
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "gb2312"));

            String line = br.readLine();
            
            if (line != null) {
            	String token[] = line.split(DataMan.COMMON_TOKEN);
            	if (token != null && token.length == 5) {
                    SetUserInfo(token[0], token[0], token[2], token[3], token[4]);
            	}
            }
            
            br.close();
            
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }
}
