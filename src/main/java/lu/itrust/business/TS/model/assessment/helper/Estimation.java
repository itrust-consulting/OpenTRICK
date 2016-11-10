/**
 * 
 */
package lu.itrust.business.TS.model.assessment.helper;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lu.itrust.business.TS.component.NaturalOrderComparator;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.cssf.RiskProbaImpact;
import lu.itrust.business.TS.model.cssf.RiskProfile;
import lu.itrust.business.TS.model.cssf.RiskStrategy;
import lu.itrust.business.TS.model.cssf.helper.CSSFFilter;
import lu.itrust.business.TS.model.cssf.tools.CSSFSort;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.parameter.impl.ImpactParameter;
import lu.itrust.business.TS.model.parameter.impl.LikelihoodParameter;
import lu.itrust.business.TS.model.scenario.Scenario;

/**
 * @author eomar
 *
 */
public class Estimation {

	private String owner;

	private String argumentation;

	private RiskProbaImpact netEvaluation;

	private RiskProfile riskProfile;

	/**
	 * @param riskProfile
	 * @param netEvaluation
	 */
	public Estimation(String owner, String argumentation, RiskProfile riskProfile, RiskProbaImpact netEvaluation) {
		this.owner = owner;
		this.argumentation = argumentation;
		this.netEvaluation = netEvaluation;
		this.riskProfile = riskProfile;

	}

	/**
	 * @param netEvaluation
	 * @param riskProfile
	 */
	public Estimation(Assessment assessment, RiskProfile riskProfile, ValueFactory convertor) {
		setOwner(assessment.getOwner());
		setRiskProfile(riskProfile);
		setArgumentation(assessment.getComment());
		setNetEvaluation(new RiskProbaImpact((LikelihoodParameter) convertor.findProbParameter(assessment.getLikelihood()),
				assessment.getImpacts().stream().map(impact -> (ImpactParameter) impact.getParameter()).collect(Collectors.toList())));
	}

	/**
	 * @return the netEvaluation
	 */
	public RiskProbaImpact getNetEvaluation() {
		return netEvaluation;
	}

	/**
	 * @param netEvaluation
	 *            the netEvaluation to set
	 */
	public void setNetEvaluation(RiskProbaImpact netEvaluation) {
		this.netEvaluation = netEvaluation;
	}

	/**
	 * @return
	 * @see lu.itrust.business.TS.model.cssf.RiskProfile#getIdentifier()
	 */
	public String getIdentifier() {
		return riskProfile.getIdentifier();
	}

	/**
	 * @return
	 * @see lu.itrust.business.TS.model.cssf.RiskProfile#getAsset()
	 */
	public Asset getAsset() {
		return riskProfile.getAsset();
	}

	/**
	 * @return
	 * @see lu.itrust.business.TS.model.cssf.RiskProfile#getScenario()
	 */
	public Scenario getScenario() {
		return riskProfile.getScenario();
	}

	/**
	 * @return
	 * @see lu.itrust.business.TS.model.cssf.RiskProfile#getRiskStrategy()
	 */
	public RiskStrategy getRiskStrategy() {
		return riskProfile.getRiskStrategy();
	}

	/**
	 * @return
	 * @see lu.itrust.business.TS.model.cssf.RiskProfile#getRiskTreatment()
	 */
	public String getRiskTreatment() {
		return riskProfile.getRiskTreatment();
	}

	/**
	 * @return
	 * @see lu.itrust.business.TS.model.cssf.RiskProfile#getActionPlan()
	 */
	public String getActionPlan() {
		return riskProfile.getActionPlan();
	}

	/**
	 * @return
	 * @see lu.itrust.business.TS.model.cssf.RiskProfile#getRawProbaImpact()
	 */
	public RiskProbaImpact getRawProbaImpact() {
		return riskProfile.getRawProbaImpact();
	}

	/**
	 * @return
	 * @see lu.itrust.business.TS.model.cssf.RiskProfile#getExpProbaImpact()
	 */
	public RiskProbaImpact getExpProbaImpact() {
		return riskProfile.getExpProbaImpact();
	}

	/**
	 * @param idAsset
	 * @param idScenario
	 * @return
	 * @see lu.itrust.business.TS.model.cssf.RiskProfile#is(int, int)
	 */
	public Boolean is(int idAsset, int idScenario) {
		return riskProfile.is(idAsset, idScenario);
	}

	/**
	 * @return
	 * @see lu.itrust.business.TS.model.cssf.RiskProfile#getComputedRawImportance()
	 */
	public int getComputedRawImportance() {
		return riskProfile.getComputedRawImportance();
	}

	/**
	 * @return
	 * @see lu.itrust.business.TS.model.cssf.RiskProfile#getComputedExpImportance()
	 */
	public int getComputedExpImportance() {
		return riskProfile.getComputedExpImportance();
	}

	/**
	 * @return
	 * @see lu.itrust.business.TS.model.cssf.RiskProbaImpact#getImportance()
	 */
	public int getNetImportance() {
		return netEvaluation == null ? 0 : netEvaluation.getImportance();
	}

	/**
	 * @return
	 * @see lu.itrust.business.TS.model.cssf.RiskProfile#isSelected()
	 */
	public Boolean isSelected() {
		return riskProfile.isSelected();
	}

	/**
	 * @param impact
	 * @param probability
	 * @return
	 * @see lu.itrust.business.TS.model.cssf.RiskProfile#isCompliant(int, int)
	 */
	public boolean isCompliant(int impact, int probability) {
		return netEvaluation == null ? probability == 0 && (probability == impact) : netEvaluation.getImpactLevel() >= impact && netEvaluation.getProbabilityLevel() >= probability;
	}

	/**
	 * @return the riskProfile
	 */
	public RiskProfile getRiskProfile() {
		return riskProfile;
	}

	/**
	 * @param riskProfile
	 *            the riskProfile to set
	 */
	public void setRiskProfile(RiskProfile riskProfile) {
		this.riskProfile = riskProfile;
	}

	public static Comparator<? super Estimation> Comparator() {
		return (E1, E2) -> Integer.compare(E1.getNetImportance(), E2.getNetImportance());
	}

	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner
	 *            the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @return the argumentation
	 */
	public String getArgumentation() {
		return argumentation;
	}

	/**
	 * @param argumentation
	 *            the argumentation to set
	 */
	public void setArgumentation(String argumentation) {
		this.argumentation = argumentation;
	}

	public static void GenerateEstimation(Analysis analysis, CSSFFilter cssfFilter, ValueFactory valueFactory, List<Estimation> directs, List<Estimation> indirects,
			List<Estimation> cias) {
		int cia = cssfFilter.getCia(), direct = cssfFilter.getDirect(), inderect = cssfFilter.getIndirect();
		Map<String, Assessment> mappedAssessment = analysis.getAssessments().stream().collect(Collectors.toMap(Assessment::getKey, Function.identity()));
		analysis.getRiskProfiles().stream().filter(RiskProfile::isSelected)
				.map(riskProfile -> new Estimation(mappedAssessment.get(Assessment.key(riskProfile.getAsset(), riskProfile.getScenario())), riskProfile, valueFactory))
				.sorted(Estimation.Comparator().reversed()).forEach(estimation -> {
					switch (CSSFSort.findGroup(estimation.getScenario().getType().getName())) {
					case CSSFSort.DIRECT:
						if (direct == -1
								|| direct > -1 && (cssfFilter.getDirect() > 0 || estimation.isCompliant((int) cssfFilter.getImpact(), (int) cssfFilter.getProbability()))) {
							directs.add(estimation);
							if (direct > 0)
								cssfFilter.setDirect(cssfFilter.getDirect() - 1);
						}
						break;
					case CSSFSort.INDIRECT:
						if (inderect == -1
								|| inderect > -1 && (cssfFilter.getIndirect() > 0 || estimation.isCompliant((int) cssfFilter.getImpact(), (int) cssfFilter.getProbability()))) {
							indirects.add(estimation);
							if (inderect > 0)
								cssfFilter.setIndirect(cssfFilter.getIndirect() - 1);
						}
						break;
					default:
						if (cia == -1 || cia > -1 && (cssfFilter.getCia() > 0 || estimation.isCompliant((int) cssfFilter.getImpact(), (int) cssfFilter.getProbability()))) {
							cias.add(estimation);
							if (cia > 0)
								cssfFilter.setCia(cssfFilter.getCia() - 1);
						}
						break;
					}
				});
		mappedAssessment.clear();
	}

	public static Comparator<? super Estimation> IdComparator() {
		return (o1, o2) -> {
			if (o1.getIdentifier() == null || o1.getIdentifier().isEmpty()) {
				if (o2.getIdentifier() == null || o2.getIdentifier().isEmpty())
					return 0;
				return -1;
			} else if (o2.getIdentifier() == null)
				return 1;
			return NaturalOrderComparator.compareTo(o1.getIdentifier(), o2.getIdentifier());
		};
	}

}
