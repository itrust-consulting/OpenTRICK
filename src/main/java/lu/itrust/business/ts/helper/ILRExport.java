package lu.itrust.business.ts.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.assessment.Assessment;
import lu.itrust.business.ts.model.asset.Asset;
import lu.itrust.business.ts.model.cssf.RiskProfile;
import lu.itrust.business.ts.model.cssf.RiskStrategy;
import lu.itrust.business.ts.model.ilr.AssetNode;
import lu.itrust.business.ts.model.ilr.ILRImpact;
import lu.itrust.business.ts.model.parameter.helper.ValueFactory;
import lu.itrust.business.ts.model.parameter.impl.IlrSoaScaleParameter;
import lu.itrust.business.ts.model.scale.ScaleType;
import lu.itrust.business.ts.model.scenario.Scenario;
import lu.itrust.business.ts.model.standard.AnalysisStandard;
import lu.itrust.business.ts.model.standard.measure.AbstractNormalMeasure;
import lu.itrust.business.ts.model.standard.measure.Measure;
import lu.itrust.monarc.MonarcAMV;
import lu.itrust.monarc.MonarcDatabase;
import lu.itrust.monarc.MonarcDuedate;
import lu.itrust.monarc.MonarcInstance;
import lu.itrust.monarc.MonarcMeasures;
import lu.itrust.monarc.MonarcRecos;
import lu.itrust.monarc.MonarcRecs;
import lu.itrust.monarc.MonarcRisks;
import lu.itrust.monarc.MonarcScales;
import lu.itrust.monarc.MonarcSoa;
import lu.itrust.monarc.MonarcSoaScaleComment;
import lu.itrust.monarc.MonarcThreats;
import lu.itrust.monarc.MonarcVulnerabilities;

public class ILRExport {

    private static final String MAPPING[][] = { { "reputation", "reputational" }, { "personal", "privacy" } };

    private static final int VULNERABILITY_SCALE_TYPE = 3;

    private static final int ILR_MAX_IMPACT = 4;

    private class IlrSoaScale {
        private double from;
        private double to;
        private MonarcSoaScaleComment scaleComment;

        public IlrSoaScale(double from, double to, MonarcSoaScaleComment scaleComment) {
            setFrom(from);
            setTo(to);
            setScaleComment(scaleComment);
            if (getFrom() > getTo())
                throw new TrickException("error.ilr_soa_scale.conflict", "Please check ILR SOA Scale Mapping!");
        }

        public double getFrom() {
            return from;
        }

        public void setFrom(double from) {
            this.from = from;
        }

        public double getTo() {
            return to;
        }

        public void setTo(double to) {
            this.to = to;
        }

        public MonarcSoaScaleComment getScaleComment() {
            return scaleComment;
        }

        public void setScaleComment(MonarcSoaScaleComment scaleComment) {
            this.scaleComment = scaleComment;
        }

        public boolean isBounded(double value) {
            return from == 0 ? value >= from && value <= to : value > from && value <= to;
        }
    }

    public void exportILRData(Analysis analysis, List<ScaleType> scales, File data, File mapping)
            throws Exception {
        final Map<Asset, List<Assessment>> assessments = analysis.getAssessments().stream()
                .filter(e -> e.getAsset().isSelected() && e.getScenario().isSelected())
                .collect(Collectors.groupingBy(Assessment::getAsset));

        final Map<String, List<String>> ilrToTSAssets = loadAssetMapping(mapping);

        final Map<String, String> stdToReferencials = loadStandardMapping(mapping);

        final Map<String, AssetNode> nodes = analysis.getAssetNodes().stream()
                .collect(Collectors.toMap(e -> e.getAsset().getName(), Function.identity()));

        final Map<String, ScaleType> scaleTypes = scales.stream()
                .collect(Collectors.toMap(e -> e.getName().toLowerCase(), Function.identity()));

        final ValueFactory factory = new ValueFactory(analysis.getParameters());

        final Map<String, RiskProfile> mappingProfiles = analysis.getRiskProfiles().stream()
                .collect(Collectors.toMap(RiskProfile::getKey, Function.identity()));

        final MonarcDatabase database = new MonarcDatabase(data.getAbsolutePath());

        final Map<String, Map<String, MonarcMeasures>> measureMappers = extractMeasures(analysis, stdToReferencials,
                database);

        final Map<String, Map<String, MonarcRecs>> recsMappers = extractRecords(analysis, stdToReferencials,
                database);

        final int maxVulnerabilityScale = Optional.ofNullable(database.searchScaleByType(VULNERABILITY_SCALE_TYPE))
                .map(MonarcScales::getMax).orElse(3);

        exportSOA(analysis, factory, stdToReferencials, database);

        updateScaleTypes(scaleTypes);

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
                    updateConsequences(scaleTypes, node, monarcInstance);
                }

                final Map<String, MonarcRisks> risks = database.searchRiskByInstanceId(monarcInstance.getId()).stream()
                        .collect(Collectors.toMap(MonarcRisks::getAmv, Function.identity()));

                final Map<String, MonarcThreats> threats = database.searchThreatByInstanceId(monarcInstance.getId())
                        .stream()
                        .collect(Collectors.toMap(MonarcThreats::getUuid, Function.identity(), (e1, e2) -> e1));

                final Map<String, MonarcVulnerabilities> vulnerabilities = database
                        .searchVulnerabilityByInstanceId(monarcInstance.getId()).stream()
                        .collect(Collectors.toMap(MonarcVulnerabilities::getUuid, Function.identity()));

                final Map<Integer, Set<MonarcRecs>> mysRects = new HashMap<>();

                database.searchAMVByInstanceId(monarcInstance.getId()).forEach(amv -> {
                    updateAMVAndRecos(mappingProfiles, measureMappers, recsMappers, myAssessments, risks,
                            threats,
                            vulnerabilities, mysRects, amv, maxVulnerabilityScale);

                });

                mysRects.forEach((id, recs) -> {
                    final Map<String, MonarcRecos> recos = monarcInstance.getRecos().computeIfAbsent(id + "",
                            r -> new HashMap<>());

                    recs.stream().map(rc -> database.createOrUpdate(id, rc))
                            .forEach(reco -> recos.computeIfAbsent(reco.getUuid(), k -> reco)
                                    .addParentInstance(monarcInstance.getId() + ""));
                });

            }
        }

        // Update Phase
        database.getMethod().setStepInit(true);
        database.getMethod().setStepModel(true);
        database.getMethod().setStepEval(true);
        database.getMethod().setStepManage(true);

        // Delete recs without description.
        database.removeIf(e -> !StringUtils.hasText(e.getDescription()));

        database.saveInstancesToJSON(data.getAbsolutePath());

    }

    private void updateScaleTypes(final Map<String, ScaleType> scaleTypes) {
        for (String[] scaleMapping : MAPPING) {
            if (!scaleTypes.containsKey(scaleMapping[0])) {
                final ScaleType myImpact = scaleTypes.get(scaleMapping[1]);
                if (myImpact != null)
                    scaleTypes.put(scaleMapping[0], myImpact);
            }
        }
    }

    private void updateAMVAndRecos(final Map<String, RiskProfile> mappingProfiles,
            final Map<String, Map<String, MonarcMeasures>> measureMappers,
            final Map<String, Map<String, MonarcRecs>> recsMappers,
            final Map<String, Assessment> myAssessments, final Map<String, MonarcRisks> risks,
            final Map<String, MonarcThreats> threats, final Map<String, MonarcVulnerabilities> vulnerabilities,
            final Map<Integer, Set<MonarcRecs>> mysRects, MonarcAMV amv, int maxVulnerabilityScale) {
        final MonarcRisks risk = risks.get(amv.getUuid());
        final MonarcThreats threat = threats.get(amv.getThreat());
        final MonarcVulnerabilities vulnerability = vulnerabilities.get(amv.getVulnerability());
        if (threat == null || vulnerability == null)
            return;

        final Assessment assessment = myAssessments
                .get(Scenario.getILRKey(threat.getCode(), vulnerability.getCode()));

        if (assessment == null)
            return;

        risk.setVulnerabilityRate(
                Math.max(Math.min(assessment.getVulnerability(), maxVulnerabilityScale), risk.getVulnerabilityRate()));

        if (StringUtils.hasText(assessment.getOwner())) {
            if (StringUtils.hasText(risk.getRiskOwner())) {
                if (!risk.getRiskOwner().toLowerCase().contains(assessment.getOwner().toLowerCase()))
                    risk.setRiskOwner(risk.getRiskOwner() + ", " + assessment.getOwner());
            } else {
                risk.setRiskOwner(assessment.getOwner());
            }
        }

        final RiskProfile riskProfile = mappingProfiles
                .get(RiskProfile.key(assessment.getAsset(), assessment.getScenario()));

        if (riskProfile == null)
            risk.setThreatRate(Math.max(risk.getThreatRate(), 0));
        else {

            if (StringUtils.hasText(riskProfile.getRiskTreatment())) {
                if (StringUtils.hasText(risk.getComment())) {
                    if (!risk.getComment().toLowerCase().contains(riskProfile.getRiskTreatment().toLowerCase()))
                        risk.setComment(risk.getComment() + ". " + riskProfile.getRiskTreatment());
                } else {
                    risk.setComment(riskProfile.getRiskTreatment());
                }
            }

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
                        Math.min(riskProfile.getRawProbaImpact().getProbability().getIlrLevel(), ILR_MAX_IMPACT),
                        risk.getThreatRate()));

            riskProfile.getMeasures().stream()
                    .map(m -> measureMappers.getOrDefault(m.getMeasureDescription().getStandard().getName(),
                            Collections.emptyMap())
                            .get(m.getMeasureDescription().getReference()))
                    .filter(Objects::nonNull)
                    .forEach(m -> amv.addMeasure(m.getUuid()));
            if (!(riskProfile.getExpProbaImpact() == null || riskProfile.getRiskStrategy() == RiskStrategy.ACCEPT)) {
                risk.setReductionAmount(
                        Math.min(Math.min(
                                Math.max(Math.max(
                                        risk.getVulnerabilityRate()
                                                - riskProfile.getExpProbaImpact().getVulnerability(),
                                        risk.getReductionAmount()),
                                        0), // the maximun with 0 can be removed as reduction amount should be by
                                            // default to 0.
                                maxVulnerabilityScale), risk.getVulnerabilityRate()));
            }

            riskProfile.getMeasures().stream()
                    .map(m -> recsMappers.getOrDefault(m.getMeasureDescription().getStandard().getName(),
                            Collections.emptyMap())
                            .get(String.format("%s %s", m.getMeasureDescription().getStandard().getName(),
                                    m.getMeasureDescription().getReference())))
                    .filter(Objects::nonNull)
                    .forEach(m -> mysRects.computeIfAbsent(risk.getId(), e -> new HashSet<>()).add(m));
        }
    }

    private void updateConsequences(final Map<String, ScaleType> scaleTypes, final AssetNode node,
            MonarcInstance monarcInstance) {
        monarcInstance.getConsequences().values().forEach(e -> {
            final ScaleType scaleType = scaleTypes.get(e.getScaleImpactType().getLabel2().toLowerCase());
            if (scaleType == null)
                return;

            final ILRImpact c = node.getImpact().getConfidentialityImpacts().get(scaleType);
            final ILRImpact i = node.getImpact().getIntegrityImpacts().get(scaleType);
            final ILRImpact a = node.getImpact().getAvailabilityImpacts().get(scaleType);

            e.setC(Math.max((c == null ? -1 : Math.min(c.getValue(), ILR_MAX_IMPACT)), e.getC()));
            e.setI(Math.max((i == null ? -1 : Math.min(i.getValue(), ILR_MAX_IMPACT)), e.getI()));
            e.setD(Math.max((a == null ? -1 : Math.min(a.getValue(), ILR_MAX_IMPACT)), e.getD()));
            e.setIsHidden(0);
        });
    }

    private Map<String, Map<String, MonarcRecs>> extractRecords(Analysis analysis,
            Map<String, String> stdToReferencials, MonarcDatabase database) {

        final Map<String, Map<String, MonarcRecs>> recMappers = new HashMap<>();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        analysis.getAnalysisStandards().forEach((name, aStd) -> {
            final Map<String, MonarcRecs> ilrRecs = database
                    .searchRecsByRecSetsLabel(stdToReferencials.getOrDefault(name.toLowerCase(), name)).stream()
                    .collect(Collectors.toMap(MonarcRecs::getCode, Function.identity()));
            if (ilrRecs.isEmpty())
                return;
            aStd.getMeasures().forEach(m -> {
                final MonarcRecs rec = ilrRecs
                        .get(String.format("%s %s", name, m.getMeasureDescription().getReference()));
                if (rec != null) {
                    rec.setComment(m.getComment());
                    rec.setResponsable(m.getResponsible());
                    rec.setDescription(m.getToDo());
                    rec.setImportance(m.getImportance());
                    rec.setDuedate(
                            new MonarcDuedate(dateFormat.format(m.getPhase().getEndDate()) + " 00:00:00.000000", 3,
                                    "Europe/Luxembourg"));
                }
            });
            recMappers.put(name, ilrRecs);
        });

        return recMappers;
    }

    private Map<String, Map<String, MonarcMeasures>> extractMeasures(Analysis analysis,
            final Map<String, String> stdToReferencials, final MonarcDatabase database) {
        final Map<String, Map<String, MonarcMeasures>> measureMappers = new HashMap<>();

        analysis.getAnalysisStandards().forEach((n, a) -> {
            final List<MonarcMeasures> ilrMeasures = database
                    .searchMeasuresByReferentialLabel(
                            stdToReferencials.getOrDefault(a.getStandard().getName().toLowerCase(),
                                    a.getStandard().getName()));
            if (!ilrMeasures.isEmpty())
                measureMappers.computeIfAbsent(a.getStandard().getName(),
                        b -> ilrMeasures.stream()
                                .collect(Collectors.toMap(MonarcMeasures::getCode, Function.identity())));
        });
        return measureMappers;
    }

    private void exportSOA(Analysis analysis, ValueFactory factory, Map<String, String> stdToReferencials,
            MonarcDatabase database) {

        final List<IlrSoaScale> soaScales = generateIlrSoaScales(analysis, database);

        final Map<String, MonarcSoa> soaMapper = database.getAllMonarcSoas().stream()
                .collect(Collectors.toMap(MonarcSoa::getMeasureId, Function.identity()));

        analysis.getAnalysisStandards().values().stream().filter(AnalysisStandard::isSoaEnabled).forEach(e -> {
            final String name = stdToReferencials.getOrDefault(e.getStandard().getName().toLowerCase(),
                    e.getStandard().getName());
            final List<MonarcMeasures> monarcMeasures = database.searchMeasuresByReferentialLabel(name);
            final Map<String, MonarcMeasures> measures = monarcMeasures.stream()
                    .collect(Collectors.toMap(MonarcMeasures::getCode, Function.identity()));
            if (measures.isEmpty())
                return;

            for (Measure measure : e.getMeasures()) {
                updateSOA(factory, soaScales, soaMapper, e, measures, measure);
            }
        });
    }

    private void updateSOA(ValueFactory factory, final List<IlrSoaScale> soaScales,
            final Map<String, MonarcSoa> soaMapper, AnalysisStandard e, final Map<String, MonarcMeasures> measures,
            Measure measure) {
        final MonarcMeasures mm = measures.get(measure.getMeasureDescription().getReference());
        if (mm != null) {
            final MonarcSoa soa = soaMapper.get(mm.getUuid());
            if (soa != null) {
                // clear status
                soa.setBP(0);
                soa.setBR(0);
                soa.setCO(0);
                soa.setEX(0);
                soa.setLR(0);
                soa.setRRA(0);

                soa.setEvidences(measure.getComment());
                soa.setActions(measure.getToDo());
                soa.setRemarks(((AbstractNormalMeasure) measure).getSoaComment());

                final double implementRate = measure.getImplementationRateValue(factory);
                final Integer scaleComment = getIlrSoaScale(implementRate, soaScales);

                switch (measure.getStatus()) {
                    case Constant.MEASURE_STATUS_APPLICABLE:
                        if (e.getStandard().isComputable())
                            soa.setRRA(1);
                        else
                            soa.setBP(1);
                        soa.setSoaScaleComment(scaleComment);
                        break;
                    case Constant.MEASURE_STATUS_MANDATORY:
                        soa.setLR(1);
                        soa.setSoaScaleComment(scaleComment);
                        break;
                    default:
                        soa.setEX(1);
                        soa.setSoaScaleComment(null);
                }
            }
        }
    }

    private Integer getIlrSoaScale(double implementRate, List<IlrSoaScale> soaScales) {
        final int mid = soaScales.size() / 2;
        IlrSoaScale scale = soaScales.get(mid);
        if (scale.isBounded(implementRate) || mid == 0)
            return scale.getScaleComment().getId();
        else if (scale.getFrom() > implementRate)
            return getIlrSoaScale(implementRate, soaScales.subList(0, mid));
        else
            return getIlrSoaScale(implementRate, soaScales.subList(mid, soaScales.size()));
    }

    private List<IlrSoaScale> generateIlrSoaScales(Analysis analysis, MonarcDatabase database) {
        final List<IlrSoaScale> soaScales = new ArrayList<>();

        final List<IlrSoaScaleParameter> parameters = analysis.getIlrSoaScaleParameters();

        parameters.sort((e1, e2) -> Double.compare(e1.getValue(), e2.getValue()));

        for (int i = 0; i < parameters.size(); i++) {
            final IlrSoaScaleParameter parameter = parameters.get(i);
            if (i == 0 && parameter.getValue() < 0) {
                soaScales.add(new IlrSoaScale(parameter.getValue() - 1, parameter.getValue(), new MonarcSoaScaleComment(
                        parameter.getId(), i, false, parameter.getColor(), parameter.getDescription())));
            } else if (i == 0) {
                soaScales.add(new IlrSoaScale(0, parameter.getValue(), new MonarcSoaScaleComment(
                        parameter.getId(), i, false, parameter.getColor(), parameter.getDescription())));
            } else {
                soaScales
                        .add(new IlrSoaScale(parameters.get(i - 1).getValue(), parameter.getValue(),
                                new MonarcSoaScaleComment(
                                        parameter.getId(), i, false, parameter.getColor(),
                                        parameter.getDescription())));
            }
        }

        if (soaScales.isEmpty())
            throw new TrickException("error.ilr_soa_scale.empty", "Please update ILR SOA Scale Mapping!");

        database.setSoaScaleComments(soaScales.stream().map(IlrSoaScale::getScaleComment).collect(Collectors.toList()));

        return soaScales;
    }

    private Map<String, String> loadStandardMapping(File file) throws IOException {
        return file == null || !file.exists() ? Collections.emptyMap()
                : Files.readAllLines(file.toPath()).stream().map(e -> e.split(";"))
                        .filter(e -> e.length == 3 && e[0].equalsIgnoreCase("STD"))
                        .collect(Collectors.toMap(e -> e[1].trim().toLowerCase(), e -> e[2].trim()));
    }

    private Map<String, List<String>> loadAssetMapping(File file) throws IOException {
        return file == null || !file.exists() ? Collections.emptyMap()
                : Files.readAllLines(file.toPath()).stream().map(e -> e.split(";"))
                        .filter(e -> e.length == 3 && e[0].equalsIgnoreCase("ASSET"))
                        .collect(Collectors.groupingBy(e -> e[1].trim().toLowerCase(),
                                Collectors.mapping(e -> e[2].trim(), Collectors.toList())));
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
