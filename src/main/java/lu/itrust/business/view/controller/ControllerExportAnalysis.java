/**
 * 
 */
package lu.itrust.business.view.controller;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.KnowLedgeBase;
import lu.itrust.business.TS.export.ExportAnalysis;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceAnalysisNorm;
import lu.itrust.business.service.ServiceAssetType;
import lu.itrust.business.service.ServiceScenarioType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author oensuifudine
 */
@Secured("ROLE_CONSULTANT")
@RequestMapping("/export")
@Controller
public class ControllerExportAnalysis {

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceAnalysisNorm serviceAnalysisNorm;

	@Autowired
	private ServiceAssetType serviceAssetType;

	@Autowired
	private ServiceScenarioType serviceScenarioType;

	public void setServiceAnalysis(ServiceAnalysis serviceAnalysis) {
		this.serviceAnalysis = serviceAnalysis;
	}

	@RequestMapping("/analysis/{analysisId}")
	public ModelAndView exportAnalysisSave(
			@PathVariable("analysisId") Integer analysisId,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Analysis analysis = serviceAnalysis.get(analysisId);

		if (analysis != null) {

			ExportAnalysis exportAnalysis = new ExportAnalysis(serviceAssetType, serviceScenarioType);

			KnowLedgeBase knowLedgeBase = new KnowLedgeBase(exportAnalysis);

			String fileName = analysis.getIdentifier() + "_"
					+ analysis.getLabel() + ".sqlite";

			fileName = fileName.replace(" ", "_").replace(":", "_");

			File file = knowLedgeBase.exportToSQLite(analysis, fileName,
					request.getServletContext());

			if (file != null) {

				response.setContentType("sqlite");
				response.setContentLength((int) file.length());
				response.setHeader("Content-Disposition",
						"attachment; filename=\"" + fileName + "\"");
				FileCopyUtils.copy(FileCopyUtils.copyToByteArray(file),
						response.getOutputStream());
			}
		}
		return null;
	}
}
