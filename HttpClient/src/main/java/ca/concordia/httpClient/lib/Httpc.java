package ca.concordia.httpClient.lib;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Httpc {

	private Socket socket;

	private ClientHttpRequest req;

	private ClientHttpResponse res;

	private boolean isConnected;
	
	private Scanner in;
	
	private String helpFile;
	
	private boolean isHelpFileCached = false;

	/**
	 * @return the isConnected
	 */
	public boolean isConnected() {
		return isConnected;
	}

	/*
	 * create a socket for TCP connection and connect it to server
	 */
	private void connect(String host, int port) {
		socket = null;
		try {
			socket = new Socket(host, port);
			this.isConnected = true;
			System.out.println("Connected Successfully with " + host + " on port " + port);
		} catch (UnknownHostException e) {
			System.out.println("Connection Failed with Exception.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Connection Failed with Exception.");
			e.printStackTrace();
		}
	}
	
	/*
	 * primarily responsible for processing the command line in console
	 * worked as a controller to dispatch different 
	 */
	public void commandLineParser(String cmd) {
		// if help, print Help
		// if GET something, assemble a get request, if post, assemble a post
		// use this method as a controller to call appropriate method
		String[] args = cmd.split(" ");
		sanitizeArgs(args);
		if (cmd.equals("httpc help")) {
			// print help -- see on pdf
			printHelp();
		} else if (cmd.equals("httpc help get")) {
			// print help -- see on pdf
			printGetHelp();
		}
		else if (cmd.equals("httpc help post")) {
			// print help -- see on pdf
			printPostHelp();
		} else if (!isConnected && cmd.startsWith("httpc")) {
			if (args[1].trim().equals("get")) {
				req = makeGetRequestObject(cmd, args); // fabricate a request based on curl cmd the user provide
				connect(req.getHost(), req.getPort());
			}
			if (args[1].trim().equals("post")) {
				req = makePostRequestObject(cmd, args); // fabricate a request based on curl cmd the user provide
				connect(req.getHost(), req.getPort());// now the connection is build, we can call sendAndRecieve
														// function
			}
		} else if (!cmd.startsWith("httpc")) {
			System.out.println(args[0] + " not considered as a valid command");
		}
	}
	
	

	private void printHelp() {
		try {
			if(!isHelpFileCached) {
				in = new Scanner(new File("help.txt"));
				StringBuilder bld = new StringBuilder();
				String line; 
				while (in.hasNextLine() && (line = in.nextLine()) != null) {
					bld.append(line + "\r\n"); 
				} 
				this.helpFile = bld.toString();
				this.isHelpFileCached = true;				
			} 
			int index = helpFile.indexOf("httpc help get");
			System.out.println(helpFile.substring(0, index));
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void printGetHelp() {
		try {
			if(!isHelpFileCached) {
				in = new Scanner(new File("help.txt"));
				StringBuilder bld = new StringBuilder();
				String line; 
				while (in.hasNextLine() && (line = in.nextLine()) != null) {
					bld.append(line + "\r\n"); 
				} 
				this.helpFile = bld.toString();
				this.isHelpFileCached = true;				
			} 
			int index1 = helpFile.indexOf("httpc help get");
			int index2 = helpFile.indexOf("httpc help post");
			System.out.println(helpFile.substring(index1, index2));
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void printPostHelp() {
		try {
			if(!isHelpFileCached) {
				in = new Scanner(new File("help.txt"));
				StringBuilder bld = new StringBuilder();
				String line; 
				while (in.hasNextLine() && (line = in.nextLine()) != null) {
					bld.append(line + "\r\n"); 
				} 
				this.helpFile = bld.toString();
				this.isHelpFileCached = true;				
			} 
			int index = helpFile.indexOf("httpc help post");
			System.out.println(helpFile.substring(index));
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * trim each string of args and replace the old args
	 */
	private void sanitizeArgs(String[] args) {
		for (int i = 0; i < args.length; i++) {
			args[i] = args[i].trim();
		}
	}

	private GetRequest makeGetRequestObject(String cmd, String... args) {
		List<String> argsList = Arrays.asList(args);
		GetRequest req = new GetRequest();
		req.setMethod(HttpMethod.GET);
		if (cmd.contains("-v")) {
			req.setVerbose(true);
		}
		if (cmd.contains("-h")) {
			int index = argsList.indexOf("-h");
			String h = argsList.get(index + 1);
			String[] headerPairs = h.split("&"); // may throw error
			for (int i = 0; i < headerPairs.length; i++) {
				String[] pair = headerPairs[i].split(":");
				req.getHeaders().put(pair[0].trim(), pair[1].trim());
			}
		}
		if (cmd.contains("://")) {
			String url = null;
			for (int i = 0; i < args.length; i++) {
				if (args[i].contains("://")) {
					url = args[i];
					break;
				}
			}
			int index1 = url.indexOf("://");
			int index2 = url.indexOf('/', index1 + 3);
			if (index2 <= 0) {
				req.setHost(url.substring(index1 + 3, url.length() - 1));
				req.setURI("/");
			} else {
				req.setHost(url.substring(index1 + 3, index2));
				req.setURI(url.substring(index2, url.length() - 1));
			}

		}
		return req;
	}

	private PostRequest makePostRequestObject(String cmd, String... args) {
		//copy from above, and implement -d and -f
		PostRequest req = new PostRequest();
		List<String> argsList = Arrays.asList(args);
		req.setMethod(HttpMethod.POST);
		if (cmd.contains("-v")) {
			req.setVerbose(true);
		}
		if (cmd.contains("-h")) {
			int index = argsList.indexOf("-h");
			String h = argsList.get(index + 1);
			String[] headerPairs = h.split("&"); // may throw error
			for (int i = 0; i < headerPairs.length; i++) {
				String[] pair = headerPairs[i].split(":");
				req.getHeaders().put(pair[0].trim(), pair[1].trim());
			}
		}
		if (cmd.contains("://")) {
			String url = null;
			for (int i = 0; i < args.length; i++) {
				if (args[i].contains("://")) {
					url = args[i];
					break;
				}
			}
			int index1 = url.indexOf("://");
			int index2 = url.indexOf('/', index1 + 3);
			if (index2 <= 0) {
				req.setHost(url.substring(index1 + 3, url.length() - 1));
				req.setURI("/");
			} else {
				req.setHost(url.substring(index1 + 3, index2));
				req.setURI(url.substring(index2, url.length() - 1));
			}

		}
		if(cmd.contains("-d") && !cmd.contains("-f")) {
			int index = argsList.indexOf("-d");
			String bodyArg = argsList.get(index+1);
			req.setBody(bodyArg.substring(1, bodyArg.length()-1));
		}
		else if(!cmd.contains("-d") && cmd.contains("-f")) {
			int index = argsList.indexOf("-f");
			String directory = argsList.get(index+1);
			if(directory.startsWith("'") && directory.endsWith("'")) {
				directory = "\"" + directory.substring(1, directory.length()-1) + "\"";
			}
			StringBuilder bld = new StringBuilder();
			File file = new File(directory);
			try {
				in = new Scanner(file);
				String line; 
			    while ((line = in.nextLine()) != null) {
			    	bld.append(line + "\r\n"); 
			  	} 
			    req.setFile(bld.toString());
			} catch (FileNotFoundException e) {
				System.out.println("File Not Found");
				e.printStackTrace();
			} finally {
				in.close();
			}
				
		} else {
			try {
				throw new Exception();
			} catch (Exception e) {
				System.out.println("[-d] and [-f] can't exist at the same time.");
			}
		}
		return req;
	}

	/*
	 * after the request object is made, call this method to send the request and receive response as string
	 */
	private ClientHttpResponse sendAndReceive() {
		DataOutputStream out = null;
		BufferedReader in = null;
		try {
			out = new DataOutputStream(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (socket != null && out != null && in != null) {
			try {
				out.writeBytes(req.toString());
				StringBuilder bld = new StringBuilder();
				String line = null;
				res = new ClientHttpResponse();
				while ((line = in.readLine()) != null) {
					if(line.trim().length() == 0) {
						res.setHeader(bld.toString());
						bld = new StringBuilder();
					}
					bld.append(line + "\r\n");
				}
				res.setBody(bld.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return res;
	}

	/*
	 * call sendAndRecieve for implementation
	 * then, process response based on isVerbose
	 */
	public void displayResultInConsole() {
		sendAndReceive();
		// we have to modify what to print, we could create a response object
		if (!req.isVerbose()) {
			System.out.println(res.getBody());
		} else {
			System.out.println(res.toString());
		}
		close();
		this.isConnected = false;
	}

	public void displayResultInFile() {
		//implement this for extra credits
	}

	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
