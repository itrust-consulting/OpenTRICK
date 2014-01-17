/**
 * 
 */
package lu.itrust.business.view.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.naming.directory.InvalidAttributesException;
import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.AssetType;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.AssessmentManager;
import lu.itrust.business.component.ChartGenerator;
import lu.itrust.business.component.CustomDelete;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceAsset;
import lu.itrust.business.service.ServiceAssetType;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.exception.ConstraintViolationException;
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

/**
 * @author eom
 * 
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@Controller
@RequestMapping("/Asset")
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

	private boolean buildAsset(List<String[]> errors, Asset asset, String source, Locale locale) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);
			int id = jsonNode.get("id").asInt();
			if (id > 0)
				asset.setId(jsonNode.get("id").asInt());
			asset.setName(jsonNode.get("name").asText());
			asset.setValue(jsonNode.get("value").asDouble());
			asset.setSelected(jsonNode.get("selected").asBoolean());
			asset.setComment(jsonNode.get("comment").asText());
			asset.setHiddenComment(jsonNode.get("hiddenComment").asText());
			JsonNode node = jsonNode.get("assetType");
			AssetType assetType = serviceAssetType.get(node.get("id").asInt());
			if (assetType == null) {
				errors.add(new String[] { "assetType", messageSource.getMessage("error.assettype.not_found", null, "Selected asset type cannot be found", locale) });
				return false;
			}
			asset.setAssetType(assetType);
			return true;

		} catch (JsonProcessingException e) {
			errors.add(new String[] { "asset", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
		} catch (IOException e) {
			errors.add(new String[] { "asset", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
		} catch (InvalidAttributesException e) {
			errors.add(new String[] { "asset", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			errors.add(new String[] { "asset", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
		} catch (Exception e) {

			errors.add(new String[] { "asset", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
		}
		return false;
	}

	@RequestMapping(value = "/Select/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
	public @ResponseBody
	String[] select(@PathVariable int id, Principal principal, Locale locale) {
		try {
			Asset asset = serviceAsset.get(id);
			if (asset == null)
				return new String[] { "error", messageSource.getMessage("error.asset.not_found", null, "Asset cannot be found", locale) };

			if (asset.isSelected())
				assessmentManager.unSelectAsset(asset);
			else
				assessmentManager.selectAsset(asset);

			return new String[] { "error", messageSource.getMessage("success.asset.update.successfully", null, "Asset was updated successfully", locale) };
		} catch (InvalidAttributesException e) {
			e.printStackTrace();
			return new String[] { "error", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) };

		} catch (Exception e) {
			e.printStackTrace();
			return new String[] { "error", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) };
		}
	}

	@RequestMapping(value = "/Delete/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
	public @ResponseBody
	String[] delete(@PathVariable int id, Principal principal, Locale locale) {
		try {
			customDelete.deleteAsset(serviceAsset.get(id));
			return new String[] { "success", messageSource.getMessage("success.asset.delete.successfully", null, "Asset was deleted successfully", locale) };
		} catch (Exception e) {
			e.printStackTrace();
			return new String[] { "error", messageSource.getMessage("error.asset.delete.failed", null, "Asset cannot be deleted", locale) };
		}
	}

	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = "Accept=application/json")
	public String section(Model model, HttpSession session, Principal principal) {
		Integer integer = (Integer) session.getAttribute("selectedAnalysis");
		if (integer == null)
			return null;
		model.addAttribute("assets", serviceAsset.findByAnalysis(integer));
		return "analysis/components/asset";
	}

	@RequestMapping("/Edit/{id}")
	public String edit(@PathVariable Integer id, Model model) throws Exception {
		model.addAttribute("assettypes", serviceAssetType.loadAll());
		model.addAttribute("asset", serviceAsset.get(id));
		return "analysis/components/widgets/assetForm";
	}
	
	@RequestMapping("/Add")
	public String edit(Model model) throws Exception {
		model.addAttribute("assettypes", serviceAssetType.loadAll());
		return "analysis/components/widgets/assetForm";
	}

	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	List<String[]> save(@RequestBody String value, HttpSession session, Principal principal, Locale locale) {
		List<String[]> errors = new LinkedList<>();
		try {
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
			if (idAnalysis == null) {
				errors.add(new String[] { "analysis", messageSource.getMessage("error.analysis.no_selected", null, "There is no selected analysis", locale) });
				return errors;
			}
			Analysis analysis = serviceAnalysis.get(idAnalysis);
			if (analysis == null) {
				errors.add(new String[] { "analysis", messageSource.getMessage("error.analysis.not_found", null, "Selected analysis cannot be found", locale) });
				return errors;
			}

			Asset asset = new Asset();
			if (!buildAsset(errors, asset, value, locale))
				return errors;
			if (asset.getId() < 1) {
				assessmentManager.build(asset, idAnalysis);
			} else {
				serviceAsset.merge(asset);
				if (asset.isSelected())
					assessmentManager.selectAsset(asset);
				else
					assessmentManager.unSelectAsset(asset);
			}
		} catch (ConstraintViolationException e) {
			errors.add(new String[] { "assetType", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
		} catch (IllegalArgumentException e) {
			errors.add(new String[] { "asset", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
		}

		catch (Exception e) {
			errors.add(new String[] { "asset", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
		}
		return errors;
	}

	@RequestMapping("/Chart/Ale")
	public @ResponseBody
	String aleByAsset(HttpSession session, Model model, Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null)
			return null;
		return chartGenerator.aleByAsset(idAnalysis, locale);
	}

	@RequestMapping("/Chart/AssetType/Ale")
	public @ResponseBody
	String assetByALE(HttpSession session, Model model, Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null)
			return null;
		return chartGenerator.aleByAssetType(idAnalysis, locale);
	}

}
