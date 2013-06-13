package com.zknx.hn.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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
		String addrId; // �û�ע���ַ
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
	public static String GetUserAddressId() {
		return (mUserInfo != null) ? mUserInfo.addrId : null;
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
	public static void SetUserInfo(String userId, String userName, String addrId, String address, String phone) {
		if (mUserInfo == null)
			mUserInfo = new UserInfo();
		
		mUserInfo.userId = userId;
		mUserInfo.userName = userName;
		mUserInfo.addrId = addrId;
		mUserInfo.address = address;
		mUserInfo.phone= phone;
		
		/* TODO A �����û���Ϣ
		
		mUserInfo.protectionZoneId = protectionZoneId;
		*/
	}

	/**
	 * �û���¼
	 */
	public static String Login(String user, String passwd) {

		String ret = null;
		
		try {
			String param = "?data=<?xml%20version=\"1.0\"%20encoding=\"gb2312\"?><Upload><Getinfo><UserInfo><UserId>"
					+ URLEncoder.encode(user, "gb2312") + "</UserId><Password>"
					+ URLEncoder.encode(passwd, "gb2312") + "</Password></UserInfo></Getinfo></Upload>";

			String value = SaveUserInfo(DataMan.URL_GET_USER_INFO + param, USER_INFO_FILE_NMAE + "." + user);

			int start = value.indexOf('=');
			int end   = value.indexOf("UserId");

			if (start >= 0 && end > start && value.substring(start + 1, end).equals("TRUE")) {
				
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
	 * ע�����û�
	 * TODO interface ע���û�
	 */
	public static String Register() {
		return null;
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
			
			Debug.Log("ret=" + returnString);
			Debug.Log("fileName=" + fileName);

			if (returnString.equals("Result=TRUE"))
				osw = new OutputStreamWriter(new FileOutputStream(DataMan.DataFile(fileName)), "gb2312"); 
			
			String text;

			while((text = r.readLine()) != null)
			{
				returnString += text;

				if (osw != null)
					osw.write(text + "\n");
			}

			if (osw != null)
				osw.close();  
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

            int start;
            String key[] = {"UserId=", "Password=", "MobileNumber=", "AddressId=", "MajorId=", 
                    "BirthDate=", "sex=", "CunName=", "UserName=", "Postalcode=", 
                    "PostalAddr=", "ServerStat=", "ManageInfo="};

            String line;
            String value[] = new String[key.length];
            
            for (int i= 0; i < key.length; ++i) {
                line = br.readLine();
                if (line == null || line.length() < 1) break;
                start = line.indexOf(key[i]);
                if (start < 0) {
                	value[i] = "";
                	continue;
                }
                else value[i] = line.substring(start + key[i].length());
            }
            
            br.close();
            
            // TODO interface ������ϸ��ַ
            SetUserInfo(key[0], key[1], key[3], null, key[2]);

            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }
}
