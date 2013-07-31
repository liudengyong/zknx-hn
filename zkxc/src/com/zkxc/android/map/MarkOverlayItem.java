package com.zkxc.android.map;


import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.OverlayItem;

public class MarkOverlayItem extends OverlayItem implements Setting{
	
	private ContentFile contentFile;
	
	public MarkOverlayItem(ContentFile contentFile) {
		super(
				new GeoPoint(contentFile.getLatE6(), contentFile.getLngE6()),
				SIMPLE_DISPLAY_DATE_FORMAT.format(contentFile.getAddTime()),
				contentFile.getContent()
		);
		this.contentFile = contentFile;
	}
	
	public MarkOverlayItem(GeoPoint geoPoint, ContentFile contentFile) {
		super(geoPoint,
				SIMPLE_DISPLAY_DATE_FORMAT.format(contentFile.getAddTime()),
				contentFile.getContent()
		);
		this.contentFile = contentFile;
	}
	
	public ContentFile getContentFile() {
		return contentFile;
	}
}
