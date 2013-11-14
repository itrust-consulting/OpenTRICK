/**
 * 
 */
package lu.itrust.business.view.controller;

import java.util.List;

import lu.itrust.business.TS.AssetType;
import lu.itrust.business.service.ServiceAssetType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author eom
 *
 */
@Secured("ROLE_USER")
@Controller
@RequestMapping("/AssetType")
public class ControllerAssetType {
	
	@Autowired
	private ServiceAssetType serviceAssetType;
	@Secured("ROLE_USER")
	@RequestMapping(value = "/All", method = RequestMethod.GET, headers = "Accept=application/json")
	public @ResponseBody List<AssetType> findAll() throws Exception{
		return serviceAssetType.loadAll();
	}

}
