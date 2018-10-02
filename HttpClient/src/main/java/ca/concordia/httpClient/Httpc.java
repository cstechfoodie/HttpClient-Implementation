package ca.concordia.httpClient;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;


public class Httpc {	
	private Socket socket;
	
	public void connect(String host, int port) {
			try {
				socket = new Socket(host, port);
				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            System.out.println("Type any thing then ENTER. Press Ctrl+C to terminate");
            //readEchoAndRepeat(socket);
	}
}
