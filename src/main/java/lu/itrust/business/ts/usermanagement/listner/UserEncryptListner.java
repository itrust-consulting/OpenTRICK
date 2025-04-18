/**
 * 
 */
package lu.itrust.business.ts.usermanagement.listner;

import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import org.hibernate.event.spi.PostLoadEvent;
import org.hibernate.event.spi.PostLoadEventListener;
import org.hibernate.event.spi.PreCollectionUpdateEvent;
import org.hibernate.event.spi.PreCollectionUpdateEventListener;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.springframework.util.StringUtils;

import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.usermanagement.User;
import lu.itrust.business.ts.usermanagement.listner.helper.EncryptedPassword;
import lu.itrust.business.ts.usermanagement.listner.helper.PasswordEncryptionHelper;


/**
 * This class is a listener that performs encryption and decryption operations on the User entity.
 * It implements the PostLoadEventListener, PreUpdateEventListener, PreInsertEventListener, and PreCollectionUpdateEventListener interfaces.
 * 
 * The listener encrypts the user's secret (password) before persisting or updating the entity, and decrypts it after loading the entity from the database.
 * The encryption and decryption operations are performed using the PasswordEncryptionHelper class.
 * 
 * This listener is responsible for ensuring that the user's secret is always encrypted when stored in the database, and decrypted when retrieved from the database.
 * It also handles encryption and decryption for collections of User entities.
 * 
 * Note: This listener assumes that the User entity has a 'secret' field of type String, which represents the user's password.
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

	/**
	 * Encrypts the 2FA secret for the given user.
	 *
	 * @param user the user for whom the 2FA secret needs to be encrypted
	 */
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
			TrickLogManager.persist(e);
		}
	}

	/**
	 * Decrypts the 2FA secret for the specified user.
	 * 
	 * @param user the user object to decrypt the 2FA secret for
	 */
	@PostLoad
	public void decrypt(User user) {
		decrypt2FASecrete(user);
	}

	/**
	 * Decrypts the 2FA secret of the given user.
	 *
	 * @param user the user whose 2FA secret needs to be decrypted
	 */
	private void decrypt2FASecrete(User user) {
		try {
			EncryptedPassword source = EncryptedPassword.fromMerge(user.getSecret());
			if (source != null)
				user.setSecret(
						PasswordEncryptionHelper.decrypt(source.getEncryption(), user.getLogin(), source.getIv()));
		} catch (Exception e) {
			TrickLogManager.persist(e);
		}
	}

	/**
	 * This method is called after an entity is loaded from the database.
	 * If the loaded entity is an instance of User, it decrypts the user's data.
	 *
	 * @param event The PostLoadEvent object containing information about the event.
	 */
	@Override
	public void onPostLoad(PostLoadEvent event) {
		if (event.getEntity() instanceof User)
			decrypt((User) event.getEntity());
	}

	/**
	 * This method is called before an update operation is performed on an entity.
	 * It checks if the entity is an instance of User and encrypts the user data.
	 *
	 * @param event the PreUpdateEvent object containing the event data
	 * @return a boolean value indicating whether the event should be further processed
	 */
	@Override
	public boolean onPreUpdate(PreUpdateEvent event) {
		if (event.getEntity() instanceof User)
			encrypt((User) event.getEntity());
		return false;
	}

	/**
	 * This method is called before an entity is inserted into the database.
	 * It checks if the entity is an instance of User and encrypts it if necessary.
	 *
	 * @param event the PreInsertEvent object containing the entity being inserted
	 * @return true if the entity is successfully encrypted, false otherwise
	 */
	@Override
	public boolean onPreInsert(PreInsertEvent event) {
		if (event.getEntity() instanceof User)
			encrypt((User) event.getEntity());
		return false;
	}

	/**
	 * This method is called before a collection is updated.
	 * It checks if the affected owner is an instance of User and calls the encrypt method.
	 *
	 * @param event The PreCollectionUpdateEvent containing information about the update.
	 */
	@Override
	public void onPreUpdateCollection(PreCollectionUpdateEvent event) {
		if (event.getAffectedOwnerOrNull() instanceof User)
			encrypt((User) event.getAffectedOwnerOrNull());
	}
	
}
