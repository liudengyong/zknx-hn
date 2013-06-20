package com.zknx.hn.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;

public class WebkitClient extends WebChromeClient {

	private final static String TITLE = "中科农信";
	
	/**
	 * 打印js错误
	 */
	@Override
	public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        Debug.Log("onConsoleMessage() - " + consoleMessage.message() + ":" + consoleMessage.lineNumber());
        return super.onConsoleMessage(consoleMessage);
    }

	/**
	 * 覆盖默认的window.alert展示界面，避免title里显示为“：来自file:////”
	 */
	@Override
	public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

		// 不需要绑定按键事件
		// 屏蔽keycode等于84之类的按键
		OnKeyListener keyListener = new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				Debug.Log("onJsAlert keyCode==" + keyCode + "event=" + event);
				return true;
			}
		};
		
		// 禁止响应按back键的事件
		ShowAlertDialog(view.getContext(), message, null, null, null, null, keyListener, false);
		
		result.confirm();// 因为没有绑定事件，需要强行confirm,否则页面会变黑显示不了内容。
		return true;
		// return super.onJsAlert(view, url, message, result);
	}

	/**
	 * 覆盖默认的window.confirm展示界面，避免title里显示为“：来自file:////”
	 */
	@Override
	public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
		
		OnClickListener clickPositiveButton = new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					result.confirm();
				}
		};

		OnClickListener clickNeutralButton = new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				result.cancel();
			}
		};
		OnCancelListener cancelListener = new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				result.cancel();
			}
		};

		// 屏蔽keycode等于84之类的按键，避免按键后导致对话框消息而页面无法再弹出对话框的问题
		OnKeyListener keyListener = new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				Debug.Log("onJsConfirm keyCode==" + keyCode + "event=" + event);
				return true;
			}
		};

		ShowAlertDialog(view.getContext(), message, null, clickPositiveButton, clickNeutralButton, cancelListener, keyListener, true);

		return true;
		// return super.onJsConfirm(view, url, message, result);
	}

	/**
	 * 覆盖默认的window.prompt展示界面，避免title里显示为“：来自file:////”
	 * window.prompt('请输入您的域名地址', '618119.com');
	 */
	@Override
	public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
		final EditText et = new EditText(view.getContext());
		et.setSingleLine();
		et.setText(defaultValue);
		
		OnClickListener clickPositiveButton = new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				result.confirm(et.getText().toString());
			}
		};
		
		OnClickListener clickNeutralButton = new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				result.cancel();
			}
		};
		
		OnKeyListener keyListener = new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				Debug.Log("onJsPrompt keyCode==" + keyCode + "event=" + event);
				return true;
			}
		};
		
		ShowAlertDialog(view.getContext(), message, et, clickPositiveButton, clickNeutralButton, null, keyListener, true);

		return true;
		// return super.onJsPrompt(view, url, message, defaultValue, result);
	}
	
	/**
	 * 创建对话框
	 * @return
	 */
	private static void ShowAlertDialog(Context context, String message, View view, OnClickListener clickPositiveButton, OnClickListener clickNeutralButton, OnCancelListener clickCancelListener, OnKeyListener keyListener, boolean cancelable) {

		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(TITLE).setMessage(message);
		
		if (view != null)
			builder.setView(view);
		
		//if (clickPositiveButton != null)
			builder.setPositiveButton("确定", clickPositiveButton);
		
		if (clickNeutralButton != null)
			builder.setNeutralButton("取消", clickNeutralButton);
		
		if (clickCancelListener != null)
			builder.setOnCancelListener(clickCancelListener);

		// 屏蔽keycode等于84之类的按键，避免按键后导致对话框消息而页面无法再弹出对话框的问题
		if (keyListener != null)
			builder.setOnKeyListener(keyListener);

		// 禁止响应按back键的事件
		if (!cancelable)
			builder.setCancelable(false);

		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	/* 沒有覆蓋的方法
	@Override
	public void onCloseWindow(WebView window) {
		super.onCloseWindow(window);
	}

	@Override
	public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, Message resultMsg) {
		return super.onCreateWindow(view, dialog, userGesture, resultMsg);
	}
	
	@Override
	public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
		return super.onJsBeforeUnload(view, url, message, result);
	}

	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		super.onProgressChanged(view, newProgress);
	}

	@Override
	public void onReceivedIcon(WebView view, Bitmap icon) {
		super.onReceivedIcon(view, icon);
	}

	@Override
	public void onReceivedTitle(WebView view, String title) {
		super.onReceivedTitle(view, title);
	}

	@Override
	public void onRequestFocus(WebView view) {
		super.onRequestFocus(view);
	}
	*/
}