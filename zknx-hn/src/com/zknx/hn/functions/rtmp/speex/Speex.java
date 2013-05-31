package com.zknx.hn.functions.rtmp.speex;

import com.zknx.hn.common.Debug;

public class Speex {
	
	private interface Status {
		public void setRunning(boolean isRunning);
		public boolean isRunning();
	}
	
	private interface CodecStatus extends Status {
		public boolean isIdle();
		public void setIdle();
	}

	public interface Decoder extends CodecStatus {
		public void putData(long ts, byte[] buf, int size);
	}

	public interface Encoder extends CodecStatus {
		public void putData(short[] data, int size);
	}
	
	public interface Consumer extends Status {
		public void putEncodedData(long ts, byte[] buf, int size);
		public void putDecodedData(long ts, short[] processedData, int getSize);
	}
	
	/**
	 * ��ȡ������
	 * @param consumer
	 * @return
	 */
	public static Decoder GetSpeexDecoder(Consumer consumer) {
		return new SpeexDecoder(consumer);
	}
	
	/**
	 * ��ȡ������
	 * @param consumer
	 * @return
	 */
	public static Encoder GetSpeexEncoder(Consumer consumer) {
		return new SpeexEncoder(consumer);
	}
	
	/* quality
	 * 1 : 4kbps (very noticeable artifacts, usually intelligible)
	 * 2 : 6kbps (very noticeable artifacts, good intelligibility)
	 * 4 : 8kbps (noticeable artifacts sometimes)
	 * 6 : 11kpbs (artifacts usually only noticeable with headphones)
	 * 8 : 15kbps (artifacts not usually noticeable)
	 */
	private static final int DEFAULT_COMPRESSION = 8;
	
	protected volatile int leftSize = 0;
	protected final Object mutex = new Object();
	protected volatile boolean isRunning;
	
	protected Consumer consumer;

	protected  byte[] encodedData; // processedData for encoder, rawdata for decoder
	protected short[] decodedData; // processedData for decoder, rawdata for encoder

	protected Speex(Consumer consumer, int buffSize) {
		load();	
		open(DEFAULT_COMPRESSION);
		
		// ����������
		this.consumer = consumer;
		
		// ��ʼ��buffer
		encodedData = new byte[buffSize];
		decodedData = new short[buffSize];

		Debug.Log("speex opened");
	}
	
	private void load() {
		try {
			System.loadLibrary("speex");
		} catch (Throwable e) {
			Debug.Log("speex load failed");	
		}

	}

	// ���ؽӿ�
	public native int open(int compression);
	public native int getFrameSize();
	public native int decode(byte encoded[], short lin[], int size);
	public native int encode(short lin[], int offset, byte encoded[], int size);
	public native void close();
	
	/*
	 * ����
	 */
	public void run() {
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
	}
	/**
	 * ���ÿ���״̬
	 */
	public void setIdle() {
		leftSize = 0;
	}
	
	/**
	 * �Ƿ����״̬
	 * @return
	 */
	public boolean isIdle() {
		return (leftSize == 0);
	}
	
	/**
	 * ��������״̬
	 */
	public void setRunning(boolean isRunning) {
		synchronized (mutex) {
			this.isRunning = isRunning;
			if (this.isRunning) {
				mutex.notify();
			}
		}
	}

	/**
	 * �Ƿ�����״̬
	 * @return
	 */
	public boolean isRunning() {
		synchronized (mutex) {
			return isRunning;
		}
	}
}
