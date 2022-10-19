package lu.itrust.business.TS.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.asset.Asset;
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

    public void exportILRData(Analysis analysis, List<ScaleType> scales, File data, File mapping)
            throws Exception {
        // final int language =
        // analysis.getLanguage().getAlpha2().equalsIgnoreCase("fr") ? 1 : 2;
        final Map<Asset, List<Assessment>> assessments = analysis.findAssessmentByAsset();
        final Map<String, List<String>> ilrToTSAssets = loadAssetMapping(mapping);
        final Map<Asset, AssetNode> nodes = analysis.getAssetNodes().stream()
                .collect(Collectors.toMap(AssetNode::getAsset, Function.identity()));
        final Map<String, ScaleType> scaleTypes = scales.stream()
                .collect(Collectors.toMap(e -> e.getName().toLowerCase(), Function.identity()));
        if (!scaleTypes.containsKey("personal")) {
            final ScaleType privacyImpact = scaleTypes.get("privacy");
            if (privacyImpact != null)
                scaleTypes.put("personal", privacyImpact);
        }

        final MonarcDatabase database = new MonarcDatabase(data.getAbsolutePath());

        for (Entry<Asset, List<Assessment>> entry : assessments.entrySet()) {
            final Asset asset = entry.getKey();

            final List<MonarcInstance> monarcInstances = findOrCreateAsset(ilrToTSAssets, database, asset);

            final Map<String, Assessment> myAssessments = monarcInstances.isEmpty() ? Collections.emptyMap()
                    : entry.getValue().stream()
                            .filter(e -> StringUtils.hasText(e.getScenario().getThreat())
                                    && StringUtils.hasText(e.getScenario().getVulnerability()))
                            .collect(Collectors.toMap(e -> e.getScenario().getILRKey(), Function.identity()));

            for (MonarcInstance monarcInstance : monarcInstances) {

                final AssetNode node = nodes.get(asset);

                if (node != null) {
                    // Update consequences
                    monarcInstance.getConsequences().values().forEach(e -> {
                        final ScaleType scaleType = scaleTypes.get(e.getScaleImpactType().getLabel2().toLowerCase());
                        if (scaleType == null)
                            return;
                        final ILRImpact c = node.getImpact().getConfidentialityImpacts().get(scaleType);
                        final ILRImpact i = node.getImpact().getIntegrityImpacts().get(scaleType);
                        final ILRImpact a = node.getImpact().getAvailabilityImpacts().get(scaleType);

                        e.setC(c == null ? -1 : c.getValue());
                        e.setI(i == null ? -1 : i.getValue());
                        e.setD(a == null ? -1 : a.getValue());

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
                    risk.setThreatRate((int) assessment.getLikelihoodReal());
                    risk.setVulnerabilityRate(assessment.getVulnerability());
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
        final List<MonarcInstance> instances = new ArrayList<>();
        MonarcInstance monarcInstance = database.searchInstanceByName(aaset.getName());
        if (monarcInstance == null) {
            monarcInstance = database.searchInstanceByLabel(aaset.getName());
            if (monarcInstance == null) {
                return ilrToTSAssets.getOrDefault(aaset.getName().toLowerCase(), Collections.emptyList()).stream()
                        .map(name -> {
                            MonarcInstance instance = database.searchInstanceByName(name);
                            return instance == null ? database.searchInstanceByLabel(name) : instance;
                        }).filter(Objects::nonNull).collect(Collectors.toList());

            } else
                instances.add(monarcInstance);
        } else
            instances.add(monarcInstance);
        return instances;
    }
}
