package com.zknx.hn;

import com.zknx.hn.data.DataMan;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

public class DataService extends Service {
	
	// �����
	private static final int SECOND = 1000; // ��

	// ���ݰ�
	private final IBinder mBinder = new DataBinder();
	
	// �û�����ص��õ���Ϣ����Handler��ͬʱҲ�������ݸ��£���ʱ����
	private Handler mHandler;
	// ����������ʾ��Ϣ
	public static final int MESSAGE_NEW_DATA = 2;
	// ��������
	public static final int MESSAGE_NEW_MESSAGE = 3;
	
	// ��ȡ����ʵ��
	public class DataBinder extends Binder {
		DataService getService(Handler handler) {
			mHandler = handler;
			return DataService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		return false;
	}
	
	@Override
	public void onDestroy() { 
		super.onDestroy();
	}
	
	/**
	 * ��ʼ���㲥����
	 */
	public void startProcessBroadcastData() {
		new Thread(mProcessDataRunnable).start();
	}
	
	/**
	 * ��ʼ���������
	 */
	public void startGetNewMessage() {
		new Thread(mGetNewMessageRunnable).start();
	}

	// ���ݸ���Runnable
	private Runnable mProcessDataRunnable= new Runnable() {    
        public void run() {

        	// ÿ1���Ӽ��������
         	if (DataMan.CheckBroadcastData())
         		mHandler.sendEmptyMessage(MESSAGE_NEW_DATA);

        	mHandler.postDelayed(this, 60 * SECOND);
        }
    };
    
    // ���������Runnable
 	private Runnable mGetNewMessageRunnable= new Runnable() {    
         public void run() {

        	// ÿ5����������
         	String message = DataMan.GetNewMessages();
         	if (message != null)
         		mHandler.sendEmptyMessage(MESSAGE_NEW_MESSAGE);

         	mHandler.postDelayed(this, 5 * SECOND);
         }
     };
}
