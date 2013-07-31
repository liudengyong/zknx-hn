package com.zkxc.android.table.controller;

import java.util.List;

import com.zkxc.android.R;
import com.zkxc.android.act.AppZkxc;
import com.zkxc.android.common.Debug;
import com.zkxc.android.table.controller.IAddressTask.MLocation;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LocationView {
	
	public static final String LOCATION_VALUE_TOKEN = "--";

	private static final int CAN_REGET = 1;
	private static final int GETTING   = 2;
	
	TextView mLocationLabelLat;
	TextView mLocationLabelLng;
	
	String mLocationValueLat;
	String mLocationValueLng;
	
	Context mContext;
	View mView;
	
	Button mGetLocationBtn;
	Button mInputLocationBtn;
	
	public LocationView(Context context, Object recordValue)
	{
		mContext = context;
		
		initView(recordValue);
	}
	
	void initView(Object recordValue)
	{
		mView = LayoutInflater.from(mContext).inflate(R.layout.location_gps, null);
		mLocationLabelLat = (TextView)mView.findViewById(R.id.location_label_lat);
		mLocationLabelLng = (TextView)mView.findViewById(R.id.location_label_lng);
		
		mGetLocationBtn = (Button)mView.findViewById(R.id.location_btn_getlocation);
		mInputLocationBtn = (Button)mView.findViewById(R.id.location_btn_inputlocation);
		
		if (AppZkxc.mSyncedRecord)
		{
			mGetLocationBtn.setVisibility(View.GONE);
			mInputLocationBtn.setVisibility(View.GONE);
		} else {
			mGetLocationBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					getLocation();
				}
			});
			
			// 增加输入位置
			mInputLocationBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					inputLocation(view.getContext());
				}
			});
		}
		
		mView.setTag(R.id.tag_view, this);
		
		if (recordValue != null)
		{
			/*
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
			LinearLayout ly = (LinearLayout)mView.findViewById(R.id.location_ly_location);
			ly.setLayoutParams(params);
			*/
			
			initRecord(recordValue.toString());
			return;
		}
		
		mLocationLabelLat.setText("经度");
		mLocationLabelLng.setText("纬度");
	}
	
	private void getLocation()
	{
		LocationManager locationManager;
		String serviceName = Context.LOCATION_SERVICE;
		locationManager = (LocationManager) mContext.getSystemService(serviceName);
		// GPS
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE); //精度要求：ACCURACY_FINE(高)ACCURACY_COARSE(低)
		criteria.setAltitudeRequired(false); // 不要求海拔信息
		criteria.setBearingAccuracy(Criteria.ACCURACY_LOW); //方位信息的精度要求：ACCURACY_HIGH(高)ACCURACY_LOW(低)
		criteria.setBearingRequired(false);  // 不要求方位信息
		criteria.setCostAllowed(true);  // 是否允许付费
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		
		setButtonStatus(GETTING);
		
		String[] token = AppZkxc.GetLocation();
		
		if (token == null)
		{
			// 启动WIFI定位
			doWifi();
			
			runnable.run();
		}
		else
		{
			setLocationView();
		}

		// 启动GPS定位
		String provider = locationManager.getBestProvider(criteria, true);
		
		if (provider == null)
		{
			List<String> list = locationManager.getAllProviders();
			if (list == null || list.size() == 0)
			{
				updateWithNewLocation(null);
				return;
			}
			
			provider = list.get(0); // 返回第一个provider
		}
		
		Location location = locationManager.getLastKnownLocation(provider);
		updateWithNewLocation(location);
		// 更新位置操作的时间间隔为1000毫秒，距离间隔为5米
		locationManager.requestLocationUpdates(provider, 1000, 5, locationListener);
	}

	private void setLocationView() {
		
		String[] token = AppZkxc.GetLocation();
		
		if (token != null && token.length == 3)
		{
			// GPS,136.3214,43.23456
			// WIFI,136.3214,43.23456
			// NO,,
			
			if (token.equals(AppZkxc.TOKEN_LOCATION_FAILED))
			{
				mLocationLabelLat.setText("定位");
				mLocationLabelLng.setText("失败");
			}
			else
			{
				mLocationValueLat = token[1];
				mLocationValueLng = token[2];
				
				mLocationLabelLat.setText("经" + mLocationValueLat);
				mLocationLabelLng.setText("纬" + mLocationValueLng);
				
				//mView.findViewById(R.id.location_btn_getlocation).setVisibility(View.INVISIBLE);
			}
			
			setButtonStatus(CAN_REGET);
			
			//handler.removeCallbacks(runnable); // 取消定时器
			return;
		}

		handler.postDelayed(runnable, 2000);
	}

	private void setButtonStatus(int status) {
		if (status == CAN_REGET)
		{
			mGetLocationBtn.setText("获取位置");
			mGetLocationBtn.setEnabled(true);
			mInputLocationBtn.setEnabled(true);
		}
		else if (status == GETTING)
		{
			mGetLocationBtn.setText("正在获取");
			mGetLocationBtn.setEnabled(false);
			mInputLocationBtn.setEnabled(false);
		}
	}

	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}

		public void onProviderDisabled(String provider) {
			updateWithNewLocation(null);
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

	};

	private void updateWithNewLocation(Location location) {

		if (location != null) {
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			//latLongString = "经度" + lat + "\n纬度" + lng;
			
			AppZkxc.SetLocation(AppZkxc.TOKEN_GPS, Double.toString(lat), Double.toString(lng));
			
			setLocationView();
		} else {
			//latLongString = "GPS定位失败";
		}
		
		//AppZkxc.SetLocation(AppZkxc.TOKEN_GPS, "22.22", "33.33");
		setLocationView();
		
		// TODO AAA WIFI定位
		//mLocationValueLat.setText(latLongString);
	}

	public View getView() {
		return mView;
	}
	
	public String getRecord()
	{
		if (mLocationValueLat == null ||
			mLocationValueLng == null)
			return null;
		
		return mLocationValueLat + LOCATION_VALUE_TOKEN + mLocationValueLng;
	}
	
	public void initRecord(String value)
	{
		if (value == null)
			return;
		
		String[] token = value.split(LOCATION_VALUE_TOKEN);
		
		if (token.length != 2)
		{
			mLocationLabelLat.setText("经度0");
			mLocationLabelLng.setText("纬度0");
			//mGetLocationBtn.setEnabled(false);
			return;
		}
		
		String lat = token[0];
		String lng = token[1];
		
		if (lat == null || lng == null)
		{
			mLocationLabelLat.setText("经度-1");
			mLocationLabelLng.setText("经度-1");
		}
		else
		{
			mLocationValueLat = lat;
			mLocationValueLng = lng;
			
			mLocationLabelLat.setText("经" + lat);
			mLocationLabelLng.setText("纬" + lng);
			
			if (mGetLocationBtn != null)
			{
			    mGetLocationBtn.setVisibility(View.GONE);
			    mView.requestLayout();
			    //mGetLocationBtn.setEnabled(false);
			}
		}
	}

	private void doWifi() {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				MLocation location = null;
				try {
					location = new AddressTask(mContext, AddressTask.DO_WIFI).doWifiPost();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(location == null)
					return null;
				return location.toString();
			}

			@Override
			protected void onPreExecute() {
				//dialog.show();
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(String result) {
				Debug.Log("GPS result=" + result);
				
				handler.removeCallbacks(runnable); // 取消定时器

				if (result != null) {
					String[] token = result.split(",");
					if (token != null && token.length == 3)
					{
						AppZkxc.SetLocation(token[0], token[1], token[2]);
						setLocationView();
						return;
					}
				}
				
				// WIFI 定位失败
				AppZkxc.SetLocation(AppZkxc.TOKEN_LOCATION_FAILED, "", "");
				setLocationView();
				setButtonStatus(CAN_REGET);
				
				super.onPostExecute(result);
			}
			
		}.execute();
	}
	
	// 如果没有取到位置信息，定时更新
	Handler handler = new Handler();
	Runnable runnable = new Runnable(){
	@Override
	public void run() {
		setLocationView();
	}
	};
	
	/**
	 * 输入位置
	 */
	private void inputLocation(final Context context) {
		LinearLayout layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.location_input, null);
		final EditText latEdit = (EditText)layout.findViewById(R.id.location_input_lat);
		final EditText lngEdit = (EditText)layout.findViewById(R.id.location_input_lng);
		
		// 只能输入数字和小数点
		//android:digits=“0123456789.”

		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Editable lat = latEdit.getEditableText();
				Editable lng = lngEdit.getEditableText();
				
				if (lat == null || lat.toString().length() == 0 ||
					lng == null || lng.toString().length() == 0) {
					Toast.makeText(context, "输入为空", Toast.LENGTH_SHORT).show();
					return;
				}
				
				// 设置位置
				mLocationValueLat = lat.toString();
				mLocationValueLng = lng.toString();
				
				mLocationLabelLat.setText("经" + lat);
				mLocationLabelLng.setText("纬" + lng);
			}
		};
		
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("请输入经纬度");
		builder.setPositiveButton("确认", listener); 
		builder.setNegativeButton("取消", null); 
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setView(layout);
		builder.show();
	}
}
