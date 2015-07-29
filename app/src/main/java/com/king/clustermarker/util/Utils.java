package com.king.clustermarker.util;


import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;


public class Utils {
	private static int num = 0;

	public static int getNextColor() {
		int[] colors = new int[] { 0xff1681db, 0xffdb162d, 0xff10af53,
				0xffff7821, 0xffc93cde};
		if (num >= colors.length) {
			num = 0;
		}
		return colors[num++];
	}

	
	/** 
     * @param context 
     * @return true 
     */  
    public static final boolean isOPen(final Context context) {  
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);  
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);  
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);  
        if (gps || network) {  
            return true;  
        }  
  
        return false;  
    }  
    
    /** 
     * ?????????GPS 
     * @param context 
     */  
    public static final void openGPS(Context context) {  
        Intent GPSIntent = new Intent();  
        GPSIntent.setClassName("com.android.settings",  
                "com.android.settings.widget.SettingsAppWidgetProvider");  
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");  
        GPSIntent.setData(Uri.parse("custom:3"));  
        try {  
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();  
        } catch (CanceledException e) {  
            e.printStackTrace();  
        }  
    }  
    
	
}
