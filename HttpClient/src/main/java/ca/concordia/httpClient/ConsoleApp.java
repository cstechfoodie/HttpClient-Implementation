package ca.concordia.httpClient;

import java.util.Scanner;

import ca.concordia.httpClient.lib.Httpc;

/**
 * Hello world!
 *
 */
public class ConsoleApp 
{
    public static void main( String[] args )
    {
        
        System.out.println("Welcome to use Httpc. Enter 'httpc help' to check usage.");
		Scanner scanner = new Scanner(System.in);
		Httpc httpc = null;
		
		
		boolean inProcess = true;
		
		while(inProcess) {
//			try {
				httpc = new Httpc();
				String cmd = scanner.nextLine().trim();
				httpc.commandLineParser(cmd);
				if(httpc.isConnected()) {
					httpc.displayResultInConsole();					
					httpc.close();
				}
//			} catch (Exception e) {
//				System.out.println("Error cmd message. Please check 'httpc help'");
//			}
			
//			System.out.println("Have more request? (Y/N)");
//			inProcess = scanner.nextLine().trim().startsWith("Y") ? true : false;
//			System.out.println();
		}
		
		scanner.close();
    }
}
