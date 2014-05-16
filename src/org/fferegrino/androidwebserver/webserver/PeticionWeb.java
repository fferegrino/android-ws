package org.fferegrino.androidwebserver.webserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

import org.fferegrino.androidwebserver.WebServerActivity;
import org.fferegrino.androidwebserver.WebServerActivity.ServidorWeb;

public class PeticionWeb extends Thread {

	private ServidorWeb web;
	private Socket scliente = null;

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
		header.parse();
		String cadena = "";
		int i = 0;
		ProcesaPeticion pp = null;
		while ((cadena = in.readLine()) != null) {
			if (i > 14) {
				break;
			}
			if (cadena != null && !"".equals(cadena)) {
				if (cadena.startsWith("GET")) {
					cadena = cadena.substring(4);
					cadena = cadena.substring(0, cadena.indexOf(" "));
					web.log("Petición: " + cadena);
					pp = new ProcesaPeticion(cadena);
				}

			} else
				break;
		}
		// web.log("Empecé a escribir", "1");
		out.println("HTTP/1.0 200 OK");
		out.println("Server: " + web.getIpAddr());
		out.println("Date: " + new Date());
		out.println("Content-Type: text/html");
		out.println("");
		if (pp != null) {
			out.println(pp.getResponse());
		}
		out.flush();
		out.close();
	} catch (Exception e) {
		web.log("Error en servidor: " + e.toString(), "2");
	}
}

}
