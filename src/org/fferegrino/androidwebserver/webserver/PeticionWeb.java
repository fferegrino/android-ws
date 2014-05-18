package org.fferegrino.androidwebserver.webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

import org.fferegrino.androidwebserver.WebServerActivity;
import org.fferegrino.androidwebserver.WebServerActivity.ServidorWeb;

import android.os.Environment;

public class PeticionWeb extends Thread {

	private ServidorWeb web;
	private Socket scliente = null;
	public static String INDEX = "index.html";

	public PeticionWeb(Socket ps, ServidorWeb web) {
		scliente = ps;
		this.web = web;
		setPriority(NORM_PRIORITY - 1); // hacemos que la prioridad sea baja
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					scliente.getInputStream()));
			OutputStream outputStream = scliente.getOutputStream();
			PrintWriter out = new PrintWriter(new OutputStreamWriter(
					outputStream), true);
			out.flush();

			HTTPHeader header = new HTTPHeader(scliente.getInputStream());
			HTTPResponse response = new HTTPResponse();
			header.parse();

			// Checamos si está montada la SD:
			response.setHttpVer("HTTP/1.0");
			response.setDate();
			response.setServer(web.getIpAddr());
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				File sdCard = Environment.getExternalStorageDirectory(), contenedor;
				contenedor = new File(sdCard.getAbsolutePath()
						+ "/AndroidWebServer/wwwroot");
				if (contenedor.exists()) {
					response.setStatusCode(500);
					
					
				}
			}else{
				response.setStatusCode(500);
			}
			String cadena = "";
			int i = 0;
			ProcesaPeticion pp = null;
			out.println(response.toString());
			/*
			 * if (pp != null) { out.println(pp.getResponse()); }
			 */
			out.flush();
			out.close();
		} catch (Exception e) {
			web.log("Error en servidor: " + e.toString(), "2");
		}
	}

}
