package com.zkxc.android.common;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;

public class MsgBox {
	
	public static void YesNo(Context context, String msg, DialogInterface.OnClickListener listener) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("请选择");
		builder.setPositiveButton("确认", listener); 
		builder.setNegativeButton("取消", null); 
		builder.setIcon(android.R.drawable.ic_dialog_info); 
		builder.setMessage(msg); 
		builder.show();
	}
}
