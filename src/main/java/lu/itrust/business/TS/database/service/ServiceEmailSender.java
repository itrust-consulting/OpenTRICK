package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.usermanagement.ResetPassword;
import lu.itrust.business.TS.usermanagement.User;

/** EmailSenderService.java: <br>
 * Detailed description...
 *
 * @author smenghi itrust consulting s.a.rl.:
 * @version 
 * @since Jan 26, 2015
 */
public interface ServiceEmailSender {
	
	public static String RESOURCE_FOLDER = "email/template/";
	
	void sendRegistrationMail(List<User> recipient, User user) throws Exception;
	
	void sendResetPassword(ResetPassword password,String hotname);
}
