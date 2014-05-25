package org.fferegrino.androidwebserver.system;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class AndroidSystem {

	Activity a;

	public AndroidSystem(Activity related) {
		a = related;
	}

	public boolean isConnectedWIFI() {
		ConnectivityManager connManager = (ConnectivityManager) a
				.getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return mWifi.isConnected();
	}

	public WifiInfo getWifiInfo() {
		WifiManager manager = (WifiManager) a.getBaseContext()
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = manager.getConnectionInfo();
		return wifiInfo;
	}

	public String getSystemInfo() {

		int sdkInt = VERSION.SDK_INT;
		int versionNumber = 0;
		String versionName = null;
		try {
			PackageInfo pinfo = a.getPackageManager().getPackageInfo(
					a.getPackageName(), 0);
			versionNumber = pinfo.versionCode;
			versionName = pinfo.versionName;
		} catch (NameNotFoundException e) {
		}
		return "VERSION: " + VERSION.RELEASE + " / SDK: " + sdkInt
				+ " -- AndroidWebServer " + versionNumber + " " + versionName;
	}

	public String systemVersion() {
		return "VERSION: " + VERSION.RELEASE + " / SDK: " + VERSION.SDK_INT;
	}

	public String awsVersion() {
		int versionNumber = 0;
		String versionName = null;
		try {
			PackageInfo pinfo = a.getPackageManager().getPackageInfo(
					a.getPackageName(), 0);
			versionNumber = pinfo.versionCode;
			versionName = pinfo.versionName;
		} catch (NameNotFoundException e) {
		}
		return versionNumber + " " + versionName;
	}

	public String getDeviceInfo() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		return manufacturer + " " + model;
	}
}
