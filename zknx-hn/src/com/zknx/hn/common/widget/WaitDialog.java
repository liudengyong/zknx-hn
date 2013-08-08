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
	
	// �ȴ��������¼�
	private static WaitListener mWaitListener;
	
	// �¼�����Handler��Function�г�ʼ�����������loop.prepare������
	private static Handler mHandler;

	private WaitDialog(Context context, WaitListener waitListener) {
		super(context);

		mWaitListener = waitListener;
	}
	
	/**
	 * ��ʼ��Handler
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
	 * ������Ϣ
	 * @param message
	 * @return
	 */
	public static boolean SendEmptyMessage(int what) {
		Handler handler = GetHandler();

		// �ڴ�������
		if (handler == null)
			return false;
		
		handler.sendEmptyMessage(what);

		return false;
	}

	/**
	 * ���ɲ������������Ի���
	 * @param context
	 * @param string
	 * @param string2
	 * @return
	 */
	public static WaitDialog Show(Context context, String title, String message, WaitListener waitListener) {

		final WaitDialog waitDialog = new WaitDialog(context, waitListener);
		
		waitDialog.setTitle(title);
		waitDialog.setMessage(message);

		// �����ֶ�ȡ������
		waitDialog.setCancelable(true);

		waitDialog.show();
		
		new Thread() {
	         public void run(){

	        	 // ���߳�������߳��ﴴ��AsynTaskʵ��
	        	 Looper.prepare();

	            // ��Ҫ�ȴ��Ķ�����ʱ����ܻ�Ƚϳ�
				if (mWaitListener != null)
					mWaitListener.startWait();
				else
					Debug.Log("���ش���startWaitΪ��");
				
				waitDialog.dismiss();
	         }
	      }.start();
		
		return waitDialog;
	}
	
	/**
	 * ���ɲ������������Ի���
	 * @param context
	 * @param string
	 * @param string2
	 * @return
	 */
	public static WaitDialog Show(Context context, final Handler handler, final int what, String message, WaitListener waitListener) {

		final WaitDialog waitDialog = new WaitDialog(context, waitListener);
		
		waitDialog.setTitle("���Ե�");
		waitDialog.setMessage(message);

		// �����ֶ�ȡ������
		waitDialog.setCancelable(true);

		waitDialog.show();
		
		new Thread() {
	         public void run(){

	        	 // ���߳�������߳��ﴴ��AsynTaskʵ��
	        	 Looper.prepare();

	            // ��Ҫ�ȴ��Ķ�����ʱ����ܻ�Ƚϳ�
				if (mWaitListener != null) {
					mWaitListener.startWait();
					handler.sendEmptyMessage(what);
				}
				else
					Debug.Log("���ش���startWaitΪ��");
				
				waitDialog.dismiss();
	         }
	      }.start();
		
		return waitDialog;
	}
	
	/**
	 * ���ɲ������������Ի���
	 * @return
	 */
	public static void Show(Context context, final Action action) {

		final ProgressDialog progressDialog = new ProgressDialog(context);
		
		progressDialog.setTitle("���Ե�");
		progressDialog.setMessage(action.getMessage());

		// �����ֶ�ȡ������
		progressDialog.setCancelable(true);

		progressDialog.show();
		
		new Thread() {
	         public void run() {

	        	// ���߳�������߳��ﴴ��AsynTaskʵ��
	        	Looper.prepare();

	            // ��Ҫ�ȴ��Ķ�����ʱ����ܻ�Ƚϳ�
				action.waitAction();
				
				progressDialog.dismiss();
	         }
	      }.start();
	}
}
