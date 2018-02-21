/**
 * 
 */
package lu.itrust.business.TS.model.general.document.impl;

import javax.persistence.AttributeOverride;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.general.Language;
import lu.itrust.business.TS.model.general.document.AbstractDocument;

/**
 * @author eomar
 *
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "idReportTemplate"))
public class ReportTemplate extends AbstractDocument {

	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "fiLanguage", nullable = false)
	private Language language;

	@Enumerated(EnumType.STRING)
	@Column(name = "dtType", nullable = false)
	private AnalysisType type;

	public ReportTemplate() {
	}

	public ReportTemplate(AnalysisType type, Language language, String label, String version, String filename, byte[] file, long size) {
		super(label, version, filename, file, size);
		this.language = language;
		this.type = type;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public AnalysisType getType() {
		return type;
	}

	public void setType(AnalysisType type) {
		if (type == null || type.isHybrid())
			throw new TrickException("error.report.template.type", "Type cannot only be quantitative or qualitative");
		this.type = type;
	}

}
