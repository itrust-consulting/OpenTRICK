package lu.itrust.business.TS.database.template;

import java.util.List;

import lu.itrust.business.TS.database.TemplateDAOService;
import lu.itrust.business.TS.model.general.document.impl.SimpleDocument;

public interface TemplateSimpleDocument extends TemplateDAOService<SimpleDocument, Long> {

    List<SimpleDocument> findByAnalysisId(Integer idAnalysis);

}
