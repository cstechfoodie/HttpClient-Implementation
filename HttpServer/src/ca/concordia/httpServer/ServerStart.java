package ca.concordia.httpServer;

import java.util.Scanner;

public class ServerStart {

	public static void main(String[] args) {
		System.out.println("Welcome to use Https. Enter 'httpc help' to check usage.");
		Scanner scanner = new Scanner(System.in);
		Https https = new Https();

		try {
			String cmd = scanner.nextLine().trim();
			https.setupServer(cmd);
			if (https.isConnected()) {
				System.out.println("Https server is up!");
				https.receiveAndReply();
			}
		} catch (Exception e) {
			System.out.println("Error cmd message. Please check 'httpc help'");
		}

		scanner.close();

	}

}
