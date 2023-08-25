/**
 * 
 */
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
 * @author eomar
 *
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

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	@Transient
	public String getKey() {
		return String.format("%s-_-Report-#$#-TS-%s-#&#-Template-_-%s", getAnalysisType(), getType(),
				getLanguage() == null ? "ALL" : getLanguage().getAlpha3());
	}

	public void update(TrickTemplate template) {
		setType(template.getType());
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

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean isOutToDate() {
		return outToDate;
	}

	public void setOutToDate(boolean outToDate) {
		this.outToDate = outToDate;
	}

	public AnalysisType getAnalysisType() {
		return analysisType;
	}

	public void setAnalysisType(AnalysisType analysisType) {
		this.analysisType = analysisType;
	}

	public TrickTemplateType getType() {
		return type;
	}

	public void setType(TrickTemplateType type) {
		this.type = type;
	}

}
