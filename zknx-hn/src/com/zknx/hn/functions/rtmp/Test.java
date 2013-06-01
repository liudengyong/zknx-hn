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
				playRtmpClient.setOnConnListener(new OnConnListener() {
					@Override
					public void onConnectSuccess() {
						Debug.Log("play enter");
						playRtmpClient.play();
						Debug.Log("play exit");
					}
				});
				playRtmpClient.connect(server, myStream);
			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				publishRtmpClient.setOnConnListener(new OnConnListener() {
					@Override
					public void onConnectSuccess() {
						Debug.Log("publish enter");
						playRtmpClient.play();
						Debug.Log("publish exit");
					}
				});
				publishRtmpClient.connect(server, toStream);
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
