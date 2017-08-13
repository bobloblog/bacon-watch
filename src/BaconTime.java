import java.util.Calendar;

/**
 * A static class that does the standard calculations for the times required by this program. This class handles differences in
 * standards for starting the week (ie Thursday vs Sunday vs Monday) and adjusts it so that all weeks start Monday at midnight.
 * Additionally, it contains static information about when meal alerts should be issued and when meals end.
 * @author Jeffrey
 */
public class BaconTime
{
	public static final long	BREAKFAST_ALERT_TIME = -10800000, //2100 the night before
								LUNCH_ALERT_TIME = 36000000,//1000 the day of
								DINNER_ALERT_TIME = 57600000,//1600 the day of
								LATE_NIGHT_ALERT_TIME = 68400000,//1800 the day of
								
								BREAKFAST_END_TIME = 32400000,//0900 the day of
								LUNCH_END_TIME = 47700000,//1315 the day of
								DINNER_END_TIME = 67500000,//1845 the day of
								LATE_NIGHT_END_TIME = 73800000,//2030 the day of
								
								HOUR = 3600000,//One hour in milliseconds
								DAY = 86400000;//One day in milliseconds
	
	/**
	 * Calculates the integer value of the current day of the month
	 * @return The current day.
	 */
	public static int currentDayInt()
	{
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * Calculates the String value of the current day of the month
	 * @return The current day.
	 */
	public static String currentDayString()
	{
		return "" + currentDayInt();
	}
	
	/**
	 * Calculates the integer value of the day of the month for the Monday of the current week.
	 * @return The day of the month of Monday of the current week.
	 */
	public static int currentMondayInt()
	{
		Calendar cal = Calendar.getInstance();
		switch(cal.get(Calendar.DAY_OF_WEEK))
		{
			case 2://Monday
				return cal.get(Calendar.DAY_OF_MONTH);
			case 3://Tuesday
				return cal.get(Calendar.DAY_OF_MONTH) - 1;
			case 4://Wednesday
				return cal.get(Calendar.DAY_OF_MONTH) - 2;
			case 5://Thursday
				return cal.get(Calendar.DAY_OF_MONTH) - 3;
			case 6://Friday
				return cal.get(Calendar.DAY_OF_MONTH) - 4;
			case 7://Saturday
				return cal.get(Calendar.DAY_OF_MONTH) - 5;
			case 1://Sunday. Of course, this is where the Calendar week starts...
				return cal.get(Calendar.DAY_OF_MONTH) - 6;
		}
		
		return -1;
	}
	
	/**
	 * Calculates the integer value of the day of the month for the Monday of the current week.
	 * @return The day of the month of Monday of the current week.
	 */
	public static String currentMondayString()
	{
		return "" + currentMondayInt();
	}
	
	/**
	 * Calculates the integer value of the current month (ie Jan = 1, Jun = 6, etc).
	 * @return The current month.
	 */
	public static int currentMonthInt()
	{
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.MONTH)+1;
	}
	
	/**
	 * Calculates the String value of the current month (ie Jan = "1", Jun = "6", etc).
	 * @return The current month.
	 */	
	public static String currentMonthString()
	{
		return "" + currentMonthInt();
	}
	
	/**
	 * Calculates the beginning of the current week in milliseconds, marked by midnight on Monday. 
	 * @return Midnight on Monday of the current week.
	 */
	public static long currentWeek()
	{
		/*
		 * Due to the nature of Java, a week begins on Thursday at midnight,
		 * because the most recent epoch began on THURSDAY January 1, 1970.
		 * All times are calculated in milliseconds from this moment.
		 * 
		 * For the purposes of this program, all times will be calculated in
		 * milliseconds from 0000 on Monday of the current week.
		 * 
		 * Additionally, the times as of EDT 2014 seem to be 4 hours behind. Unknown cause. Timezones?
		 * 
		 * 
		 * 
		 * Things get really confusing here. I have provided a graphic showing Java weeks vs Sodexo weeks. The two systems
		 * are staggered, so the program must correct.
		 * 
		 * Different(IF):			  <--------------->			  <-------------->		Solution: Push Java back three days
		 * Same(ELSE):		<--------->				  <----------->						Solution: Push Java forward four days
		 * Weekday:			M | T | W | T*| F | S | S | M | T | W | T | F | S | S
		 * Java:			L | L | L | C | C | C | C | C | C | C | N | N | N | N
		 * Sodexo:     		L | L | L | L | L | L | L | C | C | C | C | C | C | C 
		 * 							Key: L = last, C = current, N = next
		 * 							*Thurs from which calcs are being made
		 * 
		 * IF	0000 Thurs of CURRENT_JAVA/LAST_SODEXO week through 2359 Sun of CURRENT_JAVA/LAST_SODEXO week?
		 *		Thurs-Sun are on CURRENT_JAVA week but still on LAST_SODEXO week,
		 *		thus we must move backward from Thurs to get to 0000 Mon of LAST_SODEXO week for the correct menu.
		 * ELSE	0000 Tues CURRENT_JAVA/CURRENT_SODEXO week through 2359 Wed CURRENT_JAVA/CURRENT_SODEXO week?
		 * 		Mon-Wed are on both the CURRENT_JAVA and CURRENT_SODEXO week,
		 * 		thus we must move forward from Thurs to get to 0000 Mon of CURRENT_SODEXO week for the correct menu.
		 * 
		 * Whew! If you were able to follow that, you deserve some bacon.
		 */
		
		long remainder = System.currentTimeMillis() % (DAY * 7);//How far are we currently into CURRENT_JAVA week?
		
		if(remainder < (DAY * 4) + (HOUR * 4))	
			return (System.currentTimeMillis() - (remainder) - (DAY * 3)) + (HOUR * 4);
		return (System.currentTimeMillis() - (remainder) + (DAY * 4)) + (HOUR * 4);
	}
	
	
}
