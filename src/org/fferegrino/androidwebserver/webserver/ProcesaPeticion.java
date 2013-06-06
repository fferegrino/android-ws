package org.fferegrino.androidwebserver.webserver;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import android.os.Environment;

public class ProcesaPeticion {
	String path;
	String response;

	public ProcesaPeticion(String path) throws Exception {
		this.path = path;
		// Checamos si está montada la SD:
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File sdCard = Environment.getExternalStorageDirectory(), peticion, contenedor;
			contenedor = new File(sdCard.getAbsolutePath()
					+ "/AndroidWebServer/wwwroot");
			boolean x = contenedor.mkdirs();
if(x);
			File request = new File(contenedor.getAbsolutePath() + path);

			StringBuilder strLine = new StringBuilder();
			if (request.exists() && request.isFile()) {
				try {
					// Open the file that is the first
					// command line parameter
					FileInputStream fstream = new FileInputStream(request);
					// Get the object of DataInputStream
					DataInputStream in = new DataInputStream(fstream);
					BufferedReader br = new BufferedReader(
							new InputStreamReader(in));
					String s;
					while ((s = br.readLine()) != null) {
						strLine.append(s);
					}
					in.close();
				} catch (Exception e) {
				}
			} else if(request.exists() && request.isDirectory()){
				
			} else {
				strLine.append("<html>");
				strLine.append("<head><title>AndroidWebServer - NotFound</title></head>");
				strLine.append("<body>");
				strLine.append("<h1>Not found</h1>");
				strLine.append("<h2>The requested URL " + path+ " was not found on this android server</h2>");
				strLine.append("<hr />");
				strLine.append("</body></html>");
			}
			response = strLine.toString();
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		} else {
			throw new Exception("No hay una memoria SD en el dispositivo");
		}
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the response
	 */
	public String getResponse() {
		return response;
	}

	/**
	 * @param response
	 *            the response to set
	 */
	public void setResponse(String response) {
		this.response = response;
	}

}
