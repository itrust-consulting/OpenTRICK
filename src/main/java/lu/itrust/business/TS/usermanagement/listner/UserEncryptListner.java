/**
 * 
 */
package lu.itrust.business.TS.usermanagement.listner;

import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.hibernate.event.spi.PostLoadEvent;
import org.hibernate.event.spi.PostLoadEventListener;
import org.hibernate.event.spi.PreCollectionUpdateEvent;
import org.hibernate.event.spi.PreCollectionUpdateEventListener;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.springframework.util.StringUtils;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.TS.usermanagement.listner.helper.EncryptedPassword;
import lu.itrust.business.TS.usermanagement.listner.helper.PasswordEncryptionHelper;

/**
 * @author eomar
 *
 */
public class UserEncryptListner implements PostLoadEventListener, PreUpdateEventListener, PreInsertEventListener, PreCollectionUpdateEventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@PrePersist
	@PreUpdate
	public void encrypt(User user) {
		encryptTicketingSystem(user);
		encrypt2FASecrete(user);
	}

	private void encrypt2FASecrete(User user) {
		try {
			if (is2FASecreteEncrypted(user))
				return;
			String password = user.getSecret();
			if (StringUtils.isEmpty(password))
				return;
			EncryptedPassword encryptedPassword = PasswordEncryptionHelper.encrypt(password, user.getLogin());
			user.setSecret(encryptedPassword.getEncryption());
			user.setSetting(Constant.USER_IV_2_FACTOR_SECRET, encryptedPassword.getIv());
		} catch (Exception e) {
			TrickLogManager.Persist(e);
		}
	}

	private void encryptTicketingSystem(User user) {
		try {
			if (isTicketingSystemEncrypted(user))
				return;
			String username = user.getSetting(Constant.USER_TICKETING_SYSTEM_USERNAME), password = user.removeSetting(Constant.USER_TICKETING_SYSTEM_PASSWORD);
			if (StringUtils.isEmpty(username))
				return;
			EncryptedPassword encryptedPassword = PasswordEncryptionHelper.encrypt(password, username);
			user.setSetting(Constant.USER_TICKETING_SYSTEM_PASSWORD, encryptedPassword.getEncryption());
			user.setSetting(Constant.USER_TICKETING_SYSTEM_IV, encryptedPassword.getIv());
		} catch (Exception e) {
			TrickLogManager.Persist(e);
		}
	}

	@PostLoad
	public void decrypt(User user) {
		decryptTicketingSystem(user);
		decrypt2FASecrete(user);
	}

	private void decrypt2FASecrete(User user) {
		try {
			String iv = user.removeSetting(Constant.USER_IV_2_FACTOR_SECRET);
			if (!StringUtils.isEmpty(iv))
				user.setSecret(PasswordEncryptionHelper.decrypt(user.getSecret(), user.getLogin(), iv));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
		}
	}

	private void decryptTicketingSystem(User user) {
		try {
			String iv = user.removeSetting(Constant.USER_TICKETING_SYSTEM_IV);
			if (!StringUtils.isEmpty(iv))
				user.setSetting(Constant.USER_TICKETING_SYSTEM_PASSWORD,
						PasswordEncryptionHelper.decrypt(user.getSetting(Constant.USER_TICKETING_SYSTEM_PASSWORD), user.getSetting(Constant.USER_TICKETING_SYSTEM_USERNAME), iv));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
		}
	}

	private boolean is2FASecreteEncrypted(User user) {
		try {
			String iv = user.getSetting(Constant.USER_IV_2_FACTOR_SECRET);
			if (StringUtils.isEmpty(iv))
				return false;
			PasswordEncryptionHelper.decrypt(user.getLogin(), user.getSecret(), iv);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean isTicketingSystemEncrypted(User user) {
		try {
			String iv = user.getSetting(Constant.USER_TICKETING_SYSTEM_IV);
			if (StringUtils.isEmpty(iv))
				return false;
			String username = user.getSetting(Constant.USER_TICKETING_SYSTEM_USERNAME), password = user.getSetting(Constant.USER_TICKETING_SYSTEM_PASSWORD);
			PasswordEncryptionHelper.decrypt(password, username, iv);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void onPostLoad(PostLoadEvent event) {
		if (event.getEntity() instanceof User)
			decrypt((User) event.getEntity());
	}

	@Override
	public boolean onPreUpdate(PreUpdateEvent event) {
		if (event.getEntity() instanceof User)
			encrypt((User) event.getEntity());
		return false;
	}

	@Override
	public boolean onPreInsert(PreInsertEvent event) {
		if (event.getEntity() instanceof User)
			encrypt((User) event.getEntity());
		return false;
	}

	@Override
	public void onPreUpdateCollection(PreCollectionUpdateEvent event) {
		if (event.getAffectedOwnerOrNull() instanceof User)
			encrypt((User) event.getAffectedOwnerOrNull());
	}
}
