package com.zknx.hn.common.widget;

import com.zknx.hn.common.Debug;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class WaitDialog extends ProgressDialog {
	
	public interface Action {
		String getMessage();
		void waitAction();
	}

	public interface WaitListener {
		public void startWait();
	}
	
	// 等待结束后事件
	private static WaitListener mWaitListener;
	
	// 事件处理Handler（Function中初始化，否则出错loop.prepare……）
	private static Handler mHandler;

	private WaitDialog(Context context, WaitListener waitListener) {
		super(context);

		mWaitListener = waitListener;
	}
	
	/**
	 * 初始化Handler
	 */
	private static Handler GetHandler() {
		if (mHandler == null) {
	    	mHandler = new Handler() {
				@Override
				public void handleMessage(Message msg){
				   super.handleMessage(msg);
	
				   //WaitDialog.finish(msg.what);
				}
	    	};
		}
		
		return mHandler;
	}
	
	/**
	 * 发送消息
	 * @param message
	 * @return
	 */
	public static boolean SendEmptyMessage(int what) {
		Handler handler = GetHandler();

		// 内存分配错误
		if (handler == null)
			return false;
		
		handler.sendEmptyMessage(what);

		return false;
	}

	/**
	 * 生成并弹出进度条对话框
	 * @param context
	 * @param string
	 * @param string2
	 * @return
	 */
	public static WaitDialog Show(Context context, String title, String message, WaitListener waitListener) {

		final WaitDialog waitDialog = new WaitDialog(context, waitListener);
		
		waitDialog.setTitle(title);
		waitDialog.setMessage(message);

		// 可以手动取消更新
		waitDialog.setCancelable(true);

		waitDialog.show();
		
		new Thread() {
	         public void run(){

	        	 // 主线程以外的线程里创建AsynTask实例
	        	 Looper.prepare();

	            // 需要等待的动作，时间可能会比较长
				if (mWaitListener != null)
					mWaitListener.startWait();
				else
					Debug.Log("严重错误：startWait为空");
				
				waitDialog.dismiss();
	         }
	      }.start();
		
		return waitDialog;
	}
	
	/**
	 * 生成并弹出进度条对话框
	 * @param context
	 * @param string
	 * @param string2
	 * @return
	 */
	public static WaitDialog Show(Context context, final Handler handler, final int what, String message, WaitListener waitListener) {

		final WaitDialog waitDialog = new WaitDialog(context, waitListener);
		
		waitDialog.setTitle("请稍等");
		waitDialog.setMessage(message);

		// 可以手动取消更新
		waitDialog.setCancelable(true);

		waitDialog.show();
		
		new Thread() {
	         public void run(){

	        	 // 主线程以外的线程里创建AsynTask实例
	        	 Looper.prepare();

	            // 需要等待的动作，时间可能会比较长
				if (mWaitListener != null) {
					mWaitListener.startWait();
					handler.sendEmptyMessage(what);
				}
				else
					Debug.Log("严重错误：startWait为空");
				
				waitDialog.dismiss();
	         }
	      }.start();
		
		return waitDialog;
	}
	
	/**
	 * 生成并弹出进度条对话框
	 * @return
	 */
	public static void Show(Context context, final Action action) {

		final ProgressDialog progressDialog = new ProgressDialog(context);
		
		progressDialog.setTitle("请稍等");
		progressDialog.setMessage(action.getMessage());

		// 可以手动取消更新
		progressDialog.setCancelable(true);

		progressDialog.show();
		
		new Thread() {
	         public void run() {

	        	// 主线程以外的线程里创建AsynTask实例
	        	Looper.prepare();

	            // 需要等待的动作，时间可能会比较长
				action.waitAction();
				
				progressDialog.dismiss();
	         }
	      }.start();
	}
}
