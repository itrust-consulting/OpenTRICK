/**
 * 
 */
package lu.itrust.business.TS.model.parameter.helper.value;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.TS.model.parameter.AcronymParameter;

/**
 * @author eomar
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class ParameterValue implements IValue {

	@Id
	@Column(name = "idParameterValue")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name="dtName")
	private String name;

	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name="fiParameter")
	private AcronymParameter parameter;

	/**
	 * 
	 */
	public ParameterValue() {
	}

	/**
	 * @param name
	 * @param parameter
	 */
	public ParameterValue(String name, AcronymParameter parameter) {
		this.name = name;
		this.parameter = parameter;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.parameter.helper.value.IValue#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.model.parameter.helper.value.IValue#getParameter()
	 */
	@Override
	public AcronymParameter getParameter() {
		return parameter;
	}

	/**
	 * @param parameter
	 *            the parameter to set
	 */
	public void setParameter(AcronymParameter parameter) {
		this.parameter = parameter;
	}

	@Override
	public Integer getLevel() {
		return parameter.getLevel();
	}

	@Override
	public String getVariable() {
		return parameter.getAcronym();
	}

	@Override
	public Double getReal() {
		return parameter.getValue();
	}

}
