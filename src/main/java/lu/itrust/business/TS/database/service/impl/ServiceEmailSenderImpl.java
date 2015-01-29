package lu.itrust.business.TS.database.service.impl;

import lu.itrust.business.TS.database.service.ServiceEmailSender;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;

/** ServiceEmailImpl.java: <br>
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
	
	private static String FROM_EMAIL="trickservice@itrust.lu";
	
	private static String TO_EMAIL="menghi@itrust.lu";
	
	private static String SUBJECT_EMAIL="New TRICK Service User";
	
    public void setMailSender(JavaMailSender mailSender) {  
        this.javaMailSender = mailSender;  
    }  
	
	/**
	 * sendRegistrationMail: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceEmailSender#sendRegistrationMail(java.lang.String, java.lang.String)
	 */
	@Override
	public void sendRegistrationMail(String user, String email) {
        //creating message  
        SimpleMailMessage message = new SimpleMailMessage();  
        message.setFrom(FROM_EMAIL);  
        message.setTo(TO_EMAIL);  
        message.setSubject(SUBJECT_EMAIL);  
        message.setText("text");  
        //sending message  
        javaMailSender.send(message);
	}

}
