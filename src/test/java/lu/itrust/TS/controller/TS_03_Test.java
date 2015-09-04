/**
 * 
 */
package lu.itrust.TS.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.Assert.notNull;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceCustomer;
import lu.itrust.business.TS.database.service.ServiceLanguage;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.Language;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author eomar
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TS_03_Test extends SpringTestConfiguration {

	@Autowired
	private ServiceLanguage serviceLanguage;

	@Autowired
	private ServiceCustomer serviceCustomer;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Test
	@Transactional
	public void test_00_CreateSimpleAnalysis() throws Exception {
		Language language = serviceLanguage.getByAlpha3("FRA");
		notNull(language, "French language cannot be found");
		Customer customer = serviceCustomer.getFromContactPerson("me");
		notNull(customer, "'me' customer cannot be found");
		this.mockMvc
				.perform(
						post("/Analysis/Build/Save").with(csrf()).with(httpBasic(USERNAME, PASSWORD)).accept(APPLICATION_JSON_CHARSET_UTF_8).param("author", "Admin Admin")
								.param("name", "simple-analysis").param("version", "0.0.1").param("comment", "comment").param("customer", String.valueOf(customer.getId()))
								.param("language", String.valueOf(language.getId()))).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.success").exists());
	}
	
	@Test
	@Transactional(readOnly=true)
	public void test_00_SelectAnalysis(){
		Customer customer = serviceCustomer.getFromContactPerson("me");
		notNull(customer, "'me' customer cannot be found");
	
	}

}
