package com.zkxc.android.map;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import com.zkxc.android.R;
import com.zkxc.android.common.Debug;

import android.util.Log;

public class ContentFile implements Serializable,Setting{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4449824402189767242L;
	private double lat;
	private double lng;
	private Date addTime;
	private String content;
	private File file;
	private MyFilenameFilter myFilenameFilter;
	
	private ContentFile(){
		
	}
	
	public static boolean WriteCoolectionActionFile(File file, double lat, double lng, Date addTime, String content){

		try {
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.append(addTime.getTime() + ":" + lat + "," + lng + "\n");
			fileWriter.append(content);
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
			Debug.Log("保存采集表GPS失败:" + e.getMessage());
			return false;
		}
		
		return true;
	}
	
	public static ContentFile createContentFile(double lat,double lng,Date addTime,String content,boolean server){
		ContentFile contentFile = new ContentFile();
		contentFile.lat = lat;
		contentFile.lng =lng;
		contentFile.addTime = addTime;
		contentFile.content = content;
		String filename = FILE_DATE_FORMAT.format(addTime);
		File contentDir = server?Service.getContentServerDir():Service.getContentLocalDir();
		contentFile.file = new File(contentDir.getAbsolutePath() + "/" + filename + ".txt");
		contentFile.myFilenameFilter = new MyFilenameFilter(filename);
		
		return contentFile;
		
	}
	public void save(){
		try {
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.append(addTime.getTime() + ":" + lat + "," + lng + "\n");
			fileWriter.append(content);
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public Date getAddTime() {
		return addTime;
	}
	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public int getLatE6(){
		return (int)(getLat()*1E6);
	}
	
	public int getLngE6(){
		return (int)(getLng()*1E6);
	}
	
	public File getVideoFile() {
		File[] list = Service.getVideoDir().listFiles(myFilenameFilter);
		if (list.length > 0) {
			return list[0];
		}
		return null;
	}
	public File getImageFile() {
		File[] list = Service.getImageDir().listFiles(myFilenameFilter);
		if (list.length > 0) {
			return list[0];
		}
		return null;
	}
	public File getVoiceFile() {
		File[] list = Service.getVoiceDir().listFiles(myFilenameFilter);
		if (list.length > 0) {
			return list[0];
		}
		return null;
	}
	public static ContentFile getContentFile(File file){
		ContentFile contentFile = new ContentFile();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			String[] token = line.split(":");
			String strTime = token[0];
			//contentFile.setAddTime(new Date(Long.parseLong(line.substring(0,13))));//AddTime
			contentFile.setAddTime(new Date(Long.parseLong(strTime)));//AddTime
			//String[] latLng = line.substring(14).split(",");
			String[] latLng = token[1].split(",");
			contentFile.setLat(Double.parseDouble(latLng[0]));//Lat
			contentFile.setLng(Double.parseDouble(latLng[1]));//Lng
			StringBuilder content = new StringBuilder();
			while(null!=(line = reader.readLine())){
				if(content.length()>0){
					content.append("\n");
				}
				content.append(line);
			}
			//Debug.Log("content : " + content.toString());
			contentFile.setContent(content.toString());//Content
			contentFile.setFile(file);
			String filename = FILE_DATE_FORMAT.format(contentFile.getAddTime());
			contentFile.myFilenameFilter = new MyFilenameFilter(filename);

		} catch (Exception e) {
			Log.e("ERROR", e.getMessage(), e);
			e.printStackTrace();
		}
		return contentFile;
	}
	@Override
	public String toString() {
		return "ContentFile [lat=" + lat + ", lng=" + lng + ", addTime="
				+ addTime + ", content=" + content + "]";
	}

	public boolean containsType(int typeId) {
		
		switch (typeId)
		{
		case R.id.btn_track_sort_picture:
			return (getImageFile() != null);
		case R.id.btn_track_sort_video:
			return (getVideoFile() != null);
		case R.id.btn_track_sort_audio:
			return (getVoiceFile() != null);
		case R.id.btn_track_sort_file:
			return getFile().exists(); // TODO AAA 是否检查getFile？
		}
		
		return true; // 默认不过滤
	}
	
	
}
