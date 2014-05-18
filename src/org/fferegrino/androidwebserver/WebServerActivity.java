package org.fferegrino.androidwebserver;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.fferegrino.androidwebserver.webserver.PeticionWeb;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.Html;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class WebServerActivity extends Activity {

	TextView myIp, ETLog;
	ToggleButton bTurnOff;
	private int puerto;
	ServidorWeb servidorWeb;

	String systemInfo;

	public int getPuerto() {
		return puerto;
	}

	public void logView(String message) {
		logView(message, 0);
	}

	/**
	 * Prints a message on the screen, it is colored based on its level.
	 * 
	 * @param message
	 *            The message to be printed
	 * @param level
	 *            Level of the message
	 */
	public void logView(String message, int level) {
		String sColor;
		switch (level) {
		case 1:
			sColor = "green";
			break;
		case 2:
			sColor = "red";
			break;
		case 3:
			sColor = "blue";
			break;
		default:
			sColor = "black";
			break;
		}

		ETLog.append(Html.fromHtml(String.format(
				getString(R.string.logMessage), sColor, message)));

	}

	public String getDeviceInfo() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		return capitalize(manufacturer) + " " + model;
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

	private String capitalize(String s) {
		if (s == null || s.length() == 0) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		} else {
			return Character.toUpperCase(first) + s.substring(1);
		}
	}

	public void setTextIP(String sIPAddress) {
		myIp.setText(sIPAddress);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_server);
		myIp = (TextView) findViewById(R.id.myIp);
		ETLog = (TextView) findViewById(R.id.ETLog);
		bTurnOff = (ToggleButton) findViewById(R.id.bTurnOff);

		myIp.setText("El servidor está apagado");
		bTurnOff.setChecked(false);
		logView(getSystemInfo());
		bTurnOff.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				listening(isChecked);
			}

		});
	}

	@Override
	protected void onStop() {
		super.onStop();
		listening(false);
	}

	protected boolean isConnectedWIFI() {
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return mWifi.isConnected();
	}

	protected void pullToast(String message) {
		Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
	}

	public void listening(boolean isListening) {
		if (isListening) {
			if (isConnectedWIFI()) {
				puerto = 80;
				logView("Encendiendo servidor", 3);
				servidorWeb = new ServidorWeb();
				servidorWeb.execute(this);

				// Create folder
				String state = Environment.getExternalStorageState();
				if (Environment.MEDIA_MOUNTED.equals(state)) {
					File sdCard = Environment.getExternalStorageDirectory(), contenedor;
					contenedor = new File(sdCard.getAbsolutePath()
							+ "/AndroidWebServer/wwwroot");
					if (!contenedor.exists()) {
						contenedor.mkdirs();
					}
				}

			} else {
				bTurnOff.setChecked(false);
				pullToast("No estás conectado a una red WiFi");
			}
		} else {
			try {
				logView("Apagando servidor", 3);
				servidorWeb.cancel(true);
				servidorWeb.closeSocket();
			} catch (IOException e) {
				logView("Error apagando el servidor: <b>" + e.toString()
						+ "</b>", 2);
			} catch (NullPointerException e) {
				logView("Error apagando el servidor: <b>" + e.toString()
						+ "</b>", 2);
			}
		}
	}

	public class ServidorWeb extends AsyncTask<WebServerActivity, String, Void> {
		boolean setIp;
		ServerSocket ss;
		String sIPAddress;

		public String getIpAddr() {
			return this.sIPAddress;
		}

		public void closeSocket() throws IOException, NullPointerException {
			ss.close();
		}

		public String getSysInfo() {
			return getSystemInfo();
		}

		@Override
		protected Void doInBackground(WebServerActivity... arg0) {

			WifiManager manager = (WifiManager) getBaseContext()
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = manager.getConnectionInfo();
			int ip = wifiInfo.getIpAddress();
			sIPAddress = String.format(getText(R.string.ip) + " %d.%d.%d.%d:"
					+ puerto, (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff),
					(ip >> 24 & 0xff));
			setIp = true;
			publishProgress(sIPAddress);
			try {
				ss = new ServerSocket(puerto);
				publishProgress("Servidor encendido", "1");
				while (!isCancelled()) {
					Socket entrante = ss.accept();
					publishProgress("Cliente aceptado: <b>"
							+ entrante.getInetAddress().getHostName() + "</b>",
							"1");
					PeticionWeb pw = new PeticionWeb(entrante, this);
					pw.start();
				}
			} catch (Exception e) {
				if (!isCancelled()) {
					publishProgress("Error en servidor: <b>" + e.toString()
							+ "</b>", "2");
				}
			}

			return null;
		}

		@Override
		protected void onCancelled(Void result) {
			try {
				closeSocket();
				logView("Servidor apagado", 1);
				setTextIP("El servidor está apagado");
			} catch (IOException e) {
				logView("Error apagando el servidor: <b>" + e.toString()
						+ "</b>", 2);
			} catch (NullPointerException e) {
				logView("Error apagando el servidor: <b>" + e.toString()
						+ "</b>", 2);
			}
		}

		public void log(String... msg) {
			publishProgress(msg);
		}

		public String getInfo() {
			return getDeviceInfo();
		}

		@Override
		protected void onProgressUpdate(String... values) {
			if (setIp == true) {
				myIp.setText(values[0]);
				setIp = false;
			} else {
				if (values.length == 2) {
					int level = Integer.parseInt(values[1]);
					logView((values[0]), level);
				} else {
					logView((values[0]));
				}

			}
		}

	}

}
