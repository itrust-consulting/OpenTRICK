/**
 * 
 */
package lu.itrust.business.ts.database.migration;

import org.springframework.jdbc.core.JdbcTemplate;

import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.usermanagement.listner.helper.PasswordEncryptionHelper;

/**
 * @author eomar
 *
 */
public class V2_3_5__Migrate_no_encrypt_two_factor_secret extends TrickServiceDataBaseMigration {

	private final static String REQUEST_FOR_USER_AND_SECRET = "Select user.idUser as userId, user.dtLogin as username, setting1.dtValue as secret From UserSetting setting1 join User user on setting1.fiUser = user.idUser where setting1.dtName = 'user-2-factor-secret'";

	private final static String REQUEST_FOR_IV = "Select setting.dtValue as iv From UserSetting setting where setting.dtName = 'user-iv-2-factor-secret' and setting.fiUser = %d;";

	private final static String REQUEST_TO_UPDATE_SECRET = "UPDATE `UserSetting` SET `dtValue`='%s' where `dtName` = 'user-2-factor-secret' AND `fiUser`= %d";

	@Override
	public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
		jdbcTemplate.query(REQUEST_FOR_USER_AND_SECRET, (e) -> {
			final int userId = e.getInt("userId");
			final String username = e.getString("username");
			final String secret = e.getString("secret");
			final String iv = jdbcTemplate.query(String.format(REQUEST_FOR_IV, userId), (i) -> i.first() ? i.getString("iv") : null);
			if (iv == null || iv.isEmpty()) {
				try {
					jdbcTemplate.update(String.format(REQUEST_TO_UPDATE_SECRET, PasswordEncryptionHelper.encrypt(secret, username).toMerge(), userId));
				} catch (Exception e1) {
					throw new TrickException("error.encryption.two.factor.secret", "An error occured while encrypting of two factor secret!", e1);
				}
			}
		});
	}
}
