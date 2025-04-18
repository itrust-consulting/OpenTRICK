package lu.itrust.business.ts.database.migration.helper;

public class ExtendedParameterMapper  {
	
	private int id;
	
	private int idType;
	
	private int idAnalysis;
	
	private String label;
	
	private String description;
	
	private double value; 
	
	private int level;
	
	private double from;
	
	private double to;
	
	private String acronym;
	
	public ExtendedParameterMapper() {
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

	/**
	 * @return the idType
	 */
	public int getIdType() {
		return idType;
	}

	/**
	 * @param idType the idType to set
	 */
	public void setIdType(int idType) {
		this.idType = idType;
	}

	/**
	 * @return the idAnalysis
	 */
	public int getIdAnalysis() {
		return idAnalysis;
	}

	/**
	 * @param idAnalysis the idAnalysis to set
	 */
	public void setIdAnalysis(int idAnalysis) {
		this.idAnalysis = idAnalysis;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(double value) {
		this.value = value;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * @return the from
	 */
	public double getFrom() {
		return from;
	}

	/**
	 * @param from the from to set
	 */
	public void setFrom(double from) {
		this.from = from;
	}

	/**
	 * @return the to
	 */
	public double getTo() {
		return to;
	}

	/**
	 * @param to the to to set
	 */
	public void setTo(double to) {
		this.to = to;
	}

	/**
	 * @return the acronym
	 */
	public String getAcronym() {
		return acronym;
	}

	/**
	 * @param acronym the acronym to set
	 */
	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ExtendedParameterMapper [id=" + id + ", idType=" + idType + ", idAnalysis=" + idAnalysis + ", label=" + label + ", description=" + description + ", value=" + value
				+ ", level=" + level + ", from=" + from + ", to=" + to + ", acronym=" + acronym + "]";
	}
}
