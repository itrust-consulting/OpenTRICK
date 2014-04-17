/**
 * 
 */
package lu.itrust.business.component;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletContext;

import lu.itrust.business.TS.ExtendedParameter;
import lu.itrust.business.TS.MaturityParameter;
import lu.itrust.business.TS.Parameter;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 * @author eom
 * 
 */
public class ParameterManager {

	public static final String DEFAULT_SIMPLE_PARAMETER_PATH = "/WEB-INF/structure_data/simple_parameter.json";
	public static final String DEFAULT_Maturity_PARAMETER_PATH = "/WEB-INF/structure_data/maturity_parameter.json";
	private static final String DEFAULT_EXTENDED_PARAMETER_PATH = "/WEB-INF/structure_data/extended_parameter.json";

	public static void ComputeImpactValue(List<ExtendedParameter> parameters) {
		int limit = parameters.size() - 1;
		for (int i = 0; i < limit; i++) {
			if ((i % 2) != 0)
				ExtendedParameter.ComputeScales(parameters.get(i),
						parameters.get(i - 1), parameters.get(i + 1));
		}

	}
	
	public static boolean SaveDefault(List<Parameter> parameters, ServletContext servletContext) {
		List<Parameter> simpleParameters = new LinkedList<>();
		List<MaturityParameter> maturityParameters = new LinkedList<>();
		List<ExtendedParameter> extendedParameters = new LinkedList<>();
		for (Parameter parameter : parameters) {
			if(parameter instanceof ExtendedParameter)
				extendedParameters.add((ExtendedParameter) parameter);
			else if(parameter instanceof MaturityParameter)
				maturityParameters.add((MaturityParameter) parameter);
			else simpleParameters.add(parameter);
		}
		return SaveDefaultSimple(simpleParameters, servletContext) && SaveDefaultExtended(extendedParameters, servletContext) && SaveDefaultMaturity(maturityParameters, servletContext);
	}
	
	public static boolean SaveDefaultMaturity(List<MaturityParameter> parameters, ServletContext servletContext) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String filename = servletContext.getRealPath(DEFAULT_Maturity_PARAMETER_PATH);
			File file = new File(filename);
			mapper.writeValue(file, parameters);
			return true;
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean SaveDefaultSimple(List<Parameter> parameters, ServletContext servletContext){
		try {
			ObjectMapper mapper = new ObjectMapper();
			String filename = servletContext.getRealPath(DEFAULT_SIMPLE_PARAMETER_PATH);
			File file = new File(filename);
			mapper.writeValue(file, parameters);
			return true;
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	public static boolean SaveDefaultExtended(List<ExtendedParameter> parameters, ServletContext servletContext){
		try {
			ObjectMapper mapper = new ObjectMapper();
			String filename = servletContext.getRealPath(DEFAULT_EXTENDED_PARAMETER_PATH);
			File file = new File(filename);
			mapper.writeValue(file, parameters);
			return true;
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static List<Parameter> LoadDefault(ServletContext servletContext){
		List<Parameter> parameters = LoadDefaultSimple(servletContext);
		if(parameters == null)
			return null;
		List< ? extends Parameter> extendedParameters = LoadDefaultExtended(servletContext);
		if(extendedParameters == null)
			return null;
		List< ? extends Parameter> maturityParameters = LoadDefaultMaturity(servletContext);
		if(maturityParameters == null)
			return null;
		parameters.addAll(extendedParameters);
		parameters.addAll(maturityParameters);
		return parameters;
	}

	public static List<Parameter> LoadDefaultSimple(ServletContext servletContext){
		try {
			String filename = servletContext.getRealPath(DEFAULT_SIMPLE_PARAMETER_PATH);
			File file = new File(filename);
			if(!file.exists())
				return null;
			ObjectMapper mapper = new ObjectMapper();
			TypeReference<List<Parameter>> TypeReference = new TypeReference<List<Parameter>>() {
			};
			return mapper.readValue(file,TypeReference);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<MaturityParameter> LoadDefaultMaturity(ServletContext servletContext){
		try {
			String filename = servletContext.getRealPath(DEFAULT_Maturity_PARAMETER_PATH);
			File file = new File(filename);
			if(!file.exists())
				return null;
			ObjectMapper mapper = new ObjectMapper();
			TypeReference<List<MaturityParameter>> TypeReference = new TypeReference<List<MaturityParameter>>() {
			};
			return mapper.readValue(file,TypeReference);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<ExtendedParameter> LoadDefaultExtended(ServletContext servletContext){
		try {
			String filename = servletContext.getRealPath(DEFAULT_EXTENDED_PARAMETER_PATH);
			File file = new File(filename);
			if(!file.exists())
				return null;
			ObjectMapper mapper = new ObjectMapper();
			TypeReference<List<ExtendedParameter>> TypeReference = new TypeReference<List<ExtendedParameter>>() {
			};
			return mapper.readValue(file,TypeReference);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
