package fs;

import java.awt.*; 
import java.awt.event.*; 
import javax.swing.*; 
  
class Gui extends JFrame implements ActionListener  
{ 

	private static int PORT_NUMBER;
	private static String dirName;
	private static String fullDirName;
	private static String serverIP;
	private static boolean sync = true;


    // Declaration of object of JButton class. 
    JButton b1, b2; 
      
    // Constructor of Demo class 
    Gui( String dirName, String fullDirName, String serverIP, int port) 
    { 
		Gui.PORT_NUMBER = port;
		Gui.dirName = dirName;
		Gui.fullDirName = fullDirName;
		Gui.serverIP = serverIP;
		
		/////////////////////////////////////////////////////////////////////////////
         
        this.setLayout(null); 
        
        b1 = new JButton("Sync Now"); 
         
        b1.setBounds(130, 35, 150, 50); 
        this.add(b1); 
        b1.addActionListener(this); 
		/////////////////////////////////////////////////////////////////////////////
		b2 = new JButton("Exit Application"); 
        b2.setBounds(130, 105, 150, 50); 
        this.add(b2); 
        b2.addActionListener(this); 
		/////////////////////////////////////////////////////////////////////////////
    } 
  
    
    public void actionPerformed(ActionEvent evt) 
    { 
        if (evt.getSource() == b1)  
        { 
           wakeClient();
        } 
		
	if (evt.getSource() == b2)  
        { 
           System.exit(0);
        }
		
		
		
		
    }
	
	public static void wakeClient()
	{
		sync = true;
		//while (sync){
			try{
					Client c = new Client(dirName, fullDirName, serverIP, PORT_NUMBER);					
					c.runClient(); 

			}
			catch (Exception e){
				e.printStackTrace();
			}			
		//}
	}
} 
