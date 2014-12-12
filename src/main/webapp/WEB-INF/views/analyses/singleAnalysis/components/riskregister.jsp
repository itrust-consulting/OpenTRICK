<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<div class="tab-pane" id="tabRiskRegister">
	<div class="section" id="section_riskregister">
		<div class="panel panel-default">
			<div class="panel-heading"><fmt:message key="label.title.risk_register" /></div>
			<div class="panel-body">
				<c:if test="${!empty(riskregister)}">
					<table class="table table-hover table-fixed-header-analysis">
						<thead>
							<tr>
								<th rowspan="2" colspan="2"><fmt:message key="label.risk_register.category" /></th>
								<th rowspan="2"><fmt:message key="label.risk_register.id" /></th>
								<th rowspan="2" colspan="8"><fmt:message key="label.risk_register.risk_title" /></th>
								<th rowspan="2" colspan="6"><fmt:message key="label.risk_register.asset" /></th>
								<th colspan="3"><fmt:message key="label.risk_register.raw_eval" /></th>
								<th colspan="3"><fmt:message key="label.risk_register.net_eval" /></th>
								<th colspan="3"><fmt:message key="label.risk_register.exp_eval" /></th>
								<th rowspan="2" colspan="2"><fmt:message key="label.risk_register.strategy" /></th>
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
							<c:forEach items="${riskregister}" var="item" varStatus="status">
								<tr>
									<td colspan="2"><spring:message text="${item.scenario.scenarioType.name}" /></td>
									<td><spring:message text="${item.position}" /></td>
									<td colspan="8"><spring:message text="${item.scenario.name}" /></td>
									<td colspan="6"><spring:message text="${item.asset.name}" /></td>
									<fmt:setLocale value="fr" scope="session" />
									<td class="text-right" title='<fmt:formatNumber value="${item.rawEvaluation.probability}" maxFractionDigits="2" />'><fmt:formatNumber
											value="${fct:round(item.rawEvaluation.probability,0)}" maxFractionDigits="0" /></td>
									<td class="text-right" title='<fmt:formatNumber value="${item.rawEvaluation.impact}" maxFractionDigits="2" />'><fmt:formatNumber
											value="${fct:round(item.rawEvaluation.impact*0.001,0)}" maxFractionDigits="0" /></td>
									<td class="text-right" title='<fmt:formatNumber value="${item.rawEvaluation.importance}" maxFractionDigits="2" />'><fmt:formatNumber
											value="${fct:round(item.rawEvaluation.importance*0.001,0)}" maxFractionDigits="0" /></td>
									<td class="text-right" title='<fmt:formatNumber value="${item.netEvaluation.probability}" maxFractionDigits="2" />'><fmt:formatNumber
											value="${fct:round(item.netEvaluation.probability,0)}" maxFractionDigits="0" /></td>
									<td class="text-right" title='<fmt:formatNumber value="${item.netEvaluation.impact}" maxFractionDigits="2" />'><fmt:formatNumber
											value="${fct:round(item.netEvaluation.impact*0.001,0)}" maxFractionDigits="0" /></td>
									<td class="text-right" title='<fmt:formatNumber value="${item.netEvaluation.importance}" maxFractionDigits="2" />'><fmt:formatNumber
											value="${fct:round(item.netEvaluation.importance*0.001,0)}" maxFractionDigits="0" /></td>
									<td class="text-right" title='<fmt:formatNumber value="${item.expectedImportance.probability}" maxFractionDigits="2" />'><fmt:formatNumber
											value="${fct:round(item.expectedImportance.probability,0)}" maxFractionDigits="2" minFractionDigits="2" /></td>
									<td class="text-right" title='<fmt:formatNumber value="${item.expectedImportance.impact}" maxFractionDigits="2" />'><fmt:formatNumber
											value="${fct:round(item.expectedImportance.impact*0.001,0)}" maxFractionDigits="0" /></td>
									<td class="text-right" title='<fmt:formatNumber value="${item.expectedImportance.importance}" maxFractionDigits="2" />'><fmt:formatNumber
											value="${fct:round(item.expectedImportance.importance*0.001,0)}" maxFractionDigits="0" /></td>
									<fmt:setLocale value="${fn:substring(analysis.language.alpha3,0, 2)}" scope="session" />
									<td colspan="2"><fmt:message key="label.risk_register.strategy.${fn:toLowerCase(item.strategy)}" /></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</c:if>
			</div>
		</div>
	</div>
</div>