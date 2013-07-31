package com.zkxc.android.map;

import java.io.File;
import java.text.ParseException;
import java.util.Date;

import android.util.Log;

public class RecordFile implements Setting{

	private String title;
	private File file;
	private Date date;
	
	public RecordFile(File file) {
		this.file = file;
		try {
			date = FILE_DATE_FORMAT.parse(file.getName().substring(0,14));
		} catch (ParseException e) {
			Log.e("ERROR", e.getMessage(), e);
			e.printStackTrace();
		}
	}
	
	public String getTitle() {
		if(title==null){
				title = DISPLAY_DATE_FORMAT.format(date);
		}
		return title;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	@Override
	public String toString() {
		return this.getTitle();
	}
	public Date getDate() {
		return date;
	}
	
	
}
