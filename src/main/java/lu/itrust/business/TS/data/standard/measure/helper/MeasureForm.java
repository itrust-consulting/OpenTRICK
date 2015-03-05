/**
 * 
 */
package lu.itrust.business.TS.data.standard.measure.helper;

import java.util.ArrayList;
import java.util.List;

import lu.itrust.business.TS.data.standard.StandardType;
import lu.itrust.business.TS.data.standard.measure.AssetMeasure;
import lu.itrust.business.TS.data.standard.measure.Measure;
import lu.itrust.business.TS.data.standard.measure.MeasureAssetValue;
import lu.itrust.business.TS.data.standard.measure.MeasureProperties;
import lu.itrust.business.TS.data.standard.measure.NormalMeasure;
import lu.itrust.business.TS.data.standard.measuredescription.MeasureDescriptionText;

/**
 * @author eomar
 *
 */
public class MeasureForm {

	private int id = -1;

	private int idStandard = -1;

	private String reference;

	private int level;

	private boolean computable;

	private String domain;

	private String description;

	private List<MeasureAssetValueForm> assetValues;

	private MeasureProperties properties;

	private StandardType type;

	/**
	 * 
	 */
	public MeasureForm() {
	}

	public static final MeasureForm Build(Measure measure, String language) {
		MeasureForm form = new MeasureForm();
		if (measure == null)
			return form;
		form.setId(measure.getId());
		if (measure.getAnalysisStandard() != null && measure.getAnalysisStandard().getStandard() != null) {
			form.setIdStandard(measure.getAnalysisStandard().getStandard().getId());
			form.setType(measure.getAnalysisStandard().getStandard().getType());
		}
		
		if (measure.getMeasureDescription() != null) {
			form.setComputable(measure.getMeasureDescription().isComputable());
			form.setReference(measure.getMeasureDescription().getReference());
			form.setLevel(measure.getMeasureDescription().getLevel());

			MeasureDescriptionText descriptionText = measure.getMeasureDescription().findByAlph3(language);
			if (descriptionText == null)
				descriptionText = measure.getMeasureDescription().getAMeasureDescriptionText(0);

			if (descriptionText != null) {
				form.setDomain(descriptionText.getDomain());
				form.setDescription(descriptionText.getDescription());
			}
		}

		MeasureProperties properties = null;
		if (measure instanceof NormalMeasure)
			properties = ((NormalMeasure) measure).getMeasurePropertyList();
		else if (measure instanceof AssetMeasure) {
			properties = ((AssetMeasure) measure).getMeasurePropertyList();
			form.setAssetValues(new ArrayList<MeasureAssetValueForm>(((AssetMeasure) measure).getMeasureAssetValues().size()));
			for (MeasureAssetValue assetValue : ((AssetMeasure) measure).getMeasureAssetValues())
				form.assetValues.add(new MeasureAssetValueForm(assetValue));
		}

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
		this.reference = reference;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
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
		return "MeasureForm [id=" + id + ", idStandard=" + idStandard + ", reference=" + reference + ", level=" + level + ", computable=" + computable + ", domain=" + domain
				+ ", description=" + description + ", assetValues=" + assetValues + ", properties=" + properties + ", type=" + type + "]";
	}

}
