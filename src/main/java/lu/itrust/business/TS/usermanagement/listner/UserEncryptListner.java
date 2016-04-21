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
public class UserEncryptListner implements PostLoadEventListener, PreUpdateEventListener, PreInsertEventListener,PreCollectionUpdateEventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@PrePersist
	@PreUpdate
	public void encrypt(User user) {
		try {
			if (!isEncrypted(user))
				return;
			String username = user.getSetting(Constant.USER_TICKETING_SYSTEM_USERNAME), password = user.getSetting(Constant.USER_TICKETING_SYSTEM_PASSWORD);
			EncryptedPassword encryptedPassword = PasswordEncryptionHelper.encrypt(password, username);
			user.setSetting(Constant.USER_TICKETING_SYSTEM_PASSWORD, encryptedPassword.getEncryption());
			user.setSetting(Constant.USER_TICKETING_SYSTEM_IV, encryptedPassword.getIv());
		} catch (Exception e) {
			TrickLogManager.Persist(e);
		}
	}

	@PostLoad
	public void decrypt(User user) {
		try {
			String iv = user.removeSetting(Constant.USER_TICKETING_SYSTEM_IV);
			if (!StringUtils.isEmpty(iv)) {
				String username = user.getSetting(Constant.USER_TICKETING_SYSTEM_USERNAME), password = user.getSetting(Constant.USER_TICKETING_SYSTEM_PASSWORD);
				user.setSetting(Constant.USER_TICKETING_SYSTEM_PASSWORD, PasswordEncryptionHelper.decrypt(password, username, iv));
			}
		} catch (Exception e) {
			TrickLogManager.Persist(e);
		}
	}

	private boolean isEncrypted(User user) {
		try {
			String iv = user.getSetting(Constant.USER_TICKETING_SYSTEM_IV);
			if (StringUtils.isEmpty(iv))
				return true;
			String username = user.getSetting(Constant.USER_TICKETING_SYSTEM_USERNAME), password = user.getSetting(Constant.USER_TICKETING_SYSTEM_PASSWORD);
			PasswordEncryptionHelper.decrypt(password, username, iv);
			return true;
		} catch (Exception e) {
			TrickLogManager.Persist(e);
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
		if(event.getAffectedOwnerOrNull() instanceof User)
			encrypt((User) event.getAffectedOwnerOrNull());
			
	}

}
