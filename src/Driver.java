import java.util.ArrayList;


public class Driver
{

	private ArrayList<BaconInstance> bacons;
	private Thread searchThread, alertThread;
	private boolean continueThread = true;
	private String previousURL = ""; //used to store the URL information for comparison
	
	private ArrayList<String> emails;

	public Driver()
	{		
		obtainAddresses();		
		obtainBacon();
        sendNewAlerts();
		//sizzle();
	}
	
	public void sizzle()
	{		
		alertThread = new Thread(new Runnable()
		{
			public void run()
			{
				while(continueThread)
				{
					try{Thread.sleep(450000);}//900,000 = 15 minutes
					catch (InterruptedException e)
					{System.out.println("Thread broken");break;}
					sendNewAlerts();
				}
			}
		});
		
		alertThread.start();
		
		searchThread = new Thread(new Runnable()
		{
			public void run()
			{
				while(continueThread)
				{
					try{Thread.sleep(1800000);}//1,800,000 = 30 minutes
					catch (InterruptedException e)
					{break;}
					obtainBacon();
					sendNewAlerts();
				}
			}
		});		
		searchThread.start();
	}
	
	public ArrayList<BaconInstance> getBacon()
	{
		return bacons;
	}
	
	public ArrayList<String> getEmails()
	{
		return emails;
	}
	
	public void updateInformation()
	{
		obtainAddresses();		
		obtainBacon();
		BaconScheduler.updateBaconList(bacons);
	}
	
	public void updateAddresses()
	{
		obtainAddresses();
	}
	
	public void obtainBacon()
	{     
        if(BaconFinder.isNewURL(previousURL))
        {
        	BaconManager.writeBacon(BaconFinder.findBacon());
        	bacons = BaconManager.readBacon();
        }
    }
	
	public void obtainAddresses()
	{
		emails = BaconAddresses.getAddresses();
	}
	
	public void sendNewAlerts()
	{
		if(emails != null && bacons != null)
			BaconWhistle.sendBaconMessages(BaconScheduler.getBaconAlertsUpdateBaconList(bacons), emails);
	}
	
	public void sendSpecialAlert(String subject, String message)
	{
		BaconWhistle.sendSpecialBaconMessage(subject, message, emails);
	}
	
	public void sendWelcomeMessage(String recipient)
	{
		BaconWhistle.sendMessage("Welcome!", "Welcome to SU Bacon Alert! We are still in beta so please pardon any bugs.", recipient);
	}
	
	public void stopProcesses()
	{
		continueThread = false;
		try
		{
			//searchThread.interrupt();
			alertThread.interrupt();
		}catch(Exception e){}//simply means threads never started. Safe enough.
	}
}
