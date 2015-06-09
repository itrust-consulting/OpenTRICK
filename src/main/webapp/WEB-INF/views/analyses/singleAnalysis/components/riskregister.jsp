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
		<c:if test="${!empty(riskregister)}">
			<ul class="nav nav-pills bordered-bottom" id="menu_riskRegister">
				<li class="disabled" data-trick-role="menu-risk-register-control-value"><a href="#" onclick="return riskRegisterSwitchData(this.parentNode);"><fmt:message key="label.risk_register.display.value" /></a></li>
				<li data-trick-role="menu-risk-register-control-level"><a href="#" onclick="return riskRegisterSwitchData(this.parentNode);"><fmt:message key="label.risk_register.display.level" /></a></li>
			</ul>
			<table class="table table-hover table-condensed table-fixed-header-analysis">
				<thead>
					<tr>
						<th rowspan="2"><fmt:message key="label.risk_register.category" /></th>
						<th rowspan="2"><fmt:message key="label.risk_register.id" /></th>
						<th rowspan="2"><fmt:message key="label.risk_register.risk_title" /></th>
						<th rowspan="2"><fmt:message key="label.risk_register.asset" /></th>
						<th colspan="3"><fmt:message key="label.risk_register.raw_eval" /></th>
						<th colspan="3"><fmt:message key="label.risk_register.net_eval" /></th>
						<th colspan="3"><fmt:message key="label.risk_register.exp_eval" /></th>
						<th rowspan="2"><fmt:message key="label.risk_register.strategy" /></th>
						<th rowspan="2"><fmt:message key="label.risk_register.owner" /></th>
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
						<tr>
							<td><fmt:message key="label.scenario.type.${fn:toLowerCase(fn:replace(item.scenario.type.name,'-','_'))}" /></td>
							<td><spring:message text="${item.position}" /></td>
							<td><spring:message text="${item.scenario.name}" /></td>
							<td><spring:message text="${item.asset.name}" /></td>
							<fmt:message key="label.metric.keuro_by_year" var="keuro_by_year" />
							<fmt:message key="label.assessment.likelihood.unit" var="by_year" />
							<fmt:setLocale value="fr" scope="session" />
							<c:set var="registerHelper" value="${mappingRegisterHelpers[item.id]}" />
							<fmt:formatNumber value="${fct:round(item.rawEvaluation.probability,3)}" maxFractionDigits="3" var="probability" />
							<fmt:formatNumber value="${fct:round(item.rawEvaluation.impact*0.001,0)}" maxFractionDigits="0" var="impact" />
							<fmt:formatNumber value="${fct:round(item.rawEvaluation.importance*0.001,0)}" maxFractionDigits="0" var="importance" />

							<td data-scale-value="${probability}" data-scale-level='<fmt:formatNumber value="${registerHelper.rawEvaluation.probability}" maxFractionDigits="0"/>' class="text-center"
								title='<fmt:formatNumber value="${item.rawEvaluation.probability}" maxFractionDigits="2" /> ${by_year}'>${probability}</td>
							<td data-scale-value="${impact}" data-scale-level='<fmt:formatNumber value="${registerHelper.rawEvaluation.impact}" maxFractionDigits="0"/>' class="text-center"
								title='<fmt:formatNumber value="${item.rawEvaluation.impact}" maxFractionDigits="2" /> k&euro;'>${impact}</td>
							<td data-scale-value="${importance}" data-scale-level='<fmt:formatNumber value="${registerHelper.rawEvaluation.importance}" maxFractionDigits="0"/>' class="text-center"
								title='<fmt:formatNumber value="${item.rawEvaluation.importance}" maxFractionDigits="2" /> ${keuro_by_year}'>${importance}</td>

							<fmt:formatNumber value="${fct:round(item.netEvaluation.probability,3)}" maxFractionDigits="3" var="probability" />
							<fmt:formatNumber value="${fct:round(item.netEvaluation.impact*0.001,0)}" maxFractionDigits="0" var="impact" />
							<fmt:formatNumber value="${fct:round(item.netEvaluation.importance*0.001,0)}" maxFractionDigits="0" var="importance" />

							<td data-scale-value="${probability}" data-scale-level='<fmt:formatNumber value="${registerHelper.netEvaluation.probability}" maxFractionDigits="0"/>' class="text-center"
								title='<fmt:formatNumber value="${item.netEvaluation.probability}" maxFractionDigits="2" /> ${by_year}'>${probability}</td>
							<td data-scale-value="${impact}" data-scale-level='<fmt:formatNumber value="${registerHelper.netEvaluation.impact}" maxFractionDigits="0"/>' class="text-center"
								title='<fmt:formatNumber value="${item.netEvaluation.impact}" maxFractionDigits="2" /> k&euro;'>${impact}</td>
							<td data-scale-value="${importance}" data-scale-level='<fmt:formatNumber value="${registerHelper.netEvaluation.importance}" maxFractionDigits="0"/>' class="text-center"
								title='<fmt:formatNumber value="${item.netEvaluation.importance}" maxFractionDigits="2" /> ${keuro_by_year}'>${importance}</td>

							<fmt:formatNumber value="${fct:round(item.expectedImportance.probability,3)}" maxFractionDigits="3" var="probability" />
							<fmt:formatNumber value="${fct:round(item.expectedImportance.impact*0.001,0)}" maxFractionDigits="0" var="impact" />
							<fmt:formatNumber value="${fct:round(item.expectedImportance.importance*0.001,0)}" maxFractionDigits="0" var="importance" />

							<td data-scale-value="${probability}" data-scale-level='<fmt:formatNumber value="${registerHelper.expectedImportance.probability}" maxFractionDigits="0"/>'
								class="text-center" title='<fmt:formatNumber value="${item.expectedImportance.probability}" maxFractionDigits="2" /> ${by_year}'>${probability}</td>
							<td data-scale-value="${impact}" data-scale-level='<fmt:formatNumber value="${registerHelper.expectedImportance.impact}" maxFractionDigits="0"/>' class="text-center"
								title='<fmt:formatNumber value="${item.expectedImportance.impact}" maxFractionDigits="2" /> k&euro;'>${impact}</td>
							<td data-scale-value="${importance}" data-scale-level='<fmt:formatNumber value="${registerHelper.expectedImportance.importance}" maxFractionDigits="0"/>' class="text-center"
								title='<fmt:formatNumber value="${item.expectedImportance.importance}" maxFractionDigits="2" /> ${keuro_by_year}'>${importance}</td>

							<fmt:setLocale value="${language}" scope="session" />

							<fmt:message key="label.risk_register.strategy.accept" var="accept" />
							<fmt:message key="label.risk_register.strategy.reduce" var="reduce" />
							<fmt:message key="label.risk_register.strategy.transfer" var="transfer" />
							<fmt:message key="label.risk_register.strategy.avoid" var="avoid" />

							<c:set value="${fn:toLowerCase(item.strategy)}" var="strategy" />

							<c:if test="${strategy=='shrink' }">
								<c:set value="reduce" var="strategy" />
							</c:if>
							<td class="success" data-trick-id="${item.id}" data-trick-field="strategy" onclick="return editField(this);" data-trick-class="RiskRegister"
								data-trick-choose="accept,reduce,transfer,avoid" data-trick-choose-translate="${accept},${reduce},${transfer},${avoid}" data-trick-field-type="string"><fmt:message
									key="label.risk_register.strategy.${strategy}" /></td>
							<td class="success" data-trick-id="${item.id}" data-trick-field="owner" onclick="return editField(this);" data-trick-class="RiskRegister"
								data-trick-field-type="string"><spring:message text="${item.owner}"/> </td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:if>
	</div>
</div>