import java.util.ArrayList;

/**
 * This class holds information for instances of bacon. It contains information including the type(s) of bacon offered at a
 * given meal on a given day in a given location and when the alert should be issued. It then organizes all of this data into
 * a standard message that can be easily retrieved. However, this class can also be used to contain a special message, subject,
 * and/or alert time. Simply calling the set methods for these variables with set the relevant flags.
 * @author Jeffrey
 *
 */
public class BaconInstance
{
	private ArrayList<String> types;
	private String meal = "", day = "", loc = "", message = "", ID="";
	private long alertTime, staleTime = 0;
	private boolean specialMessage = false, specialAlert = false;
	
	/**
	 * Every standard BaconInstance requires at least one type of bacon, the meal name, the weekday, and the location.
	 * @param mealname A String representation of the meal ("breakfast", "lunch", "dinner", or "late night").
	 * Sensitive to spelling. NOT case-sensitive.
	 * @param weekday A String representation of the day of the week ("Monday", "Tuesday", "Wednesday", etc).
	 * Sensitive to spelling. NOT case-sensitive.
	 * @param location A String representation of the location (eg "Rockland Marketplace"). Not sensitive.
	 * @param baconType Any number of String representations of the bacon type (eg "Bacon Potato Salad").
	 * Not sensitive. Do not include quotations.
	 */
	public BaconInstance(String mealname, String weekday, String location, String... baconType)
	{
		types = new ArrayList<String>();
		for(int i = 0; i < baconType.length; i++)
			types.add(baconType[i]);
		meal = mealname.toLowerCase();//converts BREAKFAST to breakfast
		day = weekday.substring(0,1) + weekday.substring(1).toLowerCase();//converts MONDAY to Monday
		loc = location;
		
		calculateAlert();
		
		updateID();
	}
	
	/**
	 * This constructor forms a special BaconInstance that includes only the alert time, the stale time, and a special message.
	 * @param alert The time, in milliseconds from 0000 January 1, 1970, after which the alert is valid.
	 * @param stale The time, in milliseconds from 0000 January 1, 1970, after which the alert is stale.
	 * @param msg The message to be sent out in alerts for this instance.
	 */
	public BaconInstance(long alert, long stale, String msg)
	{
		specialMessage = true;
		specialAlert = true;
		alertTime = alert;
		staleTime = stale;
		message = msg;
		updateID();
	}
	
	public BaconInstance(String ID, boolean specialAlert, boolean specialMessage, String meal, String day, String loc, String message, long alertTime, long staleTime, ArrayList<String> types)
	{
		this.specialAlert = specialAlert;
		this.specialMessage = specialMessage;
		this.meal = meal;
		this.day = day;
		this.loc = loc;
		this.message = message;
		this.alertTime = alertTime;
		this.staleTime = staleTime;
		this.types = types;
		this.ID = ID;
	}
	/**
	 * Every time a time variable is changed, the times must be updated. This method handles it all in one method call.
	 */
	private void calculateAlert()
	{
		if(specialAlert)
			return;
		calculateAlertDay();
		calculateAlertTime();
	}
	
	/**
	 * This method calculates, in milliseconds, midnight of the day on which the alert should be issued. It updates the 
	 * alertMilli variable with this information.
	 */
	private void calculateAlertDay()
	{
		/*
		 * Due to the nature of Java, a week begins on Thursday at midnight,
		 * because the most recent epoch began on THURSDAY January 1, 1970.
		 * All times are calculated in milliseconds from this moment.
		 * 
		 * For the purposes of this program, all times will be calculated in
		 * milliseconds from 0000 on Monday of the current week.
		 * This allows us to set an alarm to go off a certain number of milliseconds into the week.
		 * 
		 * The following multipliers set the alarm to the correct day
		 * M, T*1, W*2, TH*3, F*4, SA*5, SU*6
		 * TH, F, SA, and SU all take place on NEXT WEEK'S menu, which is why their multipliers are so high.
		 * 
		 * While Java's week starts on Thursday, Sodexo's starts on Monday. That's why things are so complicated.
		 */
		
		switch(day.toLowerCase())
		{
			case "monday":
				alertTime = BaconTime.currentWeek();
				break;
			case "tuesday":
				alertTime = BaconTime.currentWeek() + BaconTime.DAY;
				break;
			case "wednesday":
				alertTime = BaconTime.currentWeek() + BaconTime.DAY * 2;
				break;
			case "thursday":
				alertTime = BaconTime.currentWeek() + BaconTime.DAY * 3;
				break;
			case "friday":
				alertTime = BaconTime.currentWeek() + BaconTime.DAY * 4;
				break;
			case "saturday":
				alertTime = BaconTime.currentWeek() + BaconTime.DAY * 5;
				break;
			case "sunday":
				alertTime = BaconTime.currentWeek() + BaconTime.DAY * 6;
				break;
		}
	}
	
	/**
	 * This method calculates, in milliseconds, the time of day at which the alert should be issued and when the alert is stale.
	 * It appends the alertTime and staleTime variable with this information, resulting in both the day and time being represented.
	 */
	private void calculateAlertTime()
	{
		switch(meal)
		{
			case "breakfast":
				staleTime = alertTime + BaconTime.BREAKFAST_END_TIME;
				alertTime += BaconTime.BREAKFAST_ALERT_TIME;
				break;
			case "lunch":
				staleTime = alertTime + BaconTime.LUNCH_END_TIME;
				alertTime += BaconTime.LUNCH_ALERT_TIME;
				break;	
			case "dinner":
				staleTime = alertTime + BaconTime.DINNER_END_TIME;
				alertTime += BaconTime.DINNER_ALERT_TIME;
				break;
			case "late night":
				staleTime = alertTime + BaconTime.LATE_NIGHT_END_TIME;
				alertTime += BaconTime.LATE_NIGHT_ALERT_TIME;
				break;
		}
	}
	
	private void formMessage()//Creates a properly-punctuated (including the Oxford comma) and grammatically correct list
	{
		if(specialMessage)
			return;
		message = "";
		for(int i = 0; i < types.size(); i++)
			//eg "Bacon"; "Bacon and Turkey Bacon"; or "Bacon, Turkey Bacon, and Veggie Communist 'Bacon'"
			message += "\"" + types.get(i) + ((types.size() == 1 || types.size() - 1 == i) ? "\"" : ((i < types.size() - 2) ? "\", " : ((types.size() > 2) ? "\", and " : "\" and ")));
		//message +=  " will be served at " + meal + " on " + day +  " in " + loc + ".";
		message +=  " will be served " + (meal.equals("late night") ? "during extended hours " : "at " + meal) + " on " + day +  " in " + loc + ".";
	}
	
	private void updateID()
	{
		ID = "";
		if(specialAlert)
			ID += "1";
		else
			ID += "0";
		if(specialMessage)
			ID += "1";
		else
			ID += "0";
		ID += alertTime;
	}
	
	//PUBLIC METHODS
	
	/**
	 * This method allows an additional type of bacon to be included in this instance and any of its resulting alerts. Information
	 * about all types will always be displayed, though character limits may be reached with texts.
	 * @param newType A String representation of the bacon type (eg "Bacon Potato Salad"). Do not include quotations.
	 */
	public void appendType(String newType)
	{
		types.add(newType);
	}
	
	//GETS AND SETS
	
	/**
	 * This method returns the time of the alert for this instance, represented in milliseconds from 0000 January 1, 1970
	 * @return The alert time, in milliseconds, of this instance.
	 */
	public long getAlertMilli()
	{
		return alertTime;
	}
	
	public String getID()
	{
		updateID();
		return ID;
	}
	
	public String getLocation()
	{
		return loc;
	}
	
	/**
	 * This method returns the String representation of the meal ("breakfast", "lunch", "dinner", or "late night).
	 * @return The meal of this instance.
	 */
	public String getMeal()
	{
		return meal;
	}
	
	/**
	 * This method forms and returns the standard message following this formant:
	 * (type[s]) " will be served at " (meal) " on " (day) " in " (location). "."
	 * eg: "Crispy Bacon," "Bacon Soup," and "Turkey Bacon" will be served at breakfast on Monday in Rockland Marketplace.
	 * 
	 * Alternatively, if a special message has been set for this instance, the message will be returned unchanged.
	 * @return The message of this instance.
	 */
	public String getMessage()
	{
		formMessage();
		return message;
	}
	
	public long getStaleTime()
	{
		return staleTime;
	}
	
	public ArrayList<String> getTypes()
	{
		return types;
	}
	
	/**
	 * This method returns the String representation of the weekday ("Monday", "Tuesday", "Wednesday", etc).
	 * @return The weekday of this instance.
	 */
	public String getWeekday()
	{
		return day;
	}
	
	/**
	 * This method set the time of the alert for this instance, represented in milliseconds from 0000 January 1, 1970.
	 * Additionally, this method requires that a special stale time be added so that the alert is allowed to expire.
	 * This stale time is not to exceed 14 days from the time of creation.
	 * @param alert The alert time, in milliseconds, of this instance.
	 * @param saleTime The time, in milliseconds, at which the bacon will become stale.
	 */
	public void setAlertMilli(long alert, long stale)
	{
		if(stale > (BaconTime.DAY * 7))
			return;
		specialAlert = true;
		alertTime = alert;
		staleTime = stale;
		updateID();
	}
	
	/**
	 * This method set the String representation of the meal ("breakfast", "lunch", "dinner", or "late night) and updates the alert time.
	 * @param mealTime The meal of this instance.
	 */
	public void setMeal(String mealTime)
	{
		meal = mealTime;
		calculateAlert();
	}
	
	/**
	 * This method sets the message for this instance, overriding the default message structure and flagging this instance
	 * as containing a special message. This method should be used with non-standard alerts.
	 * eg: "Bacon Burgers will be served in the stadium parking lot before Friday's game."
	 * @param msg A new message for this instance.
	 */
	public void setMessage(String msg)
	{
		specialMessage = true;
		message = msg;
		updateID();
	}
	
	/**
	 * This method sets the String representation of the weekday ("Monday", "Tuesday", "Wednesday", etc) and updates the alert time.
	 * @param weekday The weekday of this instance.
	 */
	public void setWeekday(String weekday)
	{
		day = weekday;
		calculateAlert();
	}
	
	//STATE METHODS
	
	/**
	 * This method indicates whether or not this instance has a special alert time.
	 * @return True if this instance has a special alert time.
	 */
	public boolean isSpecialAlert()
	{
		return specialAlert;
	}
	
	/**
	 * The method indicates whether or not this instance has a special message.
	 * @return True if this instance has a special message.
	 */
	public boolean isSpecialMessage()
	{
		return specialMessage;
	}
	
	/**
	 * This method determines whether or not the current time has exceeded that of the stale time, indicating whether or not the
	 * instance is still valid.
	 * @return True if the instance has not expired.
	 */
	public boolean isStale()
	{
		if(System.currentTimeMillis() > staleTime)
			return true;
		return false;
		/*
		switch(meal)
		{
			case "breakfast":
				if(System.currentTimeMillis() > (alertTime + BaconTime.BREAKFAST_END_TIME))
					return true;
				break;
			case "lunch":
				if(System.currentTimeMillis() > (alertTime + BaconTime.LUNCH_END_TIME))
					return true;
				break;	
			case "dinner":
				if(System.currentTimeMillis() > (alertTime + BaconTime.DINNER_END_TIME))
					return true;
				break;
			case "late night":
				if(System.currentTimeMillis() > (alertTime + BaconTime.LATE_NIGHT_END_TIME))
					return true;
				break;
		}
		return false;
		*/
	}
	
	/**
	 * This method forms and returns the standard message following this formant:
	 * (type[s]) " will be served at " (meal) " on " (day) " in " (location). "."
	 * eg: "Crispy Bacon," "Bacon Soup," and "Turkey Bacon" will be served at breakfast on Monday in Rockland Marketplace.
	 * 
	 * Alternatively, if a special message has been set for this instance, the message will be returned unchanged.
	 * @return The message of this instance.
	 */
	public String toString()
	{
		formMessage();
		return message;
	}
	
	public boolean equals(BaconInstance bacon)
	{
		return ID.equals(bacon.getID());
	}
	
	
}

