package fs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;
import javax.swing.*; 


public class Client  extends JFrame{
	private static String directoryName;
	private static String sIP;
	private static ObjectInputStream ois;
	private static ObjectOutputStream oos;
	private static String fulldirectoryName;
	private static int port;
	private static final String DONE = "DONE";
	private static Socket socket;
	
	public Client(String directoryName, String fulldirectoryName, String sIP, int port) {
		Client.directoryName = directoryName;
		Client.fulldirectoryName = fulldirectoryName;
		Client.port = port;
		Client.sIP = sIP;
		System.out.println("Syncing " + directoryName);
	}

	public void runClient() throws Exception {

		socket = new Socket(sIP, port);
		
		oos = new ObjectOutputStream(socket.getOutputStream()); 		
		oos.writeObject(new String(directoryName));
		oos.flush();

		ois = new ObjectInputStream(socket.getInputStream()); 

		Boolean isPresent = (Boolean) ois.readObject();

		File parent = new File(fulldirectoryName); // skipping the base dir as it already should be set up on the server
		String[] children = parent.list();

		for (int i=0; i < children.length; i++) {
			visitAll(new File(parent, children[i]));
		}
		Vector<String> status = new Vector<String>();
		status.add(DONE);
		oos.writeObject(status);
		oos.flush();
		reConnect();

		if(isPresent)
			getFromServer();

		System.out.println("Finished sync");

		oos.close();
		ois.close();
		socket.close();
	}

	// Process all files and directories under dir
	private static void visitAll(File dir) throws Exception{
		Vector<String> vec = new Vector<String>();
		vec.add(dir.getName());
		vec.add(dir.getAbsolutePath().substring((dir.getAbsolutePath().indexOf(fulldirectoryName) + fulldirectoryName.length())));

		if(!dir.isDirectory()) {
			vec.add(new Long(dir.lastModified()).toString());
			oos.writeObject(vec);
			oos.flush();
			reConnect();
			
			Integer updateToServer = (Integer) ois.readObject(); //if true update server, else update from server

			if (updateToServer == 0) { 
				dir.delete(); // first delete the current file

				oos.writeObject(new Boolean(true)); // send "Ready"
				oos.flush();

				receiveFile(dir);

				oos.writeObject(new Boolean(true)); // send back ok
				oos.flush();

				Long updateLastModified = (Long) ois.readObject(); // update the last modified date for this file from the server
				dir.setLastModified(updateLastModified);				

			} else if (updateToServer == 1) { // update file from server.
				sendFile(dir);
				ois.readObject(); // make sure server got the file
			}
			
		} else {
			oos.writeObject(vec);
			oos.flush();
			reConnect();
			ois.readObject();
		}
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i=0; i<children.length; i++) {
				visitAll(new File(dir, children[i]));
			}
		}
	}


	private static void getFromServer() throws Exception {
		Boolean done = false;
		Boolean nAll = false;
		while(!done) {
			String path = (String) ois.readObject();

			if(path.equals(DONE)) {
				done = true;
				break;
			}
			oos.writeObject(new Boolean(true));
			oos.flush();

			File newFile = new File(fulldirectoryName + path);
			Boolean ifDir = (Boolean) ois.readObject();

			oos.writeObject(new Boolean(newFile.exists()));
			oos.flush();
////////////////////////////////////////////////////////////////
	if (!newFile.exists()) {
		ois.readObject();
		String input = "temp";
		String content = null;
		if (ifDir) { 
			 content = String.format("Problem at path %s. The directory %s exists on the server but at your end. \n Enter: \ny - Delete server's copy \nn - Get the new file  ", fulldirectoryName, path); 
			
				while(!input.equals("y") && !input.equals("n"))
				{
					input= JOptionPane.showInputDialog(content);
					if (!input.equals("y") && !input.equals("n"))
						JOptionPane.showMessageDialog(null,"Please enter y or n!");
				}
			} 
		else {
			content = String.format("Problem at path %s. The File %s exists on the server but at your end. \n Enter: \ny - Delete server's copy \nn - Get the new file  ", fulldirectoryName, path); 
			while(!input.equals("y") && !input.equals("n"))
				{
					input= JOptionPane.showInputDialog(content);
					if (!input.equals("y") && !input.equals("n"))
						JOptionPane.showMessageDialog(null,"Please enter y or n!");
				}
		}
		//System.out.println("Type 'y' for yes, 'n' for no");

///////   ///////




		if (input.equalsIgnoreCase("y")) 
		{
			if (ifDir) {
				oos.writeObject(new Boolean(true));
				oos.flush();
			} 
			else {
				oos.writeObject(new Integer(1));
				oos.flush();
			}
		} 
		else if (input.equalsIgnoreCase("n")) 
		{
			if (ifDir) {
				newFile.mkdir();
				oos.writeObject(new Boolean(false));
				oos.flush();
			} 
			else {
				oos.writeObject(new Integer(0));
				oos.flush();
				receiveFile(newFile);

				oos.writeObject(new Boolean(true));
				oos.flush();

				Long lastModified = (Long) ois.readObject();
				newFile.setLastModified(lastModified);

				oos.writeObject(new Boolean(true));
				oos.flush();
			}
		}
		
		
		
		
		
	}
}
}

	private static void sendFile(File dir) throws Exception {
		byte[] buff = new byte[socket.getSendBufferSize()];
		int bytesRead = 0;
		InputStream in = new FileInputStream(dir);
		while((bytesRead = in.read(buff))>0) {
			oos.write(buff,0,bytesRead);
		}
		in.close();
		oos.flush();
		reConnect();
	}
	
		private static void receiveFile(File dir) throws Exception {
		FileOutputStream wr = new FileOutputStream(dir);
		byte[] outBuffer = new byte[socket.getReceiveBufferSize()];
		int bytesReceived = 0;
		while((bytesReceived = ois.read(outBuffer))>0) {
			wr.write(outBuffer,0,bytesReceived);
		}
		wr.flush();
		wr.close();
		reConnect();
	}

	private static void reConnect() throws Exception {

		ois.close();
		oos.close();
		socket.close();
		socket = new Socket(sIP, port);
		ois = new ObjectInputStream(socket.getInputStream());
		oos = new ObjectOutputStream(socket.getOutputStream());
	}
}
