---------------------------------
1. NAME:
---------------------------------
	File Sync

---------------------------------
2. TEAM:
---------------------------------
	Sohan Badade
	Mohammed Ali

---------------------------------
3. OVERVIEW:
---------------------------------
	File Sync is the project that helps you synchronize your files and folders that are on different computers.
	The idea of files/folders retaining multiple copies replicated (that are latest) accross multiple devices helps make our life easy.
	This project uses a client-server based architecture and socket programming paradigm to communicate between two different machine.

---------------------------------
4. STARTING APPLICATION:
---------------------------------
---------------------------------
4.1 How to run:
---------------------------------
	1. Start the server. (Mentioned below)

	2. Server starts listening to clients' requests.

	3. Starting all the clients. (Mentioned below)

	4. Create a folder name "javaSync" in your linux's home directory. 
	(This folder will be Synchronized for you. It will act as a google drive/dropbox's main folder.)

	5. Navigate to the "javaSync" folder in your client's machine.

	6. Make any required changes. (This is where you try diffrent tests, to see the effect of Synchronization

	7. Press Sync now button.
	
	8. All the files and folders inside this directory ("javaSync") will be Synchronized for you.

---------------------------------
4.2 Starting the Server:
---------------------------------
	1. Open terminal in linux.
	
	2. Navigate to "fs" directory on the server machine.
	
	3. Complile all the files using: javac *.java
	
	4. Navigate to "src" directory on the server machine.
	
	5. Start the server using: java fs.FileSync -s


---------------------------------
4.3 Starting the Client:
---------------------------------
	1. Create a folder named "javaSync" in linux's home directory.

	2. Open terminal in linux.
	
	3. Navigate to "fs" directory on the client machine.
	
	4. Complile all the files using: javac *.java
	
	5. Navigate to "src" directory on the client machine.
	
	6. Start the Client using: java fs.FileSync -s [IP addr of server machine] ~/javaSync/

---------------------------------
4.4 Starting Multiple Clients:
---------------------------------
	Follow the steps given in 4.3 for each client.


---------------------------------
5. RESULTS
---------------------------------
---------------------------------
5.1 Looking for Synchronized files on the Server (After Synchronization): 
---------------------------------
	Synchronized files on the server will be where the project files are located.
	
	1. To see, navigate to FileSync/src
	
	2. You will find a folder named "javaSync"
	
	3. This is where your files and folders will be found.

---------------------------------
5.2 Looking for Synchronized files on the Client (After Synchronization):
---------------------------------
	Synchronized files on the client will be on the clients /home directory.
	
	1. To see, navigate to /home
	
	2. You will find a folder named "javaSync"
	
	3. This is where your files and folders will be found.

---------------------------------
5.3 Exit Application:
---------------------------------
	Press the button provided under in the pop up called "Exit Application".



---------------------------------
6. CONTACT
---------------------------------
	For any help, contact:
	
	1. sohan.badade@gmail.com
	2. mxa1690@gmail.com
















