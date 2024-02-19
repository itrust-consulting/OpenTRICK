/**
 * 
 */
package lu.itrust.business.ts.form;

import java.util.ArrayList;
import java.util.List;

import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.analysis.AnalysisType;
import lu.itrust.business.ts.model.general.AssetTypeValue;
import lu.itrust.business.ts.model.standard.StandardType;
import lu.itrust.business.ts.model.standard.measure.AbstractNormalMeasure;
import lu.itrust.business.ts.model.standard.measure.Measure;
import lu.itrust.business.ts.model.standard.measure.impl.AssetMeasure;
import lu.itrust.business.ts.model.standard.measure.impl.MeasureAssetValue;
import lu.itrust.business.ts.model.standard.measure.impl.MeasureProperties;
import lu.itrust.business.ts.model.standard.measure.impl.NormalMeasure;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescriptionText;

/**
 * @author eomar
 *
 */
public class MeasureForm {

	private int id = 0;

	private int idStandard = 0;

	private String reference;

	private boolean computable;

	private String domain;

	private String description;

	private List<MeasureAssetValueForm> assetValues;

	private MeasureProperties properties;

	private StandardType type;

	private int implementationRate;

	private String status;

	private int phase;

	private String responsible;

	public static final MeasureForm build(Measure measure, AnalysisType analysisType, String language)
			throws TrickException {
		MeasureForm form = new MeasureForm();
		if (measure == null)
			return form;
		form.setId(measure.getId());
		if (measure.getMeasureDescription().getStandard() != null) {
			form.setIdStandard(measure.getMeasureDescription().getStandard().getId());
			form.setType(measure.getMeasureDescription().getStandard().getType());
		}

		if (measure.getMeasureDescription() != null) {
			form.setComputable(measure.getMeasureDescription().isComputable());
			form.setReference(measure.getMeasureDescription().getReference());

			MeasureDescriptionText descriptionText = measure.getMeasureDescription().findByAlph3(language);
			if (descriptionText == null)
				descriptionText = measure.getMeasureDescription().getAMeasureDescriptionText(0);

			if (descriptionText != null) {
				form.setDomain(descriptionText.getDomain());
				form.setDescription(descriptionText.getDescription());
			}
		}

		MeasureProperties properties = new MeasureProperties();

		if (measure instanceof AbstractNormalMeasure && analysisType.isQuantitative())
			((AbstractNormalMeasure) measure).getMeasurePropertyList().copyTo(properties);
		if (measure instanceof NormalMeasure) {
			form.assetValues = new ArrayList<>(((NormalMeasure) measure).getAssetTypeValues().size());
			for (AssetTypeValue assetTypeValue : ((NormalMeasure) measure).getAssetTypeValues())
				form.assetValues.add(new MeasureAssetValueForm(assetTypeValue.getAssetType().getId(),
						assetTypeValue.getAssetType().getName(), assetTypeValue.getValue()));
		} else if (measure instanceof AssetMeasure) {
			form.setAssetValues(new ArrayList<>(((AssetMeasure) measure).getMeasureAssetValues().size()));
			for (MeasureAssetValue assetValue : ((AssetMeasure) measure).getMeasureAssetValues())
				form.assetValues.add(new MeasureAssetValueForm(assetValue));
		}

		if (analysisType.isQuantitative())
			form.setProperties(properties);
		return form;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdStandard() {
		return idStandard;
	}

	public void setIdStandard(int idStandard) {
		this.idStandard = idStandard;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference == null ? null : reference.trim();
	}

	public boolean isComputable() {
		return computable;
	}

	public void setComputable(boolean computable) {
		this.computable = computable;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<MeasureAssetValueForm> getAssetValues() {
		return assetValues;
	}

	public void setAssetValues(List<MeasureAssetValueForm> assetValues) {
		this.assetValues = assetValues;
	}

	public MeasureProperties getProperties() {
		return properties;
	}

	public void setProperties(MeasureProperties properties) {
		this.properties = properties;
	}

	public StandardType getType() {
		return type;
	}

	public void setType(StandardType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "MeasureForm [id=" + id + ", idStandard=" + idStandard + ", reference=" + reference + ", computable="
				+ computable + ", domain=" + domain
				+ ", description=" + description + ", assetValues=" + assetValues + ", properties=" + properties
				+ ", type=" + type + "]";
	}

	/**
	 * @return the implementationRate
	 */
	public int getImplementationRate() {
		return implementationRate;
	}

	/**
	 * @param implementationRate the implementationRate to set
	 */
	public void setImplementationRate(int implementationRate) {
		this.implementationRate = implementationRate;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the phase
	 */
	public int getPhase() {
		return phase;
	}

	/**
	 * @param phase the phase to set
	 */
	public void setPhase(int phase) {
		this.phase = phase;
	}

	/**
	 * @return the responsible
	 */
	public String getResponsible() {
		return responsible;
	}

	/**
	 * @param responsible the responsible to set
	 */
	public void setResponsible(String responsible) {
		this.responsible = responsible;
	}

}
