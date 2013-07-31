package com.zkxc.android.map;

import java.io.File;
import java.io.IOException;

import com.zkxc.android.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditActivity extends Activity implements Setting {
	private EditText editor;
	private Button ok;
	private Button cancel;
	private TextView inputLabel;
	private int inputLabelDefaultColor;
	private Button imageBtn; 
	private Button videoBtn;
	private Button voiceBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit);
		final ContentFile contentFile = (ContentFile) getIntent().getSerializableExtra("contentFile");
		editor = (EditText) findViewById(R.id.edit_editor);
		ok = (Button) findViewById(R.id.edit_ok);
		cancel = (Button) findViewById(R.id.edit_cancel);
		inputLabel = (TextView) findViewById(R.id.edit_inputCountLabel);
		inputLabelDefaultColor = inputLabel.getTextColors().getDefaultColor();

		editor.setText(contentFile.getContent());
		setInputLabel(editor.length());

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
				EditActivity.this.finish();
			}
		});
		

		// 确定：保存数据
		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String content = editor.getText().toString();
				if(!contentFile.getContent().equals(content)||change){

					String filename = FILE_DATE_FORMAT.format(contentFile.getAddTime());
					if(delImage||newImage!=null){//删除
						File f = contentFile.getImageFile();
						if(f!=null){
							f.delete();
						}
					}
					Log.d("TEST", newImage!=null?"true":"false");
					if (newImage!=null) {//添加或替换
						String ext = newImage.getName().substring(newImage.getName().lastIndexOf('.'));
						File file = new File(Service.getImageDir().getAbsoluteFile()+"/"+filename+ext);
						if(file.exists()){
							file.delete();
						}
						Log.d("TEST", newImage.getAbsolutePath());
						Log.d("TEST", file.getAbsolutePath());
						
						try {
							FileUtils.copyFile(newImage, file);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					if(delVideo||newVideo!=null){//删除
						File f = contentFile.getVideoFile();
						if(f!=null){
							f.delete();
						}
					}
					if (newVideo!=null) {//添加或替换
						String ext = newVideo.getName().substring(newVideo.getName().lastIndexOf('.'));
						File file = new File(Service.getVideoDir().getAbsoluteFile()+"/"+filename+ext);
						if(file.exists()){
							file.delete();
						}
						try {
							FileUtils.copyFile(newVideo, file);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					if(delVoice||newVoice!=null){//删除
						File f = contentFile.getVoiceFile();
						if(f!=null){
							f.delete();
						}
					}
					if (newVoice!=null) {//添加或替换
						String ext = newVoice.getName().substring(newVoice.getName().lastIndexOf('.'));
						File file = new File(Service.getVoiceDir().getAbsoluteFile()+"/"+filename+ext);
						if(file.exists()){
							file.delete();
						}
						try {
							FileUtils.copyFile(newVoice, file);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					contentFile.getFile().delete();
					ContentFile newContentFile = ContentFile.createContentFile(contentFile.getLat(), contentFile.getLng(), contentFile.getAddTime(), content, false);
					newContentFile.save();
					Intent intent = EditActivity.this.getIntent();
					intent.putExtra("contentFile", newContentFile);
					EditActivity.this.setResult(RESULT_OK,intent);
					EditActivity.this.finish();
				}
				else{
					EditActivity.this.finish();
				}
			}
		});
		
		//图片
		imageBtn = (Button)findViewById(R.id.edit_image);
		imageBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				startActivityForResult(intent, IMAGE_REQUEST_CODE);
			}
		});
		imageBtn.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				if(contentFile.getImageFile()!=null){
					showDialog(DIALOG_DELETE_IMAGE);
				}
				return false;
			}
		});
		if (contentFile.getImageFile()!=null) {
			imageBtn.setText("更换"+getString(R.string.image));
		}
		else {
			imageBtn.setText("添加"+getString(R.string.image));
		}
		
		//视频
		videoBtn = (Button)findViewById(R.id.edit_video);
		videoBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("video/*");
				startActivityForResult(intent, VIDEO_REQUEST_CODE);
			}
		});
		videoBtn.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				if(contentFile.getVideoFile()!=null){
					showDialog(DIALOG_DELETE_VIDEO);
				}
				return false;
			}
		});
		if (contentFile.getVideoFile()!=null) {
			videoBtn.setText("更换"+getString(R.string.video));
		}
		else {
			videoBtn.setText("添加"+getString(R.string.video));
		}
		
		//音频
		voiceBtn = (Button)findViewById(R.id.edit_voice);
		voiceBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("audio/amr");
				startActivityForResult(intent, VOICE_REQUEST_CODE);
			}
		});
		voiceBtn.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				if(contentFile.getVoiceFile()!=null){
					showDialog(DIALOG_DELETE_VOICE);
				}
				return false;
			}
		});
		if (contentFile.getVoiceFile()!=null) {
			voiceBtn.setText("更换"+getString(R.string.sound));
		}
		else {
			voiceBtn.setText("添加"+getString(R.string.sound));
		}
	}
	
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
	

	private static final int DIALOG_DELETE_IMAGE = 1;
	private static final int DIALOG_DELETE_VIDEO = 2;
	private static final int DIALOG_DELETE_VOICE = 3;

	private static final int IMAGE_REQUEST_CODE = 1;
	private static final int VIDEO_REQUEST_CODE = 2;
	private static final int VOICE_REQUEST_CODE = 3;
	
	private boolean change = false;
	private boolean delImage = false;
	private boolean delVideo = false;
	private boolean delVoice = false;
	private File newImage = null;
	private File newVideo = null;
	private File newVoice = null;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if(resultCode==0){//没有拍照
			return;
		}
		Cursor cursor= null;

        Uri uriRecorder = data.getData();
        cursor = this.getContentResolver().query(uriRecorder, null, null, null, null);
        String filePath = null;
        if (cursor.moveToNext()) {
            /** _data：文件的绝对路径 ，_display_name：文件名 */
            filePath = cursor.getString(cursor.getColumnIndex("_data"));
            Toast.makeText(this, filePath, Toast.LENGTH_SHORT).show();
			switch (requestCode) {
				case IMAGE_REQUEST_CODE:
					newImage = new File(filePath);
					imageBtn.setText("更换"+getString(R.string.image));
					change = true;
					break;
				case VIDEO_REQUEST_CODE:
					newVideo = new File(filePath);
					videoBtn.setText("更换"+getString(R.string.video));
					change = true;
					break;
				case VOICE_REQUEST_CODE:
					newVoice = new File(filePath);
					voiceBtn.setText("更换"+getString(R.string.sound));
					change = true;
					break;
			}
        }
        else{
            Toast.makeText(this, "未获取到", Toast.LENGTH_SHORT).show();
        }
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected Dialog onCreateDialog(int id)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		switch (id) {
		case DIALOG_DELETE_IMAGE :
			builder.setMessage("删除照片？")
			.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					imageBtn.setText("添加"+getString(R.string.image));
					change = delImage = true;
				}
			});
			break;

		case DIALOG_DELETE_VIDEO :
			builder.setMessage("删除视频？")
			.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					videoBtn.setText("添加"+getString(R.string.video));
					change = delVideo = true;
				}
			});
			break;
		case DIALOG_DELETE_VOICE :
			builder.setMessage("删除录音？")
			.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					voiceBtn.setText("添加"+getString(R.string.sound));
					change = delVoice = true;
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
	
}
