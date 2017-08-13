import java.util.ArrayList;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

/**
 * This static class handles the sending of all bacon messages from the gmail account and to the Twitter account. It is important
 * to note that this class never contains recipient information.
 * @author Jeffrey
 *
 */
public class BaconWhistle
{	
	/**
	 * Sends a standard message about the specified items to the specified recipients AND to Twitter. The subject line is "Bacon Alert!"
	 * @param bacons An ArrayList of all the BaconInstances for which alerts should be sent using the standard message.
	 * @param addresses An ArrayList of the address to which alerts should be sent.
	 */
	public static void sendBaconMessages(ArrayList<BaconInstance> bacons, ArrayList<String> addresses)
	{
		for(int i = 0; i < bacons.size(); i++)
		{
			for(int j = 0; j < addresses.size(); j++)
				sendMessage("Bacon Alert!", bacons.get(i).getMessage(), addresses.get(j));
			sendTweet(bacons.get(i).getMessage());
		}
	}
	
	/**
	 * Sends a special message to the specified recipients AND to the Twitter account.
	 * @param subj The subject of the message.
	 * @param msg The body of the message.
	 * @param addresses An ArrayList of the addresses to which the alert should be sent.
	 */
	public static void sendSpecialBaconMessage(String subj, String msg, ArrayList<String> addresses)
	{
		for(int i = 0; i < addresses.size(); i++)
		{
			sendMessage(subj, msg, addresses.get(i));
			sendTweet(msg);
		}
	}
	
	/**
	 * This method sends a special message to the single specified address.
	 * @param subj The subject of the message.
	 * @param msg The body of the message.
	 * @param recipient The recipient of the message.
	 */
	public static void sendMessage(String subj, String msg, String recipient)
	{
		// Get system properties
		Properties props = System.getProperties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		
		Session session = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication("email@example.com","pwd");	//BEFORE RUN: update email address and pwd
					}
				});
	 
			try {
	 
				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress("email@example.com"));				//BEFORE RUN: update email addresss
				message.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(recipient));
				message.setSubject(subj);
				message.setText(msg);
	 
				Transport.send(message);
	 
			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}
		}
	
	/**
	 * This method tweets a message from the Twitter account.
	 * @param subj The subject of the message.
	 * @param msg The body of the message.
	 * @param recipient The recipient of the message.
	 */
	public static void sendTweet(String msg)
	{
		try{
			// The factory instance is re-useable and thread safe.
			Twitter twitter = TwitterFactory.getSingleton();
			Status status = twitter.updateStatus(msg);
			System.out.println("Successfully updated the status to [" + status.getText() + "].");
		}catch(Exception e){System.out.println(e);}
	}
}