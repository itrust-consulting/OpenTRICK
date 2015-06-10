package lu.itrust.business.TS.model.parameter;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * Represents a parameter whose value is assigned dynamically by external notifications.
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Jun 10, 2015
 */
@Entity
@PrimaryKeyJoinColumn(name = "idDynamicParameter")
@Inheritance(strategy = InheritanceType.JOINED)
public class DynamicParameter extends AcronymParameter implements Cloneable {

	/**
	 * Represents the scope of this parameter, used to group parameters coming from the same source together.
	 */
	@Access(AccessType.FIELD)
	@ManyToOne(fetch = FetchType.EAGER)
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "fiDynamicParameterScope", nullable = false)
	private DynamicParameterScope scope;

	/**
	 * Gets the scope of this parameter.
	 */
	public DynamicParameterScope getScope() {
		return scope;
	}

	/**
	 * Sets the scope of this parameter.
	 */
	public void setScope(DynamicParameterScope scope) {
		this.scope = scope;
	}

	/**
	 * {@inheritDoc}
	 * @see lu.itrust.business.TS.model.parameter.Parameter#clone()
	 */
	@Override
	public DynamicParameter clone() throws CloneNotSupportedException {
		DynamicParameter clone = (DynamicParameter) super.clone();
		clone.scope = (DynamicParameterScope) this.scope.clone();
		return clone;
	}

	/**
	 * {@inheritDoc}
	 * @see lu.itrust.business.TS.model.parameter.Parameter#duplicate()
	 */
	@Override
	public DynamicParameter duplicate() throws CloneNotSupportedException {
		DynamicParameter duplicate = (DynamicParameter) super.duplicate();
		duplicate.scope = (DynamicParameterScope) this.scope.clone(); // duplicating scope does not make sense
		return duplicate;
	}
}