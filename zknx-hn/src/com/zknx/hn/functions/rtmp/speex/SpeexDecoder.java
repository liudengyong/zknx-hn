package com.zknx.hn.functions.rtmp.speex;

import com.zknx.hn.common.Debug;

public class SpeexDecoder extends Speex implements Runnable, Speex.Decoder {

	private final static int BUFFER_SIZE = 56;
	private long ts;

	public SpeexDecoder(Consumer consumer) {
		super(consumer, BUFFER_SIZE);
	}

	@Override
	public void run() {
		super.run();

		int len = 0;
		while (this.isRunning()) {
			synchronized (mutex) {
				while (isIdle()) {
					try {
						mutex.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			synchronized (mutex) {
				len = decode(encodedData, decodedData, leftSize);
				Debug.Log("speex decoded length " + len);
				consumer.putDecodedData(ts, decodedData, len);								
				setIdle();
			}
		}
	}

	@Override
	public void putData(long ts, byte[] data, int size) {
		synchronized (mutex) {
			this.ts = ts;
			System.arraycopy(data, 0, decodedData, 0, size);
			this.leftSize = size;
			mutex.notify();
		}
	}
}
