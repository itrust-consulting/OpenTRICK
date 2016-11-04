<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<fmt:setLocale value="fr" scope="session" />
<div class="tab-pane" id="tabParameterOther">
	<div class='section row' id="section_parameter_others">
		<div class="page-header tab-content-header">
			<div class="container">
				<div class="row-fluid">
					<h3>
						<spring:message code='label.title.other.parameters' text="Other parameters" />
					</h3>
				</div>
			</div>
		</div>
		<div class="col-md-6">
			<fieldset>
				<legend>
					<spring:message code="label.title.parameter.maturity_ilps" />
				</legend>
				<table class="table table-hover table-fixed-header-analysis table-condensed" id="tableMaturityIlps">
					<thead>
						<tr>
							<th class="textaligncenter"><spring:message code="label.parameter.maturity.category" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.maturity.task" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.maturity.sml0" /> (%)</th>
							<th class="textaligncenter"><spring:message code="label.parameter.maturity.sml1" /> (%)</th>
							<th class="textaligncenter"><spring:message code="label.parameter.maturity.sml2" /> (%)</th>
							<th class="textaligncenter"><spring:message code="label.parameter.maturity.sml3" /> (%)</th>
							<th class="textaligncenter"><spring:message code="label.parameter.maturity.sml4" /> (%)</th>
							<th class="textaligncenter"><spring:message code="label.parameter.maturity.sml5" /> (%)</th>
						</tr>
					</thead>
					<tfoot></tfoot>
					<tbody>
						<c:forEach items="${mappedParameters['ILPS']}" var="parameter">
							<tr data-trick-class="MaturityParameter" data-trick-id="${parameter.id}" data-trick-callback="updateMeasureEffience()">
								<td class="textaligncenter"><spring:message code="label.parameter.maturity.rsml.category.${fn:toLowerCase(parameter.category)}" /></td>
								<td class="textaligncenter"><spring:message code="label.parameter.maturity.rsml.description.${fn:toLowerCase(fn:replace(parameter.description,' ','_'))}" /></td>
								<td class="success textaligncenter" data-trick-field="SMLLevel0" data-trick-max-value="100" data-trick-min-value="0" data-trick-field-type="double"
									onclick="return editField(this);"><fmt:formatNumber value="${parameter.SMLLevel0*100}" maxFractionDigits="0" /></td>
								<td class="success textaligncenter" data-trick-field="SMLLevel1" data-trick-max-value="100" data-trick-min-value="0" data-trick-field-type="double"
									onclick="return editField(this);"><fmt:formatNumber value="${parameter.SMLLevel1*100}" maxFractionDigits="0" /></td>
								<td class="success textaligncenter" data-trick-field="SMLLevel2" data-trick-max-value="100" data-trick-min-value="0" data-trick-field-type="double"
									onclick="return editField(this);"><fmt:formatNumber value="${parameter.SMLLevel2*100}" maxFractionDigits="0" /></td>
								<td class="success textaligncenter" data-trick-field="SMLLevel3" data-trick-max-value="100" data-trick-min-value="0" data-trick-field-type="double"
									onclick="return editField(this);"><fmt:formatNumber value="${parameter.SMLLevel3*100}" maxFractionDigits="0" /></td>
								<td class="success textaligncenter" data-trick-field="SMLLevel4" data-trick-max-value="100" data-trick-min-value="0" data-trick-field-type="double"
									onclick="return editField(this);"><fmt:formatNumber value="${parameter.SMLLevel4*100}" maxFractionDigits="0" /></td>
								<td class="success textaligncenter" data-trick-field="SMLLevel5" data-trick-max-value="100" data-trick-min-value="0" data-trick-field-type="double"
									onclick="return editField(this);"><fmt:formatNumber value="${parameter.SMLLevel5*100}" maxFractionDigits="0" /></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</fieldset>
		</div>
		<div class="col-md-6">
			<fieldset>
				<legend>
					<spring:message code="label.title.parameter.simple.various" />
				</legend>
				<table class="table table-hover table-condensed">
					<thead>
						<tr>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.internal_setup" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.external_setup" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.default_life_time" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.max_rrf" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.soa" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.mandatory_phase" /></th>
						</tr>
					</thead>
					<tbody>
						<tr data-trick-class="SimpleParameter" class='success'>
							<c:forEach items="${mappedParameters['SINGLE']}" var="parameter">
								<c:choose>
									<c:when test="${parameter.description=='max_rrf' or parameter.description=='soaThreshold'}">
										<td data-trick-id="${parameter.id}" data-trick-min-value='0' data-trick-max-value='100' class="textaligncenter" data-trick-field="value" data-trick-field-type="double"
											onclick="return editField(this);"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" pattern="#" /></td>
									</c:when>
									<c:when test="${parameter.description== 'lifetime_default'}">
										<td data-trick-id="${parameter.id}" data-trick-min-value='1e-19' class="textaligncenter" data-trick-field="value" data-trick-callback='updateMeasuresCost()'
											data-trick-field-type="double" onclick="return editField(this);"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" pattern="#" /></td>
									</c:when>
									<c:when test="${parameter.description=='mandatoryPhase'}">
										<td data-trick-id="${parameter.id}" data-trick-callback-pre="extractPhase(this,true)" class="textaligncenter" data-trick-field="value" data-trick-field-type="double"
											onclick="return editField(this);"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" pattern="#" /></td>
									</c:when>
									<c:otherwise>
										<td data-trick-id="${parameter.id}" class="textaligncenter" data-trick-field="value" data-trick-field-type="double" data-trick-callback='updateMeasuresCost()'
											onclick="return editField(this);"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" pattern="#" /></td>
									</c:otherwise>
								</c:choose>

							</c:forEach>
						</tr>
					</tbody>
				</table>
			</fieldset>
		</div>
		<spring:message code='label.nil' var="nil" />
		<spring:message code='label.all' var="all" />
		<spring:message code='label.compliant' var="compliant" />
		<c:if test="${type=='QUALITATIVE'}">
			<div class="col-md-6">
				<fieldset>
					<legend>
						<spring:message code="label.title.parameter.simple.cssf" />
					</legend>
					<table class="table table-hover table-condensed">
						<thead>
							<tr>
								<th class="textaligncenter"><spring:message code="label.parameter.simple.cssf.impact_threshold" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.simple.cssf.probability_threshold" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.simple.cssf.direct_size" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.simple.cssf.indirect_size" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.simple.cssf.cia_size" /></th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<c:forEach items="${mappedParameters['CSSF']}" var="parameter">
									<c:choose>
										<c:when test="${parameter.description=='cssfImpactThreshold' or parameter.description=='cssfProbabilityThreshold'}">
											<td data-trick-class="SimpleParameter" data-trick-id="${parameter.id}" data-trick-min-value='0' data-trick-max-value='10' data-trick-step-value='1'
												class="success textaligncenter" data-trick-field="value" data-trick-field-type="double" onclick="return editField(this);"><fmt:formatNumber
													value="${parameter.value}" maxFractionDigits="0" pattern="#" /></td>
										</c:when>
										<c:when test="${parameter.description== 'cssfCIASize' or parameter.description== 'cssfDirectSize' or parameter.description== 'cssfIndirectSize'}">
											<fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" pattern="#" var="cssfSize" />
											<td data-trick-class="Parameter" data-trick-id="${parameter.id}" data-trick-choose-translate='${nil},${all},${compliant}' data-trick-min-value='-2'
												data-trick-step-value='1' data-trick-max-value='1000' class="success textaligncenter" data-trick-field="value" data-trick-field-type="double"
												onclick="return editField(this);"><c:choose>
													<c:when test="${parameter.value <= -2 }">
														${nil}
														</c:when>
													<c:when test="${parameter.value == -1}">
														${all}
														</c:when>
													<c:when test="${parameter.value == 0}">
														${compliant}
														</c:when>
													<c:otherwise>${cssfSize}</c:otherwise>
												</c:choose></td>
										</c:when>
									</c:choose>
								</c:forEach>
							</tr>
						</tbody>
					</table>
				</fieldset>
			</div>
		</c:if>
		<div class="col-md-6">
			<fieldset>
				<legend>
					<spring:message code="label.title.parameter.simple.maturity_level" />
				</legend>

				<table class="table table-hover table-condensed">
					<thead>
						<tr>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.sml0" /> (%)</th>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.sml1" /> (%)</th>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.sml2" /> (%)</th>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.sml3" /> (%)</th>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.sml4" /> (%)</th>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.sml5" /> (%)</th>
						</tr>
					</thead>
					<tbody>
						<tr data-trick-class="SimpleParameter" data-trick-min-value='0' data-trick-max-value='100' data-trick-callback="updateMeasureEffience()">
							<c:forEach items="${mappedParameters['MAXEFF']}" var="parameter">
								<td data-trick-field="value" data-trick-field-type="double" data-trick-id="${parameter.id}" class="success textaligncenter" onclick="return editField(this);"><fmt:formatNumber
										value="${parameter.value}" maxFractionDigits="0" /></td>
							</c:forEach>
						</tr>
					</tbody>
				</table>
			</fieldset>
		</div>
		<div class="col-md-6">
			<fieldset id="Maturity_implementation_rate">
				<legend>
					<spring:message code="label.title.parameter.simple.smt" />
				</legend>
				<table class="table table-hover table-condensed">
					<thead>
						<tr>
							<th class="textaligncenter"><spring:message code="label.parameter.level" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.implementation" /> (%)</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${mappedParameters['IMPSCALE']}" var="parameter">
							<tr data-trick-class="SimpleParameter" data-trick-id="${parameter.id}">
								<td class="textaligncenter"><spring:message code="label.parameter.simple.smt.level_${parameter.description}" text="${parameter.description}" /></td>
								<td data-trick-field="value" data-trick-field-type="double" data-trick-min-value='0' data-trick-max-value='100' class="success textaligncenter"
									onclick="return editField(this);"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" /></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</fieldset>
		</div>
	</div>
</div>