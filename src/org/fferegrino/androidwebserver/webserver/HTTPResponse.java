package org.fferegrino.androidwebserver.webserver;

import java.util.Date;
import java.util.HashMap;

public class HTTPResponse {
	static HashMap<Integer, String> statusStrings;
	static HashMap<String, String> contentTypes;

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public void setHttpVer(String httpVer) {
		this.httpVer = httpVer;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public void setDate() {
		this.date = new Date();
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	private int statusCode;
	private String httpVer;
	private String server;
	private Date date;
	private String contentType;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		statusCode = statusCode == 0 ? 500 : statusCode;
		sb.append(httpVer).append(" ").append(statusCode).append(" ");
		sb.append(statusStrings.get(statusCode)).append("\n");
		sb.append("Server: ").append(" ").append(server).append("\n");
		sb.append("Date: ").append(" ").append(date).append("\n");
		sb.append("Content-Type: text/html").append("\n");
		return sb.toString();
	}

	static {
		statusStrings = new HashMap<Integer, String>();
		statusStrings.put(200, "OK");
		statusStrings.put(201, "CREATED");
		statusStrings.put(202, "Accepted");
		statusStrings.put(400, "Bad request");
		statusStrings.put(401, "Unauthorized");
		statusStrings.put(403, "Forbidden");
		statusStrings.put(404, "Not found");
		statusStrings.put(500, "Internal Error");

		contentTypes = new HashMap<String, String>();
		contentTypes.put(".pdf", "application/pdf");
		contentTypes.put(".html", "text/html");
		contentTypes.put(".txt", "text/plain");
		contentTypes.put(".jpg", "image/jpeg");
		contentTypes.put(".png", "image/png");
		contentTypes.put(".ico", "image/x-icon");
		contentTypes.put(".mp3", "audio/mpeg3");
		contentTypes.put(".doc", "application/msword");
		contentTypes.put(".docx", "application/msword");

	}
}
