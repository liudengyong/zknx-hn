package com.zknx.hn.functions.rtmp;

import java.util.Vector;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

import com.smaxe.io.ByteArray;
import com.smaxe.uv.client.microphone.AbstractMicrophone;
import com.smaxe.uv.stream.support.MediaDataByteArray;
import com.zknx.hn.common.Debug;

public class AudioCenter extends AbstractMicrophone {
	
	private final static String subTAG = "AudioCenter";
	
	private volatile boolean isPublish, isPlaying;
	private Vector<byte[]> bufferData = new Vector<byte[]>();

	/**
	 * 放置数据到缓冲区
	 * @param data
	 */
	public void putData(byte[] data) {
		bufferData.add(data);
	}

	/**
	 * 在一个线程中录音并推送
	 */
	public void publishSpeexAudio() {
		new Thread(new Runnable() {
			
			private final byte[] SpeexRtmpHead = new byte[] { (byte) 0xB2 };
			private Speex publishSpeex = new Speex();
			private byte[] processedData;
			
			@Override
			public void run() {
				int frameSize = publishSpeex.getFrameSize();
				processedData = new byte[frameSize];
				int bufferSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
				short[] mAudioRecordBuffer = new short[bufferSize];
				AudioRecord mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
				mAudioRecord.startRecording();
				int bufferRead = 0;
				int len;
				isPublish = true;

				while (isPublish) {
					bufferRead = mAudioRecord.read(mAudioRecordBuffer, 0, frameSize);
					if (bufferRead > 0) {
						try {
							len = publishSpeex.encode(mAudioRecordBuffer, 0, processedData, frameSize);
							byte[] speexData = new byte[len + 1];
							System.arraycopy(SpeexRtmpHead, 0, speexData, 0, 1);
							System.arraycopy(processedData, 0, speexData, 1, len);
							fireOnAudioData(new MediaDataByteArray(20, new ByteArray(speexData)));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				mAudioRecord.stop();
				mAudioRecord.release();
				mAudioRecord = null;
				publishSpeex.close();

				Debug.Log("Publish SpeexAudio Thread Release", subTAG);
			}
		}, "Publish SpeexAudio Thread").start();
	}

	/**
	 * 在一个线程中从buffer2获取数据，解码并播放
	 */
	public void playSpeexAudio() {
		new Thread(new Runnable() {
			
			private short[] decData = new short[256];
			private Speex playSpeex = new Speex();
			
			@Override
			public void run() {
				
				int bufferSizeInBytes = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
				AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, 8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, 2 * bufferSizeInBytes, AudioTrack.MODE_STREAM);
				audioTrack.play();
				isPlaying = true;
				
				while (isPlaying) {
					while (!bufferData.isEmpty()) {
						byte[] data = bufferData.elementAt(0);
						bufferData.remove(0);
						if (data != null) {
							// 解码
							int dec = playSpeex.decode(data, decData, data.length);
							// 播放
							if (dec > 0) {
								audioTrack.write(decData, 0, dec);
							}
						}
					}
					// 延时以缓解系统压力 时间需调试
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				audioTrack.stop();
				audioTrack.release();
				audioTrack = null;
				playSpeex.close();
				bufferData.clear();

				Debug.Log("Play SpeexAudio Thread Release", subTAG);
			}
		}, "Play SpeexAudio Thread").start();
	}

	/**
	 * 停止播放
	 */
	public void stopPlay() {
		isPlaying = false;
	}

	/**
	 * 停止录音
	 */
	public void stopPublish() {
		isPublish = false;
	}

	/**
	 * 停止录音盒播放
	 */
	public void closeAll() {
		isPlaying = false;
		isPublish = false;
	}
}
