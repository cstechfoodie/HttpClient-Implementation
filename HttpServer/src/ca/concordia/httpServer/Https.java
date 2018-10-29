package ca.concordia.httpServer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Https {
	// request

	private ServerSocket socket;

	private ClientHttpRequest req;
	
	private ClientHttpResponse res;

	private boolean hasDebuggingMessage;

	private int port = 8080;

	private String host = "localhost";

	private String pathToDir = "/";

	private boolean isConnected = false;

	public boolean isConnected() {
		return isConnected;
	}

	private void processRequest(String reqMsgFirstLine) { 
		String[] args = reqMsgFirstLine.split(" ");
		sanitizeArgs(args);

		req = new ClientHttpRequest(); 

		if (args.length == 3 && args[0].equals("GET")) {
			req.setMethod(HttpMethod.GET);
			req.setURI(args[1]);
			req.setVersion(args[2]);
		}

		if (args.length == 3 && args[0].equals("POST")) {
			req.setMethod(HttpMethod.POST);
			req.setURI(args[1]);
			req.setVersion(args[2]);
		}
	}

	private void sanitizeArgs(String[] args) {
		for (int i = 0; i < args.length; i++) {
			args[i] = args[i].trim();
		}
	}

	public void setupServer(String cmd) {
		String[] args = cmd.split(" ");
		sanitizeArgs(args);

		List<String> argsList = Arrays.asList(args);

		if (cmd.contains("-v")) {
			hasDebuggingMessage = true;
		}

		if (cmd.contains("-p")) {
			int index = argsList.indexOf("-p");
			String h = argsList.get(index + 1);
			port = Integer.parseInt(h);
		}

		if (cmd.contains("-d")) {
			int index = argsList.indexOf("-d");
			pathToDir = argsList.get(index + 1);
			File f = new File(pathToDir); 
			if(!f.exists()) {
				//new File(pathToDir).mkdirs();		
				try {
	                f.mkdirs();		//adding exception
	                System.out.println(f.getName() + " has been created.");
	            } catch (Exception e) {
	                System.out.println("Could not create resource directory.");
	                e.printStackTrace();
	            }
			}
		}
		connect(); 
	}

	private void connect() {
		socket = null;
		try {
			socket = new ServerSocket(port);  
			this.isConnected = true;
			System.out.println("Server Created Successfully with on "+ port);
		} catch (Exception e) {
			if (hasDebuggingMessage) {
				System.out.println("Connection Failed with Exception.");
				e.printStackTrace();
			}
		}
	}

	public void receiveAndReply() {
		while (true) {
			DataOutputStream out = null;
			BufferedReader in = null;
			Socket skt = null;
			try {
				skt = socket.accept();
				out = new DataOutputStream(skt.getOutputStream());	
				in = new BufferedReader(new InputStreamReader(skt.getInputStream())); 

			} catch (IOException e) {
				if (hasDebuggingMessage) {
					System.out.println("Failed to create socket connection and input/output stream reader");
					e.printStackTrace();
				}
			}

			if (skt != null && out != null && in != null) { 
				try {
					StringBuilder bld = new StringBuilder();
					String line = null;
					int lineCount = 0;
					while ((line = in.readLine()) != null) { 
						lineCount++;
						if (lineCount == 1) {
							processRequest(line);
							break;
						}
						 if (line.trim().length() == 0) {   
						 bld = new StringBuilder();			
						 continue;
						 }
						 bld.append(line + "\r\n");
					}
					req.setBody(bld.toString());  

					if (req.getMethod().toString().equals("GET")) {
						if (req.getURI().length() == 1 && req.getURI().equals("/")) { 

							File folder = new File(pathToDir);
							File[] listOfFiles = folder.listFiles();

							for (int i = 0; i < listOfFiles.length; i++) {
							  if (listOfFiles[i].isFile()) {
								  bld = new StringBuilder();
								  bld.append(listOfFiles[i].getName() + " ");
							  }
							}
							
							res = new ClientHttpResponse(); 
							res.setBody(bld.toString());
							out.writeBytes(res.toString()); 
							
						} else { 
							String fileName = req.getURI().substring(1);
							File f = new File(pathToDir + "\\" + fileName + ".txt");// C:\Users\ya_hao\Downloads\foo.txt
																					// .\foo.txt
							// PrintWriter filewriter = new PrintWriter(f);
							try {
								Scanner fileR = new Scanner(f);		
								bld = new StringBuilder();
								while (fileR.hasNextLine() && (line = fileR.nextLine()) != null) {
									bld.append(line + "\r\n");		
								}
								res = new ClientHttpResponse();		
								res.setBody(bld.toString());		
								out.writeBytes(res.toString());	
								fileR.close();					//adding close
							} catch (Exception e) {
								res = new ClientHttpResponse();		
								res.setStatusCode("404");
								res.setDescription("File Not Found");
								res.setBody("Failed to read the file");
								out.writeBytes(res.toString());	
								if (hasDebuggingMessage) {		//adding debug message
									System.out.println("Failed to find file");
									e.printStackTrace();
								}
							}
						}
					}

					if (req.getMethod().toString().equals("POST")) {
						String fileName = req.getURI().substring(1);
						File f = new File(pathToDir + "\\" + fileName + ".txt");// C:\Users\ya_hao\Downloads\foo.txt
																				// .\foo.txt
						try {
							PrintWriter filewriter = new PrintWriter(f); 
							filewriter.println(req.getBody());
							
							filewriter.write(req.getBody()); 	//using write?
							
							res = new ClientHttpResponse();
							res.setBody("Sucessfully create the file");
							out.writeBytes(res.toString());	
							filewriter.close();					//adding close
						} catch (Exception e) {
							res = new ClientHttpResponse();
							res.setStatusCode("500");
							res.setDescription("Internal Server Error");
							res.setBody("Failed to create the file");
							out.writeBytes(res.toString());	
							if (hasDebuggingMessage) {			//adding debug message
								System.out.println("Failed to create or overwrite the file");
								e.printStackTrace();
							}
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
