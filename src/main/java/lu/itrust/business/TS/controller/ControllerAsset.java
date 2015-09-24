/**
 * 
 */
package lu.itrust.business.TS.controller;

import java.security.Principal;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.naming.directory.InvalidAttributesException;
import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.component.ChartGenerator;
import lu.itrust.business.TS.component.CustomDelete;
import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAssessment;
import lu.itrust.business.TS.database.service.ServiceAsset;
import lu.itrust.business.TS.database.service.ServiceAssetType;
import lu.itrust.business.TS.database.service.ServiceDataValidation;
import lu.itrust.business.TS.database.service.ServiceLanguage;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.assessment.helper.AssessmentManager;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.asset.AssetType;
import lu.itrust.business.TS.validator.AssetValidator;
import lu.itrust.business.TS.validator.field.ValidatorField;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author eom
 * 
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@Controller
@RequestMapping("/Analysis/Asset")
public class ControllerAsset {

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceAssetType serviceAssetType;

	@Autowired
	private ServiceAsset serviceAsset;

	@Autowired
	private AssessmentManager assessmentManager;

	@Autowired
	private CustomDelete customDelete;

	@Autowired
	private ChartGenerator chartGenerator;

	@Autowired
	private ServiceAssessment serviceAssessment;

	@Autowired
	private ServiceDataValidation serviceDataValidation;

	@Autowired
	private ServiceUserAnalysisRight serviceUserAnalysisRight;

	@Autowired
	private ServiceLanguage serviceLanguage;

	@Autowired
	private ServiceUser serviceUser;

	/**
	 * select: <br>
	 * Description
	 * 
	 * @param id
	 * @param principal
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Select/{elementID}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Asset', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String select(@PathVariable int elementID, Principal principal, Locale locale, HttpSession session) throws Exception {
		try {

			Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			Locale customLocale = null;

			if (integer != null)
				customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(integer).getAlpha2());

			// retrieve asset
			Asset asset = serviceAsset.get(elementID);
			if (asset == null)
				return JsonMessage.Error(messageSource.getMessage("error.asset.not_found", null, "Asset cannot be found", customLocale != null ? customLocale : locale));

			// set asset selected or unselected (toggle)
			if (asset.isSelected())
				assessmentManager.unSelectAsset(asset);
			else
				assessmentManager.selectAsset(asset);

			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.asset.update.successfully", null, "Asset was updated successfully", customLocale != null ? customLocale
					: locale));
		} catch (InvalidAttributesException e) {
			Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			Locale customLocale = null;

			if (integer != null)
				customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(integer).getAlpha2());
			// return error message
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), customLocale != null ? customLocale : locale));

		} catch (Exception e) {
			Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			Locale customLocale = null;

			if (integer != null)
				customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(integer).getAlpha2());
			// return error message
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), customLocale != null ? customLocale : locale));
		}
	}

	/**
	 * selectMultiple: <br>
	 * Description
	 * 
	 * @param ids
	 * @param principal
	 * @param locale
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Select", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody List<String> selectMultiple(@RequestBody List<Integer> ids, Principal principal, Locale locale, HttpSession session) throws Exception {

		// init list of errors
		List<String> errors = new LinkedList<String>();

		for (Integer id : ids) {
			Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(integer).getAlpha2());

			if (!serviceAsset.belongsToAnalysis(integer, id)) {
				errors.add(JsonMessage.Error(messageSource.getMessage("label.unauthorized_asset", null, "One of the assets does not belong to this analysis!",
						customLocale != null ? customLocale : locale)));
				return errors;
			}
		}

		// parse each sent id's
		for (Integer id : ids) {
			try {

				Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

				Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(integer).getAlpha2());

				select(id, principal, customLocale != null ? customLocale : locale, session);

			} catch (Exception e) {
				e.printStackTrace();
				Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

				Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(integer).getAlpha2());

				errors.add(JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), customLocale != null ? customLocale : locale)));
			}
		}
		return errors;
	}

	@RequestMapping("/Add")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String edit(Model model, HttpSession session, Principal principal) throws Exception {
		model.addAttribute("assettypes", serviceAssetType.getAll());
		return "analyses/single/components/asset/manageAsset";
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param id
	 * @param principal
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Delete/{idAsset}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #idAsset, 'Asset', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String delete(@PathVariable int idAsset, Principal principal, Locale locale, HttpSession session) throws Exception {
		Locale customLocale = locale;
		try {
			// delete asset ( delete asset from from assessments) then from
			// assets
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());
			customDelete.deleteAsset(idAsset, idAnalysis);
			return JsonMessage.Success(messageSource.getMessage("success.asset.delete.successfully", null, "Asset was deleted successfully", customLocale != null ? customLocale
					: locale));
		} catch (TrickException e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), customLocale != null ? customLocale : locale));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.asset.delete.failed", null, "Asset cannot be deleted", customLocale != null ? customLocale : locale));
		}
	}

	/**
	 * section: <br>
	 * Description
	 * 
	 * @param model
	 * @param session
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String section(Model model, HttpSession session, Principal principal, Locale locale) throws Exception {
		// retrieve analysis id
		Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		if (integer == null)
			return null;

		Boolean isReadOnly = (Boolean) session.getAttribute(Constant.SELECTED_ANALYSIS_READ_ONLY);
		if (isReadOnly == null)
			isReadOnly = false;

		List<Asset> assets = serviceAsset.getAllFromAnalysis(integer);
		List<Assessment> assessments = serviceAssessment.getAllFromAnalysisAndSelected(integer);

		// load all assets of analysis to model
		model.addAttribute("assetALE", AssessmentManager.ComputeAssetALE(assets, assessments));
		model.addAttribute("assets", assets);
		model.addAttribute("isEditable", !isReadOnly && serviceUserAnalysisRight.isUserAuthorized(integer, principal.getName(), AnalysisRight.MODIFY));
		model.addAttribute("show_uncertainty", serviceAnalysis.isAnalysisUncertainty(integer));
		model.addAttribute("language", serviceLanguage.getFromAnalysis(integer).getAlpha2());
		return "analyses/single/components/asset/asset";
	}

	/**
	 * edit: <br>
	 * Description
	 * 
	 * @param id
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Edit/{elementID}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Asset', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String edit(@PathVariable Integer elementID, Model model, Principal principal, HttpSession session, Locale locale) throws Exception {

		// add all assettypes to model
		model.addAttribute("assettypes", serviceAssetType.getAll());

		// add asset object to model
		model.addAttribute("asset", serviceAsset.get(elementID));

		return "analyses/single/components/asset/manageAsset";
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param value
	 * @param session
	 * @param principal
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Map<String, String> save(@RequestBody String value, HttpSession session, Principal principal, Locale locale) throws Exception {

		// create error list
		Map<String, String> errors = new LinkedHashMap<String, String>();
		try {

			// retrieve analysis id
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			if (idAnalysis == null || serviceAnalysis.isProfile(idAnalysis)) {
				errors.put("asset", messageSource.getMessage("error.analysis.no_selected", null, "There is no selected analysis", locale));
				return errors;
			}
			
			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());

			// create new asset object
			Asset asset = new Asset();

			// build asset
			buildAsset(errors, asset, value, customLocale != null ? customLocale : locale);

			if (!errors.isEmpty())
				// return error on failure
				return errors;

			asset.setValue(asset.getValue() * 1000);

			if (asset.getId() > 0) {
				if (!serviceAsset.belongsToAnalysis(idAnalysis, asset.getId())) {
					errors.put("asset", messageSource.getMessage("error.asset.not_belongs_to_analysis", null, "Asset does not belong to selected analysis",
							customLocale != null ? customLocale : locale));
					return errors;
				}

				serviceAsset.saveOrUpdate(asset);
			} else if (serviceAsset.exist(idAnalysis, asset.getName())) {
				errors.put("name", messageSource.getMessage("error.asset.duplicate", new String[] { asset.getName() }, String.format("Asset (%s) is duplicated", asset.getName()),
						customLocale != null ? customLocale : locale));
				return errors;
			} else {
				Analysis analysis = serviceAnalysis.get(idAnalysis);
				analysis.addAnAsset(asset);
				serviceAnalysis.saveOrUpdate(analysis);
			}

			// update selected status
			if (asset.isSelected())
				assessmentManager.selectAsset(asset);
			else
				assessmentManager.unSelectAsset(asset);

			// create assessments for the new asset and save asset and
			// Assessments into analysis
			assessmentManager.build(asset, idAnalysis);

		} catch (TrickException e) {
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			if (idAnalysis != null) {
				Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());
				errors.put("asset", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), customLocale != null ? customLocale : locale));
			} else
				errors.put("asset", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			e.printStackTrace();
			return errors;
		} catch (Exception e) {
			e.printStackTrace();
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			if (idAnalysis != null) {
				Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());
				errors.put("asset", messageSource.getMessage(e.getMessage(), null, customLocale != null ? customLocale : locale));
			} else
				errors.put("asset", messageSource.getMessage(e.getMessage(), null, locale));

			return errors;
		}
		return errors;
	}

	/**
	 * aleByAsset: <br>
	 * Description
	 * 
	 * @param session
	 * @param model
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Chart/Ale", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody String aleByAsset(HttpSession session, Model model, Principal principal, Locale locale) throws Exception {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		if (idAnalysis == null)
			return null;

		Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());

		// generate chart of assets for this analysis
		return chartGenerator.aleByAsset(idAnalysis, customLocale != null ? customLocale : locale);
	}

	/**
	 * assetByALE: <br>
	 * Description
	 * 
	 * @param session
	 * @param model
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Chart/Type/Ale", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody String assetByALE(HttpSession session, Model model, Principal principal, Locale locale) throws Exception {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		if (idAnalysis == null)
			return null;

		Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());

		// generate chart of assets for this analysis
		return chartGenerator.aleByAssetType(idAnalysis, customLocale != null ? customLocale : locale);
	}

	/**
	 * buildAsset: <br>
	 * Description
	 * 
	 * @param errors
	 * @param asset
	 * @param source
	 * @param locale
	 * @return
	 */
	private boolean buildAsset(Map<String, String> errors, Asset asset, String source, Locale locale) {
		try {

			// create json parser for the source
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);

			// read asset id node
			int id = jsonNode.get("id").asInt();

			// check if asset is to be updated or created
			if (id > 0)
				asset.setId(id);

			ValidatorField validator = serviceDataValidation.findByClass(Asset.class);
			if (validator == null)
				serviceDataValidation.register(validator = new AssetValidator());

			String name = jsonNode.get("name").asText();

			JsonNode node = jsonNode.get("assetType");
			AssetType assetType = serviceAssetType.get(node.get("id").asInt());

			double value = NumberFormat.getInstance(Locale.FRANCE).parse(jsonNode.get("value").asText()).doubleValue();

			String error = null;

			asset.setComment(jsonNode.get("comment").asText());
			asset.setHiddenComment(jsonNode.get("hiddenComment").asText());

			error = validator.validate(asset, "name", name);
			if (error != null)
				errors.put("name", serviceDataValidation.ParseError(error, messageSource, locale));
			else {
				asset.setName(name);
				asset.setSelected(jsonNode.get("selected").asBoolean());
			}

			error = validator.validate(asset, "assetType", assetType);
			if (error != null)
				errors.put("assetType", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				asset.setAssetType(assetType);

			error = validator.validate(asset, "value", value);
			if (error != null)
				errors.put("value", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				asset.setValue(value);

			// return success message
			return true;

		} catch (TrickException e) {
			// return error message
			errors.put("asset", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			e.printStackTrace();
		} catch (Exception e) {
			// return error message
			errors.put("asset", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
		}

		// return error message
		return false;
	}
}
