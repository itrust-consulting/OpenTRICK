/**
 * 
 */
package lu.itrust.business.controller;

import java.io.File;
import java.security.Principal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lu.itrust.business.TS.Customer;
import lu.itrust.business.service.ServiceCustomer;
import lu.itrust.business.service.ServiceTaskFeedback;
import lu.itrust.business.service.WorkersPoolManager;
import lu.itrust.business.task.Worker;
import lu.itrust.business.task.WorkerAnalysisImport;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author oensuifudine
 */
@Secured("ROLE_CONSULTANT")
@Controller
public class ControllerImportAnalysis {

	@Autowired
	private ServiceCustomer serviceCustomer;

	@Autowired
	private TaskExecutor executor;

	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private WorkersPoolManager workersPoolManager;

	// ******************************************************************************************************************
	// * Request Mappings
	// ******************************************************************************************************************

	/**
	 * importAnalysis: <br>
	 * Description
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("Analysis/Import/Display")
	public String importAnalysis(Map<String, Object> model) throws Exception {
		model.put("customerId", -1);
		model.put("customers", serviceCustomer.loadAll());
		return "analysis/importAnalysisForm";
	}

	/**
	 * importAnalysisSave: <br>
	 * Description
	 * 
	 * @param session
	 * @param customerId
	 * @param file
	 * @param redirectAttributes
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("Analysis/Import/Execute")
	public Object importAnalysisSave(final Principal principal,
			final @RequestParam(value = "customerId") Integer customerId,
			final HttpServletRequest request,
			final @RequestParam(value = "file") MultipartFile file,
			final RedirectAttributes redirectAttributes) throws Exception {

		Customer customer = serviceCustomer.get(customerId);

		if (customer == null || file.isEmpty())
			return "analysis/importAnalysisForm";

		File importFile = new File(request.getServletContext().getRealPath(
				"/WEB-INF/tmp")
				+ "/" + principal.getName() + "_" + System.nanoTime() + "");

		file.transferTo(importFile);

		Worker worker = new WorkerAnalysisImport(sessionFactory,
				serviceTaskFeedback, importFile, customer);

		worker.setPoolManager(workersPoolManager);
		if (serviceTaskFeedback.registerTask(principal.getName(),
				worker.getId()))
			executor.execute(worker);
		return "redirect:/feedback";
	}

}