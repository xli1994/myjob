package com.lxs.web.controllerbeans.job.utils;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class EmailUtil
{
	private static final Logger logger = LogManager.getLogger(EmailUtil.class);
			
	//comcast account: works
	private static final String username = "xiaosongli2@comcast.net";//change accordingly
	private static final String password = "Lxslxs11";//change accordingly
	// Assuming you are sending email through comcast
	//private static final String from = "xiaosongli2@comcast.net";
	private static String host = "smpt.comcast.net";

	private static Properties props = new Properties();
	static
	{
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "587");
	}

	public static void sendEmail(String senderEmail, String recipientEmail, String subject,
			String content)
	{
		String [] emailTo = {recipientEmail};
		sendEmail(senderEmail, emailTo, null, subject, content, null);
	}

	
	public static void sendEmail(String senderEmail, String[] recipientEmails, String subject,
			String content, String attachmentFileName)
	{
		sendEmail(senderEmail, recipientEmails, null, subject, content, attachmentFileName);
	}

	public static void sendEmail(String senderEmail, String[] recipientEmails, String[] ccEmails,
			String subject, String content, String attachmentFileName)
	{
		// Get the Session object.
		Session session = getSession();
		//used for send file on fly
		//ByteArrayOutputStream outputStream = null;
		try
		{
			logger.debug("Start sending email: senderEmail="+ senderEmail+"; Subject="+subject+
					"; content="+content); 
			// Create a default MimeMessage object.
			Message message = new MimeMessage(session);

			//must use real sender email in testing with comcast, otherwise can't send
			//so, hardcode sender to my personal acount:
			senderEmail= username;
			
			// Set From: header field of the header.
			message.setFrom(new InternetAddress(senderEmail));

			// Set To: header field of the header. "to" could be a list separated with ","	
			Address addressTo[] = new InternetAddress[recipientEmails.length];
			for (int i = 0; i < recipientEmails.length; i++)
			{
				logger.debug("recipientEmails i="+i+"; mail="+recipientEmails[i]);
				addressTo[i] = new InternetAddress(recipientEmails[i]);
			}
			message.setRecipients(Message.RecipientType.TO, addressTo);

			//set cc:
			if (ccEmails != null && ccEmails.length > 0)
			{
				logger.debug("ccEmails not null:: ");
				Address addressCC[] = new InternetAddress[ccEmails.length];
				for (int i = 0; i < ccEmails.length; i++)
				{
					logger.debug("ccEmails i="+i+"; cc="+ ccEmails[i]);
					addressCC[i] = new InternetAddress(ccEmails[i]);
				}
				message.setRecipients(Message.RecipientType.CC, addressCC);
			}

			// Set Subject: header field
			message.setSubject(subject);

			//if no attachment, send it now
			if (attachmentFileName == null)
			{
				message.setText(content);
				Transport.send(message);
				return;
			}

			/*---------Add attachment -----------*/
			// Create the message part
			BodyPart messageBodyPart = new MimeBodyPart();

			// Now set the message
			messageBodyPart.setText(content);
			//may send html:
			//messageBodyPart.setContent(content, "text/html");

			// Create a multipar message
			Multipart multipart = new MimeMultipart();

			//add text message part
			multipart.addBodyPart(messageBodyPart);

			// Part two is attachment
			BodyPart attachmentBodyPart = new MimeBodyPart();
			//this works for file
			String filename = ConfigUtil.getUploadFileDir() + attachmentFileName;
			DataSource source = new FileDataSource(filename);
			attachmentBodyPart.setDataHandler(new DataHandler(source));
			attachmentBodyPart.setFileName(attachmentFileName);
			
			//add attachment to Multipart
			multipart.addBodyPart(attachmentBodyPart);

			// Send the complete message parts
			message.setContent(multipart);

			// Send message
			Transport.send(message);

			logger.debug("Sent message successfully....");
			
		}
		catch (MessagingException e)
		{
			logger.error("Something wrong when sending email, let it go");
			e.printStackTrace();
		}
	}
	/**
	 * get mail session
	 * @return
	 */
	private static Session getSession()
	{
		// Get the Session object.
		Session session = Session.getInstance(props, new javax.mail.Authenticator()
		{
			@Override
			protected PasswordAuthentication getPasswordAuthentication()
			{
				return new PasswordAuthentication(username, password);
			}
		});

		return session;
	}

}
