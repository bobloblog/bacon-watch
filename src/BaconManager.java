import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;


public class BaconManager
{
	private static final String FILE_NAME = "Resources\\baconInstances.txt";
	
	public static boolean writeBacon(ArrayList<BaconInstance> bacons)
	{
		for(int i = 0; i < bacons.size(); i++)
			if(!writeBacon(bacons.get(i)))
				return false;
		return true;
	}
	
	public static boolean writeBacon(BaconInstance bacon)
	{
		if(hasBacon(bacon))
			return false;
		
		File file = new File(FILE_NAME);
		
		 try
		 {
			 PrintWriter out = new PrintWriter(new FileWriter(file, true));	

			 out.println("id=" + bacon.getID());
			 out.println("specialAlert=" + bacon.isSpecialAlert());
			 out.println("specialMessage=" + bacon.isSpecialMessage());
			 out.println("meal=" + bacon.getMeal());
			 out.println("day=" + bacon.getWeekday());
			 out.println("loc=" + bacon.getLocation());
			 out.println("message=" + bacon.getMessage());
			 out.println("alertTime=" + bacon.getAlertMilli());
			 out.println("staleTime=" + bacon.getStaleTime());
			 out.print("types=");
			 ArrayList<String> types = bacon.getTypes();
			 for(int i = 0; i < types.size(); i++)
				 out.print(types.get(i) + ";");
			 out.println("");
			 out.println("###");
			 out.close();
		 }catch(Exception e){System.out.println(e); return false;}
		 
		 return true;
	}
	
	public static ArrayList<BaconInstance> readBacon()
	{
		ArrayList<BaconInstance> bacons = new ArrayList<BaconInstance>();
		
		boolean specialAlert = false, specialMessage = false;
		String ID = "", meal = "", day = "", loc = "", message = "";
		long alertTime = 0, staleTime = 0;
		ArrayList<String> types = new ArrayList<String>();
		
		String test = "";
		
		try
		{
			BufferedReader in;
			in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(FILE_NAME))));
			String inputLine;
			while((inputLine = in.readLine()) != null)
			{
				if (inputLine.contains("###"))
					bacons.add(new BaconInstance(ID, specialAlert, specialMessage, meal, day, loc, message, alertTime, staleTime, types));
				else if(inputLine.contains("id="))
					ID = parseString(inputLine);
				else if(inputLine.contains("specialAlert="))
					specialAlert = parseBool(inputLine);
				else if(inputLine.contains("specialMessage="))
					specialMessage = parseBool(inputLine);
				else if(inputLine.contains("meal="))
					meal = parseString(inputLine);
				else if(inputLine.contains("day="))
					day = parseString(inputLine);
				else if(inputLine.contains("loc="))
					loc = parseString(inputLine);
				else if(inputLine.contains("message="))
					message = parseString(inputLine);
				else if(inputLine.contains("alertTime="))
					alertTime = parseLong(parseString(inputLine));
				else if(inputLine.contains("staleTime="))
					staleTime = parseLong(parseString(inputLine));
				else if(inputLine.contains("types="))
					types = parseTypes(parseString(inputLine));
			}
			in.close();
		}catch(Exception e){System.out.println(e);}
		
		return bacons;
	}
	
	public static boolean removeBacon(BaconInstance bacon)
	{
		if(!hasBacon(bacon))
			return false;
		
		File tempFile = new File(FILE_NAME);
		ArrayList<BaconInstance> bacons = readBacon();
		try
		{
			PrintWriter out = new PrintWriter(new FileWriter(tempFile, false));
			
			for(int i = 0; i < bacons.size(); i++)
				if(!bacons.get(i).equals(bacon))
					writeBacon(bacons.get(i));
			out.close();
		}catch(Exception e){return false;}
		
		return true;
	}
	
	public static boolean hasBacon(BaconInstance bacon)
	{
		try
		{
			BufferedReader in;
			in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(FILE_NAME))));
			String inputLine;
			while((inputLine = in.readLine()) != null)
				if(inputLine.contains(bacon.getID()))
				{
					in.close();
					return true;
				}
			in.close();
		}catch(Exception e){System.out.println(e);}
		
		return false;
	}
	
	public static boolean removeAllBacon()
	{
		File tempFile = new File(FILE_NAME);
		
		try
		{
			PrintWriter out = new PrintWriter(new FileWriter(tempFile, false));
			return true;
		}catch(Exception e){System.out.println(e); return false;}	
	}
	
	private static String parseString(String str)
	{
		return str.substring(str.indexOf("=") + 1);
	}
	
	private static boolean parseBool(String str)
	{
		return Boolean.valueOf(str);
	}
	
	private static ArrayList<String> parseTypes(String str)
	{
		ArrayList<String> temp = new ArrayList<String>();
		while(!str.equals(""))
		{
			temp.add(str.substring(0, str.indexOf(";")));
			str = str.substring(str.indexOf(";") + 1);
		}
		
		return temp;
	}
	
	private static long parseLong(String str)
	{
		return Long.parseLong(str);
	}
}
