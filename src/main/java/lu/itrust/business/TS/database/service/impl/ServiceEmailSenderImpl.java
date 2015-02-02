package lu.itrust.business.TS.database.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;

import lu.itrust.business.TS.database.service.ServiceEmailSender;
import lu.itrust.business.TS.usermanagement.ResetPassword;
import lu.itrust.business.TS.usermanagement.User;

import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
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

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private VelocityEngine velocityEngine;

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

		if (recipients == null || recipients.isEmpty()) {
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

	@Override
	public void sendResetPassword(final ResetPassword password, final String hotname, final Locale locale) {
		MimeMessagePreparator preparator = new MimeMessagePreparator() {
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
				message.setFrom(messageSource.getMessage("label.email.not_reply", new String[] { "@itrust.lu" }, "no-reply", locale));
				message.setSubject(messageSource.getMessage("label.reset.password.email.subject", null, "Reset password", locale));
				Map<String, Object> model = new LinkedHashMap<String, Object>();
				model.put("hostname", hotname);
				model.put("username", password.getUser().getLogin());
				message.setText(
						VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, RESOURCE_FOLDER
								+ (locale.getISO3Language().equalsIgnoreCase("fra") ? "reset-password-fr.vm" : "reset-password-en.vm"), "UTF-8", model), true);
				message.setTo(password.getUser().getEmail());
			}
		};
		javaMailSender.send(preparator);
	}

}
