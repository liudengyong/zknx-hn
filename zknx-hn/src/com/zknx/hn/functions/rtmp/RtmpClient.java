package com.zknx.hn.functions.rtmp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.smaxe.uv.client.INetConnection;
import com.smaxe.uv.client.INetStream;
import com.smaxe.uv.stream.MediaData;
import com.zknx.hn.common.Debug;
import com.zknx.hn.functions.rtmp.RtmpMediaData.OnMediaDataListener;

public class RtmpClient {

	private final static String subTAG = "RtmpClient";
	
	// TODO 破解
	/*
	static {
		License.setKey("5719B-F1E0E-023C7-7E600-48689");
	}
	*/

	private String SERVER_URL;
	private String STREAM = "";
	private UltraNetConnection connection = null;
	private UltraNetStream netStream = null;
	private AudioCenter audioCenter = new AudioCenter();
	private OnConnListener mConnListener;
	
	// Rtmp数据处理
	private RtmpMediaData rtmpMediaData = new RtmpMediaData(new OnMediaDataListener() {
		@Override
		public void onAudioData(MediaData mediaData) {
			try {
				InputStream is = mediaData.read();
				byte[] audioData = new byte[is.available()];
				int re = is.read(audioData);
				if (re != 0) {
					byte[] realAudioData = new byte[re - 1];
					System.arraycopy(audioData, 1, realAudioData, 0, re - 1);
					audioCenter.putData(realAudioData);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	});

	public RtmpClient() {
	}

	/**
	 * 连接开始会话
	 * @param SERVER_URL
	 * @param STREAM
	 */
	public void connect(String SERVER_URL, String STREAM) {
		this.SERVER_URL = SERVER_URL;
		this.STREAM = STREAM;
		connection = new UltraNetConnection();
		connection.addEventListener(new NetConnectionListener());
		connection.connect(this.SERVER_URL);
	}

	private class NetConnectionListener extends UltraNetConnection.ListenerAdapter {
		@Override
		public void onAsyncError(INetConnection arg0, String arg1, Exception arg2) {
			super.onAsyncError(arg0, arg1, arg2);
		}

		@Override
		public void onIOError(INetConnection arg0, String arg1) {
			super.onIOError(arg0, arg1);
		}

		@Override
		public void onNetStatus(INetConnection source, Map<String, Object> info) {
			Debug.Log("UltraNetConnection#onNetStatus: " + info, subTAG);
			String result = info.get("code").toString();
			if (UltraNetConnection.CONNECT_SUCCESS.equals(result)) {
				Debug.Log( "UltraNetConnection#onNetStatus: connection success.",subTAG);
				netStream = new UltraNetStream(source);
				netStream.addEventListener(new NetStreamListener());
				if (mConnListener != null) {
					mConnListener.onConnectSuccess();
				}
			} else if (UltraNetConnection.CONNECT_FAILED.equals(result)) {
				Debug.Log( "UltraNetConnection#onNetStatus: connection fail.",subTAG);
			} 
		}

		private class NetStreamListener extends UltraNetStream.ListenerAdapter {
			@Override
			public void onNetStatus(INetStream netStream, Map<String, Object> info) {
				String code = info.get("code").toString();
				if (UltraNetStream.PUBLISH_START.equals(code)) {
					Debug.Log( "UltraNetStream#onNetStatus: PUBLISH_START",subTAG);
				} else if (UltraNetStream.RECORD_START.equals(code)) {
					Debug.Log( "UltraNetStream#onNetStatus: RECORD_START",subTAG);
				} else if (UltraNetStream.RECORD_STOP.equals(code)) {
					Debug.Log( "UltraNetStream#onNetStatus: RECORD_STOP",subTAG);
				} else if (UltraNetStream.PLAY_START.equals(code)) {
					Debug.Log( "UltraNetStream#onNetStatus: PLAY_START",subTAG);
				} else if (UltraNetStream.PLAY_COMPLETE.equals(code)) {
					Debug.Log( "UltraNetStream#onNetStatus: PLAY_COMPLETE",subTAG);
				} else if (UltraNetStream.PLAY_PUBLISH_NOTIFY.equals(code)) {
					Debug.Log( "UltraNetStream#onNetStatus: PLAY_PUBLISH_NOTIFY",subTAG);
				} else if (UltraNetStream.PLAY_UNPUBLISH_NOTIFY.equals(code)) {
					Debug.Log( "UltraNetStream#onNetStatus: PLAY_UNPUBLISH_NOTIFY",subTAG);
				} else if (UltraNetStream.PLAY_STOP.equals(code)) {
					Debug.Log( "UltraNetStream#onNetStatus: PLAY_STOP",subTAG);
				}
			}
		}
	}

	/**
	 * 播放对方声音
	 */
	public void play() {
		if (connection.connected()) {
			audioCenter.playSpeexAudio();
			netStream.play(rtmpMediaData, STREAM);
		}
	}

	/**
	 * 录音并推送给对方
	 */
	public void publish() {
		if (connection.connected()) {
			netStream.attachAudio(audioCenter);
			audioCenter.publishSpeexAudio();
			netStream.publish(STREAM, UltraNetStream.LIVE);
		}
	}

	/**
	 * 连接断开
	 */
	public void disConnect() {
		if (connection.connected()) {
			connection.close();
			audioCenter.closeAll();
		}
	}

	/**
	 * 监听连接成功
	 * @param onConnListener
	 */
	public void setOnConnListener(OnConnListener onConnListener) {
		this.mConnListener = onConnListener;
	}

	public interface OnConnListener{
		public void onConnectSuccess();
	}
}
