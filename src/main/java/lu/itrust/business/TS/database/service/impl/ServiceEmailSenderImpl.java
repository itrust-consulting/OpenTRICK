package lu.itrust.business.TS.database.service.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
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
	private Configuration freemarkerConfiguration;

	@Autowired
	private JavaMailSender javaMailSender;

	@Value("${app.settings.smtp.username}")
	private String emailSender;

	@Autowired
	private TaskExecutor emailTaskExecutor;

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
	public void sendRegistrationMail(final List<User> recipients, final User user) {
		try {
			MimeMessagePreparator preparator = new MimeMessagePreparator() {
				public void prepare(MimeMessage mimeMessage) throws MessagingException, TemplateNotFoundException, MalformedTemplateNameException, ParseException,
						MissingResourceException, IOException, TemplateException {
					Locale locale = user.getLocaleObject();
					MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
					message.setFrom(emailSender);
					message.setSubject(messageSource.getMessage("label.registration.email.subject", null, "Registration", locale));
					Map<String, Object> model = new LinkedHashMap<String, Object>();
					model.put("title", messageSource.getMessage("label.registration.email.subject", null, "Registration", locale));
					model.put("user", user);
					message.setText(FreeMarkerTemplateUtils.processTemplateIntoString(
							freemarkerConfiguration.getTemplate((locale.getISO3Language().equalsIgnoreCase("fra") ? "new-user-info-fr.ftl" : "new-user-info-en.ftl"), "UTF-8"),
							model), true);
					message.setTo(user.getEmail());
				}
			};

			emailTaskExecutor.execute(() -> javaMailSender.send(preparator));

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (!(recipients == null || recipients.isEmpty())) {
			for (final User admin : recipients) {
				try {
					MimeMessagePreparator preparator = new MimeMessagePreparator() {
						public void prepare(MimeMessage mimeMessage) throws MissingResourceException, MessagingException, TemplateNotFoundException, MalformedTemplateNameException,
								ParseException, IOException, TemplateException {
							Locale locale = admin.getLocaleObject();
							MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
							message.setFrom(emailSender);
							message.setSubject(messageSource.getMessage("label.registration.admin.email.subject", null, "New TRICK Service user", locale));
							Map<String, Object> model = new LinkedHashMap<String, Object>();
							model.put("title", messageSource.getMessage("label.registration.admin.email.subject", null, "New TRICK Service user", locale));
							model.put("admin", admin);
							model.put("user", user);
							message.setText(
									FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfiguration
											.getTemplate((locale.getISO3Language().equalsIgnoreCase("fra") ? "new-user-admin-fr.ftl" : "new-user-admin-en.ftl"), "UTF-8"), model),
									true);
							message.setTo(admin.getEmail());
						}
					};

					emailTaskExecutor.execute(() -> javaMailSender.send(preparator));

				} catch (Exception e) {
					TrickLogManager.Persist(e);
				}
			}
		}
	}

	@Override
	public void sendResetPassword(final ResetPassword password, final String hotname) {
		try {
			MimeMessagePreparator preparator = new MimeMessagePreparator() {
				public void prepare(MimeMessage mimeMessage) throws MessagingException, TemplateNotFoundException, MalformedTemplateNameException, ParseException,
						MissingResourceException, IOException, TemplateException {
					Locale locale = password.getUser().getLocaleObject();
					MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
					message.setFrom(emailSender);
					message.setSubject(messageSource.getMessage("label.reset.password.email.subject", null, "Reset password", locale));
					Map<String, Object> model = new LinkedHashMap<String, Object>();
					model.put("title", messageSource.getMessage("label.reset.password.email.subject", null, "Reset password", locale));
					model.put("hostname", hotname);
					model.put("username", password.getUser().getLogin());
					message.setText(FreeMarkerTemplateUtils.processTemplateIntoString(
							freemarkerConfiguration.getTemplate((locale.getISO3Language().equalsIgnoreCase("fra") ? "reset-password-fr.ftl" : "reset-password-en.ftl"), "UTF-8"),
							model), true);
					message.setTo(password.getUser().getEmail());
				}
			};
			emailTaskExecutor.execute(() -> javaMailSender.send(preparator));
		} catch (MailException e) {
			TrickLogManager.Persist(e);
		}
	}

	@Override
	public void sendOTPCode(String code, Long timeout, User user) {
		try {
			MimeMessagePreparator preparator = new MimeMessagePreparator() {
				public void prepare(MimeMessage mimeMessage) throws MessagingException, TemplateNotFoundException, MalformedTemplateNameException, ParseException,
						MissingResourceException, IOException, TemplateException {
					Locale locale = user.getLocaleObject();
					Timestamp timestamp = new Timestamp(timeout);
					MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
					message.setFrom(emailSender);
					message.setSubject(messageSource.getMessage("label.otp.email.code.subject", null, "TRICK Service authentication code", locale));
					Map<String, Object> model = new LinkedHashMap<String, Object>();
					model.put("title", messageSource.getMessage("label.otp.email.code.subject", null, "TRICK Service authentication code", locale));
					model.put("expireDate", DateFormat.getDateInstance(DateFormat.FULL, locale).format(timestamp));
					model.put("expireDateTime", DateFormat.getTimeInstance(DateFormat.MEDIUM, locale).format(timestamp));
					model.put("user", user);
					model.put("code", code);
					message.setText(
							FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfiguration
									.getTemplate((locale.getISO3Language().equalsIgnoreCase("fra") ? "on-time-password-fr.ftl" : "on-time-password-en.ftl"), "UTF-8"), model),
							true);
					message.setTo(user.getEmail());
				}
			};
			emailTaskExecutor.execute(() -> javaMailSender.send(preparator));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
