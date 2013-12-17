/**
 * 
 */
package lu.itrust.business.view.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author eomar
 *
 */
@Secured("ROLE_USER")
@RequestMapping("/Measure")
public class ControllerMeasure {

}
