

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



public class ProxyServer {

	//cache is a Map: the key is the URL and the value is the file name of the file that stores the cached content
	Map<String, String> cache;
	
	ServerSocket proxySocket;

	String logFileName = "log.txt";

	public static void main(String[] args) {
		new ProxyServer().startServer(Integer.parseInt(args[0]));
	}

	void startServer(int proxyPort) {

		cache = new ConcurrentHashMap<>();

		// create the directory to store cached files. 
		File cacheDir = new File("cached");
		if (!cacheDir.exists() || (cacheDir.exists() && !cacheDir.isDirectory())) {
			cacheDir.mkdirs();
		}

		/**
			 * To do:
			 * create a serverSocket to listen on the port (proxyPort)
			 * Create a thread (RequestHandler) for each new client connection 
			 * remember to catch Exceptions!
			 *
		*/
		try{
			//create serverSocket called newSocket on port proxyPort
		ServerSocket newSocket= new ServerSocket(proxyPort);
		//creates thread called newThread using RequestHandler as th argument
		Thread RequestHandler = new Thread();
		newSocket.close();
		RequestHandler.start();

		}
		catch(Exception e){
			System.out.println("The program has encountered an exceptoin.");
			return;
		}
		
	}



	public String getCache(String hashcode) {
		return cache.get(hashcode);
	}

	public void putCache(String hashcode, String fileName) {
		cache.put(hashcode, fileName);
	}

	public synchronized void writeLog(String info) {
		
			/**
			 * To do
			 * write string (info) to the log file, and add the current time stamp 
			 * e.g. String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
			 *
			*/
			try{
			FileWriter tempWriter = new FileWriter("log.txt");
			tempWriter.write("Date");
			tempWriter.close();
			}catch(Exception e){
				System.out.println("The program has encountered an exceptoin.");
				return;
			}
	}

}