package lu.itrust.business.TS.model.parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Represents the notification scope, used to group notifications coming from the same source together.
 * A scope can be thought of as a group of parameters. It is mainly used for granting: 
 * - read access to specific customers;
 * - write access to specific users
 * for all parameters in the respective parameter group.
 * 
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Jun 10, 2015
 */
@Entity
public class DynamicParameterScope implements Cloneable {

	/** The scope identifier in the database. */
	@Id
	@GeneratedValue
	@Column(name = "idDynamicParameterScope")
	private int id = -1;

	/** The label used to refer to the notification scope. */
	@Column(name = "dtLabel", nullable = false, unique = true)
	private String label = "";

	/** Gets the scope identifier. */
	public int getId() {
		return id;
	}

	/** Sets the scope identifier. */
	public void setId(int id) {
		this.id = id;
	}

	/** Gets the scope label. */
	public String getLabel() {
		return label;
	}

	/** Sets the scope label. */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Clones this scope.
	 */
	@Override
	public DynamicParameterScope clone() throws CloneNotSupportedException {
		DynamicParameterScope clone = (DynamicParameterScope) super.clone();
		return clone;
	}
}
