package com.zknx.hn.functions.rtmp;

public class Speex {

	/**
	 * 压缩质量
	 * 1  : 4kbps (very noticeable artifacts, usually intelligible) 	10
	 * 2  : 6kbps (very noticeable artifacts, good intelligibility) 	15
	 * 4  : 8kbps (noticeable artifacts sometimes) 						20
	 * 6  : 11kpbs (artifacts usually only noticeable with headphones) 	28
	 * 8  : 15kbps (artifacts not usually noticeable) 					38
	 * 9  : 15kbps (artifacts not usually noticeable) 					46
	 * 10 : 24.8kbps (artifacts not usually noticeable) 				62
	 */

	// 默认压缩质量 8kbps
	private final static int CODEC_QUALITY = 4;

	public Speex() {
		open(CODEC_QUALITY);
	}

	static {
		System.loadLibrary("speex");
	}

	/**
	 * 本地接口
	 * @param compression
	 * @return
	 */
	public native int open(int compression);
	public native int getFrameSize();
	public native int decode(byte encoded[], short lin[], int size);
	public native int encode(short lin[], int offset, byte encoded[], int size);
	public native void close();
}
