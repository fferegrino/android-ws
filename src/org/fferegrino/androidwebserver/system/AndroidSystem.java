package org.fferegrino.androidwebserver.system;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Build.VERSION;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class AndroidSystem extends Activity {
	public boolean isConnectedWIFI() {
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return mWifi.isConnected();
	}
	
	public String getSystemInfo() {

		int sdkInt = VERSION.SDK_INT;
		int versionNumber = 0;
		String versionName = null;
		try {
			PackageInfo pinfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			versionNumber = pinfo.versionCode;
			versionName = pinfo.versionName;
		} catch (NameNotFoundException e) {
		}
		return "Version : " + VERSION.CODENAME + " / SDK: " + sdkInt + " / "
				+ VERSION.RELEASE + " -- AndroidWebServer " + versionNumber
				+ " " + versionName;
	}
	

	public String getDeviceInfo() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		return manufacturer + " " + model;
	}
}
