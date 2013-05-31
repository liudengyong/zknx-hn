package com.zknx.hn.functions.rtmp.speex;

import com.zknx.hn.common.Debug;
import com.zknx.hn.functions.rtmp.speex.Speex.Encoder;

public class SpeexEncoder extends Speex implements Runnable, Encoder {
	
	private final static int BUFFER_SIZE = 1024;
	private int frameSize;

	public SpeexEncoder(Consumer consumer) {
		super(consumer, BUFFER_SIZE);
		frameSize = getFrameSize();
		Debug.Log("frameSize :" + frameSize);
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
				short[] temp = new short[frameSize];
				for (int i = 0; i < 4; i++) {
					System.arraycopy(decodedData, i * frameSize, temp, 0, frameSize);
					len = encode(temp, 0, encodedData, frameSize);
					Debug.Log("speex encoded size " + len);
					
					if (len > 0) {
						consumer.putEncodedData(System.currentTimeMillis(), encodedData, len);
					}
				}
			}
			setIdle();
		}
	}

	@Override
	public void putData(short[] data, int size) {
		synchronized (mutex) {
			System.arraycopy(data, 0, decodedData, 0, size);
			this.leftSize = size;
			mutex.notify();
		}
	}
}
