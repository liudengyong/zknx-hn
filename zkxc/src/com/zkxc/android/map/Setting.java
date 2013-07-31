package com.zkxc.android.map;

import java.text.SimpleDateFormat;

public interface Setting {
	public static final String API_KEY = "1B7847AC47585AE080BBE90AE19FF8655B8F8E8A";
	public static final String RECORD_LOCAL_DIR = "/FynMap/records/local";
	public static final String RECORD_SERVER_DIR = "/FynMap/records/server";
	public static final String CONTENT_LOCAL_DIR = "/FynMap/contents/local";
	public static final String CONTENT_SERVER_DIR = "/FynMap/contents/server";
	public static final String IMAGE_DIR = "/FynMap/images";
	public static final String VIDEO_DIR = "/FynMap/video";
	public static final String SOUND_DIR = "/FynMap/sound";
	public static final int RECORD_LIMIT_INTERVAL = 5000;
	public static final double RECORD_LIMIT_DISTANCE = 5.0;
	public static final int TEXT_LIMIT = 140;
//	public static final String APIURL_UPLOAD_GPS_INFO = "http://www.fanyanan.com/fu.php";
	public static final String APIURL_UPLOAD_GPS_INFO = "http://218.106.254.101:8045/upload_GPS_info.aspx";
	public static final String APIURL_GET_GPS_FILE_LIST = "http://218.106.254.101:8045/get_Gps_file_list.aspx";
	public static final String APIURL_GET_GPS_INFO = "http://218.106.254.101:8045/get_Gps_Info.aspx";
	
	public static final SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
	public static final SimpleDateFormat DISPLAY_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat SIMPLE_DISPLAY_DATE_FORMAT = new SimpleDateFormat("MM-dd HH:mm");
}
