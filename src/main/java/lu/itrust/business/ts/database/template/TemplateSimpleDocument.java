package lu.itrust.business.ts.database.template;

import java.util.List;

import lu.itrust.business.ts.database.TemplateDAOService;
import lu.itrust.business.ts.model.general.document.impl.SimpleDocument;

public interface TemplateSimpleDocument extends TemplateDAOService<SimpleDocument, Long> {

    List<SimpleDocument> findByAnalysisId(Integer idAnalysis);

}
