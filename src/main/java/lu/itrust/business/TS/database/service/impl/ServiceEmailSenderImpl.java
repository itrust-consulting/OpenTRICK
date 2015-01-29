package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import lu.itrust.business.TS.database.service.ServiceEmailSender;
import lu.itrust.business.TS.usermanagement.User;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * ServiceEmailImpl.java: <br>
 * Detailed description...
 *
 * @author smenghi itrust consulting s.a.rl.:
 * @version
 * @since Jan 26, 2015
 */
@Service
public class ServiceEmailSenderImpl implements ServiceEmailSender {

	@Autowired
	private JavaMailSender javaMailSender;

	private static String FROM_EMAIL = "no-reply@itrust.lu";

	private static String ADMIN_PART = "A new user registered on TRICK Service using the following information:";

	private static String USER_PART = "Welcome to TRICK Service\n\nYou successfully registered to TRICK Service using the following information:";

	private static String LOGIN_NAME = "Login name: ";

	private static String NAME = "Name: ";

	private static String EMAIL = "Email: ";

	private static String ADMIN_SUBJECT = "New TRICK Service User";

	private static String USER_SUBJECT = "TRICK Service user registration";

	public void setMailSender(JavaMailSender mailSender) {
		this.javaMailSender = mailSender;
	}

	/**
	 * sendRegistrationMail: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceEmailSender#sendRegistrationMail(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void sendRegistrationMail(List<User> recipients, User user) throws Exception {
		// creating message
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(FROM_EMAIL);
		String messagebody = "";

		if(recipients == null || recipients.isEmpty()) {
			message.setSubject(USER_SUBJECT);
			messagebody = USER_PART + "\n\n";
			messagebody += LOGIN_NAME + user.getLogin() + "\n";
			messagebody += NAME + user.getFirstName() + " " + user.getLastName() + "\n";
			messagebody += EMAIL + user.getEmail() + "\n";
			message.setText(messagebody);
			message.setTo(user.getEmail());
			javaMailSender.send(message);
		} else {
		
			message.setSubject(ADMIN_SUBJECT);
			messagebody = ADMIN_PART + "\n\n";
			messagebody += LOGIN_NAME + user.getLogin() + "\n";
			messagebody += NAME + user.getFirstName() + " " + user.getLastName() + "\n";
			messagebody += EMAIL + user.getEmail() + "\n";
			message.setText(messagebody);
			
			for (User recipient : recipients) {
				message.setTo(recipient.getEmail());
				javaMailSender.send(message);
			}
			
			message.setSubject(USER_SUBJECT);
			messagebody = USER_PART + "\n\n";
			messagebody += LOGIN_NAME + user.getLogin() + "\n";
			messagebody += NAME + user.getFirstName() + " " + user.getLastName() + "\n";
			messagebody += EMAIL + user.getEmail() + "\n";
			message.setText(messagebody);
			message.setTo(user.getEmail());
			javaMailSender.send(message);
		}
	}

}
