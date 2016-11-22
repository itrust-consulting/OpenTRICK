<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<fmt:setLocale value="fr" scope="session" />
<div class="tab-pane" id="tabRiskRegister">
	<div class="section" id="section_riskregister">
		<div class="page-header tab-content-header">
			<div class="container">
				<div class="row-fluid">
					<h3>
						<spring:message code="label.title.risk_register" />
					</h3>
				</div>
			</div>
		</div>
		<spring:message code="label.risk_register.strategy.accept" var="accept" />
		<spring:message code="label.risk_register.strategy.reduce" var="reduce" />
		<spring:message code="label.risk_register.strategy.transfer" var="transfer" />
		<spring:message code="label.risk_register.strategy.avoid" var="avoid" />
		<table class="table table-hover table-condensed table-fixed-header-analysis">
			<thead>
				<tr>
					<th style="width: 1%" rowspan="2"><spring:message code="label.row.index" /></th>
					<th style="width: 3%" rowspan="2" title='<spring:message code="label.title.id" />'><spring:message code="label.risk_register.id" /></th>
					<th style="width: 5%" rowspan="2" title='<spring:message code="label.risk_register.category" />'><spring:message code="label.risk_register.category" /></th>
					<th rowspan="2" title='<spring:message code="label.risk_register.risk_title" />'><spring:message code="label.risk_register.risk_title" /></th>
					<th style="width: 15%" rowspan="2" title='<spring:message code="label.risk_register.asset" />'><spring:message code="label.risk_register.asset" /></th>
					<th colspan="3" class="text-center" title='<spring:message code="label.title.risk_register.raw_eval" />'><spring:message code="label.risk_register.raw_eval" /></th>
					<th colspan="3" class="text-center" title='<spring:message code="label.title.risk_register.net_eval" />'><spring:message code="label.risk_register.net_eval" /></th>
					<th colspan="3" class="text-center" title='<spring:message code="label.title.risk_register.exp_eval" />'><spring:message code="label.risk_register.exp_eval" /></th>
					<th rowspan="2" style="width: 5%" title='<spring:message code="label.risk_register.strategy" />'><spring:message code="label.risk_register.strategy" /></th>
					<th style="width: 4%" rowspan="2" title='<spring:message code="label.risk_register.owner" />'><spring:message code="label.risk_register.owner" /></th>
				</tr>
				<tr>
					<th class="text-center" title='<spring:message code="label.risk_register.probability" />'><spring:message code="label.risk_register.acro.probability" /></th>
					<th class="text-center" title='<spring:message code="label.risk_register.impact"  />'><spring:message code="label.risk_register.acro.impact" /></th>
					<th class="text-center" title='<spring:message code="label.risk_register.importance" />'><spring:message code="label.risk_register.acro.importance" /></th>
					<th class="text-center" title='<spring:message code="label.risk_register.probability"  />'><spring:message code="label.risk_register.acro.probability" /></th>
					<th class="text-center" title='<spring:message code="label.risk_register.impact" />'><spring:message code="label.risk_register.acro.impact" /></th>
					<th class="text-center" title='<spring:message code="label.risk_register.importance" />'><spring:message code="label.risk_register.acro.importance" /></th>
					<th class="text-center" title='<spring:message code="label.risk_register.probability" />'><spring:message code="label.risk_register.acro.probability" /></th>
					<th class="text-center" title='<spring:message code="label.risk_register.impact"/>'><spring:message code="label.risk_register.acro.impact" /></th>
					<th class="text-center" title='<spring:message code="label.risk_register.importance" />'><spring:message code="label.risk_register.acro.importance" /></th>
				</tr>
			</thead>
			<tbody>
				<spring:message code="label.metric.euro_by_year" var="euro_by_year" />
				<spring:message code="label.assessment.likelihood.unit" var="by_year" />
				<c:forEach items="${estimations}" var="estimation" varStatus="status">
					<c:set var="riskProfile" value="${estimation.riskProfile}" />
					<tr data-trick-id='${riskProfile.id}'>
						<td><spring:message text="${status.index+1}" /></td>
						<td class="success" data-trick-id="${riskProfile.id}" data-trick-field=".identifier" onclick="return editField(this);" data-trick-class="RiskProfile"
							data-trick-field-type="string"><spring:message text="${riskProfile.identifier}" /></td>
						<td><spring:message code="label.scenario.type.${fn:toLowerCase(fn:replace(riskProfile.scenario.type.name,'-','_'))}" /></td>
						<td><spring:message text="${riskProfile.scenario.name}" /></td>
						<td><spring:message text="${riskProfile.asset.name}" /></td>
						<td class="text-center"><fmt:formatNumber value="${estimation.rawProbaImpact.probabilityLevel}" maxFractionDigits="0" /></td>
						<td class="text-center"><fmt:formatNumber value="${estimation.rawProbaImpact.impactLevel}" maxFractionDigits="0" /></td>
						<td class="text-center"><fmt:formatNumber value="${estimation.rawProbaImpact.importance}" maxFractionDigits="0" /></td>

						<td class="text-center"><fmt:formatNumber value="${estimation.netEvaluation.probabilityLevel}" maxFractionDigits="0" /></td>
						<td class="text-center"><fmt:formatNumber value="${estimation.netEvaluation.impactLevel}" maxFractionDigits="0" /></td>
						<td class="text-center"><fmt:formatNumber value="${estimation.netEvaluation.importance}" maxFractionDigits="0" /></td>

						<td class="text-center"><fmt:formatNumber value="${estimation.expProbaImpact.probabilityLevel}" maxFractionDigits="0" /></td>
						<td class="text-center"><fmt:formatNumber value="${estimation.expProbaImpact.impactLevel}" maxFractionDigits="0" /></td>
						<td class="text-center"><fmt:formatNumber value="${estimation.expProbaImpact.importance}" maxFractionDigits="0" /></td>

						<td class="success" data-trick-id="${riskProfile.id}" data-trick-field=".riskStrategy" onclick="return editField(this);" data-trick-class="RiskProfile"
							data-trick-choose="ACCEPT,REDUCE,TRANSFER,AVOID" data-trick-choose-translate="${accept},${reduce},${transfer},${avoid}" data-trick-field-type="string"><spring:message
								code="label.risk_register.strategy.${riskProfile.riskStrategy.nameToLower}" text="${accept}" /></td>
						<td class="success" data-trick-id="${estimation.assessmentId}" data-trick-field="owner" onclick="return editField(this);" data-trick-class="Assessment"
							data-trick-field-type="string"><spring:message text="${estimation.owner}" /></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>