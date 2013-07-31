package com.zkxc.android.table.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zkxc.android.act.ActTable.Request;
import com.zkxc.android.act.AppZkxc;
import com.zkxc.android.data.DataMan;
import com.zkxc.android.table.FileSelector;
import com.zkxc.android.table.Input.Type;
import com.zkxc.android.R;

public class MediaPicker {
	
	public interface MediaListener {
		void startMediaActivity(FileSelector fileSelector, Intent intent, int requestCode, MediaPicker mediaPicker);
	}
	
	public int MAX_MEDIA_SIZE = 5; // 最大5MB

	private Context mContext;
	private MediaListener mMediaListener;
  
    private String strFilePath      = "";
    private String strFileName      = "";
    
    Button mBtnSelect;
    Button mBtnAction;
	View mView;
	Type mType;
	
	TextView mMediaName;
	ImageView mMediaImg;
	
	String mTime;
	
	FileSelector mFileSelector;
	
	public MediaPicker(Context context, MediaListener mediaListener, Type type, Object record)
	{
		mContext = context;
		mMediaListener = mediaListener;

		mTime = DataMan.GetCurDate();
		strFilePath = GetMediaDir();
		
		mFileSelector = new FileSelector();

		initView(type);
		
		boolean bSynced = AppZkxc.mSyncedRecord;
		parseRecord(record, bSynced);
	}
	
	void initView(Type type)
	{
		mView = LayoutInflater.from(mContext).inflate(R.layout.media_picker, null);
		mBtnSelect = (Button)mView.findViewById(R.id.btn_media_select);
		mBtnAction = (Button)mView.findViewById(R.id.btn_media_action);
		mMediaImg = (ImageView)mView.findViewById(R.id.media_img);
		
		mMediaImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openFile();
			}
		});

		mMediaName = (TextView)mView.findViewById(R.id.media_name);
		mMediaName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openFile();
			}
		});
		
		mType = type;
		
		setName("空");

		mBtnSelect.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if (!checkSDCard())
				{
					Toast.makeText(mContext, R.string.no_sdcard, Toast.LENGTH_SHORT).show();
					return;
				}
				
				selectMediaFile();
			}
		});
		
		switch (mType)
		{
		case PICTURE:
			mBtnAction.setText("拍照");
			break;
		case VIDEO:
			mBtnAction.setText("录像");
			break;
		case AUDIO:
			mBtnAction.setText("录音");
			break;
		}
		
		mBtnAction.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if (!checkSDCard())
				{
					Toast.makeText(mContext, R.string.no_sdcard, Toast.LENGTH_SHORT).show();
					return;
				}
				
				switch (mType)
				{
				case PICTURE:
					cameraMethod();
					break;
				case VIDEO:
					videoMethod();
					break;
				case AUDIO:
					soundRecorderMethod();
					break;
				}
			}
		});
		
		mView.setTag(R.id.tag_view, this);
		
		// 同步后的数据不能更改
		if (AppZkxc.mSyncedRecord)
		{
			mBtnAction.setEnabled(false);
			mBtnSelect.setEnabled(false);
		}
	}
	
	void setName(String name)
	{
		if (mType == Type.AUDIO)
		{
			mMediaImg.setVisibility(View.GONE);
			mMediaName.setVisibility(View.VISIBLE);
			mMediaName.setWidth(220);
			mMediaName.setText(name);
		}
		else
		{
			mMediaImg.setVisibility(View.VISIBLE);
			mMediaName.setVisibility(View.GONE);
		}
	}
	
	 /**
	 * 打开文件
	 * @param file
	 */ 
	private void openFile()
	{ 
	    File file = new File(strFilePath + "/" + strFileName);
	    if (!file.exists() || file.isDirectory())
	    {
	    	//Toast.makeText(mContext, "文件不存在：" + strFilePath + "/" + strFileName, Toast.LENGTH_SHORT).show();
	    	Toast.makeText(mContext, "文件不存在", Toast.LENGTH_SHORT).show();
	    	return;
	    }
	    
	    Intent intent = new Intent(); 
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
	    //设置intent的Action属性 
	    intent.setAction(Intent.ACTION_VIEW); 
	    //获取文件file的MIME类型 
	    String type = getMIMEType(file); 
	    //设置intent的data和Type属性。 
	    intent.setDataAndType(/*uri*/Uri.fromFile(file), type); 
	    //跳转 
	    mContext.startActivity(intent);   
	} 
	
	/**
	 * 根据文件后缀名获得对应的MIME类型。
	 * @param file
	 */ 
	private String getMIMEType(File file) { 
	     
	    String type="*/*"; 
	    String fName = file.getName(); 
	    //获取后缀名前的分隔符"."在fName中的位置。 
	    int dotIndex = fName.lastIndexOf("."); 
	    if(dotIndex < 0){ 
	        return type; 
	    } 
	    /* 获取文件的后缀名*/ 
	    String end=fName.substring(dotIndex,fName.length()).toLowerCase(); 
	    if(end=="")return type; 
	    //在MIME和文件类型的匹配表中找到对应的MIME类型。 
	    for(int i=0;i<MIME_MapTable.length;i++){ //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？ 
	        if(end.equals(MIME_MapTable[i][0])) 
	            type = MIME_MapTable[i][1]; 
	    }        
	    return type; 
	} 
	
	private boolean checkSDCard() {
		return Environment.getExternalStorageState().
				equals(android.os.Environment.MEDIA_MOUNTED);

		/* ȡ��SD Card·����Ϊ¼�����ļ�λ�� */
		// mRecAudioPath = Environment.getExternalStorageDirectory();
	}
	
	public static String GetMediaDir() {
		String filePath = DataMan.GetDataForlder() + "media/";
		
		File mediaDir = new File(filePath);
		if (!mediaDir.exists()) {
			mediaDir.mkdirs();
		}
		
		return filePath;
	}

	/**
     * 拍照
     */
    private void cameraMethod() {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaOutputUri(".jpg"));
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            mMediaListener.startMediaActivity(mFileSelector, intent, Request.CAPTURE_IMAGE, this);
            
            // TODO 不能返回拍照结果
            setName();
    }
    
    Uri mediaOutputUri(String type)
    {
    	strFileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + type; // 图片文件名
        
        File out = new File(strFilePath + "/" + strFileName);
        
        return Uri.fromFile(out);
    }
    
    /**
     * 录像
     */
    private void videoMethod() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaOutputUri(".3gp"));
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        mMediaListener.startMediaActivity(mFileSelector, intent, Request.RECORD_VIDEO, this);
    }
    /**
     * 录音
     */
    private void soundRecorderMethod() {
    	//Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaOutputUri(".amr"));
        intent.setType("audio/amr");
        mMediaListener.startMediaActivity(mFileSelector, intent, Request.RECORD_SOUND, this);
    }
    
    // ѡȡ�ļ�
    private void selectMediaFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setClass(mContext, FileSelector.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mMediaListener.startMediaActivity(mFileSelector, intent, Request.PICK_MEDIA_FILE, this);
    }
    
    public void processMediaPicker(int requestCode, Intent data)
    {
    	String filePathName = null;
    	
    	switch (requestCode) {
    	case Request.PICK_MEDIA_FILE: // 选取的文件
    		filePathName = data.getExtras().getString("filename");
    		break;
        case Request.CAPTURE_IMAGE://����
        	filePathName = strFilePath + strFileName;
            break;
        case Request.RECORD_VIDEO://������Ƶ
        {
            Uri uriVideo = data.getData();
            
            filePathName = uriVideo.getPath();
            
            Cursor cursor = mContext.getContentResolver().query(uriVideo, null, null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                    /** _data���ļ��ľ��·�� ��_display_name���ļ��� */
            	//filePathName = cursor.getString(cursor.getColumnIndex("_data"));
            	
            	//Toast.makeText(mContext, filePathName, Toast.LENGTH_LONG).show();
            }
        }
        break;
        case Request.RECORD_SOUND://¼��
        {
            Uri uriRecorder = data.getData();
            
            filePathName = uriRecorder.getPath();
            
            Cursor cursor = mContext.getContentResolver().query(uriRecorder, null, null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                    /** _data���ļ��ľ��·�� ��_display_name���ļ��� */
            	filePathName = cursor.getString(cursor.getColumnIndex("_data"));
            }
        }
        break;
        }
    	
		strFilePath = null;
		strFileName = null;
		
    	if (filePathName == null)
			return;
    	
    	File file = new File(filePathName);
    	if (file.exists())
    	{
			String path[] = DataMan.ParsePath(filePathName);
			
			if (path != null)
			{
				if (file.length() > MAX_MEDIA_SIZE * 1024 * 1024)
				{
					Toast.makeText(mContext, "媒体文件大小超限，最大" + MAX_MEDIA_SIZE + "MB", Toast.LENGTH_SHORT).show();
					return;
				}
				
				strFilePath = path[0];
				strFileName = path[1];
				
				setName();
			}
    	}
    }
    
    /**
     * 根据指定的图像路径和大小来获取缩略图
     * 此方法有两点好处：
     *     1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
     *        第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。
     *     2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使
     *        用这个工具生成的图像不会被拉伸。
     * @param imagePath 图像的路径
     * @param width 指定输出图像的宽度
     * @param height 指定输出图像的高度
     * @return 生成的缩略图
     */
    private Bitmap getImageThumbnail(String imagePath, int width, int height) {
    	
    	Bitmap bitmap = null;
    	
		 int lastIndexOfDot = imagePath.lastIndexOf('.');
		 
		 if (lastIndexOfDot > 0)
		 {
			 int fileNameLength = imagePath.length();
			 String extension = imagePath.substring(lastIndexOfDot+1, fileNameLength);
			 
			 if (extension.equalsIgnoreCase("3gp") ||
			     extension.equalsIgnoreCase("mp4"))
			 {
				 bitmap = ThumbnailUtils.createVideoThumbnail(imagePath, MediaStore.Video.Thumbnails.MINI_KIND);
				 
				 return bitmap;
			 }
		 }
    	
     
     BitmapFactory.Options options = new BitmapFactory.Options();
     options.inJustDecodeBounds = true;
     // 获取这个图片的宽和高，注意此处的bitmap为null
     bitmap = BitmapFactory.decodeFile(imagePath, options);
     
     options.inJustDecodeBounds = false; // 设为 false
     // 计算缩放比
     int h = options.outHeight;
     int w = options.outWidth;
     
     if (width == 0)
    	 width = 200;
     
     if (height == 0)
    	 height = 200;
     
     int beWidth = w / width;
     int beHeight = h / height;
     int be = 1;
     if (beWidth < beHeight) {
      be = beWidth;
     } else {
      be = beHeight;
     }
     if (be <= 0) {
      be = 1;
     }
     options.inSampleSize = be;
     // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
     bitmap = BitmapFactory.decodeFile(imagePath, options);
     
     // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
     bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
       ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
     
     return bitmap;
    }
    
    void setName()
    {
    	if (mMediaImg != null)
    	{
    		Bitmap bitmap = null;
    		
    		try {
    			bitmap = getImageThumbnail(strFilePath + "/" + strFileName, mMediaImg.getWidth(), mMediaImg.getHeight());
    		}
    		catch (Exception exp)
    		{
    			exp.printStackTrace();
    			
    			Toast.makeText(mContext, "bitmap exp:" + exp.getMessage(), Toast.LENGTH_LONG).show();
    		}
    		
    		if (mType == Type.AUDIO)
    		{
    			if (strFileName != null)
    				mMediaName.setText(strFileName);
    		}
    		else
    		{
	    		if (bitmap != null)
	    		{
	    			mMediaName.setVisibility(View.GONE);
	    			mMediaImg.setVisibility(View.VISIBLE);
	    			
	    		    mMediaImg.setImageBitmap(bitmap);
	    		}
	    		else
	    		{
	    			mMediaImg.setVisibility(View.GONE);
	    			mMediaName.setVisibility(View.VISIBLE);
	    			
	    			mMediaImg.setImageResource(R.drawable.default_img);
	    			
	    			if (strFileName != null)
	    				mMediaName.setText(strFileName);
	    		}
    		}
    	}
    }

	public View getView() {
		return mView;
	}

	public final static String KEY_PICTURE = "[PICTURE]";
	public final static String KEY_VIDEO   = "[VIDEO]";
	public final static String KEY_AUDIO   = "[AUDIO]";

	public String getRecord() {
		String type = null;
		
		switch (mType)
		{
		case PICTURE:
			type =  KEY_PICTURE;
			break;
		case VIDEO:
			type =  KEY_VIDEO;
			break;
		case AUDIO:
			type =  KEY_AUDIO;
			break;
		default:
		    return null;
		}
		
		// 文件存在时才返回有效记录
		File file = new File(strFilePath + "/" + strFileName);
		if (!file.isDirectory() && file.exists())
		{
			return (type + HALFEN + mTime + HALFEN + strFilePath + HALFEN + strFileName);
		}
		else
			return null;
	}
	
	public final static String HALFEN = "\"";
	
	private void parseRecord(Object recordValue, boolean bSynced)
	{
		if (recordValue != null)
		{
		    String token[] = recordValue.toString().split(HALFEN);
		    
		    //Toast.makeText(mContext, recordValue.toString(), Toast.LENGTH_LONG).show();
		    
		    if (token != null && token.length == 4)
		    {
		    	if (token[0].endsWith(KEY_PICTURE))
		    	{
		    		mType = Type.PICTURE;
		    	}
		    	else if (token[0].endsWith(KEY_VIDEO))
		    	{
		    		mType = Type.VIDEO;
		    	}
		    	else if (token[0].endsWith(KEY_AUDIO))
		    	{
		    		mType = Type.AUDIO;
		    	}
		    	else
		    		return; // 内部错误
		    	
		    	mTime = token[1];
		    	
		    	// 没同步的数据设置本地路径
		    	if (!bSynced)
		    		strFilePath = token[2];
		    	
		    	strFileName = token[3];
		    		
		        setName();
		    }
		}
	}
	
	public static boolean IsContainsMedia(String[] valueToken) {
		// [PICTURE],2012_1127_002932,/mnt/sdcard/zkxc/media/,20121127002939.jpg
		return (valueToken != null &&
				valueToken.length == 4 &&
				(valueToken[0].equals(MediaPicker.KEY_PICTURE) || 
				 valueToken[0].equals(MediaPicker.KEY_VIDEO) ||
				 valueToken[0].equals(MediaPicker.KEY_AUDIO)));
	}
	
	private final String[][] MIME_MapTable={ 
            //{后缀名，MIME类型} 
            {".3gp",    "video/3gpp"}, 
            {".apk",    "application/vnd.android.package-archive"}, 
            {".asf",    "video/x-ms-asf"}, 
            {".avi",    "video/x-msvideo"}, 
            {".bin",    "application/octet-stream"}, 
            {".bmp",    "image/bmp"}, 
            {".c",  "text/plain"}, 
            {".class",  "application/octet-stream"}, 
            {".conf",   "text/plain"}, 
            {".cpp",    "text/plain"}, 
            {".doc",    "application/msword"}, 
            {".docx",   "application/vnd.openxmlformats-officedocument.wordprocessingml.document"}, 
            {".xls",    "application/vnd.ms-excel"},  
            {".xlsx",   "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}, 
            {".exe",    "application/octet-stream"}, 
            {".gif",    "image/gif"}, 
            {".gtar",   "application/x-gtar"}, 
            {".gz", "application/x-gzip"}, 
            {".h",  "text/plain"}, 
            {".htm",    "text/html"}, 
            {".html",   "text/html"}, 
            {".jar",    "application/java-archive"}, 
            {".java",   "text/plain"}, 
            {".jpeg",   "image/jpeg"}, 
            {".jpg",    "image/jpeg"}, 
            {".js", "application/x-javascript"}, 
            {".log",    "text/plain"}, 
            {".m3u",    "audio/x-mpegurl"}, 
            {".m4a",    "audio/mp4a-latm"}, 
            {".m4b",    "audio/mp4a-latm"}, 
            {".m4p",    "audio/mp4a-latm"}, 
            {".m4u",    "video/vnd.mpegurl"}, 
            {".m4v",    "video/x-m4v"},  
            {".mov",    "video/quicktime"}, 
            {".mp2",    "audio/x-mpeg"}, 
            {".mp3",    "audio/x-mpeg"}, 
            {".mp4",    "video/mp4"}, 
            {".mpc",    "application/vnd.mpohun.certificate"},        
            {".mpe",    "video/mpeg"},   
            {".mpeg",   "video/mpeg"},   
            {".mpg",    "video/mpeg"},   
            {".mpg4",   "video/mp4"},    
            {".mpga",   "audio/mpeg"}, 
            {".msg",    "application/vnd.ms-outlook"}, 
            {".ogg",    "audio/ogg"}, 
            {".pdf",    "application/pdf"}, 
            {".png",    "image/png"}, 
            {".pps",    "application/vnd.ms-powerpoint"}, 
            {".ppt",    "application/vnd.ms-powerpoint"}, 
            {".pptx",   "application/vnd.openxmlformats-officedocument.presentationml.presentation"}, 
            {".prop",   "text/plain"}, 
            {".rc", "text/plain"}, 
            {".rmvb",   "audio/x-pn-realaudio"}, 
            {".rtf",    "application/rtf"}, 
            {".sh", "text/plain"}, 
            {".tar",    "application/x-tar"},    
            {".tgz",    "application/x-compressed"},  
            {".txt",    "text/plain"}, 
            {".wav",    "audio/x-wav"}, 
            {".wma",    "audio/x-ms-wma"}, 
            {".wmv",    "audio/x-ms-wmv"}, 
            {".wps",    "application/vnd.ms-works"}, 
            {".xml",    "text/plain"}, 
            {".z",  "application/x-compress"}, 
            {".zip",    "application/x-zip-compressed"}, 
            {"",        "*/*"}   
        }; 
}
