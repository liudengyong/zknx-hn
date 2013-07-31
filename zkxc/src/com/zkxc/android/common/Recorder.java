package com.zkxc.android.common;

import java.io.File;

import com.zkxc.android.R;
import com.zkxc.android.data.DataMan;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;

public class Recorder extends Activity {
	
	enum STATE {READY, RECORDING};
	
	STATE mState = STATE.READY;
	
	final int MIN_LENGH = 16; // bytes
	
	Button btnStart;
	Button btnStop;
	Button btnSave;
	
	MediaRecorder mediaRecorder;
	File mediaFile = null;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.recorder);
		
		findViewById(R.id.recorder_close).setOnClickListener(mClickListener);
		
		btnStart = (Button)findViewById(R.id.recorder_start);
		btnStop  = (Button)findViewById(R.id.recorder_stop);
		btnSave  = (Button)findViewById(R.id.recorder_save);
		
		btnStart.setOnClickListener(mClickListener);
		btnStop.setOnClickListener(mClickListener);
		btnSave.setOnClickListener(mClickListener);
		
		updateStatus();
		
		setTitle("录音：");
		setFileName(DataMan.GetDataFolder() + "xxx.2gpp");
	}
	
	void updateStatus()
	{
		if (mState == STATE.READY) {
			btnStart.setEnabled(true);
			btnStop.setEnabled(false);
		} else {
			btnStart.setEnabled(false);
			btnStop.setEnabled(true);
		}
		
		// 是否有足够长可以保存
		boolean shouldSave = false;
		if (mediaFile != null && mediaFile.exists()) {
			shouldSave = mediaFile.length() > MIN_LENGH;
		}
		
		btnSave.setEnabled(shouldSave);
	}
	
	OnClickListener mClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View view) {
			switch (view.getId())
			{
			case R.id.recorder_start:
				startRecording();
				break;
			case R.id.recorder_stop:
				stopRecording();
				break;
			case R.id.recorder_close:
				deleteMediaFile();
			case R.id.recorder_save:
				finish();
				break;
			}
		}
	};
	
	boolean deleteMediaFile()
	{
		if (mediaFile != null && mediaFile.exists())
			return mediaFile.delete();
		
		return true;
	}
	
	void setTitle(String title)
	{
		if (findViewById(R.id.recorder_title) != null)
		{
			TextView tv = (TextView)findViewById(R.id.recorder_title);
			tv.setText(title);
		}
	}
	
	void setFileName(String filename)
	{
		mediaFile = null;
		
		if (findViewById(R.id.recorder_filename) != null)
		{
			TextView tv = (TextView)findViewById(R.id.recorder_filename);
			tv.setText(filename);
			
			String token[] = DataMan.ParsePath(filename);
			
			if (token != null && token.length == 2)
			{
				String path = token[0];
				String name = token[1];
				
				File pathFile = new File(path);
				if (pathFile.exists() || pathFile.mkdirs()){
					mediaFile = new File(path, name);
				}
			}
		}
	}
	
	/**
	 * This method starts recording process
	 */
	private void startRecording() {
		if (mState != STATE.RECORDING)
		{
			try {
				if (mediaRecorder == null) {
					mediaRecorder = new MediaRecorder();
					mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
					mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
					mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
				}
				
				if (mediaFile != null)
				{
					// 开录时覆盖原来的文件
					if (mediaFile.exists())
						mediaFile.delete();
					
					mediaRecorder.setOutputFile(mediaFile.getAbsolutePath());
					mediaRecorder.prepare();
					mediaRecorder.start();
					
					mState = STATE.RECORDING;
				}
			}
		    catch (Exception exp) {
				exp.printStackTrace();
			}
		}
		
		updateStatus();
	}
	
	/**
	 * This method stops recording
	 */
	private void stopRecording() {
		if (mediaRecorder != null) {
			mediaRecorder.stop();
			mediaRecorder.release();
			mediaRecorder = null;
		}
		
		mState = STATE.READY;
		updateStatus();
	}
}
