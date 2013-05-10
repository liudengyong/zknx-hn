package com.zknx.hn.common;

import com.zknx.hn.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class Dialog {
	
	public interface ConfirmListener extends DialogInterface.OnClickListener {
	}
	
	/**
	 * 确认对话框
	 * @param context
	 * @param msgConfirm
	 * @param listener
	 */
	public static void Confirm(Context context, int msgConfirm, ConfirmListener listener)
    {
        new AlertDialog.Builder(context)
        .setIcon(null)
        .setTitle(R.string.app_name)
        .setMessage(msgConfirm)
        .setPositiveButton(android.R.string.yes, listener)
        .setNegativeButton(android.R.string.no, null)
        .show();
    }
	
	/**
	 * MessageBox
	 * @param context
	 * @param messageRes
	 */
	public static void MessageBox(Context context, int messageRes)
    {
        new AlertDialog.Builder(context)
        .setIcon(null)
        .setTitle(R.string.app_name)
        .setMessage(messageRes)
        .setPositiveButton(android.R.string.ok, null)
        .show();
    }
	
	/**
	 * MessageBox
	 * @param context
	 * @param messageRes
	 */
	public static void MessageBox(Context context, String message)
    {
        new AlertDialog.Builder(context)
        .setIcon(null)
        .setTitle(R.string.app_name)
        .setMessage(message)
        .setPositiveButton(android.R.string.ok, null)
        .show();
    }
	
	/**
	 * 弹出Toast提示
	 * @param context
	 * @param msg
	 */
	public static void Toast(Context context, int msgRes) {
		Toast.makeText(context, msgRes, Toast.LENGTH_LONG).show();
	}
}
