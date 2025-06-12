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

import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.model.general.Credential;
import lu.itrust.business.ts.usermanagement.UserCredential;
import lu.itrust.business.ts.usermanagement.listner.helper.EncryptedPassword;
import lu.itrust.business.ts.usermanagement.listner.helper.PasswordEncryptionHelper;

/**
 * This class is responsible for encrypting and decrypting the credentials of a user.
 * It implements several event listeners to perform encryption and decryption operations
 * before and after certain database operations.
 */
public class CredentialEncryptListner implements PostLoadEventListener, PreUpdateEventListener, PreInsertEventListener,
		PreCollectionUpdateEventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Encrypts the given credential value using a password encryption algorithm.
	 * This method is called before persisting or updating a Credential object.
	 * If the credential value is already encrypted, the method returns without performing any encryption.
	 * 
	 * @param credential The Credential object to be encrypted.
	 */
	@PrePersist
	@PreUpdate
	public void encrypt(Credential credential) {
		try {
			EncryptedPassword source = EncryptedPassword.fromMerge(credential.getValue());
			if (source!=null && PasswordEncryptionHelper
					.isEncrypted(source.getEncryption(), credential.getName(), source.getIv()))
				return;
			EncryptedPassword encryptedPassword = PasswordEncryptionHelper.encrypt(credential.getValue(),
					credential.getName());
			credential.setValue(encryptedPassword.toMerge());
	
		} catch (Exception e) {
			TrickLogManager.persist(e);
		}
	}

	/**
	 * Decrypts the encrypted password value of the given Credential object.
	 * 
	 * @param credential The Credential object to decrypt.
	 */
	@PostLoad
	public void decrypt(Credential credential) {
		try {
			EncryptedPassword source = EncryptedPassword.fromMerge(credential.getValue());
			if (source == null)
				return;
			credential.setValue(
					PasswordEncryptionHelper.decrypt(source.getEncryption(), credential.getName(), source.getIv()));
		} catch (Exception e) {
			TrickLogManager.persist(e);
		}
	}

	/**
	 * This method is called before a collection is updated.
	 * It checks if the affected owner is an instance of UserCredential and calls the encrypt method.
	 *
	 * @param event The PreCollectionUpdateEvent object containing information about the update event.
	 */
	@Override
	public void onPreUpdateCollection(PreCollectionUpdateEvent event) {
		if (event.getAffectedOwnerOrNull() instanceof UserCredential)
			encrypt((UserCredential) event.getAffectedOwnerOrNull());
	}

	/**
	 * This method is called before an entity is inserted into the database.
	 * It checks if the entity is an instance of Credential and encrypts it if necessary.
	 *
	 * @param event The PreInsertEvent object containing information about the insert event.
	 * @return true if the entity is an instance of Credential and encryption is performed, false otherwise.
	 */
	@Override
	public boolean onPreInsert(PreInsertEvent event) {
		if (event.getEntity() instanceof Credential)
			encrypt((Credential) event.getEntity());
		return false;
	}

	/**
	 * This method is called before an update operation is performed on an entity.
	 * It checks if the entity is an instance of Credential and then encrypts the credential.
	 * It also forces encryption on the event.
	 *
	 * @param event the PreUpdateEvent object representing the update event
	 * @return false
	 */
	@Override
	public boolean onPreUpdate(PreUpdateEvent event) {
		if (event.getEntity() instanceof Credential) {
			encrypt((Credential) event.getEntity());
			forceEncryption(event);
		}
		return false;
	}

	/**
	 * Forces encryption for the given event.
	 *
	 * @param event The PreUpdateEvent object representing the update event.
	 */
	private void forceEncryption(PreUpdateEvent event) {
		final Object[] states = event.getState();
		final Credential credential = (Credential) event.getEntity();
		final String[] properties = event.getPersister().getEntityMetamodel().getPropertyNames();
		for (int i = 0; i < properties.length; i++) {
			switch (properties[i]) {
			case Credential.VALUE_PROPERTY_NAME:
				states[i] = credential.getValue();
				return;
			default:
				break;
			}
		}
	}

	/**
	 * This method is called after an entity is loaded from the database.
	 * If the loaded entity is an instance of Credential, it decrypts the entity.
	 *
	 * @param event The PostLoadEvent object containing information about the event.
	 */
	@Override
	public void onPostLoad(PostLoadEvent event) {
		if (event.getEntity() instanceof Credential)
			decrypt((Credential) event.getEntity());
	}


}
