package com.zknx.hn.functions.rtmp;

import com.zknx.hn.common.Debug;
import com.zknx.hn.functions.rtmp.RtmpClient.OnConnListener;

public class Test {
	
	private RtmpClient playRtmpClient    = new RtmpClient();
	private RtmpClient publishRtmpClient = new RtmpClient();

	/**
	 * 开始会话
	 * @param server
	 * @param myStream
	 * @param toStream
	 */
	public void start(final String server, final String myStream, final String toStream) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				connect_server(server, myStream, new OnConnListener() {
					@Override
					public void onConnectSuccess() {
						Debug.Log("publish enter");
						publishRtmpClient.publish();
						Debug.Log("publish exit");
					}
				});
			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				connect_server(server, toStream, new OnConnListener() {
					@Override
					public void onConnectSuccess() {
						Debug.Log("play enter");
						playRtmpClient.play();
						Debug.Log("play exit");
					}
				});
			}
		}).start();
	}
	
	/**
	 * 连接服务器
	 * @param server
	 * @param stream
	 * @param connListener
	 */
	private void connect_server(String server, String stream, OnConnListener connListener) {
		try {
			publishRtmpClient.connect(server, stream);
			publishRtmpClient.setOnConnListener(connListener);
		} catch (Exception e) {
			Debug.Log("connect_server failed : " + e.getMessage());
		}
	}

	/**
	 * 停止会话
	 */
	public void stop() {
		playRtmpClient.disConnect();
		publishRtmpClient.disConnect();
	}
}
