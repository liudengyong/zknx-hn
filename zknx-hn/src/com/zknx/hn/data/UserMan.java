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
	
	// 保存用户信息的文件名
	private static final String USER_INFO_FILE_NMAE = "userinfo";
	
	/**
	 * 用户信息结构
	 * @author Dengyong
	 *
	 */
	private static class UserInfo {
		String userId; // 用户id
		String userName; // 用户注册名字
		String addrId; // 用户注册地址
		String address; // 用户注册地址
		String phone; // 电话
	}
	
	// 当前用户信息
	public static UserInfo mUserInfo;

	/**
	 * 获取当前用户id
	 * @return
	 * 如果用户已经登录，则返回用户id
	 */
	public static String GetUserId() {
		return (mUserInfo != null) ? mUserInfo.userId : null;
	}
	
	/**
	 * 获取当前用户名字
	 * @return
	 * 如果用户已经登录，则返回用户名字
	 */
	public static String GetUserName() {
		return (mUserInfo != null) ? mUserInfo.userName : null;
	}
	
	/**
	 * 获取当前用户地址id
	 * @return
	 * 如果用户已经登录，则返回用户地址id
	 */
	public static String GetUserAddressId() {
		return (mUserInfo != null) ? mUserInfo.addrId : null;
	}

	/**
	 * 获取当前用户地址
	 * @return
	 * 如果用户已经登录，则返回用户地址
	 */
	public static String GetUserAddress() {
		return (mUserInfo != null) ? mUserInfo.address : null;
	}
	
	/**
	 * 获取当前用户电话
	 * @return
	 * 如果用户已经登录，则返回用户电话
	 */
	public static String GetUserPhone() {
		return (mUserInfo != null) ? mUserInfo.phone : null;
	}
	/**
	 * 设置当前用户信息
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
		
		/* TODO A 完善用户信息
		
		mUserInfo.protectionZoneId = protectionZoneId;
		*/
	}

	/**
	 * 用户登录
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
					// 登录失败
					return "登录错误：" + ret;
				}
				
				return null;
			} else {
				return "用户名或密码错误！";
			}
			
		} catch (UnknownHostException e) {
			ret = "联网失败，请检查网络状态！";
		} catch (Exception e) {
			ret = "网络错误，请检查网络状态！";
		}
		
		return ret;
	}

	/**
	 * 注册新用户
	 * TODO interface 注册用户
	 */
	public static String Register() {
		return null;
	}
	
	/**
	 * 保存用户信息
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
	 * 读取用户信息
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
            
            // TODO interface 暂无详细地址
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
