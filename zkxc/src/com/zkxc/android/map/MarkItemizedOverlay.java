package com.zkxc.android.map;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.OverlayItem;
import com.zkxc.android.R;

public class MarkItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	private List<MarkOverlayItem> items = new ArrayList<MarkOverlayItem>();
	private Drawable marker;
	private MapView mMapView;
	private MapActivity activity;
	static View mPopView = null;	// 点击mark时弹出的气泡View
	
//	public List<OverlayItem> getItems() {
//		return items;
//	}
	
	public void addItem(MarkOverlayItem item){
		items.add(item);
		populate();
	}
	
	public void clearItem(){
		items.clear();
		populate();
	}
	
	public MarkItemizedOverlay(Drawable marker,MapView mapView,MapActivity activity) {
		super(boundCenterBottom(marker));
		this.mMapView = mapView;
		this.marker = marker;
		this.activity = activity;
		if(mPopView==null){
			mPopView=activity.getLayoutInflater().inflate(R.layout.popview, null);
			mMapView.addView( mPopView,
	                new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
	                		null, MapView.LayoutParams.TOP_LEFT));
			mPopView.setVisibility(View.GONE);
		}
		populate();
	}
	
	@Override
	protected OverlayItem createItem(int i) {
		return items.get(i);
	}

	@Override
	public int size() {
		return items.size();
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		//调整一个drawable边界，使得（0，0）是这个drawable底部最后一行中心的一个像素
		boundCenterBottom(marker);
	}
	
	
	@Override
	protected boolean onTap(int i) {
		Intent intent = new Intent(activity, ReaderActivity.class);
		intent.putExtra("contentFile", items.get(i).getContentFile());
		activity.startActivity(intent);
		return true;
	}
}
