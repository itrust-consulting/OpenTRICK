package lu.itrust.business.TS.model.parameter;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Transient;

/**
 * Represents a parameter whose value is assigned dynamically by external
 * notifications.
 * 
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Jun 10, 2015
 */
@Entity
@PrimaryKeyJoinColumn(name = "idDynamicParameter")
public class DynamicParameter extends AcronymParameter implements Cloneable {

	@Transient
	private int level;

	@Override
	public int getLevel() {
		return level;
	}

	@Override
	public void setLevel(int level) {
		this.level = level;
	}

}