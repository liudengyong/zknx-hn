package com.zkxc.android.map;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import com.zkxc.android.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WriterActivity extends Activity implements Setting {
	private EditText editor;
	private Button ok;
	private Button cancel;
	private TextView inputLabel;
	private int inputLabelDefaultColor;
	private ImageView imageView;
	private ImageButton videoView;
	private ImageButton soundView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.writer);

		editor = (EditText) findViewById(R.id.contentEditor);
		ok = (Button) findViewById(R.id.okButton);
		cancel = (Button) findViewById(R.id.cancelButton);
		imageView = (ImageView) findViewById(R.id.photoPreview);
		inputLabel = (TextView) findViewById(R.id.inputCountLabel);
		inputLabelDefaultColor = inputLabel.getTextColors().getDefaultColor();
		setInputLabel(editor.length());
		videoView = (ImageButton) findViewById(R.id.writer_removeVideo);
		soundView = (ImageButton) findViewById(R.id.writer_removeSound);
		videoView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DIALOG_DELETE_VIDEO);
			}
		});
		soundView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DIALOG_DELETE_SOUND);
			}
		});
		

		final File photoDir = Service.getImageDir();
		final File videoDir = Service.getVideoDir();
		final File soundDir = Service.getVoiceDir();
		photoTmpFile = new File(photoDir.getAbsolutePath()+"/tmp.tmp");
//		videoTmpFile = new File(videoDir.getAbsolutePath()+"/tmp.tmp");
//		soundTmpFile = new File(soundDir.getAbsolutePath()+"/tmp.tmp");
		editor.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,int count) {

			}

			public void beforeTextChanged(CharSequence s, int start, int count,int after) {

			}

			public void afterTextChanged(Editable s) {
				setInputLabel(s.length());
			}
		});

		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				WriterActivity.this.onBackPressed();
			}
		});

		// 确定：保存数据
		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				double lat = WriterActivity.this.getIntent().getDoubleExtra(
						"lat", 0);
				double lng = WriterActivity.this.getIntent().getDoubleExtra(
						"lng", 0);
				long time = System.currentTimeMillis();
				String filename = FILE_DATE_FORMAT.format(new Date(time));
				try {
					ContentFile contentFile = ContentFile.createContentFile(lat, lng, new Date(time), editor.getText().toString(), false);
					contentFile.save();
					editor.setText("");
					setInputLabel(0);
					if(photo!=null){
					    File photoFile = new File(photoDir.getAbsolutePath() + "/" + filename+".jpg");
						ByteArrayOutputStream baos = null;  
					    baos = new ByteArrayOutputStream();  
					    photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
					    byte[] byteArray = baos.toByteArray();
					    FileOutputStream fos = new FileOutputStream(photoFile);
					    fos.write(byteArray);
					    photo = null;
					    rotate = 0;
					}
					if(videoView.getVisibility()==View.VISIBLE){
					    String ext = videoTmpFile.getName().substring(videoTmpFile.getName().lastIndexOf("."));
					    File newFile = new File(videoDir.getAbsolutePath() + "/" + filename+ext);
					    videoTmpFile.renameTo(newFile);
						videoView.setVisibility(View.GONE);
					}
					if(soundView.getVisibility()==View.VISIBLE){
					    String ext = soundTmpFile.getName().substring(soundTmpFile.getName().lastIndexOf("."));
					    File newFile = new File(soundDir.getAbsolutePath() + "/" + filename+ext);
					    soundTmpFile.renameTo(newFile);
						soundView.setVisibility(View.GONE);
					}
				} catch (IOException e) {
					Log.e("ERROR", e.getMessage(), e);
					e.printStackTrace();
				}
				WriterActivity.this.onBackPressed();
			}
		});

		//拍照按钮
		((Button) findViewById(R.id.writer_photoBtn))
				.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoTmpFile));
						startActivityForResult(intent, PHOTO_REQUEST_CODE);
					}
				});
		
		//点击照片：旋转照片
		imageView.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				if(photo!=null){
					Matrix matrix = new Matrix(); 
					matrix.postRotate(90);
					rotate = (rotate+90)%360;
					photo = Bitmap.createBitmap(photo, 0, 0,photo.getWidth(), photo.getHeight(),  matrix, true);
					imageView.setImageBitmap(photo);
				}
			}
		});
		//长按照片：删除
		imageView.setOnLongClickListener(new View.OnLongClickListener() {
			
			public boolean onLongClick(View v) {
				showDialog(DIALOG_DELETE_PHOTO);
				return false;
			}
		});
		
		//录象按钮
		((Button) findViewById(R.id.writer_videoBtn))
				.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
						startActivityForResult(intent, VIDEO_REQUEST_CODE);
					}
				});

		//录音按钮
		((Button) findViewById(R.id.writer_soundBtn))
				.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
						intent.setType("audio/amr");
						startActivityForResult(intent, SOUND_REQUEST_CODE);
					}
				});
	}
	private int rotate = 0;
	private File photoTmpFile;
	private File videoTmpFile;
	private File soundTmpFile;
	
	private static final int DIALOG_DELETE_PHOTO = 1;
	private static final int DIALOG_DELETE_VIDEO = 2;
	private static final int DIALOG_DELETE_SOUND = 3;
	/**
	 * 删除照片
	 */
	@Override
	protected Dialog onCreateDialog(int id)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		switch (id) {
		case DIALOG_DELETE_PHOTO :
			builder.setMessage("删除照片？")
			.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					photo = null;
					rotate = 0;
					imageView.setVisibility(View.GONE);
				}
			});
			break;

		case DIALOG_DELETE_VIDEO :
			builder.setMessage("删除视频？")
			.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					videoView.setVisibility(View.GONE);
				}
			});
			break;
		case DIALOG_DELETE_SOUND :
			builder.setMessage("删除录音？")
			.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					soundView.setVisibility(View.GONE);
				}
			});
			break;
		default:
			break;
		}
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		return alert;
	}
	
	private Bitmap photo;

	/**
	 * 拍照
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if(resultCode==0){//没有拍照
			return;
		}
		Cursor cursor= null;
		switch (requestCode) {
		case PHOTO_REQUEST_CODE:
			photo = BitmapFactory.decodeFile(photoTmpFile.getAbsolutePath());
			Matrix matrix = new Matrix();
			int w = photo.getWidth();
			int h = photo.getHeight();
			if(w>1024||h>1024){//限制图片只存
				float scale = 1024f/Math.max(w, h);
				matrix.postScale(scale, scale);
				Bitmap preview = Bitmap.createBitmap(photo, 0, 0,photo.getWidth(), photo.getHeight(),  matrix, true);
				photo.recycle();
				photo = preview;
			}
			imageView.setVisibility(View.VISIBLE);
			imageView.setImageBitmap(photo);
			Log.d("test", imageView.getHeight()+".");
			break;
		case VIDEO_REQUEST_CODE:
            Uri uriVideo = data.getData();
            cursor = this.getContentResolver().query(uriVideo, null, null, null, null);
            if (cursor.moveToNext()) {
	            /** _data：文件的绝对路径 ，_display_name：文件名 */
	            String strVideoPath = cursor.getString(cursor.getColumnIndex("_data"));
//	            Toast.makeText(this, strVideoPath, Toast.LENGTH_SHORT).show();
	            videoTmpFile = new File(strVideoPath);
	            videoView.setVisibility(View.VISIBLE);
            }

			break;
		case SOUND_REQUEST_CODE:
            Uri uriRecorder = data.getData();
            cursor = this.getContentResolver().query(uriRecorder, null, null, null, null);
            if (cursor.moveToNext()) {
                /** _data：文件的绝对路径 ，_display_name：文件名 */
                String strSoundPath = cursor.getString(cursor.getColumnIndex("_data"));
                Toast.makeText(this, strSoundPath, Toast.LENGTH_SHORT).show();
                
	            soundTmpFile = new File(strSoundPath);
	            soundView.setVisibility(View.VISIBLE);
            }

			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * 屏幕旋转
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if(photo!=null){
			imageView.setImageBitmap(photo);
		}
	}

	
	private static final int PHOTO_REQUEST_CODE = 1;
	private static final int VIDEO_REQUEST_CODE = 2;
	private static final int SOUND_REQUEST_CODE = 3;

	private void setInputLabel(int length) {
		if (length <= TEXT_LIMIT) {
			inputLabel.setText(getResources().getString(R.string.youCanInput)
					.replace("%n", Integer.toString(TEXT_LIMIT - length)));
			inputLabel.setTextColor(inputLabelDefaultColor);
			ok.setEnabled(length != 0);
		} else {
			inputLabel.setText(getResources()
					.getString(R.string.youCanNotInput).replace("%n",
							Integer.toString(length - TEXT_LIMIT)));
			inputLabel.setTextColor(0xFFFF0000);
			ok.setEnabled(false);
		}
	}
}
