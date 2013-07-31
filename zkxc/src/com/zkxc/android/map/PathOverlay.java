package com.zkxc.android.map;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.Overlay;
import com.baidu.mapapi.Projection;

public class PathOverlay extends Overlay {

	private List<GeoPoint> points = new ArrayList<GeoPoint>();

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		Projection projection = mapView.getProjection();
		List<Point> points = new ArrayList<Point>();
		for (GeoPoint point : this.points) {
			Point p = new Point();
			p = projection.toPixels(point, p);
			points.add(p);
		}
//		Paint fillPaint = new Paint();
//		fillPaint.setColor(Color.BLUE);
//		fillPaint.setAntiAlias(true);
//		fillPaint.setStyle(Style.FILL);

		// 将图画到上层
//		for (Point point : points) {
//			canvas.drawCircle(point.x, point.y, 5.0f, fillPaint);	
//		}

		// 第二个画�?画线
		Paint paint = new Paint();
//		paint.setAlpha(0x80);
//		paint.setColor(Color.BLUE);
		paint.setARGB(0x80, 0x00, 0x00, 0xFF);
		paint.setDither(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(4);

		// 连接
		Path path = new Path();
		for (int i = 0; i < points.size(); i++) {
			Point point = points.get(i);
			if(i==0){
				path.moveTo(point.x, point.y);
			}
			else{
				path.lineTo(point.x, point.y);
			}
		}
		// 画出路径
		canvas.drawPath(path, paint);
	}

	public void addPoint(GeoPoint point) {
		points.add(point);
	}

	public void addPoint(int lat, int lng) {
		this.addPoint(new GeoPoint(lat, lng));
	}

	public void addPoint(double lat, double lng) {
		this.addPoint((int) (lat * 1E6), (int) (lng * 1E6));
	}
	
}
