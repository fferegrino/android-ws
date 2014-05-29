package org.fferegrino.androidwebserver.system;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;

public class ServerFiles {
	public final static String INDEX = "index.html";

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

	public static void writeIndex(AndroidSystem sys, CharSequence fileContent) {
		if (rootExists()) {
			File index = new File(container, INDEX);
			String content = String.format(fileContent.toString(),
					sys.awsVersion(), sys.systemVersion());
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(index);
				fos.write(content.getBytes());
				fos.flush();
				fos.close();
			} catch (FileNotFoundException e) {
			} catch (IOException e) {

			}
		}
	}
}
