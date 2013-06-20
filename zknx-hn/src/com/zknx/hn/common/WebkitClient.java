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

	private final static String TITLE = "�п�ũ��";
	
	/**
	 * ��ӡjs����
	 */
	@Override
	public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        Debug.Log("onConsoleMessage() - " + consoleMessage.message() + ":" + consoleMessage.lineNumber());
        return super.onConsoleMessage(consoleMessage);
    }

	/**
	 * ����Ĭ�ϵ�window.alertչʾ���棬����title����ʾΪ��������file:////��
	 */
	@Override
	public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

		// ����Ҫ�󶨰����¼�
		// ����keycode����84֮��İ���
		OnKeyListener keyListener = new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				Debug.Log("onJsAlert keyCode==" + keyCode + "event=" + event);
				return true;
			}
		};
		
		// ��ֹ��Ӧ��back�����¼�
		ShowAlertDialog(view.getContext(), message, null, null, null, null, keyListener, false);
		
		result.confirm();// ��Ϊû�а��¼�����Ҫǿ��confirm,����ҳ�������ʾ�������ݡ�
		return true;
		// return super.onJsAlert(view, url, message, result);
	}

	/**
	 * ����Ĭ�ϵ�window.confirmչʾ���棬����title����ʾΪ��������file:////��
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

		// ����keycode����84֮��İ��������ⰴ�����¶Ի�����Ϣ��ҳ���޷��ٵ����Ի��������
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
	 * ����Ĭ�ϵ�window.promptչʾ���棬����title����ʾΪ��������file:////��
	 * window.prompt('����������������ַ', '618119.com');
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
	 * �����Ի���
	 * @return
	 */
	private static void ShowAlertDialog(Context context, String message, View view, OnClickListener clickPositiveButton, OnClickListener clickNeutralButton, OnCancelListener clickCancelListener, OnKeyListener keyListener, boolean cancelable) {

		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(TITLE).setMessage(message);
		
		if (view != null)
			builder.setView(view);
		
		//if (clickPositiveButton != null)
			builder.setPositiveButton("ȷ��", clickPositiveButton);
		
		if (clickNeutralButton != null)
			builder.setNeutralButton("ȡ��", clickNeutralButton);
		
		if (clickCancelListener != null)
			builder.setOnCancelListener(clickCancelListener);

		// ����keycode����84֮��İ��������ⰴ�����¶Ի�����Ϣ��ҳ���޷��ٵ����Ի��������
		if (keyListener != null)
			builder.setOnKeyListener(keyListener);

		// ��ֹ��Ӧ��back�����¼�
		if (!cancelable)
			builder.setCancelable(false);

		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	/* �]�и��w�ķ���
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