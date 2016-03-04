package lu.itrust.business.TS.database.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.database.service.ServiceEmailSender;
import lu.itrust.business.TS.usermanagement.ResetPassword;
import lu.itrust.business.TS.usermanagement.User;

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
	private MessageSource messageSource;

	@Autowired
	private VelocityEngine velocityEngine;

	@Value("${app.settings.smtp.host}")
	private String mailserver;
	
	@Value("${app.settings.email.template}")
	private  String ressourceFolder = "../data/email/template/";


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
	public void sendRegistrationMail(final List<User> recipients, final User user)  {
		MimeMessagePreparator preparator;

		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		Properties p = new Properties();
		p.put("mail.smtp.host", mailserver);
		p.put("mail.smtp.localhost", "itrust.lu");
		Session con = Session.getInstance(p, null);
		sender.setSession(con);

		try {
			preparator = new MimeMessagePreparator() {
				public void prepare(MimeMessage mimeMessage) throws MessagingException  {
					Locale locale = user.getLocaleObject();
					MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
					message.setFrom(messageSource.getMessage("label.email.not_reply", new String[] { "@itrust.lu" }, "no-reply", locale));
					message.setSubject(messageSource.getMessage("label.registration.email.subject", null, "Registration", locale));
					Map<String, Object> model = new LinkedHashMap<String, Object>();
					model.put("title", messageSource.getMessage("label.registration.email.subject", null, "Registration", locale));
					model.put("user", user);
					message.setText(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, ressourceFolder
						+ (locale.getISO3Language().equalsIgnoreCase("fra") ? "new-user-info-fr.vm" : "new-user-info-en.vm"), "UTF-8", model), true);
					message.setTo(user.getEmail());
				}
			};

			sender.send(preparator);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (!(recipients == null || recipients.isEmpty())) {
			for (final User admin : recipients) {
				try {
					preparator = new MimeMessagePreparator() {
						public void prepare(MimeMessage mimeMessage) throws VelocityException, MissingResourceException, MessagingException  {
							Locale locale = admin.getLocaleObject();
							MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
							message.setFrom(messageSource.getMessage("label.email.not_reply", new String[] { "@itrust.lu" }, "no-reply", locale));
							message.setSubject(messageSource.getMessage("label.registration.admin.email.subject", null, "New TRICK Service user", locale));
							Map<String, Object> model = new LinkedHashMap<String, Object>();
							model.put("title", messageSource.getMessage("label.registration.admin.email.subject", null, "New TRICK Service user", locale));
							model.put("admin", admin);
							model.put("user", user);
							message.setText(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, ressourceFolder
								+ (locale.getISO3Language().equalsIgnoreCase("fra") ? "new-user-admin-fr.vm" : "new-user-admin-en.vm"), "UTF-8", model), true);
							message.setTo(admin.getEmail());
						}
					};
					sender.send(preparator);
				} catch (Exception e) {
					TrickLogManager.Persist(e);
				}
			}
		}
	}

	@Override
	public void sendResetPassword(final ResetPassword password, final String hotname) {

		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		Properties p = new Properties();
		p.put("mail.smtp.host", mailserver);
		p.put("mail.smtp.localhost", "itrust.lu");
		Session con = Session.getInstance(p, null);
		sender.setSession(con);

		try {
			MimeMessagePreparator preparator = new MimeMessagePreparator() {
				public void prepare(MimeMessage mimeMessage) throws MessagingException  {
					Locale locale = password.getUser().getLocaleObject();
					MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
					message.setFrom(messageSource.getMessage("label.email.not_reply", new String[] { "@itrust.lu" }, "no-reply", locale));
					message.setSubject(messageSource.getMessage("label.reset.password.email.subject", null, "Reset password", locale));
					Map<String, Object> model = new LinkedHashMap<String, Object>();
					model.put("title", messageSource.getMessage("label.reset.password.email.subject", null, "Reset password", locale));
					model.put("hostname", hotname);
					model.put("username", password.getUser().getLogin());
					message.setText(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, ressourceFolder
						+ (locale.getISO3Language().equalsIgnoreCase("fra") ? "reset-password-fr.vm" : "reset-password-en.vm"), "UTF-8", model), true);
					message.setTo(password.getUser().getEmail());
				}
			};
			sender.send(preparator);
		} catch (MailException e) {
			TrickLogManager.Persist(e);
		}
	}

	
}
