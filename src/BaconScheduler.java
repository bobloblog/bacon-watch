import java.sql.Time;
import java.util.ArrayList;

/**
 * This static class determines whether or not an alert should be sent for a given item.
 * @author Jeffrey
 *
 */
public class BaconScheduler
{
	//gets bacon alerts AND updates the array passed to it to remove items that have already been alerted.
	/**
	 * Given an ArrayList of BaconInstances, this method determines which bacons need to have alerts sent now,
	 * returning them in a new ArrayList and deleting them DIRECTLY from the passed ArrayList; determines which bacons
	 * should have their alerts postponed, maintaining them in the passed ArrayList; and determines which bacons are
	 * stale, deleting them DIRECTLY from the passed ArrayList. There is NO ROLLBACK here.
	 * @param bacons An ArrayList of all BaconInstances for which alerts may need to be sent. This ArrayList is DIRECTLY changed.
	 * @return A new ArrayList of all BaconInstances for which alerts should be immediately sent.
	 */
	public static ArrayList<BaconInstance> getBaconAlertsUpdateBaconList(ArrayList<BaconInstance> bacons)
	{
		ArrayList<BaconInstance> temp = new ArrayList<BaconInstance>();
		for(int i = 0; i < bacons.size(); i++)
			if(System.currentTimeMillis() >= bacons.get(i).getAlertMilli())//if the current time is beyond the alert time
			{
				if(!bacons.get(i).isStale())//if the current time is still before the stale time
				{
					temp.add(bacons.get(i));
					BaconManager.removeBacon(bacons.get(i));
					System.out.println("Alert for " + bacons.get(i).getWeekday() + " sent. Instance Deleted.");
				}
				else//if the current time is beyond the stale time
				{
					BaconManager.removeBacon(bacons.get(i));
					System.out.println("Alert for " + bacons.get(i).getWeekday() + " stale. Instance deleted.");
				}
				System.out.println(	bacons.get(i).getMessage() +
									"\nCT:" + new Time(System.currentTimeMillis()).toLocaleString()+ 
									"\nAT: "+ new Time(bacons.get(i).getAlertMilli()).toLocaleString() +
									"\nST: " + new Time(bacons.get(i).getStaleTime()).toLocaleString());//DELETE AFTER TESTING
				bacons.remove(i);
				i--;//adjusts for the removal of an item from the ArrayList
			}
			else//if the current time is before the alert time. This can be removed after full release.
				System.out.println("Alert for " + bacons.get(i).getWeekday() + " postponed.\n" +
									bacons.get(i).getMessage() +
									"\nCT:" + new Time(System.currentTimeMillis()).toLocaleString() +
									"\nAT: " + new Time(bacons.get(i).getAlertMilli()).toLocaleString() +
									"\nST: " + new Time(bacons.get(i).getStaleTime()).toLocaleString());//DELETE AFTER TESTING
		return temp;
	}
	
	public static void updateBaconList(ArrayList<BaconInstance> bacons)
	{
		for(int i = 0; i < bacons.size(); i++)
			if(System.currentTimeMillis() >= bacons.get(i).getAlertMilli())//if the current time is beyond the alert time
			{
				if(!bacons.get(i).isStale())//if the current time is still before the stale time
					System.out.println("Alert for " + bacons.get(i).getWeekday() + " sent. Instance Deleted.");
				else//if the current time is beyond the stale time
					System.out.println("Alert for " + bacons.get(i).getWeekday() + " stale. Instance deleted.");
				System.out.println(	bacons.get(i).getMessage() +
									"\nCT:" + new Time(System.currentTimeMillis()).toLocaleString()+ 
									"\nAT: "+ new Time(bacons.get(i).getAlertMilli()).toLocaleString() +
									"\nST: " + new Time(bacons.get(i).getStaleTime()).toLocaleString());//DELETE AFTER TESTING
				bacons.remove(i);
				i--;//adjusts for the removal of an item from the ArrayList
			}
			else//if the current time is before the alert time. This can be removed after full release.
				System.out.println("Alert for " + bacons.get(i).getWeekday() + " postponed.\n" +
									bacons.get(i).getMessage() +
									"\nCT:" + new Time(System.currentTimeMillis()).toLocaleString() +
									"\nAT: " + new Time(bacons.get(i).getAlertMilli()).toLocaleString() +
									"\nST: " + new Time(bacons.get(i).getStaleTime()).toLocaleString());//DELETE AFTER TESTING
	}
	
	/**
	 * Given an ArrayList of BaconInstances, this method returns a new ArrayList containing all of the BaconInstances for which
	 * an alert should be sent immediately. This method has no effect on the passed ArrayList.
	 * @param bacons An ArrayList of all BaconInstances for which alerts may need to be sent.
	 * @return A new ArrayList of all BaconInstances for which alerts should be immediately sent.
	 */
	public static ArrayList<BaconInstance> getBaconAlerts(ArrayList<BaconInstance> bacons)
	{
		ArrayList<BaconInstance> temp = new ArrayList<BaconInstance>();
		for(int i = 0; i < bacons.size(); i++)
			if(System.currentTimeMillis() >= bacons.get(i).getAlertMilli())//if current time is beyond alert time
				if(bacons.get(i).isStale())//if current time is still before stale time
					temp.add(bacons.get(i));
		return temp;
	}
	
	public static ArrayList<BaconInstance> getPostponedBaconAlerts(ArrayList<BaconInstance> bacons)
	{
		ArrayList<BaconInstance> temp = new ArrayList<BaconInstance>();
		for(int i = 0; i < bacons.size(); i++)
			if(System.currentTimeMillis() < bacons.get(i).getAlertMilli())
				temp.add(bacons.get(i));
		return temp;
	}
}
