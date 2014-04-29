/**
 * 
 */
package lu.itrust.business.component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import lu.itrust.business.TS.RiskInformation;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 * @author eomar
 *
 */
public class RiskInformationManager {
	
	public static final String DEFAULT_RISK_INFORMATION_PATH = "/WEB-INF/structure_data/risk_informations.json";
	
	public static Map<String, List<RiskInformation>> Split(List<RiskInformation> riskInformations){
		Map<String, List<RiskInformation>> mapper = new LinkedHashMap<>();
		String key = "";
		Collections.sort(riskInformations, new RiskInformationComparator());
		for (RiskInformation riskInformation : riskInformations) {
			key = riskInformation.getCategory().startsWith("Risk_TB")? "Risk" : riskInformation.getCategory();
			List<RiskInformation> informations = mapper.get(key);
			if(informations == null)
				mapper.put(key, informations = new ArrayList<>());
			informations.add(riskInformation);
		}
		return mapper;
	}
}
