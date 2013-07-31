package com.zkxc.android.map;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.baidu.mapapi.LocationListener;

import android.location.Location;
import android.util.Log;

/**
 * 记录路径监听器，当位置发生改变的时�?会将坐标存入指定文件
 * @author fanyanan
 *
 */
public class LocationRecordListener implements LocationListener,Setting {

	private File recordFile;
	
	public LocationRecordListener() {
	}
	
	public LocationRecordListener(File recordFile) {
		this.recordFile = recordFile;
	}
	
	public void setRecordFile(File recordFile) {
		this.recordFile = recordFile;
	}
	public File getRecordFile() {
		return recordFile;
	}
	
	private long lastRecordTime = 0;
	private double lastLat = 0;
	private double lastLng = 0;
	
	public static boolean SaveLocation(File recFile, double lat, double lng, double distance)
	{
		try {
			long time = System.currentTimeMillis();
			FileOutputStream fos = new FileOutputStream(recFile, true);
			String log = time + ":" + lat + "," + lng + ","
					+ Math.round(distance * 100) / 100.0 + "m\n";
			fos.write(log.getBytes());
			fos.close();
			
		} catch (IOException e) {
			Log.e("Location", e.getMessage(), e);
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/* 改为保存文字内容
	public static boolean SaveLocation(File recFile, double lat, double lng)
	{
		return SaveLocation(recFile, lat, lng, 5); // 最小距离
	}
	*/
	
	public void onLocationChanged(Location location)
	{
		long time = System.currentTimeMillis();
		if(time-lastRecordTime>RECORD_LIMIT_INTERVAL){//限制n毫秒内只记录�?��
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			double distance = MapUtils.getDistance(lat, lng, lastLat, lastLng);
			if(distance>RECORD_LIMIT_DISTANCE){//限制n米内只记录一�?
				lastRecordTime  = time;
				lastLat = lat;
				lastLng = lng;
				SaveLocation(recordFile, lat, lng, distance);
			}
		}
	}
}
