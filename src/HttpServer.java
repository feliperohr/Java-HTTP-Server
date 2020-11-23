import httpManager.request.HttpRequestHandler;
import httpManager.response.HttpResponseHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

	private final static int PORT = 80;
	private final static String MAIN_ROOT = "./public_html";

	public static void main(String[] args) {

		try {
			ServerSocket server = new ServerSocket(PORT);
			HttpResponseHandler response = HttpResponseHandler.getInstance();

			while (true) {

				Socket socket = server.accept();

				InputStream in = socket.getInputStream();
				OutputStream out = socket.getOutputStream();
				PrintWriter pr = new PrintWriter(out);

				String parsedRequest = parseClientReq(in);

				HttpRequestHandler req = new HttpRequestHandler(parsedRequest);

				String method = req.getMethod();
				String urlPath = req.getPath();

				System.out.println(method + " - " + urlPath);

				String res;

				if (method.equals("GET")) {

					if(urlPath.equals("/manutencao")){
						res = response.getSimpleResponseMsg(503, "Service Unavailable");
						out.write(res.getBytes());
					}

					if(urlPath.equals("/tea")){
						File file = new File(MAIN_ROOT + urlPath+".html");

						res = response.getResponseWithData(200, "I'm a teapot", file);
						out.write(res.getBytes());
					}

					if (urlPath.equals("/")) {
						urlPath = "/index.html";

						File file = new File(MAIN_ROOT + urlPath);

						if (file.exists()) {
							res = response.getResponseWithData(200, "OK", file);
							out.write(res.getBytes());

						}else {
							res = response.getSimpleResponseMsg(403, "Forbidden");
							out.write(res.getBytes());
						}

					}else {
						File file = new File(MAIN_ROOT + urlPath);

						if (file.exists()) {

							//Verify if it's a directory and has a index.html file.
							if(file.isDirectory()) {
								if(checkForIndexFile(urlPath)){
									urlPath = "/index.html";

									File indexFile = new File(MAIN_ROOT + urlPath);

									res = response.getResponseWithData(200, "OK", indexFile);
									out.write(res.getBytes());

								}else {
									res = response.getResponseWithData(200, "OK", file);
									out.write(res.getBytes());
								}

								// get the file name in url and return the date.
							}else {
								res = response.getResponseWithData(200, "OK", file);
								out.write(res.getBytes());

							}


						}else {
							res = response.getSimpleResponseMsg(404, "Not Found");
							out.write(res.getBytes());

						}
					}

				} else if (method.equals("POST")) {

					String fileName = urlPath+".txt";

					String formattedBody = formatBodyRequest(req.getBody());

					File file = new File(MAIN_ROOT+urlPath);

					if(file.isDirectory()){
						File f = new File(MAIN_ROOT+urlPath+fileName);
						if(f.createNewFile()){
							System.out.println("Criado novo arquivo.");
						}else{
							System.out.println("Arquivo já existente.");
						}

						try {
							FileWriter writer = new FileWriter(f.getPath(), true);
							writer.write(formattedBody);
							writer.close();
							System.out.println("Inserido: " + formattedBody + "!!");
						} catch (IOException e) {
							e.printStackTrace();
						}

						res = response.getSimpleResponseMsg(201, "Created");
						out.write(res.getBytes());
					}else{
						res = response.getSimpleResponseMsg(400, "Bad Request");
						out.write(res.getBytes());
					}


				} else if( method.equals("PUT") || method.equals("DELETE") || method.equals("HEAD") || method.equals("CONNECT") ||
						method.equals("TRACE") || method.equals("OPTIONS") || method.equals("PATCH")) {

					res = response.getSimpleResponseMsg(501, "Not Implemented");
					out.write(res.getBytes());

				}else {
					res = response.getSimpleResponseMsg(400, "Bad Request");
					out.write(res.getBytes());
				}

				in.close();
				out.close();
				socket.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static String formatBodyRequest(String reqBody) {
		String[] array, name, otherName;

		array = reqBody.split("&");
		name = array[0].split("=");
		otherName = array[1].split("=");

		String msg = name[1] + ";" + otherName[1] + "\r\n";

		return msg;
	}

	private static boolean checkForIndexFile(String urlPath) {
		File f = new File(MAIN_ROOT+urlPath+"/index.html");
		return f.isFile();
	}

	public static String parseClientReq(InputStream inputStream) throws IOException {
		StringBuilder result = new StringBuilder();
		do {
			result.append((char) inputStream.read());
		} while (inputStream.available() > 0);
		return result.toString();
	}

}
