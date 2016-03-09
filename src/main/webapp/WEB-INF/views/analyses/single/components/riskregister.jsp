<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<div class="tab-pane" id="tabRiskRegister">
	<div class="section" id="section_riskregister">
		<div class="page-header tab-content-header">
			<div class="container">
				<div class="row-fluid">
					<h3>
						<fmt:message key="label.title.risk_register" />
					</h3>
				</div>
			</div>
		</div>
		<ul class="nav nav-pills bordered-bottom" id="menu_riskRegister">
			<c:if test="${not empty riskregister}">
				<li class="disabled" data-trick-role="menu-risk-register-control-value"><a href="#" onclick="return riskRegisterSwitchData(this.parentNode);"><fmt:message
							key="label.risk_register.display.value" /></a></li>
				<li data-trick-role="menu-risk-register-control-level"><a href="#" onclick="return riskRegisterSwitchData(this.parentNode);"><fmt:message
							key="label.risk_register.display.level" /></a></li>
			</c:if>
			<li style="display: none;" class="dropdown-header"><fmt:message key="label.menu.advanced" /></li>
			<li class="pull-right"><a href="#" onclick="return calculateRiskRegister();"><i class="glyphicon glyphicon-expand"></i> <fmt:message key="label.action.compute" /></a></li>
		</ul>
		<c:choose>
			<c:when test="${!empty(riskregister)}">
				<fmt:message key="label.risk_register.strategy.accept" var="accept" />
				<fmt:message key="label.risk_register.strategy.reduce" var="reduce" />
				<fmt:message key="label.risk_register.strategy.transfer" var="transfer" />
				<fmt:message key="label.risk_register.strategy.avoid" var="avoid" />
				<table class="table table-hover table-condensed table-fixed-header-analysis">
					<thead>
						<tr>
							<th style="width: 1%" rowspan="2" title='<fmt:message key="label.title.id" />'><fmt:message key="label.risk_register.id" /></th>
							<th style="width: 5%" rowspan="2" title='<fmt:message key="label.risk_register.category" />'><fmt:message key="label.risk_register.category" /></th>
							<th rowspan="2" title='<fmt:message key="label.risk_register.risk_title" />'><fmt:message key="label.risk_register.risk_title" /></th>
							<th style="width: 15%" rowspan="2" title='<fmt:message key="label.risk_register.asset" />'><fmt:message key="label.risk_register.asset" /></th>
							<th colspan="3" class="text-center" title='<fmt:message key="label.title.risk_register.raw_eval" />'><fmt:message key="label.risk_register.raw_eval" /></th>
							<th colspan="3" class="text-center" title='<fmt:message key="label.title.risk_register.net_eval" />'><fmt:message key="label.risk_register.net_eval" /></th>
							<th colspan="3" class="text-center" title='<fmt:message key="label.title.risk_register.exp_eval" />'><fmt:message key="label.risk_register.exp_eval" /></th>
							<th rowspan="2" style="width: 5%" title='<fmt:message key="label.risk_register.strategy" />' ><fmt:message key="label.risk_register.strategy" /></th>
							<th style="width: 4%" rowspan="2" title='<fmt:message key="label.risk_register.owner" />' ><fmt:message key="label.risk_register.owner" /></th>
						</tr>
						<tr>
							<th class="text-center" title='<fmt:message key="label.risk_register.probability" />'><fmt:message key="label.risk_register.acro.probability" /></th>
							<th class="text-center" title='<fmt:message key="label.risk_register.impact"  />'><fmt:message key="label.risk_register.acro.impact" /></th>
							<th class="text-center" title='<fmt:message key="label.risk_register.importance" />'><fmt:message key="label.risk_register.acro.importance" /></th>
							<th class="text-center" title='<fmt:message key="label.risk_register.probability"  />'><fmt:message key="label.risk_register.acro.probability" /></th>
							<th class="text-center" title='<fmt:message key="label.risk_register.impact" />'><fmt:message key="label.risk_register.acro.impact" /></th>
							<th class="text-center" title='<fmt:message key="label.risk_register.importance" />'><fmt:message key="label.risk_register.acro.importance" /></th>
							<th class="text-center" title='<fmt:message key="label.risk_register.probability" />'><fmt:message key="label.risk_register.acro.probability" /></th>
							<th class="text-center" title='<fmt:message key="label.risk_register.impact"/>'><fmt:message key="label.risk_register.acro.impact" /></th>
							<th class="text-center" title='<fmt:message key="label.risk_register.importance" />'><fmt:message key="label.risk_register.acro.importance" /></th>
						</tr>
					</thead>
					<tbody>
						<spring:eval expression="T(lu.itrust.business.TS.model.cssf.helper.RiskRegisterMapper).Generate(riskregister,parameters)" var="mappingRegisterHelpers" />
						<c:forEach items="${riskregister}" var="item" varStatus="status">
							<spring:eval expression="T(lu.itrust.business.TS.model.cssf.RiskProfile).key(item.asset,item.scenario)" var="strategyKey" />
							<spring:eval expression="T(lu.itrust.business.TS.model.assessment.Assessment).key(item.asset,item.scenario)" var="ownerKey" />
							<c:set var="riskProfile" value="${riskProfileMapping[strategyKey]}" />
							<c:set var="riskAssessment" value="${estimationMapping[ownerKey]}" />
							<tr ${empty riskProfile or empty riskAssessment? 'class="warning"':''} data-trick-id='${riskProfile.id}'>
								<td><spring:message text="${item.position}" /></td>
								<td><fmt:message key="label.scenario.type.${fn:toLowerCase(fn:replace(item.scenario.type.name,'-','_'))}" /></td>
								<td><spring:message text="${item.scenario.name}" /></td>
								<td><spring:message text="${item.asset.name}" /></td>
								<fmt:message key="label.metric.euro_by_year" var="euro_by_year" />
								<fmt:message key="label.assessment.likelihood.unit" var="by_year" />
								<fmt:setLocale value="fr" scope="session" />
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

								<fmt:setLocale value="${language}" scope="session" />
								
								<c:choose>
									<c:when test="${empty riskProfile}">
										<td></td>
									</c:when>
									<c:otherwise>
										<td class="success" data-trick-id="${riskProfile.id}" data-trick-field=".riskStrategy" onclick="return editField(this);" data-trick-class="RiskProfile"
											data-trick-choose="ACCEPT,REDUCE,TRANSFER,AVOID" data-trick-choose-translate="${accept},${reduce},${transfer},${avoid}" data-trick-field-type="string"><fmt:message
												key="label.risk_register.strategy.${riskProfile.riskStrategy.nameToLower}" /></td>
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
					<fmt:message key="info.risk_register.empty" />
				</div>
			</c:otherwise>
		</c:choose>
	</div>
</div>