package com.appengine.planit.servlet;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.utils.SystemProperty;


/**
 * A servlet for sending a confirmation email when a (successfully) creates 
 * a conference.
 * 
 */
public class SendConfirmationEmailServlet extends HttpServlet {
	
	private static final Logger LOG = Logger.getLogger(
			SendConfirmationEmailServlet.class.getName());
	
	/**
	 * Override the doPost() method of the HttpServlet class to send the email
	 * This approach ensures that only we, application developers, can trigger the behavior
	 * (outside of its intended place) when testing the app
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
	
		String email = request.getParameter("email");
		String eventInfo = request.getParameter("eventInfo");
		
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		
		String body = "Congratulations, you have successfully created the following event:" + "\n"
		+ eventInfo;
		
		try {
			Message message = new MimeMessage(session);
			InternetAddress from = new InternetAddress(
					String.format("noreply@%s.appspotmail.com", 
							SystemProperty.applicationId.get()), "Planit");
			message.setFrom(from);
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, ""));
			message.setSubject("You've Created a New Event!");
			message.setText(body);
			Transport.send(message);		
			
		} catch (MessagingException e) {
			LOG.log(Level.WARNING, String.format("Failed to send an email to %s.", email), e);
			throw new RuntimeException(e);
		}
	}	
}