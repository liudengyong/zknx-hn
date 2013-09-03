package com.zkxc.android.act;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.MKLocationManager;
import com.baidu.mapapi.MKOfflineMap;
import com.baidu.mapapi.MKOfflineMapListener;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;
import com.zkxc.android.act.frame.Frame;
import com.zkxc.android.common.Debug;
import com.zkxc.android.map.ContentFile;
import com.zkxc.android.map.LocationRecordListener;
import com.zkxc.android.map.MarkItemizedOverlay;
import com.zkxc.android.map.MarkOverlayItem;
import com.zkxc.android.map.PathOverlay;
import com.zkxc.android.map.RecordFile;
import com.zkxc.android.map.Service;
import com.zkxc.android.map.Setting;
import com.zkxc.android.map.WriterActivity;
import com.zkxc.android.R;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ActMapTrack extends MapActivity implements Setting {

	private MapView mapView;
	private View historyView;
	private PathOverlay pathOverlay;
	private BMapManager mapManager;
	private MapController mapController;
	private MarkItemizedOverlay markItemizedOverlay;
	private MarkItemizedOverlay markItemizedOverlayVideo;
	
	private String searchDateString = null;
	private DatePickerDialog datePickerDialog;
	
	//private CheckBox recordCheckBox;
	private boolean isRecording;
	private Button btnStartRec;
	private Button btnStopRec;

	private LocationListener locationCenterListener = new LocationListener() {

		public void onLocationChanged(Location location) {
			if (location != null) {
				mapController.animateTo(new GeoPoint((int) (location
						.getLatitude() * 1E6),
						(int) (location.getLongitude() * 1E6)));
			}
		}
	};
	private LocationRecordListener locationRecordListener;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 防止屏幕休眠
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		setContentView(R.layout.track);
		
        Frame.InitFrame(this, getString(R.string.map_track));
        
		// 初始化地图
		initMap();
		historyView = findViewById(R.id.track_historyView);
		// 加载历史列表
		refreshList();
		// Listener
		//recordCheckBox = (CheckBox)findViewById(R.id.track_record);
		
		// 默认不采集记录

		
		btnStartRec = (Button)findViewById(R.id.btn_start_record_track);
		btnStopRec = (Button)findViewById(R.id.btn_stop_record_track);
		
		setRecordStatus(false);
		
		btnStartRec.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				startRecord();
			}
		});
		
		btnStopRec.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				stopRecord();
			}
		});
		
		findViewById(R.id.track_history).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ActMapTrack.this.showView(VIEW_HISTORY_LIST);
			}
		});
		findViewById(R.id.track_today).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ActMapTrack.this.showView(VIEW_TODAY);
				ActMapTrack.this.showTrack(null,"今天");
			}
		});
		findViewById(R.id.track_upload).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				//if(ActMapTrack.this.recordCheckBox.isChecked()){
				if (isRecording)
				{
					ActMapTrack.this.showDialog(DIALOG_CONFIRM_UPLOAD);
				}
				else{
					Service.uploadData(ActMapTrack.this);
					refreshList();
					refreshMark();
				}
			}
		});
		
		/*
		recordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					startRecord();
				}
				else{
					stopRecord();
				}
				
			}
		});
		*/
		
		findViewById(R.id.track_all).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				searchDateString = null;
				refreshList();
			}
		});

		DatePickerDialog.OnDateSetListener handler = new DatePickerDialog.OnDateSetListener() {
			
			public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
				searchDateString = year+"";
				if(monthOfYear<9){
					searchDateString+="0";
				}
				searchDateString+=(monthOfYear+1);
				if(dayOfMonth<10){
					searchDateString+="0";
				}
				searchDateString+=dayOfMonth;
				refreshList();
				
			}
		};
		Date now = new Date(System.currentTimeMillis());
		datePickerDialog = new DatePickerDialog(ActMapTrack.this,handler,now.getYear()+1900,now.getMonth(),now.getDate());
		
		findViewById(R.id.track_date).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				datePickerDialog.show();
			}
		});
		
		// 初始化过滤按钮
		initSortButtons();
		
		recordBtnVisbility(View.INVISIBLE);
	}

	private void setRecordStatus(boolean record) {
		
		isRecording = record;
		
		if (btnStartRec != null && btnStopRec != null)
		{
			btnStartRec.setEnabled(!isRecording);
			btnStopRec.setEnabled(isRecording);
		}
	}
	
	private void recordBtnVisbility(int visibility) {
		
		if (btnStartRec != null && btnStopRec != null)
		{
			btnStartRec.setVisibility(visibility);
			btnStopRec.setVisibility(visibility);
		}
	}
	
	private int mSortId = 0;
	
	OnClickListener mOnSortButtonClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			sortMark(view.getId());
		}
	};
	
	void initSortButtons()
	{
		findViewById(R.id.btn_track_sort_all).setOnClickListener(mOnSortButtonClickListener);
		findViewById(R.id.btn_track_sort_picture).setOnClickListener(mOnSortButtonClickListener);
		findViewById(R.id.btn_track_sort_video).setOnClickListener(mOnSortButtonClickListener);
		findViewById(R.id.btn_track_sort_audio).setOnClickListener(mOnSortButtonClickListener);
		findViewById(R.id.btn_track_sort_file).setOnClickListener(mOnSortButtonClickListener);
	}

	void sortMark(int id)
	{
		mSortId = id;
		
		refreshMark();
	}
	
	private static final int VIEW_HISTORY_LIST = 1;
	private static final int VIEW_TODAY = 2;
	private static final int VIEW_HISTORY = 3;
	private static final int DIALOG_CONFIRM_UPLOAD = 1;
	private static final int DIALOG_CONFIRM_LEAVE  = 2;
	
//	private int currentView;
	private void showView(int index){
		switch (index) {
		case VIEW_HISTORY_LIST:
			historyView.setVisibility(View.VISIBLE);
			mapView.setVisibility(View.GONE);
			mapView.getOverlays().remove(pathOverlay);
			findViewById(R.id.track_history).setEnabled(false);
			findViewById(R.id.track_today).setEnabled(true);
			mapManager.getLocationManager().removeUpdates(locationCenterListener);
			((TextView) findViewById(R.id.track_fileTitle)).setText("");
			recordBtnVisbility(View.INVISIBLE);
			break;
		case VIEW_TODAY:
			historyView.setVisibility(View.GONE);
			mapView.setVisibility(View.VISIBLE);
			findViewById(R.id.track_history).setEnabled(true);
			findViewById(R.id.track_today).setEnabled(false);
			mapManager.getLocationManager().requestLocationUpdates(locationCenterListener);
			Location locationInfo = mapManager.getLocationManager().getLocationInfo();
			if (locationInfo==null) {
				Toast.makeText(ActMapTrack.this, "未获取到当前坐标...", Toast.LENGTH_SHORT).show();
			}
			else{
				mapController.animateTo(new GeoPoint((int)(locationInfo.getLatitude()*1E6),(int)(locationInfo.getLongitude()*1E6)));
			}
			recordBtnVisbility(View.VISIBLE);
			break;
		case VIEW_HISTORY:
			historyView.setVisibility(View.GONE);
			mapView.setVisibility(View.VISIBLE);
			findViewById(R.id.track_history).setEnabled(true);
			findViewById(R.id.track_today).setEnabled(true);
			recordBtnVisbility(View.INVISIBLE);
			break;

		default:
			break;
		}
//		currentView = index;
	}

	private void initMap() {

		mapManager = new BMapManager(getApplication());
		mapManager.init(API_KEY, new MKGeneralListener() {

			public void onGetPermissionState(int iError) {
				Log.d("MyGeneralListener", "onGetNetworkState error is "
						+ iError);
				Toast.makeText(getApplicationContext(), "您的网络出错啦！",
						Toast.LENGTH_LONG).show();

			}

			public void onGetNetworkState(int iError) {
				Log.d("MyGeneralListener", "onGetPermissionState error is "
						+ iError);
				if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
					// 授权Key错误：
					Toast.makeText(getApplicationContext(), "请输入正确的授权Key！",
							Toast.LENGTH_LONG).show();
				}

			}
		});
		mapManager.getLocationManager().enableProvider(MKLocationManager.MK_GPS_PROVIDER);
		super.initMapActivity(mapManager);

		mapView = (MapView) findViewById(R.id.track_mapsView);
		mapView.setBuiltInZoomControls(true); // 设置启用内置的缩放控件
		mapController = mapView.getController(); // 得到mMapView的控制权,可以用它控制和驱动平移和缩放
//		GeoPoint point = new GeoPoint((int) (39.915 * 1E6),
//				(int) (116.404 * 1E6)); // 用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
//		mapController.setCenter(point);
		mapController.setZoom(17);
		MyLocationOverlay myLocationOverlay = new MyLocationOverlay(this,
				mapView) {
			@Override
			public boolean onTap(GeoPoint geoPoint, MapView mapView) {
				return super.onTap(geoPoint, mapView);
			}

			@Override
			protected boolean dispatchTap() {
				//打开文本框
				Intent intent = new Intent(ActMapTrack.this,WriterActivity.class);
				Location locationInfo = mapManager.getLocationManager().getLocationInfo();
				if(locationInfo==null){
					Toast.makeText(ActMapTrack.this, "无法获取当前坐标，请稍后重试", Toast.LENGTH_SHORT).show()
					;
				}
				intent.putExtra("lat", locationInfo.getLatitude());
				intent.putExtra("lng", locationInfo.getLongitude());
				startActivity(intent);
				return true;
			}
		};
		myLocationOverlay.enableCompass();
		myLocationOverlay.enableMyLocation();

		mapView.getOverlays().add(myLocationOverlay);
		refreshMark();
		mapView.getOverlays().add(markItemizedOverlay);
		mapView.getOverlays().add(markItemizedOverlayVideo);
		initOfflineMap();
	}

	/**
	 * ˢ�±��
	 */
	public void refreshMark() {
		if(markItemizedOverlay==null){
			// TODO BBB 不同类型，放置不同图标
			markItemizedOverlay = new MarkItemizedOverlay(getResources().getDrawable(R.drawable.iconmark_all), mapView,this);
		}
		else{
			markItemizedOverlay.clearItem();
		}
		
		if (markItemizedOverlayVideo == null){
			// TODO BBB 不同类型，放置不同图标
			markItemizedOverlayVideo = new MarkItemizedOverlay(getResources().getDrawable(R.drawable.iconmark_video), mapView,this);
		}
		else{
			markItemizedOverlayVideo.clearItem();
		}
		
		Debug.Log("refreshMark");
		
		List<ContentFile> list = Service.getContentList();
		for (ContentFile contentFile : list) {
			MarkOverlayItem item = new MarkOverlayItem(contentFile);
			
			if (contentFile.containsType(mSortId))
			{
				markItemizedOverlay.addItem(item);
				
				/* TODO AAA 分类显示类型图标
				GeoPoint geoPoint = new GeoPoint(contentFile.getLatE6() + 2, contentFile.getLngE6() + 2);
				
				markItemizedOverlayVideo.addItem(new MarkOverlayItem(geoPoint, contentFile));
				*/
			}
		}
		
		mapView.invalidate();
	}
	/**
	 * 初始化离线地图
	 */
	private void initOfflineMap() {
		// 写在onCreate函数里
		MKOfflineMap mOffline = new MKOfflineMap();
		mOffline.init(mapManager, new MKOfflineMapListener() {

			public void onGetOfflineMapState(int type, int state) {
				switch (type) {
				case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
					// MKOLUpdateElement update = mOffline.getUpdateInfo(state);
					// mText.setText(String.format("%s : %d%%", update.cityName,
					// update.ratio));
				}
					break;
				case MKOfflineMap.TYPE_NEW_OFFLINE:
					Log.d("OfflineDemo",
							String.format("add offlinemap num:%d", state));
					break;
				case MKOfflineMap.TYPE_VER_UPDATE:
					Log.d("OfflineDemo", String.format("new offlinemap ver"));
					break;
				}
			}
		});
	}

	/**
	 * 刷新历史轨迹列表
	 */
	private void refreshList() {
		ListView listView = (ListView) findViewById(R.id.track_listView);
		listView.setAdapter(new ArrayAdapter<RecordFile>(this,R.layout.record_item, Service.getRecordList(searchDateString)));
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> listView, View textView,
					int position, long id) {
				showView(VIEW_HISTORY);
				@SuppressWarnings("unchecked")
				ArrayAdapter<RecordFile> adapter = (ArrayAdapter<RecordFile>) listView
						.getAdapter();
				RecordFile recordFile = adapter.getItem(position);
				showTrack(recordFile);
			}
		});
	}

	
	private void showTrack(RecordFile recordFile) {
		showTrack(recordFile,recordFile.getTitle().toString());
		
		// 刷新content图标
		refreshMark();
	}
	/**
	 * 根据文件显示路径
	 * @param recordFile
	 */
	private void showTrack(RecordFile recordFile,String title) {
		try {
			((TextView) findViewById(R.id.track_fileTitle)).setText(title);
			if(recordFile!=null){
				pathOverlay = new PathOverlay();
				int firstLatE6 = 0;
				int firstLngE6 = 0;
				FileInputStream openFileInput = new FileInputStream(
						recordFile.getFile());
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						openFileInput));
				String line = null;
				while (null != (line = reader.readLine())) {
					if (line.length() > 0) {
						// long time = Long.parseLong(line.substring(0,13));
						Log.d("test",line);
						String[] latLng = line.substring(14).split(",");
						
						String prefix = line.substring(0, 3);
						
						// content读取
						if (prefix.equals("采集："))
							continue;
						
						double lat = Double.parseDouble(latLng[0]);
						double lng = Double.parseDouble(latLng[1]);
						if (firstLatE6 == 0) {
							firstLatE6 = (int) (lat * 1E6);
							firstLngE6 = (int) (lng * 1E6);
						}
						pathOverlay.addPoint(lat, lng);
					}
				}
				openFileInput.close();
				mapView.getController().animateTo(
						new GeoPoint(firstLatE6, firstLngE6));
				mapView.getOverlays().add(pathOverlay);
			}

		} catch (IOException e) {
			Log.e("ERROR", e.getMessage(), e);
			e.printStackTrace();
		}
	}
	
	public static File CreateRecordFile(String path)
	{
		String datetime = FILE_DATE_FORMAT.format(new Date(System.currentTimeMillis()));
		
		File filePath = new File(path);
		if (!filePath.exists())
			filePath.mkdirs();
		
		File file = new File(path + "/" + datetime + ".txt");
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				Log.e("Location", e.getMessage(), e);
				e.printStackTrace();
			}
		}
		
		return file;
	}
	
	/**
	 * 开始记录轨迹
	 */
	public void startRecord(){
		//开始;
		File file = CreateRecordFile(Service.getRecordLocalDir().getAbsolutePath());
		
		if (locationRecordListener == null)
			locationRecordListener = new LocationRecordListener();

		locationRecordListener.setRecordFile(file);
		
		mapManager.getLocationManager().setNotifyInternal(10, 5);
		mapManager.getLocationManager().requestLocationUpdates(locationRecordListener);
		mapManager.start();
		
		Toast.makeText(getApplicationContext(), getString(R.string.start_record)+"...", Toast.LENGTH_SHORT).show();
		refreshList();
		
		setRecordStatus(true);
	}
	
	/**
	 * 停止记录轨迹
	 */
	public void stopRecord(){
		
		setRecordStatus(false);
		
		//停止;
		mapManager.getLocationManager().removeUpdates(locationRecordListener);
		File recordFile = locationRecordListener.getRecordFile();
		if(recordFile.length()<80){
			recordFile.delete();
			Toast.makeText(getApplicationContext(), "没有采集任何信息...", Toast.LENGTH_SHORT).show();
			refreshList();
		}
		else{
			Toast.makeText(getApplicationContext(), getString(R.string.stop_record)+"...", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 确认是否停止记录轨迹并上传
	 */
	@Override
	protected Dialog onCreateDialog(int id)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		switch (id) {
		case DIALOG_CONFIRM_UPLOAD://确认是否停止记录轨迹并上传
			builder.setMessage("当前处于记录轨迹的状态，无法进行数据同步。")
			.setCancelable(false)
			.setPositiveButton("停止并上传", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					//((CheckBox)findViewById(R.id.track_record)).setChecked(false);
					stopRecord();
					Service.uploadData(ActMapTrack.this);
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			return builder.create();
		case DIALOG_CONFIRM_LEAVE:
			builder.setMessage("当前处于记录轨迹的状态，确认要停止记录吗？")
			.setCancelable(false)
			.setPositiveButton("停止记录", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					backPress();
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			return builder.create();

		default:
			break;
		}
		return null;
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO 地图  实现isRouteDisplayed
		return false;
	}

	@Override
	protected void onDestroy() {
		if (mapManager != null) {
			mapManager.destroy();
			mapManager = null;
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		if (mapManager != null) {
			mapManager.stop();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		if (mapManager != null) {
			mapManager.start();
		}
		refreshMark();
		super.onResume();
	}
	
	void backPress()
	{
		super.onBackPressed();
	}

	@Override
	public void onBackPressed() {
		if (mapView.getVisibility() == View.VISIBLE) {
			showView(VIEW_HISTORY_LIST);
		} else {
			if (isRecording)
				showDialog(DIALOG_CONFIRM_LEAVE);
			else
				backPress();
		}
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	public boolean isRecording() {
		
		return isRecording;
	}
}