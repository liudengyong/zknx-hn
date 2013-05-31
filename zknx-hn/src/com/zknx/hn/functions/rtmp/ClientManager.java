package com.zknx.hn.functions.rtmp;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.zknx.hn.functions.rtmp.RtmpClient.PlayStatusListener;
import com.zknx.hn.functions.rtmp.speex.Speex.Consumer;

public class ClientManager implements Runnable, Consumer, PlayStatusListener {
	private final Object mutex = new Object();
	// NONE = live
	public static final int NONE = 0;
	public static final int NETONLY = 1;
	public static final int FILEONLY = 2;
	public static final int NETANDFILE = 3;
	private int recordMode = NETONLY;
	//record or playback
	public static final int SING = 1;
	public static final int PLAY = 2;
	private int clientMode = SING;

	private int seq = 1;
	private int duration = 10 * 1000;
	private int start = 0;
	private volatile boolean isRunning;
	private volatile boolean isNeedExit;
	private EncodedData eData;
	private DecodedData dData;
	private List<EncodedData> encodeList;
	private List<DecodedData> decodeList;
	private String publishNameBase = "test";
	private String publishName;
	private String playName;
	@SuppressLint("SdCardPath")
	private String fileNameBase = "/mnt/sdcard/test";
	private String fileName;
	private PcmRecorder recorder = null;
	private RtmpClient rtmpClient;
	private FileClient fileClient;
	private AudioTrack audioTrack;

	public ClientManager() {
		super();
		
		rtmpClient = new RtmpClient();
		fileClient = new FileClient();
		
		encodeList = Collections
				.synchronizedList(new LinkedList<EncodedData>());
		decodeList = Collections
				.synchronizedList(new LinkedList<DecodedData>());
	}

	@Override
	public void run() {

		netClientInit();
		fileClientInit();

		while (!this.isNeedExit()) {
			synchronized (mutex) {
				while (!this.isRunning) {
					try {
						mutex.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			setupParams();
			if (this.clientMode == SING) {
				startSingClient();
				startPcmRecorder();
				while (this.isRunning()) {
					if (encodeList.size() > 0) {
						writeTag();
					} else {
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				startPlayClient();
				startAudioTrack();
				while (this.isRunning()) {
					if (decodeList.size() > 50) {
						playTag();
					} else {
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
			stop();
		}
	}

	private void setupParams() {
		if (this.clientMode == SING) {
			fileName = fileNameBase + seq + ".flv";
			publishName = publishNameBase + seq;
			playName = publishName + ".flv";
			start = 0;
			duration = -2;
			seq++;
		}
	}

	private void writeTag() {
		eData = encodeList.remove(0);
		if (this.recordMode == NETONLY || this.recordMode == NETANDFILE) {
			rtmpClient.writeTag(eData.processed, eData.size, eData.ts);
		}
		if (this.recordMode == FILEONLY || this.recordMode == NETANDFILE) {
			fileClient.writeTag(eData.processed, eData.size, eData.ts);
		}
	}

	private void playTag() {
		while (decodeList.size() > 0 && this.isRunning) {
			dData = decodeList.remove(0);
			audioTrack.write(dData.processed, 0, dData.size);
		}
	}
	
	private void startPlayClient() {
		rtmpClient.play(playName, start, duration, null);
	}

	private void startSingClient() {
		switch (this.recordMode) {
		case NONE:
			rtmpClient.publish(publishName, "live", null);
			break;
		case NETONLY:
			rtmpClient.publish(publishName, "record", null);
			break;
		case FILEONLY:
			fileClient.start(fileName);
			break;
		case NETANDFILE:
			rtmpClient.publish(publishName, "record", null);
			fileClient.start(fileName);
			break;
		default:
			rtmpClient.publish(publishName, "record", null);
		}
	}

	private void startPcmRecorder() {
		recorder = new PcmRecorder(this);
		recorder.setRunning(true);
		Thread th = new Thread(recorder);
		th.start();
	}

	private void startAudioTrack() {
		int bufferSizeInBytes = AudioTrack.getMinBufferSize(8000,
				AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
				AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
				2 * bufferSizeInBytes, AudioTrack.MODE_STREAM);
		audioTrack.play();
	}
	
	private void netClientInit() {
		rtmpClient.setHost("192.168.1.207");
		rtmpClient.setPort(1935);
		rtmpClient.setApp("live");
		rtmpClient.setChannle(1);
		rtmpClient.setSampleRate(8000);
		rtmpClient.setConsumer(this);
	}

	private void fileClientInit() {
		fileClient.setChannle(1);
		fileClient.setSampleRate(8000);
	}

	public void setRecordMode(int mode) {
		this.recordMode = mode;
	}

	public void setClientMode(int mode) {
		this.clientMode = mode;
	}

	private void stop() {
		rtmpClient.stop();
		if (this.clientMode == SING) {
			recorder.stop();
			fileClient.stop();
		} else {
			audioTrack.stop();
			audioTrack.release();
		}
	}

	public boolean isNeedExit() {
		synchronized (mutex) {
			return isNeedExit;
		}
	}

	public void setNeedExit(boolean isNeedExit) {
		synchronized (mutex) {
			this.isNeedExit = isNeedExit;
			if (this.isNeedExit) {
				mutex.notify();
			}
		}
	}

	@Override
	public void setRunning(boolean isRunning) {
		synchronized (mutex) {
			this.isRunning = isRunning;
			if (this.isRunning) {
				mutex.notify();
			}
		}
	}

	@Override
	public boolean isRunning() {
		synchronized (mutex) {
			return isRunning;
		}
	}

	@Override
	public void putDecodedData(long ts, short[] buf, int size) {
		DecodedData data = new DecodedData();
		//data.ts = ts;
		data.size = size;
		System.arraycopy(buf, 0, data.processed, 0, size);
		decodeList.add(data);
	}

	@Override
	public void putEncodedData(long ts, byte[] buf, int size) {
		EncodedData data = new EncodedData();
		data.ts = ts;
		data.size = size;
		System.arraycopy(buf, 0, data.processed, 0, size);
		encodeList.add(data);
	}

	class EncodedData {
		private long ts;
		private int size;
		private byte[] processed = new byte[256];
	}

	class DecodedData {
		//private long ts;
		private int size;
		private short[] processed = new short[256];
	}

	@Override
	public void OnPlayStatus(int status) {		
		if (status == RtmpClient.STOPPED){
			setRunning(false);
		}		
	}
}
