import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.SecureRandom;

// RequestHandler is thread that process requests of one client connection
public class RequestHandler extends Thread {

	Socket clientSocket;

	InputStream inFromClient;

	OutputStream outToClient;

	byte[] request = new byte[1024];

	private ProxyServer server;

	public RequestHandler(Socket clientSocket, ProxyServer proxyServer) {

		this.clientSocket = clientSocket;

		this.server = proxyServer;

		try {
			clientSocket.setSoTimeout(20000);
			inFromClient = clientSocket.getInputStream();
			outToClient = clientSocket.getOutputStream();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		/**

		 * To do
		 * Process the requests from a client. In particular,
		 * (1) Check the request type, only process GET request and ignore others
		 * (2) Write log.
		 * (3) If the url of GET request has been cached, respond with cached content
		 * (4) Otherwise, call method proxyServertoClient to process the GET request
		 *
		 */
		try {

			

			String requestLine = getLine(inFromClient);
			System.out.println("This is the requestLine: " + requestLine);
			String[] splitLine = requestLine.split(" ");
			String requestType = splitLine[0];
			String urlString = splitLine[1];

			System.out.println("This is the request type: " + requestType);
			// checks if requestType is a GET then checks to see if its in chache if it is
			// it sents the file back to the user else it processes the request.
			System.out.println("10");
			if (requestType.equals("GET")) {
				System.out.println("11");
				if (server.cache.containsKey(urlString)) {
					System.out.println("12");
					sendCachedInfoToClient(server.getCache(urlString));
					System.out.println("13");
					proxyServertoClient(request);
					System.out.println("14");
				} else {
					inFromClient.read(request);
					System.out.println(requestLine);
					System.out.println(requestType);
					System.out.println(urlString);
					System.out.println(clientSocket.isConnected());
					
					System.out.println(request[0]);
					proxyServertoClient(request);

				}

			}

			else {
				clientSocket.close();
			}
		} catch (Exception e) {
			e.getStackTrace();
		}
	}


	private void proxyServertoClient(byte[] clientRequest) {

		FileOutputStream fileWriter = null;
		Socket toWebServerSocket = null;
		InputStream inFromServer;
		OutputStream outToServer;
		
		// Create Buffered output stream to write to cached copy of file
		String fileName = "cached/" + generateRandomFileName() + ".dat";
		
		// to handle binary content, byte is used
		byte[] serverReply = new byte[4096];
		
			
		/**
		 * To do
		 * (1) Create a socket to connect to the web server (default port 80)
		 * (2) Send client's request (clientRequest) to the web server, you may want to use flush() after writing.
		 * (3) Use a while loop to read all responses from web server and send back to client
		 * (4) Write the web server's response to a cache file, put the request URL and cache file name to the cache Map
		 * (5) close file, and sockets.
		*/

		System.out.println("15");

		try {
			//toWebServerSocket = new Socket("localhost", 1234);
			System.out.println("port is " + clientSocket.getPort());
			toWebServerSocket = new Socket(clientSocket.getInetAddress(), 1234);
			
			System.out.println("16");
			System.out.println(toWebServerSocket.toString());
			//inFromServer = toWebServerSocket.getInputStream();
			inFromServer = clientSocket.getInputStream();
			String sentence = inFromServer.readAllBytes();



			outToServer = toWebServerSocket.getOutputStream();
			System.out.println(toWebServerSocket.getInetAddress());
			System.out.println("17b");
			int bytesRead = inFromClient.read(serverReply);
			System.out.println(bytesRead);
			System.out.println("17c");
			while((bytesRead = inFromClient.read(clientRequest)) != -1){
				System.out.println(bytesRead);
				outToServer.write(clientRequest, 0, bytesRead);
				outToServer.flush();
			}
			System.out.println("17c");

			outToServer.write(clientRequest);
			System.out.println("17");

			fileWriter = new FileOutputStream(fileName);
			System.out.println("18");
			System.out.println("is toWebServer Connected");
			System.out.println(toWebServerSocket.isConnected());

			outToServer.write(clientRequest);
			System.out.println("19");

			outToServer.flush();
			System.out.println("20");

			outToServer.close();
			System.out.println("21");

			//while(inFromServer.available() != 0){
				//System.out.println("22");
				fileWriter.write(serverReply);
				
				//System.out.println("23");
			//}


		fileWriter.close();
		toWebServerSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	// Sends the cached content stored in the cache file to the client
	private void sendCachedInfoToClient(String fileName) {

		try {

			byte[] bytes = Files.readAllBytes(Paths.get(fileName));

			outToClient.write(bytes);
			outToClient.flush();

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			if (clientSocket != null) {
				clientSocket.close();
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	// Generates a random file name
	public String generateRandomFileName() {

		String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_";
		SecureRandom RANDOM = new SecureRandom();
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 10; ++i) {
			sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
		}
		return sb.toString();
	}


	// https://android.googlesource.com/platform/frameworks/base/+/8a56d18/packages/services/Proxy/src/com/android/proxyhandler/ProxyServer.java
	private String getLine(InputStream inputStream) throws IOException {
		StringBuffer buffer = new StringBuffer();
		int byteBuffer = inputStream.read();
		if (byteBuffer < 0)
			return "";
		do {
			if (byteBuffer != '\r') {
				buffer.append((char) byteBuffer);

			}
			byteBuffer = inputStream.read();
		} while ((byteBuffer != '\n') && (byteBuffer >= 0));
		return buffer.toString();
	}

}