<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fct" uri="https://trickservice.com/tags/functions"%>
<fmt:setLocale value="fr" scope="session" />
<div class="tab-pane" id="tab-risk-register">
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
					<th style="width: 1.8%" rowspan="2"><a href="#" onclick="return sortTable('index',this,true)" data-order='0'><spring:message code="label.row.index" /></a></th>
					<th style="width: 4.5%" rowspan="2" title='<spring:message code="label.title.id" />'><a href="#" onclick="return sortTable('.identifier',this)" data-order='1'><spring:message
								code="label.risk_register.id" /></a></th>
					<th style="width: 5%" rowspan="2" title='<spring:message code="label.risk_register.category" />'><a href="#" onclick="return sortTable('category',this)" data-order='1'><spring:message
								code="label.risk_register.category" /></a></th>
					<th rowspan="2" title='<spring:message code="label.risk_register.risk_title" />'><a href="#" onclick="return sortTable('scenario',this)" data-order='1'><spring:message
								code="label.risk_register.risk_title" /></a></th>
					<th style="width: 8%" rowspan="2" title='<spring:message code="label.risk_register.asset" />'><a href="#" onclick="return sortTable('asset',this)" data-order='1'><spring:message
								code="label.risk_register.asset" /></a></th>
					<c:if test="${showRawColumn}">
						<th colspan="3" class="text-center" title='<spring:message code="label.title.risk_register.raw_eval" />'><spring:message code="label.risk_register.raw_eval" /></th>
					</c:if>
					<th colspan="3" class="text-center" title='<spring:message code="label.title.risk_register.net_eval" />'><spring:message code="label.risk_register.net_eval" /></th>
					<th colspan="3" class="text-center" title='<spring:message code="label.title.risk_register.exp_eval" />'><spring:message code="label.risk_register.exp_eval" /></th>
					<th rowspan="2" style="width: 5%" title='<spring:message code="label.risk_register.strategy" />'><a href="#" onclick="return sortTable('.riskStrategy',this)"
						data-order='1'><spring:message code="label.risk_register.strategy" /></a></th>
					<th style="width: 5%" rowspan="2" title='<spring:message code="label.risk_register.owner" />'><a href="#" onclick="return sortTable('owner',this)" data-order='1'><spring:message
								code="label.risk_register.owner" /></a></th>
				</tr>
				<tr>
					<c:if test="${showRawColumn}">
						<th class="text-center" title='<spring:message code="label.risk_register.probability" />'><a href="#" onclick="return sortTable('raw.probability',this,true)"
							data-order='1'><spring:message code="label.risk_register.acro.probability" /></a></th>
						<th class="text-center" title='<spring:message code="label.risk_register.impact"  />'><a href="#" onclick="return sortTable('raw.impact',this,true)" data-order='1'><spring:message
									code="label.risk_register.acro.impact" /></a></th>
						<th class="text-center" title='<spring:message code="label.risk_register.importance" />'><a href="#" onclick="return sortTable('raw.importance',this,true)"
							data-order='1'><spring:message code="label.risk_register.acro.importance" /></a></th>
					</c:if>
					<th class="text-center" title='<spring:message code="label.risk_register.probability"  />'><a href="#" onclick="return sortTable('net.probability',this,true)"
						data-order='1'><spring:message code="label.risk_register.acro.probability" /></a></th>
					<th class="text-center" title='<spring:message code="label.risk_register.impact" />'><a href="#" onclick="return sortTable('net.impact',this,true)" data-order='1'><spring:message
								code="label.risk_register.acro.impact" /></a></th>
					<th class="text-center" title='<spring:message code="label.risk_register.importance" />'><a href="#" onclick="return sortTable('net.importance',this,true)" data-order='1'><spring:message
								code="label.risk_register.acro.importance" /></a></th>
					<th class="text-center" title='<spring:message code="label.risk_register.probability" />'><a href="#" onclick="return sortTable('exp.probability',this,true)"
						data-order='1'><spring:message code="label.risk_register.acro.probability" /></a></th>
					<th class="text-center" title='<spring:message code="label.risk_register.impact"/>'><a href="#" onclick="return sortTable('exp.impact',this,true)" data-order='1'><spring:message
								code="label.risk_register.acro.impact" /></a></th>
					<th class="text-center" title='<spring:message code="label.risk_register.importance" />'><a href="#" onclick="return sortTable('exp.importance',this,true)" data-order='1'><spring:message
								code="label.risk_register.acro.importance" /></a></th>
				</tr>
			</thead>
			<tbody>
				<spring:message code="label.metric.euro_by_year" var="euro_by_year" />
				<spring:message code="label.assessment.likelihood.unit" var="by_year" />
				<c:forEach items="${estimations}" var="estimation" varStatus="status">
					<c:set var="riskProfile" value="${estimation.riskProfile}" />
					<tr data-trick-id='${riskProfile.id}' data-trick-callback='riskEstimationUpdate(true)' >
						<td data-trick-field="index"><spring:message text="${status.index+1}" /></td>
						<td class="editable" data-trick-id="${riskProfile.id}" data-trick-field=".identifier" onclick="return editField(this);" data-trick-class="RiskProfile"
							data-trick-field-type="string"><spring:message text="${riskProfile.identifier}" /></td>
						<td data-trick-field="category"><spring:message code="label.scenario.type.${fn:toLowerCase(fn:replace(riskProfile.scenario.type.name,'-','_'))}" /></td>
						<td data-trick-field="scenario"><spring:message text="${riskProfile.scenario.name}" /></td>
						<td data-trick-field="asset"><spring:message text="${riskProfile.asset.name}" /></td>
						<c:if test="${showRawColumn}">
							<td data-trick-field="raw.probability" class="text-center"><fmt:formatNumber value="${estimation.rawProbaImpact.probabilityLevel}" maxFractionDigits="0" /></td>
							<td data-trick-field="raw.impact" class="text-center"><fmt:formatNumber value="${estimation.rawProbaImpact.impactLevel}" maxFractionDigits="0" /></td>
							<td data-trick-field="raw.importance" class="text-center"><span
								style="color: ${colorManager.getColor(estimation.rawProbaImpact.importance)};"><i class="fa fa-flag" aria-hidden="true"></i></span> <fmt:formatNumber
										value="${estimation.rawProbaImpact.importance}" maxFractionDigits="0" /></td>
						</c:if>

						<td data-trick-field="net.probability" class="text-center"><fmt:formatNumber value="${estimation.netEvaluation.probabilityLevel}" maxFractionDigits="0" /></td>
						<td data-trick-field="net.impact" class="text-center"><fmt:formatNumber value="${estimation.netEvaluation.impactLevel}" maxFractionDigits="0" /></td>
						<td data-trick-field="net.importance" class="text-center"><span
							style="color: ${colorManager.getColor(estimation.netEvaluation.importance)};"><i class="fa fa-flag" aria-hidden="true"></i></span> <fmt:formatNumber
									value="${estimation.netEvaluation.importance}" maxFractionDigits="0" /></td>

						<td data-trick-field="exp.probability" class="text-center"><fmt:formatNumber value="${estimation.expProbaImpact.probabilityLevel}" maxFractionDigits="0" /></td>
						<td data-trick-field="exp.impact" class="text-center"><fmt:formatNumber value="${estimation.expProbaImpact.impactLevel}" maxFractionDigits="0" /></td>
						<td data-trick-field="exp.importance" class="text-center"><span
							style="color: ${colorManager.getColor(estimation.expProbaImpact.importance)};"><i class="fa fa-flag" aria-hidden="true"></i></span> <fmt:formatNumber
									value="${estimation.expProbaImpact.importance}" maxFractionDigits="0" /></td>

						<td class="editable" data-trick-id="${riskProfile.id}" data-trick-field=".riskStrategy" onclick="return editField(this);" data-trick-class="RiskProfile"
							data-trick-choose="ACCEPT,REDUCE,TRANSFER,AVOID" data-trick-choose-translate="${accept},${reduce},${transfer},${avoid}" data-trick-field-type="string"><spring:message
								code="label.risk_register.strategy.${riskProfile.riskStrategy.nameToLower}" text="${accept}" /></td>
						<td class="editable" data-trick-id="${estimation.assessmentId}" data-trick-field="owner" onclick="return editField(this);" data-trick-class="Assessment"
							data-trick-field-type="string"><spring:message text="${estimation.owner}" /></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>