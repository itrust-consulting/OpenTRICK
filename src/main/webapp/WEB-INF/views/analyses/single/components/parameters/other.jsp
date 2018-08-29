<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<fmt:setLocale value="fr" scope="session" />
<div class="tab-pane" id="tab-parameter">
	<div class='section row' id='section_parameter'>
		<div class="page-header tab-content-header">
			<div class="container">
				<div class="row-fluid">
					<h3>
						<spring:message code='label.title.parameter' />
					</h3>
				</div>
			</div>
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
							<c:if test="${type.quantitative }">
								<th class="textaligncenter"><spring:message code="label.parameter.simple.max_rrf" /></th>
							</c:if>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.soa" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.simple.mandatory_phase" /></th>
						</tr>
					</thead>
					<tbody>
						<tr data-trick-class="SimpleParameter" class='editable'>
							<c:forEach items="${mappedParameters['SINGLE']}" var="parameter">
								<c:choose>
									<c:when test="${parameter.description=='soaThreshold'}">
										<td data-trick-id="${parameter.id}" data-name="soaThreshold" data-trick-min-value='0' data-trick-max-value='100' class="textaligncenter" data-trick-field="value" data-trick-field-type="double"
											onclick="return editField(this);" data-trick-callback='soaThresholdUpdate()'><fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" pattern="#" /></td>
									</c:when>
									<c:when test="${parameter.description=='max_rrf'}">
										<td data-trick-id="${parameter.id}" data-name="max_rrf" data-trick-min-value='0' data-trick-max-value='100' class="textaligncenter" data-trick-field="value" data-trick-field-type="double"
											onclick="return editField(this);" data-trick-callback='updateMaxRRF()'><fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" pattern="#" /></td>
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
		<c:if test="${type.qualitative}">
			<spring:message code='label.nil' var="nil" />
			<spring:message code='label.all' var="all" />
			<spring:message code='label.compliant' var="compliant" />
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
							<tr data-trick-callback="reloadSection('section_riskregister');" data-trick-class="SimpleParameter">
								<c:forEach items="${mappedParameters['CSSF']}" var="parameter">
									<c:choose>
										<c:when test="${parameter.description=='cssfImpactThreshold' or parameter.description=='cssfProbabilityThreshold'}">
											<td data-trick-id="${parameter.id}" data-trick-min-value='0' data-trick-max-value='10' data-trick-step-value='1' class="editable textaligncenter"
												data-trick-field="value" data-trick-field-type="double" onclick="return editField(this);"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="0"
													pattern="#" /></td>
										</c:when>
										<c:when test="${parameter.description== 'cssfCIASize' or parameter.description== 'cssfDirectSize' or parameter.description== 'cssfIndirectSize'}">
											<fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" pattern="#" var="cssfSize" />
											<td data-trick-id="${parameter.id}" data-trick-choose-translate='${nil},${all},${compliant}' data-trick-min-value='-2' data-trick-step-value='1'
												data-trick-max-value='1000' class="editable textaligncenter" data-trick-field="value" data-trick-field-type="double" onclick="return editField(this);"><c:choose>
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

			<div class="col-sm-3">
				<fieldset>
					<legend>
						<spring:message code="label.parameter.probability.label" />
					</legend>
					<table class="table table-hover table-fixed-header-analysis table-condensed">
						<thead>
							<tr>
								<th class="textaligncenter"><spring:message code="label.parameter.level" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.label" /></th>
							</tr>
						</thead>
						<tbody>

							<c:forEach items="${mappedParameters['PROBA']}" var="parameter" varStatus="status">
								<c:if test="${parameter.level>0}">
									<tr data-trick-class="LikelihoodParameter" data-trick-id="${parameter.id}">
										<td data-trick-field="level" class="textaligncenter"><spring:message text="${parameter.level}" /></td>
										<td data-trick-field="label" data-trick-field-type="string" class="editable textaligncenter"
											data-trick-callback="reloadSection('section_parameter_impact_probability');reloadRiskHeatMapSection();" onclick="return editField(this);"><spring:message
												text="${parameter.label}" /></td>
									</tr>
								</c:if>
							</c:forEach>
						</tbody>
					</table>
				</fieldset>
			</div>
			<div class="col-sm-3">
				<fieldset>
					<legend>
						<spring:message code="label.parameter.impact.label" />
					</legend>
					<table class="table table-hover table-fixed-header-analysis table-condensed">
						<thead>
							<tr>
								<th class="textaligncenter"><spring:message code="label.parameter.level" /></th>
								<th class="textaligncenter"><spring:message code="label.parameter.label" /></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${mappedParameters[impactLabel]}" var="parameter" varStatus="status">
								<c:if test="${parameter.level>0}">
									<tr data-trick-class="ImpactParameter" data-trick-id="${parameter.id}">
										<td data-trick-field="level" class="textaligncenter"><spring:message text="${parameter.level}" /></td>
										<td data-trick-field="label" data-trick-field-type="string" class="editable textaligncenter"
											data-trick-callback="reloadSection('section_parameter_impact_probability');reloadRiskHeatMapSection();" onclick="return editField(this);"><spring:message
												text="${parameter.label}" /></td>
									</tr>
								</c:if>
							</c:forEach>
						</tbody>
					</table>
				</fieldset>
			</div>
			<div class="col-md-6">
				<jsp:include page="risk-acceptance.jsp" />
			</div>
		</c:if>
	</div>
</div>