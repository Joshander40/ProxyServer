

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
			clientSocket.setSoTimeout(2000);
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
		try{

			String requestLine = getLine(clientSocket.getInputStream());
			if(request == null)
			{

				clientSocket.close();
				return;
			}
			String[] splitLine = requestLine.split(" ");
			String requestType = splitLine[0];
			String urlString = splitLine[1];
			

			if(!(requestType.equals("GET"))){
				//proxyServertoClient();
				return;
			}

			else
			{
				proxyServertoClient(request);
			}
		}
            catch(Exception e)
            {
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

		try {
		toWebServerSocket = new Socket("default", 80);
		 
			inFromServer =  new ByteArrayInputStream(serverReply);
			outToServer = new ByteArrayOutputStream();

			fileWriter = new FileOutputStream(fileName);

		outToServer.write(clientRequest);
		outToServer.flush();
		outToServer.close();
		
			while(inFromServer.available() != 0)
			{
				fileWriter.write(serverReply);
			}
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

	//https://android.googlesource.com/platform/frameworks/base/+/8a56d18/packages/services/Proxy/src/com/android/proxyhandler/ProxyServer.java
	private String getLine(InputStream inputStream) throws IOException {
		StringBuffer buffer = new StringBuffer();
		int byteBuffer = inputStream.read();
		if (byteBuffer < 0) return "";
		do {
			if (byteBuffer != '\r') {
				buffer.append((char)byteBuffer);
			}
			byteBuffer = inputStream.read();
		} while ((byteBuffer != '\n') && (byteBuffer >= 0));
		return buffer.toString();
	}

}