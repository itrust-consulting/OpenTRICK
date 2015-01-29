package lu.itrust.business.TS.database.service;

/** EmailSenderService.java: <br>
 * Detailed description...
 *
 * @author smenghi itrust consulting s.a.rl.:
 * @version 
 * @since Jan 26, 2015
 */
public interface ServiceEmailSender {
	public static String RESOURCE_FOLDER = "emailTemplate/";

	public abstract void sendRegistrationMail(String user, String email);
}
