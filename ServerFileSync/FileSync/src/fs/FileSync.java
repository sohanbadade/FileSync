package fs;

import java.io.File;

public class FileSync {
	private static final int PORT_NUMBER = 17555;
	private static String localName;
	private static String fullPathName;
	
	public static void main(String[] args) {
		try {
			if(args.length > 0) {
				if (args[0].equalsIgnoreCase("-s")) {
					server();
				} 
		}
		else {
				System.out.println("Invalid entry.");
			}
		}
		 catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void server() throws Exception {
		Server s = new Server(PORT_NUMBER);
		s.startServer();
	}
	
}
