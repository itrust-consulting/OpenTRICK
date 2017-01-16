<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<fmt:setLocale value="fr" scope="session" />
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
					<c:if test="${type=='QUANTITATIVE' }">
						<th class="textaligncenter"><spring:message code="label.parameter.simple.max_rrf" /></th>
					</c:if>
					<th class="textaligncenter"><spring:message code="label.parameter.simple.soa" /></th>
					<th class="textaligncenter"><spring:message code="label.parameter.simple.mandatory_phase" /></th>
				</tr>
			</thead>
			<tbody>
				<tr data-trick-class="SimpleParameter" class='success'>
					<c:forEach items="${mappedParameters['SINGLE']}" var="parameter">
						<c:choose>
							<c:when test="${parameter.description=='max_rrf' or parameter.description=='soaThreshold'}">
								<c:if test="${parameter.description!='max_rrf' or type=='QUANTITATIVE' }">
									<td data-trick-id="${parameter.id}" data-trick-min-value='0' data-trick-max-value='100' class="textaligncenter" data-trick-field="value" data-trick-field-type="double"
										onclick="return editField(this);"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" pattern="#" /></td>
								</c:if>
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
<c:if test="${type=='QUALITATIVE'}">
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
									<td data-trick-id="${parameter.id}" data-trick-min-value='0' data-trick-max-value='10' data-trick-step-value='1' class="success textaligncenter" data-trick-field="value"
										data-trick-field-type="double" onclick="return editField(this);"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" pattern="#" /></td>
								</c:when>
								<c:when test="${parameter.description== 'cssfCIASize' or parameter.description== 'cssfDirectSize' or parameter.description== 'cssfIndirectSize'}">
									<fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" pattern="#" var="cssfSize" />
									<td data-trick-id="${parameter.id}" data-trick-choose-translate='${nil},${all},${compliant}' data-trick-min-value='-2' data-trick-step-value='1'
										data-trick-max-value='1000' class="success textaligncenter" data-trick-field="value" data-trick-field-type="double" onclick="return editField(this);"><c:choose>
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

	<div class="col-md-6">
		<fieldset>
			<legend>
				<c:choose>
					<c:when test="${isEditable}">
						<spring:message code="label.title.parameter.risk.acceptance.threshold" />
						<span class="pull-right">
							<button class='btn btn-xs btn-link' onclick="return manageRiskAcceptance()" style="font-size: 15px">
								<i class="fa fa-cog" aria-hidden="true"></i> <spring:message code='label.action.manage' />
							</button>
						</span>
					</c:when>
					<c:otherwise>
						<spring:message code="label.title.parameter.risk.acceptance.threshold" />
					</c:otherwise>
				</c:choose>

			</legend>
			<table class="table table-hover table-condensed">
				<thead>
					<tr>
						<th class="textaligncenter"><spring:message code="label.importance.threshold" /></th>
						<th style="width: 20%" class="textaligncenter"><spring:message code="label.parameter.label" /></th>
						<th style="width: 50%" class="textaligncenter"><spring:message code="label.description" /></th>
						<th class="textaligncenter"><spring:message code="label.color" /></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${mappedParameters['RISK_ACCEPTANCE']}" var="parameter">
						<tr data-trick-class="RiskAcceptanceParameter" data-trick-id="${parameter.id}" ${isEditable? 'ondblclick="return manageRiskAcceptance()"':''}  data-trick-callback='loadRiskHeatMap()'>
							<td class='textaligncenter' data-trick-field="value"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" /></td>
							<td class='textaligncenter success' data-trick-field='label' data-trick-field-type='string' onclick="return editField(this);"><spring:message text="${parameter.label}"/></td>
							<spring:message text="${parameter.color}" var="color" />
							<td class='textaligncenter success' data-trick-field='description' data-trick-content="text" data-trick-field-type='string' onclick="return editField(this);"><spring:message text="${parameter.description}" /></td>
							<td style="background-color: ${color};" data-trick-field='color'></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</fieldset>
	</div>

</c:if>