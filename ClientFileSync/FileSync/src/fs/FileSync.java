package fs;

import java.io.File;

public class FileSync {
	private static final int PORT_NUMBER = 17555;
	private static String localName;
	private static String fullPathName;
	
	public static void main(String[] args) {
		try {
			if(args.length > 0 && args[0].equalsIgnoreCase("-c") && args.length > 1 && args.length < 4) {
					String[] path_split = args[2].split("/");
					String localName  = "./"+path_split[path_split.length-1];
					client(localName, args[2], args[1]);				
			} else {
				System.out.println("Invalid entry.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void client(String dirName, String fullDirName, String serverIP) throws Exception {
		 Gui gui = new Gui(dirName, fullDirName, serverIP, PORT_NUMBER); 
        // Setting Bounds of a Frame. 
        gui.setBounds(200, 200, 400, 300); 
        // Setting Resizeable status of frame as false 
        gui.setResizable(false); 
        // Setting Visible status of frame as true. 
        gui.setVisible(true); 
		//Client c = new Client(dirName, fullDirName, serverIP, PORT_NUMBER);
		//c.runClient();
	}
}

