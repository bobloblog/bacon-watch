import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

/**
 * This static class connects to the Sodexo server and scrapes any bacon-related information from it.
 * @author Jeffrey
 *
 */
public class BaconFinder
{
	public static boolean isNewURL(String oldURL)
	{
		try
		{
			if(new URL(oldURL).equals(discoverMenu()))
				return false;
		}catch(Exception e){}
		return true;
	}
	
	/**
	 * This method returns fully-populated BaconInstances gathered from the Sodexo server.
	 * @return An ArrayList of fully-populated BaconInstances.
	 */	
	public static ArrayList<BaconInstance> findBacon()
	{
		String weekday = "";
		String mealname = "";
		String baconType = "";
		String previousWeekday = "";//(See below)
		String previousMeal = "";//Used to determine if two bacon items are at the same time on the same day.
		
		ArrayList<BaconInstance> bacons = new ArrayList<BaconInstance>();
		
		BufferedReader in;
        String inputLine;
        
        //http://stevensondiningservices.com/WeeklyMenu4.7.htm <-- example of server address for April 7, 2014
        //http://stevensondiningservices.com/WeeklyMenu_038.htm <--example of server address for April 14, 2014
        //Addresses are non-standard, so must be parsed from the home page of the website.
        try
        {        	
        	URL siteAddress = discoverMenu();
        	siteAddress = new URL("https://m-stevenson.sodexomyway.com/dining-choices/index.html"); /////////////////////////////////////////
        	System.out.println("Connecting to menu...");
        	System.out.println(siteAddress);
       
            in = new BufferedReader(
            new InputStreamReader(
            siteAddress.openStream()));
            System.out.println("Connection Successful.");
        }
        catch(Exception e)
        {
            System.out.println("Connection Unsuccessful.");
            return new ArrayList<BaconInstance>();
        }
        
        try
        {
        	while ((inputLine = in.readLine()) != null)
        	{
        		System.out.println(inputLine);
        		if(inputLine.contains("<!-- "))//Days are displayed as <!-- MONDAY -->
        		{
        			try
        			{
        				int slot = inputLine.indexOf(" ", inputLine.indexOf("<!--"));
        		        weekday = inputLine.substring(slot + 1, inputLine.indexOf(" ", slot + 1));
        			}catch(Exception e){System.out.println("Error with weekday: " + weekday);}
        		}
        		else if(inputLine.contains("mealname"))
        		{
        			try{mealname = parseStringAfter(inputLine, "mealname");}catch(Exception e){}
        		}
        		else if(inputLine.toLowerCase().contains("bacon"))
        		{
        			try
        			{
        				baconType = parseStringAfter(inputLine, "pcls");//This is simply the code right before every meal type.
        				if(weekday.equals(previousWeekday) && mealname.equals(previousMeal))
        					bacons.get(bacons.size()-1).appendType(baconType);
        				else
        					bacons.add(new BaconInstance(mealname, weekday, "Rockland Marketplace", baconType));
        				previousWeekday = weekday;
        				previousMeal = mealname;
        				
        			}catch(Exception e){}		
        		}
        	}
        	in.close();
        }
        catch(Exception e)
        {
        	System.out.println("Someone burned the bacon!\n" + e);
        }
        
        return bacons;
	}
	
	private static URL discoverMenu()
	{
		BufferedReader in;
        String inputLine;
        
        //http://stevensondiningservices.com/WeeklyMenu4.7.htm <-- example of server address for April 7, 2014
        try
        {
        	System.out.println("Connecting to server...");
        	URL site = new URL("https://stevenson.sodexomyway.com/dining-choices/index.html");
        	//URL siteAddress = new URL(baseSite, "/WeeklyMenu" + month + "." + day +".htm");
        	System.out.println(site);
       
        
            in = new BufferedReader(
            new InputStreamReader(
            site.openStream()));
            System.out.println("Connection Successful.");

            URL test;
            
        	while ((inputLine = in.readLine()) != null)
        	{
        		System.out.println(inputLine);
        		if(inputLine.contains("WeeklyMenu"))//Sample code: <div align="left"><a href="WeeklyMenu_038.htm">Rockland Marketplace</a></div>
        		{
        			in.close();
        			String temp = inputLine.substring(inputLine.indexOf("href=\"") + 6, inputLine.indexOf("\"", inputLine.indexOf("href=\"") + 6));
        			System.out.println("Menu parse: " + temp);
        			test = new URL(site, inputLine.substring(inputLine.indexOf("href=\"") + 6, inputLine.indexOf("\"", inputLine.indexOf("href=\"") + 6)));
        			System.out.println("Menu URL: " + test);
        			return test;
        		}
        	}
        	in.close();
        	
        }catch(Exception e){System.out.println(e); return null;}
        
        return null;
	}
	
	/**
	 * Parses a string within standard XML code. eg <search>parsedString</search>
	 * @param line The line of code from which the String will be parsed.
	 * @param search Text within the code which denotes where to parse the line.
	 * @return The String with all XML tags and symbols removed.
	 */
	private static String parseStringAfter(String line, String search)
    {
    	int slot = line.indexOf(">", line.indexOf(search));
        String temp = line.substring(slot + 1, line.indexOf("<", slot + 1));
        return temp;
    }
}
