package org.fferegrino.androidwebserver.system;

import java.io.File;

import android.os.Environment;

public class ServerFiles {

	public static final String SERVER_FODLER = "/AndroidWebServer/wwwroot";
	private static File sdCard, container;

	public static boolean rootExists() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			if (sdCard == null)
				sdCard = Environment.getExternalStorageDirectory();
			if (container == null)
				container = new File(sdCard.getAbsolutePath() + SERVER_FODLER);
			if (container.exists()) {
				return true;
			}
		}
		return false;
	}

	public static boolean createRoot() {
		if (!rootExists()) {
			container.mkdirs();
			return true;
		} else {
			return false;
		}
	}

	public boolean fileExists(String file) {
		File fole = new File(container, file);
		return fole.exists();
	}

	public File getFile(String file) {
		File fole = new File(container, file);
		return fole;
	}
}
