package com.zkxc.android.map;

public class MapUtils {
	
	private static final double EARTH_RADIUS = 6378.137;

	public static double getDistance(double latA, double lngA, double latB, double lngB) {
       double radLat1 = (latA * Math.PI / 180.0);
       double radLat2 = (latB * Math.PI / 180.0);
       double a = radLat1 - radLat2;
       double b = (lngA - lngB) * Math.PI / 180.0;
       double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
              + Math.cos(radLat1) * Math.cos(radLat2)
              * Math.pow(Math.sin(b / 2), 2)));
       s = s * EARTH_RADIUS;
       s = s * 1000;
//       s = Math.round(s * 10000) / 10;//m if km then /10000
       return s;
    }
	
}
