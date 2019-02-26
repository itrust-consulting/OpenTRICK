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
import lu.itrust.business.TS.model.general.Credential;
import lu.itrust.business.TS.usermanagement.UserCredential;
import lu.itrust.business.TS.usermanagement.listner.helper.EncryptedPassword;
import lu.itrust.business.TS.usermanagement.listner.helper.PasswordEncryptionHelper;

/**
 * @author eomar
 *
 */
public class CredentialEncryptListner implements PostLoadEventListener, PreUpdateEventListener, PreInsertEventListener, PreCollectionUpdateEventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@PrePersist
	@PreUpdate
	public void encrypt(Credential credential) {
		try {
			if (StringUtils.isEmpty(credential.getValue()) || PasswordEncryptionHelper.isEncrypted(credential.getValue(), credential.getName(), credential.getIv()))
				return;
			if (credential.getName() == null)
				credential.setName("");
			EncryptedPassword encryptedPassword = PasswordEncryptionHelper.encrypt(credential.getValue(), credential.getName());
			credential.setValue(encryptedPassword.getEncryption());
			credential.setIv(credential.getIv());
		} catch (Exception e) {
			TrickLogManager.Persist(e);
		}
	}

	@PostLoad
	public void decrypt(Credential credential) {
		try {
			if (StringUtils.isEmpty(credential.getIv()))
				return;
			credential.setValue(PasswordEncryptionHelper.decrypt(credential.getValue(), credential.getName(), credential.getIv()));
			credential.setIv(null);
		} catch (Exception e) {
			TrickLogManager.Persist(e);
		}
	}

	@Override
	public void onPreUpdateCollection(PreCollectionUpdateEvent event) {
		if (event.getAffectedOwnerOrNull() instanceof UserCredential)
			encrypt((UserCredential) event.getAffectedOwnerOrNull());
	}

	@Override
	public boolean onPreInsert(PreInsertEvent event) {
		if (event.getEntity() instanceof Credential)
			encrypt((Credential) event.getEntity());
		return false;
	}

	@Override
	public boolean onPreUpdate(PreUpdateEvent event) {
		if (event.getEntity() instanceof Credential)
			encrypt((Credential) event.getEntity());
		return false;
	}

	@Override
	public void onPostLoad(PostLoadEvent event) {
		if (event.getEntity() instanceof Credential)
			decrypt((Credential) event.getEntity());
	}

}
