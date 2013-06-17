package com.zknx.hn;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

public class DataService extends Service {
	
	// �����
	private int mCheckDataInterval = 10; // ��

	// ���ݰ�
	private final IBinder mBinder = new DataBinder();
	
	// �û�����ص��õ���Ϣ����Handler��ͬʱҲ�������ݸ��£���ʱ����
	private Handler mHandler;
	// ����������ʾ��Ϣ
	public static final int MESSAGE_NEW_DATA = 1;
	
	// ��ȡ����ʵ��
	public class DataBinder extends Binder{
		DataService getService() {
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
	public void checkBroadcastData(Handler handler) {

		mHandler = handler;

		new Thread(mCheckDataRunnable).start();
	}

	// ���ݸ���Runnable
	private Runnable mCheckDataRunnable= new Runnable() {    
        public void run() {
/* TODO ������ݸ���
        	if (DataMan.CheckBroadcastData())
        		mHandler.sendEmptyMessage(MESSAGE_NEW_DATA);
        	*/

        	mHandler.postDelayed(this, mCheckDataInterval * 1000);
        }
    }; 
}
