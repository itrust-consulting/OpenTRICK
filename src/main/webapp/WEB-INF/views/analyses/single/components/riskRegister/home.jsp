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
		<ul class="nav nav-pills bordered-bottom" id="menu_riskRegister">
			<c:if test="${not empty riskregister}">
				<li class="disabled" data-trick-role="menu-risk-register-control-value"><a href="#" onclick="return riskRegisterSwitchData(this.parentNode);"><spring:message
							code="label.risk_register.display.value" /></a></li>
				<li data-trick-role="menu-risk-register-control-level"><a href="#" onclick="return riskRegisterSwitchData(this.parentNode);"><spring:message
							code="label.risk_register.display.level" /></a></li>
			</c:if>
			<li style="display: none;" class="dropdown-header"><spring:message code="label.menu.advanced" /></li>
			<li class="pull-right"><a href="#" onclick="return calculateRiskRegister();"><i class="glyphicon glyphicon-expand"></i> <spring:message code="label.action.compute" /></a></li>
		</ul>
		<c:choose>
			<c:when test="${!empty(riskregister)}">
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
						<spring:eval expression="T(lu.itrust.business.TS.model.cssf.helper.RiskRegisterMapper).Generate(riskregister,valueFactory)" var="mappingRegisterHelpers" />
						<c:forEach items="${riskregister}" var="item" varStatus="status">
							<spring:eval expression="T(lu.itrust.business.TS.model.cssf.RiskProfile).key(item.asset,item.scenario)" var="strategyKey" />
							<spring:eval expression="T(lu.itrust.business.TS.model.assessment.Assessment).key(item.asset,item.scenario)" var="ownerKey" />
							<c:set var="riskProfile" value="${riskProfileMapping[strategyKey]}" />
							<c:set var="riskAssessment" value="${estimationMapping[ownerKey]}" />
							<tr ${empty riskProfile or empty riskAssessment? 'class="warning"':''} data-trick-id='${riskProfile.id}'>
								<td><spring:message text="${status.index+1}" /></td>
								<td class="success" data-trick-id="${riskProfile.id}" data-trick-field=".identifier" onclick="return editField(this);" data-trick-class="RiskProfile"
									data-trick-field-type="string"><spring:message text="${riskProfile.identifier}" /></td>
								<td><spring:message code="label.scenario.type.${fn:toLowerCase(fn:replace(item.scenario.type.name,'-','_'))}" /></td>
								<td><spring:message text="${item.scenario.name}" /></td>
								<td><spring:message text="${item.asset.name}" /></td>
								<spring:message code="label.metric.euro_by_year" var="euro_by_year" />
								<spring:message code="label.assessment.likelihood.unit" var="by_year" />

								<c:set var="registerHelper" value="${mappingRegisterHelpers[item.id]}" />
								<fmt:formatNumber value="${fct:round(item.rawEvaluation.probability,3)}" maxFractionDigits="3" var="probability" />
								<fmt:formatNumber value="${fct:round(item.rawEvaluation.impact*0.001,0)}" maxFractionDigits="0" var="impact" />
								<fmt:formatNumber value="${fct:round(item.rawEvaluation.importance*0.001,0)}" maxFractionDigits="0" var="importance" />

								<td data-scale-value="${probability}" data-scale-level='<fmt:formatNumber value="${registerHelper.rawEvaluation.probability}" maxFractionDigits="0"/>' class="text-center"
									title='<fmt:formatNumber value="${item.rawEvaluation.probability}" maxFractionDigits="2" />${by_year}'>${probability}</td>
								<td data-scale-value="${impact}" data-scale-level='<fmt:formatNumber value="${registerHelper.rawEvaluation.impact}" maxFractionDigits="0"/>' class="text-center"
									title='<fmt:formatNumber value="${item.rawEvaluation.impact}" maxFractionDigits="2" />&euro;'>${impact}</td>
								<td data-scale-value="${importance}" data-scale-level='<fmt:formatNumber value="${registerHelper.rawEvaluation.importance}" maxFractionDigits="0"/>' class="text-center"
									title='<fmt:formatNumber value="${item.rawEvaluation.importance}" maxFractionDigits="2" />${euro_by_year}'>${importance}</td>

								<fmt:formatNumber value="${fct:round(item.netEvaluation.probability,3)}" maxFractionDigits="3" var="probability" />
								<fmt:formatNumber value="${fct:round(item.netEvaluation.impact*0.001,0)}" maxFractionDigits="0" var="impact" />
								<fmt:formatNumber value="${fct:round(item.netEvaluation.importance*0.001,0)}" maxFractionDigits="0" var="importance" />

								<td data-scale-value="${probability}" data-scale-level='<fmt:formatNumber value="${registerHelper.netEvaluation.probability}" maxFractionDigits="0"/>' class="text-center"
									title='<fmt:formatNumber value="${item.netEvaluation.probability}" maxFractionDigits="2" />${by_year}'>${probability}</td>
								<td data-scale-value="${impact}" data-scale-level='<fmt:formatNumber value="${registerHelper.netEvaluation.impact}" maxFractionDigits="0"/>' class="text-center"
									title='<fmt:formatNumber value="${item.netEvaluation.impact}" maxFractionDigits="2" />&euro;'>${impact}</td>
								<td data-scale-value="${importance}" data-scale-level='<fmt:formatNumber value="${registerHelper.netEvaluation.importance}" maxFractionDigits="0"/>' class="text-center"
									title='<fmt:formatNumber value="${item.netEvaluation.importance}" maxFractionDigits="2" />${euro_by_year}'>${importance}</td>

								<fmt:formatNumber value="${fct:round(item.expectedEvaluation.probability,3)}" maxFractionDigits="3" var="probability" />
								<fmt:formatNumber value="${fct:round(item.expectedEvaluation.impact*0.001,0)}" maxFractionDigits="0" var="impact" />
								<fmt:formatNumber value="${fct:round(item.expectedEvaluation.importance*0.001,0)}" maxFractionDigits="0" var="importance" />

								<td data-scale-value="${probability}" data-scale-level='<fmt:formatNumber value="${registerHelper.expectedEvaluation.probability}" maxFractionDigits="0"/>'
									class="text-center" title='<fmt:formatNumber value="${item.expectedEvaluation.probability}" maxFractionDigits="2" />${by_year}'>${probability}</td>
								<td data-scale-value="${impact}" data-scale-level='<fmt:formatNumber value="${registerHelper.expectedEvaluation.impact}" maxFractionDigits="0"/>' class="text-center"
									title='<fmt:formatNumber value="${item.expectedEvaluation.impact}" maxFractionDigits="2" />&euro;'>${impact}</td>
								<td data-scale-value="${importance}" data-scale-level='<fmt:formatNumber value="${registerHelper.expectedEvaluation.importance}" maxFractionDigits="0"/>'
									class="text-center" title='<fmt:formatNumber value="${item.expectedEvaluation.importance}" maxFractionDigits="2" />${euro_by_year}'>${importance}</td>
								<c:choose>
									<c:when test="${empty riskProfile}">
										<td></td>
									</c:when>
									<c:otherwise>
										<td class="success" data-trick-id="${riskProfile.id}" data-trick-field=".riskStrategy" onclick="return editField(this);" data-trick-class="RiskProfile"
											data-trick-choose="ACCEPT,REDUCE,TRANSFER,AVOID" data-trick-choose-translate="${accept},${reduce},${transfer},${avoid}" data-trick-field-type="string"><spring:message
												code="label.risk_register.strategy.${riskProfile.riskStrategy.nameToLower}" text="${accept}" /></td>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${empty riskAssessment}">
										<td></td>
									</c:when>
									<c:otherwise>
										<td class="success" data-trick-id="${riskAssessment.id}" data-trick-field="owner" onclick="return editField(this);" data-trick-class="Assessment"
											data-trick-field-type="string"><spring:message text="${riskAssessment.owner}" /></td>
									</c:otherwise>
								</c:choose>


							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:when>
			<c:otherwise>
				<div style="padding: 20px;">
					<spring:message code="info.risk_register.empty" />
				</div>
			</c:otherwise>
		</c:choose>
	</div>
</div>