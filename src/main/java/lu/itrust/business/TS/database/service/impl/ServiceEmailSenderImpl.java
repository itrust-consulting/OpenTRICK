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

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.database.dao.DAOUser;
import lu.itrust.business.TS.database.service.ServiceEmailSender;
import lu.itrust.business.TS.model.analysis.AnalysisShareInvitation;
import lu.itrust.business.TS.usermanagement.EmailValidatingRequest;
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
@Transactional(readOnly = true)
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

	@Value("${app.settings.hostserver}")
	private String hostServer;

	@Autowired
	private TaskExecutor emailTaskExecutor;

	@Autowired
	private DAOUser daoUser;

	/**
	 * sendRegistrationMail: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceEmailSender#send(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void send(final List<User> recipients, final User user) {
		try {
			MimeMessagePreparator preparator = new MimeMessagePreparator() {
				public void prepare(MimeMessage mimeMessage) throws MessagingException, TemplateNotFoundException, MalformedTemplateNameException, ParseException,
						MissingResourceException, IOException, TemplateException {
					Locale locale = user.getLocaleObject();
					MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
					message.setFrom(emailSender);
					message.setSubject(messageSource.getMessage("label.registration.email.subject", null, "Registration", locale));
					Map<String, Object> model = new LinkedHashMap<>();
					model.put("title", messageSource.getMessage("label.registration.email.subject", null, "Registration", locale));
					model.put("firstName", StringEscapeUtils.escapeHtml4(user.getFirstName()));
					model.put("lastName", StringEscapeUtils.escapeHtml4(user.getLastName()));
					message.setText(FreeMarkerTemplateUtils.processTemplateIntoString(
							freemarkerConfiguration.getTemplate((locale.getISO3Language().equalsIgnoreCase("fra") ? "new-user-info-fr.ftl" : "new-user-info-en.ftl"), "UTF-8"),
							model), true);
					message.setTo(user.getEmail());
				}
			};

			emailTaskExecutor.execute(() -> javaMailSender.send(preparator));

		} catch (Exception e) {
			TrickLogManager.Persist(e);
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
							model.put("login", StringEscapeUtils.escapeHtml4(admin.getLogin()));
							model.put("userLogin", StringEscapeUtils.escapeHtml4(user.getLogin()));
							model.put("userEmail", StringEscapeUtils.escapeHtml4(user.getEmail()));
							model.put("firstName", StringEscapeUtils.escapeHtml4(user.getFirstName()));
							model.put("lastName", StringEscapeUtils.escapeHtml4(user.getLastName()));
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
	public void send(final ResetPassword password, final String hotname) {
		try {
			MimeMessagePreparator preparator = new MimeMessagePreparator() {
				public void prepare(MimeMessage mimeMessage) throws MessagingException, TemplateNotFoundException, MalformedTemplateNameException, ParseException,
						MissingResourceException, IOException, TemplateException {
					Locale locale = password.getUser().getLocaleObject();
					MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
					message.setFrom(emailSender);
					message.setSubject(messageSource.getMessage("label.reset.password.email.subject", null, "Reset password", locale));
					Map<String, Object> model = new LinkedHashMap<>();
					model.put("title", messageSource.getMessage("label.reset.password.email.subject", null, "Reset password", locale));
					model.put("hostname", hotname);
					model.put("username", StringEscapeUtils.escapeHtml4(password.getUser().getLogin()));
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
					model.put("expireDate", StringEscapeUtils.escapeHtml4(DateFormat.getDateInstance(DateFormat.FULL, locale).format(timestamp)));
					model.put("expireDateTime", StringEscapeUtils.escapeHtml4(DateFormat.getTimeInstance(DateFormat.MEDIUM, locale).format(timestamp)));
					model.put("firstName", StringEscapeUtils.escapeHtml4(user.getFirstName()));
					model.put("lastName", StringEscapeUtils.escapeHtml4(user.getLastName()));
					model.put("code", code);
					message.setText(
							FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfiguration
									.getTemplate((locale.getISO3Language().equalsIgnoreCase("fra") ? "on-time-password-fr.ftl" : "on-time-password-en.ftl"), "UTF-8"), model),
							true);
					message.setTo(user.getEmail());
				}
			};
			emailTaskExecutor.execute(() -> javaMailSender.send(preparator));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public void sendAccountLocked(String code, String ip, Long timeout, String username) {

		try {
			User user = daoUser.get(username);
			if (user == null)
				return;
			MimeMessagePreparator preparator = new MimeMessagePreparator() {
				public void prepare(MimeMessage mimeMessage) throws MessagingException, TemplateNotFoundException, MalformedTemplateNameException, ParseException,
						MissingResourceException, IOException, TemplateException {
					Locale locale = user.getLocaleObject();
					Timestamp timestamp = new Timestamp(timeout);
					MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
					message.setFrom(emailSender);
					message.setSubject(messageSource.getMessage("label.account.locked.subject", null, "TRICK Service account locked", locale));
					Map<String, Object> model = new LinkedHashMap<String, Object>();
					model.put("title", messageSource.getMessage("label.title.account.locked", null, "TRICK Service account locked", locale));
					model.put("expireDate", StringEscapeUtils.escapeHtml4(DateFormat.getDateInstance(DateFormat.FULL, locale).format(timestamp)));
					model.put("expireDateTime", StringEscapeUtils.escapeHtml4(DateFormat.getTimeInstance(DateFormat.MEDIUM, locale).format(timestamp)));
					model.put("hostname", String.format("%s/Unlock-account/%s", hostServer, code));
					model.put("firstName", StringEscapeUtils.escapeHtml4(user.getFirstName()));
					model.put("lastName", StringEscapeUtils.escapeHtml4(user.getLastName()));
					model.put("ip", ip);
					message.setText(FreeMarkerTemplateUtils.processTemplateIntoString(
							freemarkerConfiguration.getTemplate((locale.getISO3Language().equalsIgnoreCase("fra") ? "account-locked-fr.ftl" : "account-locked-en.ftl"), "UTF-8"),
							model), true);
					message.setTo(user.getEmail());
				}
			};
			emailTaskExecutor.execute(() -> javaMailSender.send(preparator));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
		}

	}

	@Override
	public void send(AnalysisShareInvitation invitation) {
		try {
			MimeMessagePreparator preparator = new MimeMessagePreparator() {
				public void prepare(MimeMessage mimeMessage) throws MessagingException, TemplateNotFoundException, MalformedTemplateNameException, ParseException,
						MissingResourceException, IOException, TemplateException {
					final Map<String, Object> model = new LinkedHashMap<String, Object>();
					final Locale locale = new Locale(invitation.getAnalysis().getLanguage().getAlpha3());
					final MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
					final User user = invitation.getHost();
					message.setFrom(emailSender);
					message.setSubject(messageSource.getMessage("label.share.analysis.subject", null, "TRICK Service: Risk analysis access", locale));
					model.put("title", messageSource.getMessage("label.title.share.analysis", null, "TRICK Service: Risk analysis access", locale));
					model.put("firstName", StringEscapeUtils.escapeHtml4(user.getFirstName()));
					model.put("lastName", StringEscapeUtils.escapeHtml4(user.getLastName()));
					model.put("accept", String.format("%s/Analysis/ManageAccess/%s/Accept", hostServer, invitation.getToken()));
					model.put("reject", String.format("%s/Analysis-access-management/%s/Reject", hostServer, invitation.getToken()));
					message.setText(FreeMarkerTemplateUtils.processTemplateIntoString(
							freemarkerConfiguration.getTemplate((locale.getISO3Language().equalsIgnoreCase("fra") ? "share-analysis-fr.ftl" : "share-analysis-en.ftl"), "UTF-8"),
							model), true);
					message.setTo(invitation.getEmail());
				}
			};
			emailTaskExecutor.execute(() -> javaMailSender.send(preparator));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
		}

	}

	@Override
	public void send(EmailValidatingRequest validatingRequest) {
		try {
			MimeMessagePreparator preparator = new MimeMessagePreparator() {
				public void prepare(MimeMessage mimeMessage) throws MessagingException, TemplateNotFoundException, MalformedTemplateNameException, ParseException,
						MissingResourceException, IOException, TemplateException {
					final Map<String, Object> model = new LinkedHashMap<String, Object>();
					final Locale locale = validatingRequest.getUser().getLocaleObject();
					final MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
					final User user = validatingRequest.getUser();
					message.setFrom(emailSender);
					message.setSubject(messageSource.getMessage("label.email.validation.subject", null, "TRICK Service: Email validation", locale));
					model.put("title", messageSource.getMessage("label.title.email.validation", null, "TRICK Service: Email validation", locale));
					model.put("firstName", StringEscapeUtils.escapeHtml4(user.getFirstName()));
					model.put("lastName", StringEscapeUtils.escapeHtml4(user.getLastName()));
					model.put("link", String.format("%s/Validate/%s/Email", hostServer, validatingRequest.getToken()));
					message.setText(FreeMarkerTemplateUtils.processTemplateIntoString(
							freemarkerConfiguration.getTemplate((locale.getISO3Language().equalsIgnoreCase("fra") ? "email-validation-fr.ftl" : "email-validation-en.ftl"), "UTF-8"),
							model), true);
					message.setTo(validatingRequest.getEmail());
				}
			};
			emailTaskExecutor.execute(() -> javaMailSender.send(preparator));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
		}
	}
}
