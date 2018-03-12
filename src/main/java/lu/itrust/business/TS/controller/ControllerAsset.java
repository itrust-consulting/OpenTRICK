/**
 * 
 */

package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

import java.security.Principal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

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

import lu.itrust.business.TS.component.AssessmentAndRiskProfileManager;
import lu.itrust.business.TS.component.ChartGenerator;
import lu.itrust.business.TS.component.CustomDelete;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAssessment;
import lu.itrust.business.TS.database.service.ServiceAsset;
import lu.itrust.business.TS.database.service.ServiceAssetType;
import lu.itrust.business.TS.database.service.ServiceDataValidation;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.helper.JsonMessage;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisSetting;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.asset.AssetType;
import lu.itrust.business.TS.model.general.OpenMode;
import lu.itrust.business.TS.validator.AssetValidator;
import lu.itrust.business.TS.validator.field.ValidatorField;

/**
 * @author eom
 * 
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@Controller
@RequestMapping("/Analysis/Asset")
public class ControllerAsset {

	@Autowired
	private AssessmentAndRiskProfileManager assessmentAndRiskProfileManager;

	@Autowired
	private ChartGenerator chartGenerator;

	@Autowired
	private CustomDelete customDelete;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceAssessment serviceAssessment;

	@Autowired
	private ServiceAsset serviceAsset;

	@Autowired
	private ServiceAssetType serviceAssetType;

	@Autowired
	private ServiceDataValidation serviceDataValidation;

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
	@RequestMapping(value = "/Chart/Ale", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Object aleByAsset(HttpSession session, Model model, Principal principal, Locale locale) throws Exception {
		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		// generate chart of assets for this analysis
		return chartGenerator.aleByAsset(idAnalysis, locale);
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
	@RequestMapping(value = "/Chart/Type/Ale", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Object assetByALE(HttpSession session, Model model, Principal principal, Locale locale) throws Exception {
		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		// generate chart of assets for this analysis
		return chartGenerator.aleByAssetType(idAnalysis, locale);
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
	@RequestMapping(value = "/Delete/{idAsset}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #idAsset, 'Asset', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String delete(@PathVariable int idAsset, Principal principal, Locale locale, HttpSession session) throws Exception {
		try {
			// delete asset ( delete asset from from assessments) then from
			// assets
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			customDelete.deleteAsset(idAsset, idAnalysis);
			return JsonMessage.Success(messageSource.getMessage("success.asset.delete.successfully", null, "Asset was deleted successfully", locale));
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.asset.delete.failed", null, "Asset cannot be deleted", locale));
		}
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
	@RequestMapping(value = "/Edit/{elementID}", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Asset', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String edit(@PathVariable Integer elementID, Model model, Principal principal, HttpSession session, Locale locale) throws Exception {
		// add all assettypes to model
		model.addAttribute("assettypes", serviceAssetType.getAll());
		// add asset object to model
		model.addAttribute("asset", serviceAsset.get(elementID));
		loadAnalysisSettings(model, (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
		return "analyses/single/components/asset/form";
	}

	@RequestMapping("/Add")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String edit(Model model, HttpSession session, Principal principal) throws Exception {
		model.addAttribute("assettypes", serviceAssetType.getAll());
		loadAnalysisSettings(model, (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
		return "analyses/single/components/asset/form";
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
	@RequestMapping(value = "/Chart/Risk", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Object riskByAsset(HttpSession session, Model model, Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		return chartGenerator.riskByAsset(idAnalysis, locale);
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
	@RequestMapping(value = "/Chart/Type/Risk", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Object riskByAssetType(HttpSession session, Model model, Principal principal, Locale locale) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		return chartGenerator.riskByAssetType(idAnalysis, locale);
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
	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Object save(@RequestBody String value, HttpSession session, Principal principal, Locale locale) throws Exception {
		Map<String, Object> results = new LinkedHashMap<>(), errors = new HashMap<>();
		try {
			results.put("errors", errors);
			// retrieve analysis id
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			if (serviceAnalysis.isProfile(idAnalysis))
				throw new TrickException("error.action.not_authorise", "Action does not authorised");
			JsonNode assetNode = new ObjectMapper().readTree(value);
			// read asset id node
			int idAsset = getInt("id", assetNode);

			Asset asset = idAsset > 1 ? serviceAsset.getFromAnalysisById(idAnalysis, idAsset) : new Asset();
			if (asset == null) {
				errors.put("asset", messageSource.getMessage("error.asset.not_belongs_to_analysis", null, "Asset does not belong to selected analysis", locale));
				return results;
			}
			// build asset
			buildAsset(assetNode, idAnalysis, errors, asset, locale);
			if (!errors.isEmpty())
				return results;
			asset.setValue(asset.getValue() * 1000);
			if (asset.getId() > 0)
				serviceAsset.saveOrUpdate(asset);
			else {
				Analysis analysis = serviceAnalysis.get(idAnalysis);
				analysis.add(asset);
				serviceAnalysis.saveOrUpdate(analysis);
			}

			// update selected status
			if (asset.isSelected())
				assessmentAndRiskProfileManager.selectAsset(asset);
			else
				assessmentAndRiskProfileManager.unSelectAsset(asset);
			// create assessments for the new asset and save asset and
			// Assessments into analysis
			assessmentAndRiskProfileManager.build(asset, idAnalysis);
			results.put("id", asset.getId());
		} catch (TrickException e) {
			errors.put("asset", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			TrickLogManager.Persist(e);
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			errors.put("asset", messageSource.getMessage(e.getMessage(), null, locale));
		}
		return results;
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
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String section(Model model, HttpSession session, Principal principal, Locale locale) throws Exception {
		// retrieve analysis id
		Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		List<Asset> assets = serviceAsset.getAllFromAnalysis(integer);
		List<Assessment> assessments = serviceAssessment.getAllFromAnalysisAndSelected(integer);
		loadAnalysisSettings(model, integer);
		AnalysisType type = serviceAnalysis.getAnalysisTypeById(integer);
		// load all assets of analysis to model
		if(AnalysisType.isQuantitative(type))
			model.addAttribute("assetALE", AssessmentAndRiskProfileManager.ComputeAssetALE(assets, assessments));
		model.addAttribute("assets", assets);
		model.addAttribute("type", type);
		model.addAttribute("isEditable", !OpenMode.isReadOnly((OpenMode) session.getAttribute(Constant.OPEN_MODE)));
		model.addAttribute("show_uncertainty", serviceAnalysis.isAnalysisUncertainty(integer));
		return "analyses/single/components/asset/asset";
	}

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
	@RequestMapping(value = "/Select/{elementID}", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Asset', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String select(@PathVariable int elementID, Principal principal, Locale locale, HttpSession session) throws Exception {
		try {
			// retrieve asset
			assessmentAndRiskProfileManager.toggledAsset(elementID);
			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.asset.update.successfully", null, "Asset was updated successfully", locale));
		} catch (Exception e) {
			// return error message
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.internal", null, "Internal error occurred", locale));
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
	@RequestMapping(value = "/Select", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody List<String> selectMultiple(@RequestBody List<Integer> ids, Principal principal, Locale locale, HttpSession session) throws Exception {
		Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		// init list of errors
		List<String> errors = new LinkedList<String>();
		if (!serviceAsset.belongsToAnalysis(integer, ids)) {
			errors.add(JsonMessage.Error(messageSource.getMessage("label.unauthorized_asset", null, "One of the assets does not belong to this analysis!", locale)));
			return errors;
		}
		assessmentAndRiskProfileManager.toggledAssets(ids);
		return errors;
	}

	/**
	 * buildAsset: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * 
	 * @param errors
	 * @param asset
	 * @param source
	 * @param locale
	 * @return
	 */
	private boolean buildAsset(JsonNode jsonNode, Integer idAnalysis, Map<String, Object> errors, Asset asset, Locale locale) {
		try {
			// check if asset is to be updated or created
			ValidatorField validator = serviceDataValidation.findByClass(Asset.class);
			if (validator == null)
				serviceDataValidation.register(validator = new AssetValidator());

			String name = jsonNode.get("name").asText();

			JsonNode node = jsonNode.get("assetType");

			AssetType assetType = serviceAssetType.get(node.get("id").asInt());

			Double value = getDouble(jsonNode);

			String error = null;

			asset.setComment(jsonNode.get("comment").asText());

			asset.setHiddenComment(jsonNode.get("hiddenComment").asText(""));

			error = validator.validate(asset, "name", name);
			if (error != null)
				errors.put("name", serviceDataValidation.ParseError(error, messageSource, locale));
			else if ((asset.getId() > 0 && !asset.getName().equals(name) || asset.getId() < 0) && serviceAsset.exist(idAnalysis, name))
				errors.put("name", messageSource.getMessage("error.asset.duplicate", null, String.format("Asset name is already in use", name), locale));
			else
				asset.setName(name);

			asset.setSelected(jsonNode.get("selected").asBoolean());

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
			TrickLogManager.Persist(e);
		} catch (Exception e) {
			// return error message
			errors.put("asset", messageSource.getMessage("error.internal", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
		}

		// return error message
		return false;
	}

	private Double getDouble(JsonNode jsonNode) throws ParseException {
		try {
			return NumberFormat.getInstance(Locale.FRANCE).parse(jsonNode.get("value").asText()).doubleValue();
		} catch (Exception e) {
			return null;
		}
	}

	private int getInt(String fieldName, JsonNode jsonNode) {
		try {
			return jsonNode.get(fieldName).asInt(0);
		} catch (Exception e) {
			return 0;
		}
	}

	private void loadAnalysisSettings(Model model, Integer integer) {
		Map<String, String> settings = serviceAnalysis.getSettingsByIdAnalysis(integer);
		AnalysisSetting rawSetting = AnalysisSetting.ALLOW_RISK_ESTIMATION_RAW_COLUMN, hiddenCommentSetting = AnalysisSetting.ALLOW_RISK_HIDDEN_COMMENT;
		model.addAttribute("showHiddenComment", Analysis.findSetting(hiddenCommentSetting, settings.get(hiddenCommentSetting.name())));
		model.addAttribute("showRawColumn", Analysis.findSetting(rawSetting, settings.get(rawSetting.name())));
	}
}
