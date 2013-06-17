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

	// 登录和注册html资源路径
	private static final String ASSET_HTML_LOGIN    = "file:///android_asset/login.html";
	private static final String ASSET_HTML_REGISTER = "file:///android_asset/register.html";

	// 登录和注册url
	private static final String URL_LOGIN    = "http://login/todo/";
	private static final String URL_REGISTER = "http:///android_asset/register.html";

	private static final String KEY_USER_INFO = "user_info";
	private static final String KEY_USER = "user";
	private static final String KEY_PASSWD = "passwd";
	private static final String KEY_REM_PASSWD = "remember_passwd";

	private SharedPreferences mPreferences;

	// 盛放Login和Register的Flipper
	private ViewFlipper mViewFlipper;
	// 标题
	private TextView mTitle;

	// 等待对话框(不能同时登录和注册)
	private WaitDialog mWaitDialog;
	// 注册或者登录返回值
	private String mWaitRet;
	// 注册和返回消息
	private static final int MESSAGE_LOGIN    = 1;
	private static final int MESSAGE_REGISTER = 2;

	// 为静态Handler保存实例
	private static Login LoginInstance;
	private static Handler mHandler = new Handler() {
	    /**
	     * 实现消息处理
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
		
		// 添加子功能按钮
        mViewFlipper = (ViewFlipper)findViewById(R.id.login_flipper);
        
        mPreferences = getSharedPreferences(KEY_USER_INFO, 0);

        // 添加并初始化登录视图
        mViewFlipper.addView(getLoginView());
        // 添加并初始化注册视图
        mViewFlipper.addView(getRegisterView());

		// TODO A 调试登录
        /*
		if (App.mDebug) {
			mJsInterface.user = "12345678";
        	mJsInterface.passwd = "12345678";
		}
		*/
		
		LoginInstance = this;
	}

	/**
	 * 初始化html5登录视图
	 */
	private WebView getLoginView() {
		WebView webView = getWebView(ASSET_HTML_LOGIN);
		return webView;
	}
	
	/**
	 * 初始化html5注册视图
	 */
	private WebView getRegisterView() {
		WebView webView = getWebView(ASSET_HTML_REGISTER);
		return webView;
	}

	/**
	 * 获取注册的WebView
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private WebView getWebView(String url) {

		WebView webView = new WebView(this);

		WebSettings settings = webView.getSettings();
        settings.setSupportZoom(true);          // 支持缩放
        settings.setBuiltInZoomControls(true);  // 启用内置缩放装置
        settings.setJavaScriptEnabled(true);    // 启用JS脚本
        
        // 添加JS接口
        webView.addJavascriptInterface(mJsInterface, "zknx");
        
        webView.setWebViewClient(mWebViewClient);

        // 加载assets中的html文件
        webView.loadUrl(url);

        return webView;
	}
	
	/**
	 * 处理注册/登录消息
	 */
	private void processWaitMessage(int message) {

		// 隐藏等待进度条
    	mWaitDialog.dismiss();
    	
    	// 登录和注册接口返回空表示成功，失败返回失败消息
		if (mWaitRet == null) {
	    	if (MESSAGE_LOGIN == message) {
	    		// 登录成功保存信息
	    		mJsInterface.saveConfig();
				Dialog.Toast(this, R.string.login_success);
				successLogin();
	    	} else if (MESSAGE_REGISTER == message) {
	    		// 注册成功后跳转到登录界面
				Dialog.Toast(this, R.string.register_success);
				switchLoginRegisterView(false);
	    	}
		} else {
			String failedMessagePrefix = (MESSAGE_LOGIN == message) ? "登录失败：" : "注册失败：";

			Dialog.MessageBox(this, failedMessagePrefix + mWaitRet);
		}
	}
	
	private JsInterface mJsInterface = new JsInterface();

	/**
	 * 本地JS接口，用于初始化，保存设置，登录，注册
	 * @author Dengyong
	 *
	 */
	class JsInterface {

		// 上次登录信息
		String user;
		String passwd;
		boolean remember;
		
		JsInterface() {
		}

	    /**
	     * 初始化上次配置
	     */
	    public void initConfig() {
	    	user     = mPreferences.getString(KEY_USER, null);
	    	remember = mPreferences.getBoolean(KEY_REM_PASSWD, false);
	    	
	    	if (remember)
	    		passwd = mPreferences.getString(KEY_PASSWD, null);
	    }
	    
	    /**
	     * 开始登录
	     */
	    public void login(String _user, String _passwd, boolean _remember) {
	    	user = _user;
	    	passwd = _passwd;
	    	remember = _remember;
	    	tryLogin();
	    }
	    
	    /**
	     * 保存配置
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
	     * 开始登录：弹出等待进度条，启动新线程连接服务器登录
	     */
		private void startLogin() {

			WaitListener loginThread = new WaitListener() {
				/**
				 * 开始登录
				 */
				@Override
				public void startWait() {
					mWaitRet = UserMan.Login(mJsInterface.getUser(), mJsInterface.getPasswd());
					// TODO DataMan.CheckBroadcastData();
					mHandler.sendEmptyMessage(MESSAGE_LOGIN);
				}
			};
			
			// 创建等待进度条
			mWaitDialog = WaitDialog.Show(Login.this, null, "正在登录", loginThread);
		}
		
	    /**
	     * 开始注册：弹出等待进度条，启动新线程连接服务器注册
	     */
		private void startRegister() {
			
			WaitListener registerThread = new WaitListener() {
				/**
				 * 开始注册
				 */
				@Override
				public void startWait() {
					
					mWaitRet = UserMan.Register();
					
					mHandler.sendEmptyMessage(MESSAGE_REGISTER);
				}
			};
			
			// 创建等待进度条
			mWaitDialog = WaitDialog.Show(Login.this, "注册", "正在注册", registerThread);
		}
		
		/**
		 * 获取用户名
		 * @return
		 */
		public String getUser() {
			return (user == null) ? "" : user;
		}
		
		/**
		 * 获取用户密码
		 * @return
		 */
		public String getPasswd() {
			return (!remember || passwd == null) ? "" : passwd;
		}

		/**
		 * 获取是否记住密码
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
    		
    		// XXX 集成注册框
    		//switchLoginRegisterView(currentLoginView);
    		
    		// 截获登录地址，避免出现WebView"网页找不到"提示
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
    	 * 页面加载完成事件
    	 */
		@Override
    	public void onPageFinished(WebView webView, String url) {
    	}
    };
    
    /**
     * 尝试登录
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
     * 启动系统浏览器注册
     */
    void runBrowserActivity(final String url) {
    	Runnable action = new Runnable() {
            @Override
            public void run() {
            	// 跳转系统浏览器
    			Intent intent= new Intent();        
                intent.setAction(Intent.ACTION_VIEW);    
                intent.setData(Uri.parse(url));           
                startActivity(intent);
            }
    	};

    	// 界面线程中运行
		runOnUiThread(action);
    }
    
    /**
     * 切换登录和注册界面
     */
	private void switchLoginRegisterView(boolean currentLoginView) {
    	
    	if (currentLoginView) {
    		// 设置标题为注册
    		mTitle.setText(R.string.please_register);
    		mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in));
    		mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_out));
    		mViewFlipper.showNext();
    	} else {
    		// 设置标题为登录
    		mTitle.setText(R.string.please_login);
    		mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in));
    		mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_out));
    		mViewFlipper.showPrevious();
    	}
    }

    /***
     * 登录成功，调出Pending的功能界面，退出登录界面
     */
    private void successLogin() {
    	startFunctionActivity();
    	finish();
    }
	
    /***
     * 启动功能Activity，参数从Intent来（Home界面设置的参数）
     */
    private void startFunctionActivity() {
    	Intent intent = getIntent();
    	intent.setClass(this, Function.class);
    	startActivity(intent);
	} 
}