package com.zkxc.android.act;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.zkxc.android.R;
import com.zkxc.android.act.ActTable.Request;
import com.zkxc.android.common.Debug;
import com.zkxc.android.common.Launcher;
import com.zkxc.android.data.DataMan;
import com.zkxc.android.data.ZxDataMan;
import com.zkxc.android.map.Setting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ActLogon extends Activity implements Setting {
	
	private Button btnLogin;
	private Button btnExit;
	private EditText etAccount;
	private EditText etPW;
	private CheckBox cbrp;
	private SharedPreferences sPreferences;
	private static String login_return_value = "";
	
	//资源
	private Runnable getdata; 
	private ProgressBar secondBar = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zx_login);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		
	    etAccount=(EditText)findViewById(R.id.editText1);
	    etPW=(EditText)findViewById(R.id.editText2);
	    btnLogin=(Button)findViewById(R.id.submitbt);
	    btnExit=(Button)findViewById(R.id.resetbt);
	    cbrp=(CheckBox)findViewById(R.id.checkBox1);
	    secondBar = (ProgressBar)findViewById(R.id.secondBar);
	    secondBar.setVisibility(View.GONE);
	    //Toast.makeText(getApplicationContext(),channelname + columnname, Toast.LENGTH_LONG).show();
	    //转动的圈圈

	      
	      //cbal=(CheckBox)findViewById(R.id.checkBox2);
	      InitConfig();
	      
	      
	      
	      cbrp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				sPreferences=getSharedPreferences("UserInfo",0);
				sPreferences.edit().putBoolean("cbrp",isChecked).commit();
			}
		});
	     
	      btnLogin.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				LoginNow();
			}
		});
	      btnExit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				etAccount.setText("");
				etPW.setText("");
				//System.exit(0);
			}
		});
		
		findViewById(R.id.login_regBtn).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Uri uri = Uri.parse("http://218.106.254.101:8099/register.aspx");  
				Intent intent = new Intent(Intent.ACTION_VIEW, uri); 
				ActLogon.this.startActivity(intent);
			}
		});
		
		// 调试
		if (AppZkxc.mDebug) {
			if (AppZkxc.mUserInfo == null || AppZkxc.mUserInfo.userId == null)
			{
				etAccount.setText("22");
				etPW.setText("1");
				LoginNow();
			}
		}
	}
	
	private void LoginNow() {
		secondBar.setVisibility(View.VISIBLE);

		//user=new User(etAccount.getText().toString(),etPW.getText().toString());
		//userMgr=new UserMgr();
		
		
		Log.e("login", "getdata");
		//把网络请求放到这里来
		getdata = new Runnable(){   
            public void run() {
            	//System.out.println("1");
            	get_data_fun();  
            }  
        };

        Thread thread =  new Thread(null, getdata, "getdata");  
        thread.start(); 
		
        Log.e("login", "thread");
	}
	
	//网络请求反应慢的地方1
	private void get_data_fun()
	{
		String useridString =etAccount.getText().toString();
		String passwordString = etPW.getText().toString();
		
		try {
			
			Log.e("login", "get_data_fun");
			
			String xmlString = "";
			String urlString = "";
			xmlString = "userid="+useridString+"&"+"password="+passwordString;
			//xmlString = "";
			urlString = "http://218.106.254.101:8045/login.aspx";
			//urlString = "http://218.106.254.101:8045/get_collection_table.aspx?id=45";
			login_return_value = saveUserInfo(xmlString,urlString,"userinf.bat");
			//save_base_table_version("", "http://218.106.254.101:8045/Get_base_table_version.aspx", "base_table_version.bat");
			//得到服务器上基础表版本，返回一个list。
			ZxDataMan.Downlad_Base_Table_Ver();
			//打开本地基础表版本信息返回一个数组
			
			//对比list版本信息，如果服务器大于本地下载
			ZxDataMan.DownloadAddressList();
			
			
			//address.list
			
			//log.e("login", login_return_value);
			
			

		} catch (Exception e) {
			//Log.e("BACKGROUND_PROC", e.getMessage());
		}
		
		// 离线登录
		String[] lines = DataMan.ReadLines(ZxDataMan.DATA_DIR + "userinf.bat");
		
		if (lines != null && lines.length > 0)
		{
			for (String line : lines)
			{
				Debug.Log("line = " + line);
				String[] token = line.split(",");
				if (token == null || token.length < 4)
					continue;
				
				if (useridString.equals(token[0]) && passwordString.equals(token[1]))
				{
					login_return_value = line;
					break;
				}
			}
		}
		
		Debug.Log("login_return_value = " + login_return_value);
		String[] token = login_return_value.split(",");
		
		if (token != null && token.length >= 9)
		{
			AppZkxc.SetUsrInfo(token[0], token[2], token[3]);
			
			if (AppZkxc.mPenddingAct == Request.PENDDING_ACT_TABLE)
				Launcher.StartActivity(this, ActTable.class);
			else if (AppZkxc.mPenddingAct == Request.PENDDING_ACT_TRACK)
				Launcher.StartActivity(this, ActMapTrack.class);
			
			finish();
		}
		
		runOnUiThread(returnRes);
	}
	
	//网络请求反应慢的地方2
	
	private Runnable returnRes = new Runnable() {
		@SuppressLint({ "WorldReadableFiles", "WorldWriteableFiles" })
		public void run() {
			//在这里更新UI
			
			secondBar.setVisibility(View.GONE);

			//log.e("login", login_return_value.substring(0, 3));
			
			
			//etAccount.setText(login_return_value);
			//log.w("login", login_return_value);
			
			if ( login_return_value.equals("false")) {
				Toast.makeText(ActLogon.this, "登录失败", Toast.LENGTH_LONG).show();
				//Toast.makeText(LoginActivity.this,"用户名密码出错", 1000).show();
			}else {
				Log.e("login", login_return_value);
				
				
				//etAccount.setText("aaaaa");
				
				//String arrayString[] = login_return_value.split(",");
				//user=new User(arrayString[0],arrayString[1],arrayString[2],arrayString[3],arrayString[4],arrayString[5],arrayString[6],arrayString[7],arrayString[8]);
				
				//log.e("login", "登录成功"+arrayString[0]+arrayString[1]+arrayString[2]+arrayString[3]+arrayString[4]+arrayString[5]+arrayString[6]+arrayString[7]);
	
				String[] token = login_return_value.split(",");
				
				if (token != null && token.length >= 9) {
					if (cbrp.isChecked()) {
						sPreferences=getSharedPreferences("UserInfo", Context.MODE_WORLD_READABLE|Context.MODE_WORLD_WRITEABLE);
						sPreferences.edit().putString("account",etAccount.getText().toString()).commit();
						sPreferences.edit().putString("password",etPW.getText().toString()).commit();
					}
				}
				else
				{
					Toast.makeText(ActLogon.this, "登录失败", Toast.LENGTH_LONG).show();
				}
			}
        }  
    };
	
	
    public static String saveUserInfo(String xml_content,String page_name,String save_filename) throws Exception {

		//String xml = "data=" + xml_content;
		String xml = xml_content;
		//String path = "http://www.163.com" + page_name;
		String path = page_name;
		byte[] data = xml.getBytes();
		URL url = new URL(path);
		
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();

		conn.setRequestMethod("POST");
		conn.setConnectTimeout(10 * 1000);
		conn.setReadTimeout(10 * 1000);
		conn.setDoOutput(true);//如果通过post提交数据，必须设置允许对外输出数据
		conn.setRequestProperty("user-agent"," kSOAP/2.0");    
		//conn.setRequestProperty("soapaction"," http://tempuri.org/IAvatarApplicationService/" + methename);    
		conn.setRequestProperty("content-type"," application/x-www-form-urlencoded");    
		conn.setRequestProperty("connection"," close");    
		conn.setRequestProperty("Accept"," *, */*");  
		conn.setRequestProperty("Host"," 218.106.254.101:8045");
		conn.setRequestProperty("Content-Length", String.valueOf(data.length));


		OutputStream outStream = conn.getOutputStream();
		outStream.write(data);
		outStream.flush();
		outStream.close();

		String returnString = "";
		if(conn.getResponseCode()==200){
			
		    File folderData  = new File(ZxDataMan.DATA_DIR);
		    if (!folderData.exists() && !folderData.mkdirs())
		        throw new Exception("Failed to create folder : " + ZxDataMan.DATA_DIR);

			BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));  
			OutputStreamWriter osw = null;
			
			//returnString = r.readLine();
			Log.e("login", ZxDataMan.DATA_DIR + save_filename);
			//if (returnString.equals("Result=TRUE"))
			osw = new OutputStreamWriter(new FileOutputStream(ZxDataMan.DATA_DIR + save_filename)); 

			String text;

			//reader.readline() != -1
			while((text = r.readLine()) != null)
			{
				returnString += text;

				if (osw != null)
					osw.write(text + "\n");
			}
			//log.e("login", returnString+"");
			
			if (osw != null)
				osw.close();  
			r.close(); 

		}
		return returnString;
	}
    
    public static String save_base_table_version(String xml_content,String page_name,String save_filename) throws Exception {

		//String xml = "data=" + xml_content;
		String xml = xml_content;
		//String path = "http://www.163.com" + page_name;
		String path = page_name;
		byte[] data = xml.getBytes();
		URL url = new URL(path);
		
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();

		conn.setRequestMethod("POST");
		conn.setConnectTimeout(10 * 1000);
		conn.setReadTimeout(10 * 1000);
		conn.setDoOutput(true);//如果通过post提交数据，必须设置允许对外输出数据
		conn.setRequestProperty("user-agent"," kSOAP/2.0");    
		//conn.setRequestProperty("soapaction"," http://tempuri.org/IAvatarApplicationService/" + methename);    
		conn.setRequestProperty("content-type"," application/x-www-form-urlencoded");    
		conn.setRequestProperty("connection"," close");    
		conn.setRequestProperty("Accept"," *, */*");  
		conn.setRequestProperty("Host"," 218.106.254.101:8045");
		conn.setRequestProperty("Content-Length", String.valueOf(data.length));


		OutputStream outStream = conn.getOutputStream();
		outStream.write(data);
		outStream.flush();
		outStream.close();

		String returnString = "";
		if(conn.getResponseCode()==200){
			
			

			
			
		    File folderData  = new File(ZxDataMan.DATA_DIR);
		    if (!folderData.exists() && !folderData.mkdirs())
		        throw new Exception("Failed to create folder : " + ZxDataMan.DATA_DIR);

			BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));  
			OutputStreamWriter osw = null;
			
			//returnString = r.readLine();
			Log.e("login", ZxDataMan.DATA_DIR + save_filename);
			//if (returnString.equals("Result=TRUE"))
			osw = new OutputStreamWriter(new FileOutputStream(ZxDataMan.DATA_DIR + save_filename), "utf-8"); 
			

			String text;

			while((text = r.readLine()) != null)
			{
				returnString += text;

				if (osw != null)
					osw.write(text + "\n");
			}
			//log.e("login", returnString+"");
			
			
			if (osw != null)
				osw.close();  
			r.close(); 

		}
		return returnString;
	}

    private void InitConfig(){
    	sPreferences=getSharedPreferences("UserInfo", 0);
    	etAccount.setText(sPreferences.getString("account",null));
    	etPW.setText(sPreferences.getString("password", null));
    	//cbal.setChecked(sPreferences.getBoolean("cbal", false));
    	cbrp.setChecked(sPreferences.getBoolean("cbrp", false));
    }
}