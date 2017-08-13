import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


@SuppressWarnings("serial")
public class MainWindow extends JPanel implements ActionListener
{	
	private final int WIDTH = 300, HEIGHT = 250;
	private JTextArea console;
	
	private JScrollPane scroll;
	private JLabel subHeader;
	private Driver driver;
	
	private JButton emailSubmit, emailCancel;
	private JTextField emailInput;
	
	public MainWindow(Driver nDriver)
	{
		Dimension buttonDimension = new Dimension(125, 25);
		driver = nDriver;
		
		JLabel title;
		title = new JLabel("SU Bacon Watch Tools");
		title.setFont(new Font("Verdana", 1, 20));
		add(title);
		
		JButton getAddresses;
		getAddresses = new JButton("Addresses");
		getAddresses.setPreferredSize(buttonDimension);
		getAddresses.addActionListener(this);
		getAddresses.setActionCommand("getAddresses");
		add(getAddresses);
		
		JButton getWeekMenu;
		getWeekMenu = new JButton("Week Menu");
		getWeekMenu.setPreferredSize(buttonDimension);
		getWeekMenu.addActionListener(this);
		getWeekMenu.setActionCommand("getWeekMenu");
		add(getWeekMenu);
		
		JButton getFutureAlerts;
		getFutureAlerts = new JButton("Future Alerts");
		getFutureAlerts.setPreferredSize(buttonDimension);
		getFutureAlerts.addActionListener(this);
		getFutureAlerts.setActionCommand("getFutureAlerts");
		add(getFutureAlerts);
		
		JButton forceUpdate;
		forceUpdate = new JButton("Force Update");
		forceUpdate.setPreferredSize(buttonDimension);
		forceUpdate.addActionListener(this);
		forceUpdate.setActionCommand("forceUpdate");
		add(forceUpdate);
		
		JButton addEmail;
		addEmail = new JButton("Add Email");
		addEmail.setPreferredSize(buttonDimension);
		addEmail.addActionListener(this);
		addEmail.setActionCommand("addEmail");
		add(addEmail);
		
		JButton addPhone;
		addPhone = new JButton("Add Phone");
		addPhone.setPreferredSize(buttonDimension);
		addPhone.addActionListener(this);
		addPhone.setActionCommand("addPhone");
		add(addPhone);
		
		addConsole();
		
		subHeader = new JLabel();
		subHeader.setVisible(false);
		subHeader.setFont(new Font("Verdana", 1, 15));
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().compareTo("getAddresses") == 0)
		{
			ArrayList<String> temp = driver.getEmails();
			if(temp == null)
			{
				console.setText("Addresses Unavailable");
				return;
			}
			console.setText("Addresses (" + timestamp() + ")\n");
			for(int i = 0; i < temp.size(); i++)
				console.append(temp.get(i) + "\n");
		}	
		else if(e.getActionCommand().compareTo("getWeekMenu") == 0)
		{
			ArrayList<BaconInstance> temp = BaconFinder.findBacon();
			if(temp == null)
			{
				console.setText("Menu Unavailable");
				return;
			}
			console.setText("Menu for week of " + timestamp("MM/dd",BaconTime.currentWeek()) + ")\n");
			for(int i = 0; i < temp.size(); i++)
				console.append("----\n" + timestamp(temp.get(i).getAlertMilli()) + "\n" + temp.get(i) + "\n");
			console.setCaretPosition(0);
		}
		else if(e.getActionCommand().compareTo("getFutureAlerts") == 0)
		{
			ArrayList<BaconInstance> temp = driver.getBacon();
			if(temp == null)
			{
				console.setText("Future Alerts Unavailable");
				return;
			}
			console.setText("Future alerts\n(" + timestamp() + ")\n");
			for(int i = 0; i < temp.size(); i++)
				console.append("----\n" + timestamp(temp.get(i).getAlertMilli()) + "\n" + temp.get(i) + "\n");
			console.setCaretPosition(0);
		}
		else if(e.getActionCommand().compareTo("forceUpdate") == 0)
		{
			driver.updateInformation();
			console.setText("Information updated: " + timestamp());
		}
		else if(e.getActionCommand().compareTo("addEmail") == 0)
		{
			removeConsole();
			
			subHeader = new JLabel("Add Email Address");
			subHeader.setFont(new Font("Verdana", 1, 15));
			
			emailInput = new JTextField(23);
			
			emailSubmit = new JButton("Submit");
			emailSubmit.addActionListener(this);
			emailSubmit.setActionCommand("emailSubmit");
			
			emailCancel = new JButton("Cancel");
			emailCancel.addActionListener(this);
			emailCancel.setActionCommand("emailCancel");
			
			add(subHeader);
			add(emailInput);
			add(emailSubmit);
			add(emailCancel);
			subHeader.setVisible(true);
			emailInput.setVisible(true);
			emailSubmit.setVisible(true);
			emailCancel.setVisible(true);
			
			this.validate();
			this.repaint();
			
			disableButtons(emailSubmit, emailCancel);
			
			this.validate();
			this.repaint();
		}
		else if(e.getActionCommand().compareTo("emailSubmit") == 0)
		{						
			this.actionPerformed(new ActionEvent(emailCancel, ActionEvent.ACTION_PERFORMED, "emailCancel"));
			
			emailAction();
		}
		else if(e.getActionCommand().compareTo("emailCancel") == 0)
		{						
			remove(subHeader);
			remove(emailInput);
			remove(emailSubmit);
			remove(emailCancel);
			subHeader.setVisible(false);
			emailInput.setVisible(false);
			emailSubmit.setVisible(false);
			emailCancel.setVisible(false);
			
			addConsole();
			enableButtons();
			
			this.validate();
			this.repaint();
		}

		repaint();
	}
	
	private void emailAction()
	{
		String address = emailInput.getText();
		
		if(address.equals(""))
			return; //If they submit with nothing, they needn't be bothered with warning.
		
		if(address.length() > 7)//Seven is the minimum length: 1@3.567
			if(address.substring(1).contains("@"))//If the address contains '@' beyond the first character
			{
			 	if(		address.substring(address.length() - 4, address.length() - 3).equals(".")  //.com, .net, etc
			 		||  address.substring(address.length() - 3, address.length() - 2).equals(".")) //.ja, .eu, etc
			 	{
			 		for(int i = 0; i < address.length(); i++)			
			 		 	if(!(		address.charAt(i) == '@'
			 					||	address.charAt(i) == '_'
			 					||	address.charAt(i) == '-'
			 					||	address.charAt(i) == '.'
			 					||	(address.charAt(i) >= '0' && address.charAt(i) <= '9')
			 					|| 	(address.charAt(i) >= 'A' &&	address.charAt(i) <= 'Z')
			 					||	(address.charAt(i) >= 'a' &&	address.charAt(i) <= 'z')))
			 		 	{
			 				console.setText("\"" + address + "\" is not a valid address.");
			 		 		return;	
			 		 	}
			 	}
			 	else
			 	{
			 		console.setText("\"" + address + "\" is not a valid address.");
			 		return;
			 	}
			}
			 else
			 {
				 console.setText("\"" + address + "\" is not a valid address.");
				return;
			 }
		else
		{
			console.setText("\"" + address + "\" is not a valid address.");
			return;
		}
		
		if(!BaconAddresses.addAddress(emailInput.getText()))
			console.setText("\"" + address + "\" already exists!");
		driver.updateAddresses();
		return; 
	}
	
	
	private void removeConsole()
	{
		this.remove(console);
		this.remove(scroll);
		
		this.validate();
		this.repaint();
	}
	
	private void addConsole()
	{
		console = new JTextArea(5, 32);
		console.setEditable(false);
		console.setWrapStyleWord(true);//Wraps words
		console.setLineWrap(true);//Wraps, but by letter, not word. See above. Idk.
		console.setOpaque(false);
		this.add(console);
		
		scroll = new JScrollPane(console);//Creates a scroll pane for alerts
		scroll.setVisible(true);
		this.add(scroll);
		
		this.validate();
		this.repaint();
	}
	
	private void disableButtons(JButton... exceptions)
	{
		Component[] comps = this.getComponents();
		for(int i = 0; i < comps.length; i++)
			if(comps[i].getClass() == JButton.class)
			{
				comps[i].setEnabled(false);
				for(int j = 0; j < exceptions.length; j++)
					if(comps[i] == exceptions[j])
						comps[i].setEnabled(true);
			}
	}
	
	private void enableButtons(JButton... exceptions)
	{
		Component[] comps = this.getComponents();
		for(int i = 0; i < comps.length; i++)
			if(comps[i].getClass() == JButton.class)
			{
				comps[i].setEnabled(true);
				for(int j = 0; j < exceptions.length; j++)
					if(comps[i] == exceptions[j])
						comps[i].setEnabled(false);
			}
					
	}
	
	private String timestamp()
	{
		return new java.text.SimpleDateFormat("MM/dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
	}
	
	private String timestamp(long time)
	{
		return new java.text.SimpleDateFormat("MM/dd HH:mm:ss").format(new Date(time));
	}
	
	private String timestamp(String format, long time)
	{
		return new java.text.SimpleDateFormat(format).format(new Date(time));
	}

}
