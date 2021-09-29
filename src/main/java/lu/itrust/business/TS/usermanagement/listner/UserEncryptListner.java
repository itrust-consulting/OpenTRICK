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
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.TS.usermanagement.listner.helper.EncryptedPassword;
import lu.itrust.business.TS.usermanagement.listner.helper.PasswordEncryptionHelper;

/**
 * @author eomar
 *
 */
public class UserEncryptListner implements PostLoadEventListener, PreUpdateEventListener, PreInsertEventListener,
		PreCollectionUpdateEventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@PrePersist
	@PreUpdate
	public void encrypt(User user) {
		encrypt2FASecrete(user);
	}

	private void encrypt2FASecrete(User user) {
		try {
			EncryptedPassword source = EncryptedPassword.fromMerge(user.getSecret());
			if (source != null
					&& PasswordEncryptionHelper.isEncrypted(source.getEncryption(), user.getLogin(), source.getIv()))
				return;
			String password = user.getSecret();
			if (!StringUtils.hasText(password))
				return;
			user.setSecret(PasswordEncryptionHelper.encrypt(password, user.getLogin()).toMerge());
		} catch (Exception e) {
			TrickLogManager.Persist(e);
		}
	}

	@PostLoad
	public void decrypt(User user) {
		decrypt2FASecrete(user);
	}

	private void decrypt2FASecrete(User user) {
		try {
			EncryptedPassword source = EncryptedPassword.fromMerge(user.getSecret());
			if (source != null)
				user.setSecret(
						PasswordEncryptionHelper.decrypt(source.getEncryption(), user.getLogin(), source.getIv()));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
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
