package com.zkxc.android.common;

import com.zkxc.android.act.AppZkxc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;
import android.widget.Toast;

public class MessageBox {

	public static void Show(Context context, String msg)
	{
		new AlertDialog.Builder(context)
		.setTitle("备注")
		.setIcon(android.R.drawable.ic_dialog_info)
		.setMessage(msg)
		.setPositiveButton(android.R.string.ok, null)
		.show();
	}

	public static void ShowList(final Context context, final TextView list, String data)
	{
		final String[] items = data.split(",");
		
		new AlertDialog.Builder(context)
		.setTitle("请选择")
		.setIcon(android.R.drawable.ic_dialog_info)
		.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int item) {
				if (AppZkxc.mDebug)
					Toast.makeText(context, items[item], Toast.LENGTH_SHORT).show();
				list.setText(items[item]);
				dialog.cancel();
		}})
		.show();
	}
	
	public static void YesOrNo(Context context, String msg, DialogInterface.OnClickListener yesListener)
	{
		new AlertDialog.Builder(context)
		.setTitle("请确认")
		.setIcon(android.R.drawable.ic_dialog_info)
		.setMessage(msg)
		.setPositiveButton(android.R.string.yes, yesListener)
		.setNegativeButton(android.R.string.no, null)
		.show();
	}
}
