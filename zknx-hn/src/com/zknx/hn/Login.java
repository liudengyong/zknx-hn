package com.zknx.hn;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.zknx.hn.common.Debug;
import com.zknx.hn.data.DataMan;
import com.zknx.hn.data.UserMan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Login extends Activity {
	
	private static final String KEY_USER_INFO = "user_info";
	private static final String KEY_USER = "user";
	private static final String KEY_PASSWD = "passwd";
	private static final String KEY_REM_PASSWD = "remember_passwd";
	
	private static final String USER_INFO_FILE_NAME = "userinfo.dat";
	
	private Button   mBtnLogin;
	private EditText mEditUser;
	private EditText mEditPasswd;
	private CheckBox mCheckBoxRemPasswd;
	
	private SharedPreferences mPreferences;
	private static String mRetValue = "";
	
	private ProgressBar mProgressBar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		
	    mEditUser = (EditText)findViewById(R.id.login_user);
	    mEditPasswd = (EditText)findViewById(R.id.login_passwd);
	    mBtnLogin = (Button)findViewById(R.id.login_submitbt);
	    mCheckBoxRemPasswd = (CheckBox)findViewById(R.id.longin_check_rem_passwd);
	    mProgressBar = (ProgressBar)findViewById(R.id.login_secondBar);
	    mProgressBar.setVisibility(View.GONE);

	    // ��ʼ������
	    InitConfig();
	    
	    mCheckBoxRemPasswd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mPreferences = getSharedPreferences(KEY_USER_INFO,0);
				mPreferences.edit().putBoolean(KEY_REM_PASSWD,isChecked).commit();
			}
		});
	     
	      mBtnLogin.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				LoginNow();
			}
		});
/*
	    TextView regLink = (TextView)findViewById(R.id.login_reg_link);
	    regLink.setText(Html.fromHtml("<a href=\"" + UIConst.REG_ADDRESS + "\">ע���˺�</a>"));
	    regLink.setMovementMethod(LinkMovementMethod.getInstance());
*/
		// TODO A ���Ե�¼
			{
				//mEditUser.setText("linshi");
				//mEditPasswd.setText("123456");
				//LoginNow();
			}
	}
	
	private void LoginNow() {
		mProgressBar.setVisibility(View.VISIBLE);

		// ����������ŵ�������
		Runnable getdata = new Runnable(){   
            public void run() {
            	get_data_fun();  
            }  
        };

        Thread thread =  new Thread(null, getdata, "getdata");  
        thread.start(); 
	}

	// ��������Ӧ���ĵط�1
	private void get_data_fun()
	{
		String useridString =mEditUser.getText().toString();
		String passwordString = mEditPasswd.getText().toString();
		
		try {
			mRetValue = UserMan.Login(useridString, passwordString);
		} catch (Exception e) {
			//Log.e("BACKGROUND_PROC", e.getMessage());
		}

		if (mRetValue != null) {
			String token[] = mRetValue.split(DataMan.COMMON_TOKEN); 
	
			if (token != null && token.length == 6)
			{
				successLogin();
			}
		}

		runOnUiThread(returnRes);
	}
	
	//��������Ӧ���ĵط�2
	
	private Runnable returnRes = new Runnable() {
		@SuppressLint({ "WorldReadableFiles", "WorldWriteableFiles" })
		public void run() {
			//���������UI
			
			mProgressBar.setVisibility(View.GONE);
			
			//log.e("login", "��¼�ɹ�"+arrayString[0]+arrayString[1]+arrayString[2]+arrayString[3]+arrayString[4]+arrayString[5]+arrayString[6]+arrayString[7]);

			if (mRetValue != null) {
				String token[] = mRetValue.split(DataMan.COMMON_TOKEN); 
		
				if (token != null && token.length == 6)
				{
					if (mCheckBoxRemPasswd.isChecked()) {
						mPreferences=getSharedPreferences(KEY_USER_INFO, Context.MODE_WORLD_READABLE|Context.MODE_WORLD_WRITEABLE);
						mPreferences.edit().putString(KEY_USER,mEditUser.getText().toString()).commit();
						mPreferences.edit().putString(KEY_PASSWD,mEditPasswd.getText().toString()).commit();
					}
					
					Toast.makeText(Login.this, "��¼�ɹ�", Toast.LENGTH_LONG).show();
					
					successLogin();
					return;
				}
			}
			
			Toast.makeText(Login.this, "��¼ʧ��", Toast.LENGTH_LONG).show();
        }
    };
    
    /***
     * ��¼�ɹ�������Pending�Ĺ��ܽ��棬�˳���¼����
     */
    private void successLogin() {
    	startFunctionActivity();
    	finish();
    }
	
    /***
     * ��������Activity��������Intent����Home�������õĲ�����
     */
    private void startFunctionActivity() {
    	Intent intent = getIntent();
    	intent.setClass(this, Function.class);
    	startActivity(intent);
	} 

    private void InitConfig() {
    	mPreferences = getSharedPreferences(KEY_USER_INFO, 0);
    	mEditUser.setText(mPreferences.getString(KEY_USER,null));
    	mEditPasswd.setText(mPreferences.getString(KEY_PASSWD, null));
    	mCheckBoxRemPasswd.setChecked(mPreferences.getBoolean(KEY_REM_PASSWD, false));
    }
}