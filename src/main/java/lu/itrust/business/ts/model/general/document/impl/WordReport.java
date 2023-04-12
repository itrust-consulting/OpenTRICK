/**
 * 
 */
package lu.itrust.business.ts.model.general.document.impl;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.ts.model.general.ReportType;
import lu.itrust.business.ts.model.general.document.UserDocument;
import lu.itrust.business.ts.usermanagement.User;

/**
 * @author eomar
 *
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name="id", column=@Column(name="idWordReport"))
public class WordReport extends UserDocument {

	@Enumerated(EnumType.STRING)
	@Column(name = "dtType", columnDefinition = "varchar(255) default 'STA'")
	ReportType type;

	/**
	 * 
	 */
	public WordReport() {
	}

	protected WordReport(ReportType type, User user, String identifier, String label, String version, String filename, byte[] file, long size) {
		super(user, identifier, label, version, filename, file, size);
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public ReportType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(ReportType type) {
		this.type = type;
	}

	public static WordReport BuildReport(String identifier, String label, String version, User user, String name, long length, byte[] file) {
		return new WordReport(ReportType.STA, user, identifier, label, version, name, file, length);
	}
	
	public static WordReport BuildSOA(String identifier, String label, String version, User user, String name, long length, byte[] file) {
		return new WordReport(ReportType.SOA, user, identifier, label, version, name, file, length);
	}

	public static WordReport BuildRiskSheet(String identifier, String label, String version, User user, String name, long length, byte[] file) {
		return new WordReport(ReportType.RISK_SHEET, user, identifier, label, version, name, file, length);
	}
	
	public static WordReport BuildRawRiskSheet(String identifier, String label, String version, User user, String name, long length, byte[] file) {
		return new WordReport(ReportType.RISK_SHEET_RAW, user, identifier, label, version, name, file, length);
	}

	public static WordReport BuildRiskRegister(String identifier, String label, String version, User user, String name, long length, byte[] file) {
		return new WordReport(ReportType.RISK_REGISTER, user, identifier, label, version, name, file, length);
	}
}
