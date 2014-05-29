package org.fferegrino.androidwebserver.webserver;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

import org.fferegrino.androidwebserver.WebServerActivity.ServidorWeb;
import org.fferegrino.androidwebserver.system.ServerFiles;

public class PeticionWeb extends Thread {

	private ServidorWeb web;
	private Socket scliente = null;
	public static String INDEX = "index.html";
	byte[] buffer;
	int wrote;

	public PeticionWeb(Socket ps, ServidorWeb web) {
		buffer = new byte[1024];
		wrote = 0;
		scliente = ps;
		this.web = web;
		setPriority(NORM_PRIORITY - 1);
	}

	public void run() {
		try {
			OutputStream outputStream = scliente.getOutputStream();
			BufferedOutputStream bos = new BufferedOutputStream(outputStream);
			bos.flush();

			ServerFiles files = new ServerFiles();
			HTTPHeader header = new HTTPHeader(scliente.getInputStream());
			HTTPResponse response = new HTTPResponse();
			header.parse();

			response.setHttpVer("HTTP/1.0");
			response.setDate();
			response.setServer(web.getIpAddr());
			response.setContentType(header.getExtension());
			if (ServerFiles.rootExists()) {
				if (!files.fileExists(header.getFile())) {
					response.setStatusCode(404);
				} else {
					response.setStatusCode(200);
					web.log("File " + header.getFile());

					HashMap<String, String> parametros = header.getParametros();
					if(parametros.size() > 0){
						web.log("Parámetros:");
					for (String s : parametros.keySet()) {
						web.log(s + " : " + parametros.get(s));
					}
					}

				}
			} else {
				response.setStatusCode(500);
			}

			bos.write(response.toString().getBytes());
			bos.flush();

			if (response.getStatusCode() == 200) {
				String f = header.getFile().equals("") ? ServerFiles.INDEX
						: header.getFile();
				FileInputStream fr = new FileInputStream(files.getFile(f));
				wrote = fr.read(buffer);
				while (wrote != -1) {
					bos.write(buffer, 0, wrote);
					wrote = fr.read(buffer);
					bos.flush();
				}
				fr.close();
			}

			bos.flush();
			bos.close();
		} catch (Exception e) {
			web.log("Error en servidor archivos: " + e.toString(), "2");
		}
	}

}
