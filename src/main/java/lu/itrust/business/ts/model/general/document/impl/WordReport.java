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
 * Represents a Word report document.
 * Extends the UserDocument class and adds additional properties specific to Word reports.
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
	 * Default constructor for WordReport.
	 */
	public WordReport() {
	}

	/**
	 * Constructor for WordReport with parameters.
	 *
	 * @param type      The type of the report.
	 * @param user      The user associated with the report.
	 * @param identifier The identifier of the report.
	 * @param label     The label of the report.
	 * @param version   The version of the report.
	 * @param filename  The filename of the report.
	 * @param file      The file content of the report.
	 * @param size      The size of the report.
	 */
	protected WordReport(ReportType type, User user, String identifier, String label, String version, String filename, byte[] file, long size) {
		super(user, identifier, label, version, filename, file, size);
		this.type = type;
	}

	/**
	 * Get the type of the report.
	 *
	 * @return The type of the report.
	 */
	public ReportType getType() {
		return type;
	}

	/**
	 * Set the type of the report.
	 *
	 * @param type The type of the report.
	 */
	public void setType(ReportType type) {
		this.type = type;
	}

	/**
	 * Build a Word report with the specified parameters.
	 *
	 * @param identifier The identifier of the report.
	 * @param label      The label of the report.
	 * @param version    The version of the report.
	 * @param user       The user associated with the report.
	 * @param name       The name of the report.
	 * @param length     The length of the report.
	 * @param file       The file content of the report.
	 * @return A new WordReport instance.
	 */
	public static WordReport BuildReport(String identifier, String label, String version, User user, String name, long length, byte[] file) {
		return new WordReport(ReportType.STA, user, identifier, label, version, name, file, length);
	}

	/**
	 * Build a Word report of type SOA with the specified parameters.
	 *
	 * @param identifier The identifier of the report.
	 * @param label      The label of the report.
	 * @param version    The version of the report.
	 * @param user       The user associated with the report.
	 * @param name       The name of the report.
	 * @param length     The length of the report.
	 * @param file       The file content of the report.
	 * @return A new WordReport instance of type SOA.
	 */
	public static WordReport BuildSOA(String identifier, String label, String version, User user, String name, long length, byte[] file) {
		return new WordReport(ReportType.SOA, user, identifier, label, version, name, file, length);
	}

	/**
	 * Build a Word report of type Risk Sheet with the specified parameters.
	 *
	 * @param identifier The identifier of the report.
	 * @param label      The label of the report.
	 * @param version    The version of the report.
	 * @param user       The user associated with the report.
	 * @param name       The name of the report.
	 * @param length     The length of the report.
	 * @param file       The file content of the report.
	 * @return A new WordReport instance of type Risk Sheet.
	 */
	public static WordReport BuildRiskSheet(String identifier, String label, String version, User user, String name, long length, byte[] file) {
		return new WordReport(ReportType.RISK_SHEET, user, identifier, label, version, name, file, length);
	}

	/**
	 * Build a Word report of type Raw Risk Sheet with the specified parameters.
	 *
	 * @param identifier The identifier of the report.
	 * @param label      The label of the report.
	 * @param version    The version of the report.
	 * @param user       The user associated with the report.
	 * @param name       The name of the report.
	 * @param length     The length of the report.
	 * @param file       The file content of the report.
	 * @return A new WordReport instance of type Raw Risk Sheet.
	 */
	public static WordReport BuildRawRiskSheet(String identifier, String label, String version, User user, String name, long length, byte[] file) {
		return new WordReport(ReportType.RISK_SHEET_RAW, user, identifier, label, version, name, file, length);
	}

	/**
	 * Build a Word report of type Risk Register with the specified parameters.
	 *
	 * @param identifier The identifier of the report.
	 * @param label      The label of the report.
	 * @param version    The version of the report.
	 * @param user       The user associated with the report.
	 * @param name       The name of the report.
	 * @param length     The length of the report.
	 * @param file       The file content of the report.
	 * @return A new WordReport instance of type Risk Register.
	 */
	public static WordReport BuildRiskRegister(String identifier, String label, String version, User user, String name, long length, byte[] file) {
		return new WordReport(ReportType.RISK_REGISTER, user, identifier, label, version, name, file, length);
	}
}
