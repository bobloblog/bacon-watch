import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


public class Initializer
{
	private static JFrame f;
	private static Driver driver;
	
	public static void main(String args[])
	{
		driver = new Driver();
		
		Thread GUIThread = new Thread(new Runnable()
		{
			public void run()
			{
				SwingUtilities.invokeLater(new Runnable()//Standard for Swing programs
				{
					public void run()
					{
						createAndShowGUI(); 
						addSystemTray();
					}
				});
			}
		});
		
		GUIThread.start();
	}
	
	private static void createAndShowGUI()
	 {		 
		 f = new JFrame("BaconWatch");
		 f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}//Java look-and-feel
	     catch (Exception e) {}//Non-fatal with no ill-effects. No need to bother the user.
		 f.setSize(300, 275);
		 f.setResizable(true);
	     f.setLocationRelativeTo(null);//Centers it
	     f.setVisible(true);
	     
	     f.add(new MainWindow(driver));
	     
	     //Allows for a graceful close that 
	     f.addWindowListener(new WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
				{
					driver.stopProcesses();//***************************************************
					f.dispose();
					System.exit(0); //calling the method is a must
				}
			});
	 }
	
	public static void addSystemTray()
	{		
		if (SystemTray.isSupported())
		{
			final SystemTray tray = SystemTray.getSystemTray();
			Image image = Toolkit.getDefaultToolkit().getImage("Resources/pig.jpg");
			final TrayIcon trayIcon;// = new TrayIcon(image);
			
			PopupMenu menu = new PopupMenu();
			MenuItem itemShowHide = new MenuItem("Show/Hide");
			MenuItem itemExit = new MenuItem("Exit");
			
			ActionListener showHide = new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					if(f.isVisible())
						f.setVisible(false);
					else
						f.setVisible(true);
				}
			};
			
			ActionListener exit = new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					WindowEvent wev = new WindowEvent(f, WindowEvent.WINDOW_CLOSING);
	                Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
				}
			};
			
			itemShowHide.addActionListener(showHide);
			itemExit.addActionListener(exit);
			menu.add(itemShowHide);
			menu.add(itemExit);
		    
			MouseListener mouseListener = new MouseListener()
			{

				@Override
				public void mouseClicked(MouseEvent e)
				{  
					try   
					  {  
					    if (e.getModifiers() == MouseEvent.BUTTON1_MASK)//If left button, NOT right button
					    {  
					    	if(f.isVisible())
								f.setVisible(false);
							else
								f.setVisible(true);
					    }  
					  } catch(Throwable ex) {  
					      System.err.println("ValidDialog.mouseClicked: " + ex);  
					  } 
				}

				@Override
				public void mouseExited(MouseEvent e) {
					 //This method is unused but required by MouseListener()	
				}

				@Override
				public void mousePressed(MouseEvent e) {
					//This method is unused but required by MouseListener()
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					//This method is unused but required by MouseListener()
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					//This method is unused but required by MouseListener()
				}
			};
			
			trayIcon = new TrayIcon(image, "SU Bacon Watch", menu);	
			trayIcon.setImageAutoSize(true);
			trayIcon.addMouseListener(mouseListener);
			
			
			try
			{
				tray.add(trayIcon);
			}
			catch(AWTException e)
			{
				System.out.println("error");
			}
		}
	}
	
}
