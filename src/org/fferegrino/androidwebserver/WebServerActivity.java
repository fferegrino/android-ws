package org.fferegrino.androidwebserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.fferegrino.androidwebserver.system.AndroidSystem;
import org.fferegrino.androidwebserver.system.ServerFiles;
import org.fferegrino.androidwebserver.webserver.PeticionWeb;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
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

	AndroidSystem sys;

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

	public void setTextIP(String sIPAddress) {
		myIp.setText(sIPAddress);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		sys = new AndroidSystem(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_server);
		puerto = 8080;
		myIp = (TextView) findViewById(R.id.myIp);
		ETLog = (TextView) findViewById(R.id.ETLog);
		bTurnOff = (ToggleButton) findViewById(R.id.bTurnOff);

		myIp.setText("El servidor está apagado");
		bTurnOff.setChecked(false);
		logView(sys.getSystemInfo());
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.optCreateRoot:
			if (!ServerFiles.rootExists()) {
				ServerFiles.createRoot();
			}
			return true;
		case R.id.optCreateIndex:
			ServerFiles.writeIndex(sys, getString(R.string.index_html));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void pullToast(CharSequence message) {
		pullToast(message.toString());
	}

	protected void pullToast(String message) {
		Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
	}

	public void listening(boolean isListening) {
		if (isListening) {
			if (sys.isConnectedWIFI()) {
				logView(getString(R.string.turningOn), 3);
				if (ServerFiles.rootExists()) {
					servidorWeb = new ServidorWeb();
					servidorWeb.execute(this);
				} else {
					pullToast(getString(R.string.serverErrorNoRoot));
					bTurnOff.setChecked(false);
				}
			} else {
				bTurnOff.setChecked(false);
				pullToast(getString(R.string.serverErrorNoWiFi));
			}
		} else {
			try {
				logView(getString(R.string.turningOff), 3);
				servidorWeb.cancel(true);
				servidorWeb.closeSocket();
			} catch (IOException e) {
				logView(String
						.format(getString(R.string.serverErrorTurningOff),
								e.toString()));
			}
		}
	}

	/**
	 * Inner class
	 * 
	 * @author Antonio
	 */
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

		@Override
		protected Void doInBackground(WebServerActivity... arg0) {

			int ip = sys.getWifiInfo().getIpAddress();
			sIPAddress = String.format("%d.%d.%d.%d:%d", (ip & 0xff),
					(ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff),
					puerto);
			setIp = true;
			publishProgress(sIPAddress);
			try {
				ss = new ServerSocket(puerto);
				publishProgress(getString(R.string.serverOn), "1");
				while (!isCancelled()) {
					Socket entrante = ss.accept();

					publishProgress(String.format(
							getString(R.string.acceptedClient),
							entrante.getInetAddress().getHostAddress(),
							entrante.getPort()), "1");
					PeticionWeb pw = new PeticionWeb(entrante, this);
					pw.start();
				}
			} catch (Exception e) {
				if (!isCancelled()) {
					publishProgress(String.format(getString(R.string.serverError), e.toString(), "2"));
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
