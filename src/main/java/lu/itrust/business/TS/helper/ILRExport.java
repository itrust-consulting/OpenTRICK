package lu.itrust.business.TS.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.cssf.RiskProfile;
import lu.itrust.business.TS.model.cssf.RiskStrategy;
import lu.itrust.business.TS.model.ilr.AssetNode;
import lu.itrust.business.TS.model.ilr.ILRImpact;
import lu.itrust.business.TS.model.scale.ScaleType;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.monarc.MonarcDatabase;
import lu.itrust.monarc.MonarcInstance;
import lu.itrust.monarc.MonarcRisks;
import lu.itrust.monarc.MonarcThreats;
import lu.itrust.monarc.MonarcVulnerabilities;

public class ILRExport {

    private static final String MAPPING[][] = { { "reputation", "reputational" }, { "personal", "privacy" } };

    public void exportILRData(Analysis analysis, List<ScaleType> scales, File data, File mapping)
            throws Exception {
        final Map<Asset, List<Assessment>> assessments = analysis.getAssessments().stream()
                .filter(e -> e.getAsset().isSelected() && e.getScenario().isSelected())
                .collect(Collectors.groupingBy(Assessment::getAsset));
        final Map<String, List<String>> ilrToTSAssets = loadAssetMapping(mapping);
        final Map<String, AssetNode> nodes = analysis.getAssetNodes().stream()
                .collect(Collectors.toMap(e -> e.getAsset().getName(), Function.identity()));
        final Map<String, ScaleType> scaleTypes = scales.stream()
                .collect(Collectors.toMap(e -> e.getName().toLowerCase(), Function.identity()));
        for (String[] scaleMapping : MAPPING) {
            if (!scaleTypes.containsKey(scaleMapping[0])) {
                final ScaleType myImpact = scaleTypes.get(scaleMapping[1]);
                if (myImpact != null)
                    scaleTypes.put(scaleMapping[0], myImpact);
            }

        }

        final Map<String, RiskProfile> mappingProfiles = analysis.getRiskProfiles().stream()
                .collect(Collectors.toMap(RiskProfile::getKey, Function.identity()));

        final MonarcDatabase database = new MonarcDatabase(data.getAbsolutePath());

        for (Entry<Asset, List<Assessment>> entry : assessments.entrySet()) {

            final Asset asset = entry.getKey();

            final AssetNode node = nodes.get(asset.getName());

            final List<MonarcInstance> monarcInstances = findOrCreateAsset(ilrToTSAssets, database, asset);

            final Map<String, Assessment> myAssessments = monarcInstances.isEmpty() ? Collections.emptyMap()
                    : entry.getValue().stream()
                            .filter(e -> StringUtils.hasText(e.getScenario().getThreat())
                                    && StringUtils.hasText(e.getScenario().getVulnerability()))
                            .collect(Collectors.toMap(e -> e.getScenario().getILRKey(), Function.identity()));

            for (MonarcInstance monarcInstance : monarcInstances) {
                if (node != null) {
                    // Update consequences
                    monarcInstance.getConsequences().values().forEach(e -> {
                        final ScaleType scaleType = scaleTypes.get(e.getScaleImpactType().getLabel2().toLowerCase());
                        if (scaleType == null)
                            return;

                        final ILRImpact c = node.getImpact().getConfidentialityImpacts().get(scaleType);
                        final ILRImpact i = node.getImpact().getIntegrityImpacts().get(scaleType);
                        final ILRImpact a = node.getImpact().getAvailabilityImpacts().get(scaleType);

                        e.setC(Math.max((c == null ? -1 : Math.min(c.getValue(), 4)), e.getC()));
                        e.setI(Math.max((i == null ? -1 : Math.min(i.getValue(), 4)), e.getI()));
                        e.setD(Math.max((a == null ? -1 : Math.min(a.getValue(), 4)), e.getD()));
                        e.setIsHidden(0);
                    });
                }

                final Map<String, MonarcRisks> risks = database.searchRiskByInstanceId(monarcInstance.getId()).stream()
                        .collect(Collectors.toMap(MonarcRisks::getAmv, Function.identity()));

                final Map<String, MonarcThreats> threats = database.searchThreatByInstanceId(monarcInstance.getId())
                        .stream()
                        .collect(Collectors.toMap(MonarcThreats::getUuid, Function.identity(), (e1, e2) -> e1));

                final Map<String, MonarcVulnerabilities> vulnerabilities = database
                        .searchVulnerabilityByInstanceId(monarcInstance.getId()).stream()
                        .collect(Collectors.toMap(MonarcVulnerabilities::getUuid, Function.identity()));

                database.searchAMVByInstanceId(monarcInstance.getId()).forEach(e -> {
                    final MonarcRisks risk = risks.get(e.getUuid());
                    final MonarcThreats threat = threats.get(e.getThreat());
                    final MonarcVulnerabilities vulnerability = vulnerabilities.get(e.getVulnerability());
                    if (threat == null || vulnerability == null)
                        return;

                    final Assessment assessment = myAssessments
                            .get(Scenario.getILRKey(threat.getCode(), vulnerability.getCode()));

                    if (assessment == null)
                        return;

                    risk.setVulnerabilityRate(
                            Math.max(Math.min(assessment.getVulnerability(), 4), risk.getVulnerabilityRate()));

                    if (StringUtils.hasText(assessment.getOwner())) {
                        if (StringUtils.hasText(risk.getRiskOwner())) {
                            if (!risk.getRiskOwner().toLowerCase().contains(assessment.getOwner().toLowerCase()))
                                risk.setRiskOwner(risk.getRiskOwner() + ", " + assessment.getOwner());
                        } else {
                            risk.setRiskOwner(assessment.getOwner());
                        }
                    }

                    if (StringUtils.hasText(assessment.getComment())) {
                        if (StringUtils.hasText(risk.getComment())) {
                            if (!risk.getComment().toLowerCase().contains(assessment.getComment().toLowerCase()))
                                risk.setComment(risk.getComment() + ". " + assessment.getComment());
                        } else {
                            risk.setComment(assessment.getComment());
                        }
                    }

                    final RiskProfile riskProfile = mappingProfiles
                            .get(RiskProfile.key(asset, assessment.getScenario()));

                    if (riskProfile == null)
                        risk.setThreatRate(Math.max(risk.getThreatRate(), 0));
                    else {

                        if (StringUtils.hasText(riskProfile.getActionPlan())) {
                            if (StringUtils.hasText(risk.getContext())) {
                                if (!risk.getContext().toLowerCase()
                                        .contains(riskProfile.getActionPlan().toLowerCase()))
                                    risk.setContext(risk.getContext() + ". " + riskProfile.getActionPlan());
                            } else {
                                risk.setContext(riskProfile.getActionPlan());
                            }
                        }

                        risk.setKindOfMeasure(
                                Math.min(risk.getKindOfMeasure(), getRiskStrategy(riskProfile.getRiskStrategy())));

                        if (riskProfile.getRawProbaImpact() == null
                                || riskProfile.getRawProbaImpact().getProbability() == null)
                            risk.setThreatRate(Math.max(risk.getThreatRate(), 0));
                        else
                            risk.setThreatRate(Math.max(
                                    Math.min(riskProfile.getRawProbaImpact().getProbability().getIlrLevel(), 4),
                                    risk.getThreatRate()));
                    }

                });
            }
        }

        database.saveInstancesToJSON(data.getAbsolutePath());

    }

    private Map<String, List<String>> loadAssetMapping(File file) throws IOException {
        return file == null || !file.exists() ? Collections.emptyMap()
                : Files.readAllLines(file.toPath()).stream().map(e -> e.split(";")).filter(e -> e.length == 2)
                        .collect(Collectors.groupingBy(e -> e[0].trim().toLowerCase(),
                                Collectors.mapping(e -> e[1].trim(), Collectors.toList())));
    }

    private List<MonarcInstance> findOrCreateAsset(final Map<String, List<String>> ilrToTSAssets,
            final MonarcDatabase database,
            Asset aaset) {
        List<MonarcInstance> instances = database.searchInstanceByName(aaset.getName());
        if (instances == null || instances.isEmpty()) {
            instances = database.searchInstanceByLabel(aaset.getName());
            if (instances == null || instances.isEmpty()) {
                return ilrToTSAssets.getOrDefault(aaset.getName().toLowerCase(), Collections.emptyList()).stream()
                        .map(name -> {
                            List<MonarcInstance> myInstance = database.searchInstanceByName(name);
                            return myInstance == null || myInstance.isEmpty() ? database.searchInstanceByLabel(name)
                                    : myInstance;
                        }).filter(Objects::nonNull).flatMap(List::stream).collect(Collectors.toList());

            }
        }
        return instances;
    }

    private int getRiskStrategy(RiskStrategy strategy) {
        if (strategy == null)
            return 1;
        switch (strategy) {
            case REDUCE: // Reduce
                return 1;
            case AVOID: // Denied
                return 2;
            case ACCEPT: // ACCEPT
                return 3;
            case TRANSFER: // Share
                return 4;
            default: // Not defined
                return 5;

        }

    }
}
