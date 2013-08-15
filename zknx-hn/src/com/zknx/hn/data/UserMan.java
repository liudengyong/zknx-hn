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
		String major; // 用户注册专业
		String address; // 用户注册地址
		String addressId; // 用户注册地址
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
	public static String GetUserMajor() {
		return (mUserInfo != null) ? mUserInfo.major : null;
	}

	/**
	 * 获取当前用户地址
	 * @return
	 * 如果用户已经登录，则返回用户地址
	 */
	public static String GetUserAddress() {
		return (mUserInfo != null) ? mUserInfo.address : null;
	}
	
	public static String GetUserAddressId() {
		return (mUserInfo != null) ? mUserInfo.addressId : "";
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
	public static void SetUserInfo(String userId, String userName, String major, String address, String addressId, String phone) {
		if (mUserInfo == null)
			mUserInfo = new UserInfo();
		
		mUserInfo.userId = userId;
		mUserInfo.userName = userName;
		mUserInfo.major = major;
		mUserInfo.address = address;
		mUserInfo.addressId = addressId;
		mUserInfo.phone= phone;
		
		/*
		 * 完善用户信息
		*/
	}

	/**
	 * 用户登录
	 */
	public static String Login(String user, String passwd) {

		String ret = null;

		try {
			String param = "?userid=" + user + "&password=" + passwd;

			String value = SaveUserInfo(DataMan.URL_LOGIN + param, USER_INFO_FILE_NMAE + "." + user);

			String token[] = value.split(DataMan.COMMON_TOKEN); 

			if (token != null && token.length == 6) {
				
				if (!ParseUserInfo(user)) {
					// 登录失败
					return "登录错误：" + ret;
				}

				return value;
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

			String token[] = returnString.split(DataMan.COMMON_TOKEN);
			// name,passwd,addressId,vlilage,phone; 
			if (token != null && token.length == 6) {
				osw = new OutputStreamWriter(new FileOutputStream(DataMan.DataFile(fileName, true)), "gb2312"); 
			
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
	 * 读取用户信息
	 * @return
	 */
	private static boolean ParseUserInfo(String user)
    {
		String fileName = DataMan.DataFile(USER_INFO_FILE_NMAE + "." + user, true);
		
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "gb2312"));

            String line = br.readLine();

            if (line != null) {
            	String token[] = line.split(DataMan.COMMON_TOKEN);
            	if (token != null && token.length == 6) {
            		// ffddf,123456,04,1,dsdfs,13426180733;
            		String name = token[0];
            		String major = token[3];
            		String addressId = token[2];
            		String addressVillige = token[4];
            		String phone = token[5];
            		// 删除最后的;符号
            		phone = phone.replace(";", "");

                    SetUserInfo(user, name, major, addressVillige, addressId, phone);
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
