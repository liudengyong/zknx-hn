package com.zknx.hn;

import com.zknx.hn.common.Debug;
import com.zknx.hn.common.Dialog;
import com.zknx.hn.common.WaitDialog;
import com.zknx.hn.common.WaitDialog.WaitListener;
import com.zknx.hn.data.UserMan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.AnimationUtils;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class Login extends Activity {

	// ��¼��ע��html��Դ·��
	private static final String ASSET_HTML_LOGIN    = "file:///android_asset/login.html";
	private static final String ASSET_HTML_REGISTER = "file:///android_asset/register.html";

	// ��¼��ע��url
	private static final String URL_LOGIN    = "http://login/todo/";
	private static final String URL_REGISTER = "http:///android_asset/register.html";

	private static final String KEY_USER_INFO = "user_info";
	private static final String KEY_USER = "user";
	private static final String KEY_PASSWD = "passwd";
	private static final String KEY_REM_PASSWD = "remember_passwd";

	private SharedPreferences mPreferences;

	// ʢ��Login��Register��Flipper
	private ViewFlipper mViewFlipper;
	// ����
	private TextView mTitle;

	// �ȴ��Ի���(����ͬʱ��¼��ע��)
	private WaitDialog mWaitDialog;
	// ע����ߵ�¼����ֵ
	private String mWaitRet;
	// ע��ͷ�����Ϣ
	private static final int MESSAGE_LOGIN    = 1;
	private static final int MESSAGE_REGISTER = 2;

	// Ϊ��̬Handler����ʵ��
	private static Login LoginInstance;
	private static Handler mHandler = new Handler() {
	    /**
	     * ʵ����Ϣ����
	     */
	    @Override
	    public void handleMessage(Message msg) {
	    	LoginInstance.processWaitMessage(msg.what);
	    }
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_html5_login);
		
		mTitle = (TextView) findViewById(R.id.login_title_tv);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		// ����ӹ��ܰ�ť
        mViewFlipper = (ViewFlipper)findViewById(R.id.login_flipper);
        
        mPreferences = getSharedPreferences(KEY_USER_INFO, 0);

        // ��Ӳ���ʼ����¼��ͼ
        mViewFlipper.addView(getLoginView());
        // ��Ӳ���ʼ��ע����ͼ
        mViewFlipper.addView(getRegisterView());

		// TODO A ���Ե�¼
        /*
		if (App.mDebug) {
			mJsInterface.user = "12345678";
        	mJsInterface.passwd = "12345678";
		}
		*/
		
		LoginInstance = this;
	}

	/**
	 * ��ʼ��html5��¼��ͼ
	 */
	private WebView getLoginView() {
		WebView webView = getWebView(ASSET_HTML_LOGIN);
		return webView;
	}
	
	/**
	 * ��ʼ��html5ע����ͼ
	 */
	private WebView getRegisterView() {
		WebView webView = getWebView(ASSET_HTML_REGISTER);
		return webView;
	}

	/**
	 * ��ȡע���WebView
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private WebView getWebView(String url) {

		WebView webView = new WebView(this);

		WebSettings settings = webView.getSettings();
        settings.setSupportZoom(true);          // ֧������
        settings.setBuiltInZoomControls(true);  // ������������װ��
        settings.setJavaScriptEnabled(true);    // ����JS�ű�
        
        // ���JS�ӿ�
        webView.addJavascriptInterface(mJsInterface, "zknx");
        
        webView.setWebViewClient(mWebViewClient);

        // ����assets�е�html�ļ�
        webView.loadUrl(url);

        return webView;
	}
	
	/**
	 * ����ע��/��¼��Ϣ
	 */
	private void processWaitMessage(int message) {

		// ���صȴ�������
    	mWaitDialog.dismiss();
    	
    	// ��¼��ע��ӿڷ��ؿձ�ʾ�ɹ���ʧ�ܷ���ʧ����Ϣ
		if (mWaitRet == null) {
	    	if (MESSAGE_LOGIN == message) {
	    		// ��¼�ɹ�������Ϣ
	    		mJsInterface.saveConfig();
				Dialog.Toast(this, R.string.login_success);
				successLogin();
	    	} else if (MESSAGE_REGISTER == message) {
	    		// ע��ɹ�����ת����¼����
				Dialog.Toast(this, R.string.register_success);
				switchLoginRegisterView(false);
	    	}
		} else {
			String failedMessagePrefix = (MESSAGE_LOGIN == message) ? "��¼ʧ�ܣ�" : "ע��ʧ�ܣ�";

			Dialog.MessageBox(this, failedMessagePrefix + mWaitRet);
		}
	}
	
	private JsInterface mJsInterface = new JsInterface();

	/**
	 * ����JS�ӿڣ����ڳ�ʼ�����������ã���¼��ע��
	 * @author Dengyong
	 *
	 */
	class JsInterface {

		// �ϴε�¼��Ϣ
		String user;
		String passwd;
		boolean remember;
		
		JsInterface() {
		}

	    /**
	     * ��ʼ���ϴ�����
	     */
	    public void initConfig() {
	    	user     = mPreferences.getString(KEY_USER, null);
	    	remember = mPreferences.getBoolean(KEY_REM_PASSWD, false);
	    	
	    	if (remember)
	    		passwd = mPreferences.getString(KEY_PASSWD, null);
	    }
	    
	    /**
	     * ��ʼ��¼
	     */
	    public void login(String _user, String _passwd, boolean _remember) {
	    	user = _user;
	    	passwd = _passwd;
	    	remember = _remember;
	    	tryLogin();
	    }
	    
	    /**
	     * ��������
	     */
	    private void saveConfig() {
	    	Editor editor = mPreferences.edit();

	    	editor.putString(KEY_USER, user);
	    	editor.putBoolean(KEY_REM_PASSWD, remember);

	    	if (remember)
	    		editor.putString(KEY_PASSWD, passwd);
	    	else
	    		editor.putString(KEY_PASSWD, "");

	    	editor.commit();
	    }

	    /**
	     * ��ʼ��¼�������ȴ����������������߳����ӷ�������¼
	     */
		private void startLogin() {

			WaitListener loginThread = new WaitListener() {
				/**
				 * ��ʼ��¼
				 */
				@Override
				public void startWait() {
					mWaitRet = UserMan.Login(mJsInterface.getUser(), mJsInterface.getPasswd());
					// TODO DataMan.CheckBroadcastData();
					mHandler.sendEmptyMessage(MESSAGE_LOGIN);
				}
			};
			
			// �����ȴ�������
			mWaitDialog = WaitDialog.Show(Login.this, null, "���ڵ�¼", loginThread);
		}
		
	    /**
	     * ��ʼע�᣺�����ȴ����������������߳����ӷ�����ע��
	     */
		private void startRegister() {
			
			WaitListener registerThread = new WaitListener() {
				/**
				 * ��ʼע��
				 */
				@Override
				public void startWait() {
					
					mWaitRet = UserMan.Register();
					
					mHandler.sendEmptyMessage(MESSAGE_REGISTER);
				}
			};
			
			// �����ȴ�������
			mWaitDialog = WaitDialog.Show(Login.this, "ע��", "����ע��", registerThread);
		}
		
		/**
		 * ��ȡ�û���
		 * @return
		 */
		public String getUser() {
			return (user == null) ? "" : user;
		}
		
		/**
		 * ��ȡ�û�����
		 * @return
		 */
		public String getPasswd() {
			return (!remember || passwd == null) ? "" : passwd;
		}

		/**
		 * ��ȡ�Ƿ��ס����
		 * @return
		 */
		public boolean getRemember() {
			return remember;
		}
	}
	
	private WebViewClient mWebViewClient = new WebViewClient() {
		@Override
    	public boolean shouldOverrideUrlLoading(WebView webView, String url) {
			
			Debug.Log("loadUrl:" + url);
			
    		//view.loadUrl(url);

    		//boolean currentLoginView = !url.startsWith(URL_REGISTER);
    		
    		// XXX ����ע���
    		//switchLoginRegisterView(currentLoginView);
    		
    		// �ػ��¼��ַ���������WebView"��ҳ�Ҳ���"��ʾ
    		if (url.startsWith(URL_LOGIN)) {
    			//tryLogin(webView);
    			webView.loadUrl("javascript:login()");
    		} else if (url.startsWith(URL_REGISTER)) {
    			mJsInterface.startRegister();
    		} else {
    			runBrowserActivity(url);
    		}

    		return true;
    	}
    	
    	/**
    	 * ҳ���������¼�
    	 */
		@Override
    	public void onPageFinished(WebView webView, String url) {
    	}
    };
    
    /**
     * ���Ե�¼
     */
    private void tryLogin() {
        
    	if (mJsInterface.user == null || mJsInterface.user.length() == 0)
    		Dialog.MessageBox(this, R.string.empty_username);
    	else if (mJsInterface.passwd == null || mJsInterface.passwd.length() == 0)
    		Dialog.MessageBox(this, R.string.empty_passwd);
    	else
    		mJsInterface.startLogin();
    }
    
    /**
     * ����ϵͳ�����ע��
     */
    void runBrowserActivity(final String url) {
    	Runnable action = new Runnable() {
            @Override
            public void run() {
            	// ��תϵͳ�����
    			Intent intent= new Intent();        
                intent.setAction(Intent.ACTION_VIEW);    
                intent.setData(Uri.parse(url));           
                startActivity(intent);
            }
    	};

    	// �����߳�������
		runOnUiThread(action);
    }
    
    /**
     * �л���¼��ע�����
     */
	private void switchLoginRegisterView(boolean currentLoginView) {
    	
    	if (currentLoginView) {
    		// ���ñ���Ϊע��
    		mTitle.setText(R.string.please_register);
    		mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in));
    		mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_out));
    		mViewFlipper.showNext();
    	} else {
    		// ���ñ���Ϊ��¼
    		mTitle.setText(R.string.please_login);
    		mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in));
    		mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_out));
    		mViewFlipper.showPrevious();
    	}
    }

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
}