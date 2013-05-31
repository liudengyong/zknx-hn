package com.zknx.hn.functions.rtmp;

import com.zknx.hn.functions.rtmp.RtmpClient.OnConnListener;

public class Test {
	
	private RtmpClient playRtmpClient = new RtmpClient();
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
				publishRtmpClient.connect(server, myStream);
				publishRtmpClient.setOnConnListener(new OnConnListener() {
					@Override
					public void onConnectSuccess() {
						publishRtmpClient.publish();
					}
				});
			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				playRtmpClient.connect(server, toStream);
				playRtmpClient.setOnConnListener(new OnConnListener() {
					@Override
					public void onConnectSuccess() {
						playRtmpClient.play();
					}
				});
			}
		}).start();
	}

	/**
	 * 停止会话
	 */
	public void stop() {
		playRtmpClient.disConnect();
		publishRtmpClient.disConnect();
	}
}
