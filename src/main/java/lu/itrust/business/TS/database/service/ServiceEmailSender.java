package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.model.analysis.AnalysisShareInvitation;
import lu.itrust.business.TS.usermanagement.EmailValidatingRequest;
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
	
	/**
	 * Send invitation 
	 * @param invitation
	 */
	void send(AnalysisShareInvitation invitation);
	
	/**
	 * Send request for email validation 
	 * @param validatingRequest
	 */
	void send(EmailValidatingRequest validatingRequest);

	/**
	 * Notify new user to all administrators
	 * @param recipient
	 * @param user
	 */
	void send(List<User> recipient, User user);
	
	/**
	 * Send request reset password
	 * @param password
	 * @param hotname
	 */
	void send(ResetPassword password,String hotname);

	/**
	 * Notify locked account
	 * @param code
	 * @param ip
	 * @param timeout
	 * @param username
	 */
	void sendAccountLocked(String code, String ip,Long timeout, String username);

	/**
	 * Send OTP code
	 * @param code
	 * @param timeout
	 * @param user
	 */
	void sendOTPCode(String code, Long timeout, User user);
}
