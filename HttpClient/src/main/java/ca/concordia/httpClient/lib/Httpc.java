package ca.concordia.httpClient.lib;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

public class Httpc {

	private Socket socket;

	private ClientHttpRequest req;

	private String res;

	private boolean isConnected;

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
		System.out.println("In help");
	}

	private void printGetHelp() {
		System.out.println("In get help");
	}

	private void printPostHelp() {
		System.out.println("In Post help");
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
		return req;
	}

	/*
	 * after the request object is made, call this method to send the request and receive response as string
	 */
	private String sendAndReceive() {
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
				while ((line = in.readLine()) != null) {
					bld.append(line + "\r\n");
				}
				res = bld.toString();
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
			// don't show response header
			int indexOfBody = res.indexOf('{');
			String body = res.substring(indexOfBody);
			System.out.println(body);
		} else {
			System.out.println(res);
		}

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
