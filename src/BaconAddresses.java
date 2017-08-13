import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;


public class BaconAddresses
{
	public static String 	CARRIER_VERIZON = "@vtext.com",
							CARRIER_SPRINT = "@messaging.sprintpcs.com",
							CARRIER_TMOBILE = "@tmomail.net",
							CARRIER_ATT = "@mms.att.net";							
	
	/**
	 * This method reads all addresses contained in the "addresses.txt" file in the "Resources" folder. All addresses are returned
	 * in an ArrayList of Strings
	 * @return An ArrayList of Strings representing the addresses.
	 */
	public static ArrayList<String> getAddresses()
	{
		ArrayList<String> addresses = new ArrayList<String>();
		
		try
		{
			BufferedReader in;
			in = new BufferedReader(new InputStreamReader(new FileInputStream(new File("Resources\\addresses.txt"))));
			String inputLine;
			while((inputLine = in.readLine()) != null)
				addresses.add(inputLine);
			in.close();
		}catch(Exception e){System.out.println(e);}
		
		return addresses;
	}
	
	/**
	 * This method adds an address to the "addresses.txt" file in the "Resources" folder if the address does not already exist.
	 * @param address The address to be added.
	 * @return True if the address was added, false if it failed or the address already existed.
	 */
	public static boolean addAddress(String address)
	{
		File file = new File("Resources\\addresses.txt");
		
		if(hasAddress(address))
			return false;
		
		 try
		 {
			 PrintWriter out = new PrintWriter(new FileWriter(file, true));	
			 out.println(address);
			 out.close();
		 }catch(Exception e){return false;}
		 
		 return true;
	}
	
	/**
	 * This method adds the phone number of a standard carrier to the "addresses.txt" file in the "Resources" folder.
	 * @param number A String representation of the phone number with NO spaces and NO dashes (eg 1234567890).
	 * @param carrier A BaconAddresses static final String of one of the major carriers:
	 * CURRENT_VERIZON, CURRENT_SPRINT, CURRENT_TMOBILE, CURRENT_ATT.
	 * @return True if the address was added, false if it failed or the address already existed.
	 */
	public static boolean addPhoneNumber(String number, String carrier)
	{
		number.trim();//removes trailing/leading whitespace.
		if(number.length() < 10 || (carrier != CARRIER_VERIZON && carrier != CARRIER_SPRINT && carrier != CARRIER_TMOBILE && carrier != CARRIER_ATT))
			return false;//returns false if the number is less than 10 digits or if the carrier is not supported.
		
		if(number.length() > 10 )//if the number is longer than it should be, it could have unwanted characters
			for(int i = 0; i < number.length(); i++)
			{
				if(number.charAt(i) == '-' || number.charAt(i) == ' ')//dashes and spaces are common and can be removed
				{
					number = (number.substring(0, i) + (number.substring(i + 1)));//removes the unwanted character
					i--;//Moves the index back one to correct for the removal
				}
				else if (number.charAt(i) < '0' || number.charAt(i) > '9')
					return false;//If the character is not a number, the entire number must be thrown away
			}
		
		if(number.length() > 10|| number.length() < 10)//If, after all that, the number still isn't the right size, it failed.
			return false;
		
		return addAddress(number + carrier);
	}
	
	/**
	 * This method removes an address from the "addresses.txt" file in the "Resources" folder if the address exists.
	 * @param address The address to be remove.
	 * @return True if the address was removed, false if it failed or the address did not exist.
	 */
	public static boolean removeAddress(String address)
	{
		if(hasAddress(address))
			return false;
		
		File tempFile = new File("Resources\\addresses.txt");
		ArrayList<String> allAddresses = getAddresses();
		try
		{
			PrintWriter out = new PrintWriter(new FileWriter(tempFile, false));
			
			for(int i = 0; i < allAddresses.size(); i++)
				if(!allAddresses.get(i).equals(address))
					out.println(allAddresses.get(i));
			out.close();
		}catch(Exception e){return false;}
		
		return true;
	}
	
	/**
	 * This method removes the phone number of a standard carrier from the "addresses.txt" file in the "Resources" folder.
	 * @param number A String representation of the phone number with NO spaces and NO dashes (eg 1234567890).
	 * @param carrier A BaconAddresses static final String of one of the major carriers:
	 * CURRENT_VERIZON, CURRENT_SPRINT, CURRENT_TMOBILE, CURRENT_ATT.
	 * @return True if the address was removed, false if it failed or the address did not exist.
	 */
	public static boolean removePhoneNumber(String number, String carrier)
	{
		number.trim();//removes trailing/leading whitespace.
		if(number.length() < 10 || (carrier != CARRIER_VERIZON && carrier != CARRIER_SPRINT && carrier != CARRIER_TMOBILE && carrier != CARRIER_ATT))
			return false;//returns false if the number is less than 10 digits or if the carrier is not supported.
		
		if(number.length() > 10 )//if the number is longer than it should be, it could have unwanted characters
			for(int i = 0; i < number.length(); i++)
			{
				if(number.charAt(i) == '-' || number.charAt(i) == ' ')//dashes and spaces are common and can be removed
				{
					number = (number.substring(0, i) + (number.substring(i + 1)));//removes the unwanted character
					i--;//Moves the index back one to correct for the removal
				}
				else if (number.charAt(i) < '0' || number.charAt(i) > '9')
					return false;//If the character is not a number, the entire number must be thrown away
			}
		
		if(number.length() > 10|| number.length() < 10)//If, after all that, the number still isn't the right size, it failed.
			return false;
		
		return removeAddress(number + carrier);
	}
	
	public static boolean hasAddress(String address)
	{	
		try
		{
			BufferedReader in;
			in = new BufferedReader(new InputStreamReader(new FileInputStream(new File("Resources\\addresses.txt"))));
			String inputLine;
			while((inputLine = in.readLine()) != null)
				if(inputLine.equals(address))
				{
					in.close();
					return true;
				}
			in.close();
		}catch(Exception e){return false;}
		
		return false;
	}
	
	public static boolean hasPhoneNumber(String number, String carrier)
	{
		return hasAddress(number + carrier);
	}
}
