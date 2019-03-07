/**
 * 
 */
package lu.itrust.TS.model;

import org.junit.Assert;
import org.junit.Test;

import lu.itrust.business.TS.usermanagement.listner.helper.EncryptedPassword;

/**
 * @author eomar
 *
 */
public class TestEncryption {
	
	@Test
	public void loadFromMerge() {
		final EncryptedPassword source = new EncryptedPassword("testaolaosùqdlùqdqjqmsù", "test+1+2+3");
		final String mergeSource = source.toMerge();
		final EncryptedPassword destination = EncryptedPassword.fromMerge(mergeSource);
		Assert.assertNotNull("Destination cannot be null", destination);
		Assert.assertEquals("Bad password", destination.getEncryption(), source.getEncryption());
		Assert.assertEquals("Bad iv", destination.getIv(), source.getIv());
		Assert.assertEquals("Bad merge", destination.toMerge(), mergeSource);
	}

}
