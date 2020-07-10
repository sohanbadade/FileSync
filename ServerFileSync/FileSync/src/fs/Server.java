package fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {
	private static int port;
	private static final String DONE = "DONE";
	private static Socket sock;
	private static ServerSocket socket;
	private static String baseDirectory;
	private static ObjectOutputStream oos;
	private static ObjectInputStream ois;

	public Server(int port) {
		Server.port = port;
	}

	public void startServer() throws Exception {
		System.out.println("Starting Server!");

		socket = new ServerSocket(port);
		while (true) {
			sock = socket.accept();
			ois = new ObjectInputStream(sock.getInputStream());
			baseDirectory = (String) ois.readObject();
			oos = new ObjectOutputStream(sock.getOutputStream());

			System.out.println();
			System.out.println("Client Connected! IP address: "+sock.getInetAddress().toString() );

			 
				File syncDirectory = new File(baseDirectory);
				Boolean syncDirectoryPresent = syncDirectory.exists();

				if(!syncDirectoryPresent)
					syncDirectory.mkdir();

				oos.writeObject(new Boolean(syncDirectoryPresent));
				oos.flush();

				Boolean done = false;

				while (!done) {
					Vector<String> vec = (Vector<String>) ois.readObject();
					reConnect();

					if(vec.elementAt(0).equals("DONE")) { 
						done = true; 
						break;
					}
					
					if(vec.size() == 2) {
						File dir = new File(baseDirectory, vec.elementAt(1));
						if (!dir.exists())
							dir.mkdir();
						oos.writeObject(new Boolean(true)); 
						oos.flush();
					}
					
					if (vec.size() == 3)
					{
						File newFile = new File(baseDirectory, vec.elementAt(1));

						Long lastModified = new Long(vec.elementAt(2));
						if (!newFile.exists() || (newFile.lastModified() < lastModified))
						{
							newFile.delete();
							oos.writeObject(new Integer(1));
							oos.flush();
							receiveFile(newFile);
							newFile.setLastModified(lastModified);
							oos.writeObject(new Boolean(true));
						}
						else if(newFile.exists() || (newFile.lastModified() > lastModified))
						{
							oos.writeObject(new Integer(0));
							oos.flush();
							ois.readObject();
							sendFile(newFile);
							ois.readObject();
							oos.writeObject(new Long(newFile.lastModified()));
							oos.flush();
						}
						else { 
							oos.writeObject(new Integer(2));
							oos.flush();
						}
					}
				}

				File baseDirectoryFile = new File(baseDirectory);
				if(syncDirectoryPresent)
					visitAllDirsAndFiles(baseDirectoryFile);

				oos.writeObject(new String("DONE"));
				oos.flush();
				System.out.println("Sync Complete!");
			oos.close();
			ois.close();
			sock.close();
		}
	}

	private static void visitAllDirsAndFiles(File dir) throws Exception {
		oos.writeObject(new String(dir.getAbsolutePath().substring((dir.getAbsolutePath().indexOf(baseDirectory) + baseDirectory.length()))));
		oos.flush();

		ois.readObject();

		Boolean isDirectory = dir.isDirectory();
		oos.writeObject(new Boolean(isDirectory));
		oos.flush();

		if (isDirectory) {
			if (!(Boolean) ois.readObject()) {
				oos.writeObject(new Boolean(true));
				oos.flush();

				Boolean delete = (Boolean) ois.readObject();

				if (delete) {
					delete(dir);
					return;
				} 
			}
		} else {
			if (!(Boolean) ois.readObject()) {
				oos.writeObject(new Boolean(true));
				oos.flush();

				Integer delete = (Integer) ois.readObject();

				if (delete == 1) {
					dir.delete();
					return;
				} else if (delete == 0) {
					sendFile(dir);

					ois.readObject();

					oos.writeObject(new Long(dir.lastModified()));
					oos.flush();

					ois.readObject();
				}
			}
		}

		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				visitAllDirsAndFiles(new File(dir, children[i]));
			}
		}
	}

	private static void sendFile(File dir) throws Exception {
		byte[] buff = new byte[sock.getSendBufferSize()];
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
		byte[] outBuffer = new byte[sock.getReceiveBufferSize()];
		int bytesReceived = 0;
		while((bytesReceived = ois.read(outBuffer))>0) {
			wr.write(outBuffer,0,bytesReceived);
		}
		wr.flush();
		wr.close();
		reConnect();
	}

	private static void reConnect() throws Exception {
		oos.close();
		ois.close();
		sock.close();
		sock = socket.accept();
		oos = new ObjectOutputStream(sock.getOutputStream());
		ois = new ObjectInputStream(sock.getInputStream());
	}

	private static void delete(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i=0; i<children.length; i++) {
				delete(new File(dir, children[i]));
			}
		}
		dir.delete();
	}
}
