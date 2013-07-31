package com.zkxc.android.map;

import java.io.File;

import com.zkxc.android.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ReaderActivity extends Activity implements Setting {

	private ContentFile contentFile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reader);
		contentFile = (ContentFile) getIntent().getSerializableExtra("contentFile");
		
		Button editBtn = (Button) findViewById(R.id.reader_edit);
		editBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(ReaderActivity.this,EditActivity.class);
				intent.putExtra("contentFile", contentFile);
				startActivityForResult(intent,1);
				
			}

		});
		refreshContent();

	}
	
	private void refreshContent(){

		((TextView) findViewById(R.id.reader_time)).setText(DISPLAY_DATE_FORMAT
				.format(contentFile.getAddTime()));
		((TextView) findViewById(R.id.reader_content)).setText(contentFile
				.getContent());

		// ͼƬ
		ImageView image = (ImageView) findViewById(R.id.reader_image);
		File imageDir = Service.getImageDir();
		final String filename = FILE_DATE_FORMAT.format(contentFile.getAddTime());
		File imageFile = new File(imageDir.getAbsolutePath() + "/" + filename
				+ ".jpg");
		if (imageFile.exists()) {
			Bitmap bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
			image.setImageBitmap(bmp);
			image.setVisibility(View.VISIBLE);
		} else {
			image.setVisibility(View.GONE);
		}

		MyFilenameFilter myFilenameFilter = new MyFilenameFilter(filename);
		File[] videoList = Service.getVideoDir().listFiles(myFilenameFilter);
		Button videoBtn = (Button)findViewById(R.id.reader_video);
		if (videoList.length > 0) {
			final File videoFile = videoList[0];
			videoBtn.setVisibility(View.VISIBLE);
			videoBtn.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					Uri name = Uri.fromFile(videoFile);
					String ext = videoFile.getName().substring(filename.length()+1);
					intent.setDataAndType(name, MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext));
					startActivity(intent);
				}
			});
		}
		else{
			videoBtn.setVisibility(View.GONE);
		}
		File[] soundList = Service.getVoiceDir().listFiles(myFilenameFilter);
		Button soundBtn = (Button)findViewById(R.id.reader_sound);
		if (soundList.length > 0) {
			final File soundFile = soundList[0];
			soundBtn.setVisibility(View.VISIBLE);
			soundBtn.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					Uri name = Uri.fromFile(soundFile);
					String ext = soundFile.getName().substring(filename.length()+1);
					intent.setDataAndType(name, MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext));
					startActivity(intent);
				}
			});
		}
		else{
			soundBtn.setVisibility(View.GONE);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==RESULT_OK&&requestCode==1){
			this.contentFile = (ContentFile)data.getSerializableExtra("contentFile");
			refreshContent();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
