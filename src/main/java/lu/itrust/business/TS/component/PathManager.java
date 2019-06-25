/**
 * 
 */
package lu.itrust.business.TS.component;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * @author eomar
 *
 */
@Component
public class PathManager {
	
	@Autowired
	private ServletContext servletContext;
	
	public String getPath(String name) {
		return servletContext == null? null : servletContext.getRealPath(name);
	}
	
	public Resource getResource(String name) {
		return new ClassPathResource(name);
	}

}
