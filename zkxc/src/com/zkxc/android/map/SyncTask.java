package com.zkxc.android.map;

public class SyncTask {
	public static final int TYPE_UPLOAD_CONTENT = 1;
	public static final int TYPE_UPLOAD_PATH = 2;
	public static final int TYPE_DOWNLOAD_PATH = 3;
	public static final int TYPE_DOWNLOAD_IMAGE = 4;
	public static final int TYPE_DOWNLOAD_SOUND = 5;
	public static final int TYPE_DOWNLOAD_VIDEO = 6;
	public static final int TYPE_DOWNLOAD_CONTENT = 7;
	private int type;
	private Object item;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public Object getItem() {
		return item;
	}
	public void setItem(Object item) {
		this.item = item;
	}
	
}
