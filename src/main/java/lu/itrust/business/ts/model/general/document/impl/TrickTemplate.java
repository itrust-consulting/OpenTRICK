package lu.itrust.business.ts.model.general.document.impl;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lu.itrust.business.ts.model.analysis.AnalysisType;
import lu.itrust.business.ts.model.general.Language;
import lu.itrust.business.ts.model.general.document.AnalysisDocument;

/**
 * Represents a Trick Template, which is a specific type of Analysis Document.
 * It contains information about the template's analysis type, language, editability, type, and version.
 * It also provides methods to update the template and retrieve its key.
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "idTemplate"))
@AttributeOverride(name = "name", column = @Column(name = "dtFilename", unique = false))
public class TrickTemplate extends AnalysisDocument {

	@Enumerated(EnumType.STRING)
	@Column(name = "dtAnalysisType", nullable = false)
	private AnalysisType analysisType = AnalysisType.HYBRID;

	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "fiLanguage")
	private Language language;

	@Column(name = "dtEditable", nullable = false)
	private boolean editable = true;

	@Enumerated(EnumType.STRING)
	@Column(name = "dtType", nullable = false)
	private TrickTemplateType type;

	@Transient
	private boolean outToDate;

	public TrickTemplate() {
	}

	public TrickTemplate(TrickTemplateType type) {
		setType(type);
	}

	public TrickTemplate(AnalysisType analysisType) {
		setType(TrickTemplateType.REPORT);
		setAnalysisType(analysisType);
	}

	/**
	 * Represents a Trick Template.
	 * 
	 * This class provides a constructor to create a Trick Template object with the specified parameters.
	 */
	public TrickTemplate(TrickTemplateType type, Language language, String label, String version, String filename,
			byte[] file, long size) {
		this(type, AnalysisType.HYBRID, language, label, version, filename, file, size);
	}

	/**
	 * @param type
	 * @param analysisType
	 * @param language
	 * @param label
	 * @param version
	 * @param filename
	 * @param file
	 * @param size
	 */
	public TrickTemplate(TrickTemplateType type, AnalysisType analysisType, Language language, String label,
			String version,
			String filename,
			byte[] file, long size) {
		super(label, version, filename, file, size);
		setType(type);
		setLanguage(language);
		setAnalysisType(analysisType);
	}

	/**
	 * Returns the language of the document.
	 *
	 * @return the language of the document
	 */
	public Language getLanguage() {
		return language;
	}

	/**
	 * Sets the language of the trick template.
	 *
	 * @param language the language to set
	 */
	public void setLanguage(Language language) {
		this.language = language;
	}

	/**
	 * Returns the key for the TrickTemplate.
	 *
	 * @return the key in the format: "{analysisType}-_-Report-#$#-TS-{type}-#&#-Template-_-{language}"
	 */
	@Transient
	public String getKey() {
		return String.format("%s-_-Report-#$#-TS-%s-#&#-Template-_-%s", getAnalysisType(), getType(),
				getLanguage() == null ? "ALL" : getLanguage().getAlpha3());
	}

	/**
	 * Updates the current TrickTemplate object with the values from the provided template.
	 * If the provided template has non-null data, the data and name fields of the current object are updated as well.
	 *
	 * @param template The TrickTemplate object containing the updated values.
	 */
	public void update(TrickTemplate template) {
		setType(template.getType());
		setLength(template.getLength());
		setAnalysisType(template.getAnalysisType());
		setLabel(template.getLabel());
		setCreated(template.getCreated());
		setVersion(template.getVersion());
		setEditable(template.isEditable());
		setLanguage(template.getLanguage());
		if (template.getData() != null) {
			setData(template.getData());
			setName(template.getName());
		}
	}

	/**
	 * Returns a boolean value indicating whether the document is editable or not.
	 *
	 * @return true if the document is editable, false otherwise
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * Sets the editable flag for this TrickTemplate.
	 *
	 * @param editable true if the TrickTemplate is editable, false otherwise
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	/**
	 * Checks if the trick template is out of date.
	 *
	 * @return true if the trick template is out of date, false otherwise.
	 */
	public boolean isOutToDate() {
		return outToDate;
	}

	/**
	 * Sets the flag indicating whether the document is out of date.
	 *
	 * @param outToDate true if the document is out of date, false otherwise
	 */
	public void setOutToDate(boolean outToDate) {
		this.outToDate = outToDate;
	}

	/**
	 * Returns the analysis type of the trick template.
	 *
	 * @return the analysis type of the trick template
	 */
	public AnalysisType getAnalysisType() {
		return analysisType;
	}

	/**
	 * Sets the analysis type for the TrickTemplate.
	 *
	 * @param analysisType the analysis type to be set
	 */
	public void setAnalysisType(AnalysisType analysisType) {
		this.analysisType = analysisType;
	}

	/**
	 * Represents the type of a trick template.
	 */
	public TrickTemplateType getType() {
		return type;
	}

	/**
	 * Sets the type of the TrickTemplate.
	 *
	 * @param type the TrickTemplateType to set
	 */
	public void setType(TrickTemplateType type) {
		this.type = type;
	}

}
