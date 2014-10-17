<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<span class="anchor" id="anchorRiskRegister"></span>
<div class="section" id="section_riskregister">
	<div class="page-header">
		<h3 id="RiskRegister">
			<fmt:message key="label.title.risk_register" />
		</h3>
	</div>
	<div class="panel panel-default">
		<div class="panel-heading">&nbsp;</div>
		<div class="panel-body autofitpanelbodydefinition">
			<c:if test="${!empty(riskregister)}">
				<table class="table table-hover table-fixed-header">
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
								<td class="text-right" title=<fmt:formatNumber value='${item.rawEvaluation.probability}' />><fmt:formatNumber value="${item.rawEvaluation.probability}"
										maxFractionDigits="2" minFractionDigits="2" /></td>
								<td class="text-right" title=<fmt:formatNumber value="${item.rawEvaluation.impact}" />><fmt:formatNumber value="${item.rawEvaluation.impact*0.001}"
										maxFractionDigits="0" /></td>
								<td class="text-right" title=<fmt:formatNumber value="${item.rawEvaluation.importance}" />><fmt:formatNumber value="${item.rawEvaluation.importance*0.001}"
										maxFractionDigits="0" /></td>
								<td class="text-right" title=<fmt:formatNumber value="${item.netEvaluation.probability}" />><fmt:formatNumber value="${item.netEvaluation.probability}"
										maxFractionDigits="2" minFractionDigits="2" /></td>
								<td class="text-right" title=<fmt:formatNumber value="${item.netEvaluation.impact}" />><fmt:formatNumber value="${item.netEvaluation.impact*0.001}"
										maxFractionDigits="0" /></td>
								<td class="text-right" title=<fmt:formatNumber value="${item.netEvaluation.importance}"/>><fmt:formatNumber value="${item.netEvaluation.importance*0.001}"
										maxFractionDigits="0" /></td>
								<td class="text-right" title=<fmt:formatNumber value="${item.expectedImportance.probability}"/>><fmt:formatNumber value="${item.expectedImportance.probability}"
										maxFractionDigits="2" minFractionDigits="2" /></td>
								<td class="text-right" title=<fmt:formatNumber value="${item.expectedImportance.impact}"/>><fmt:formatNumber value="${item.expectedImportance.impact*0.001}"
										maxFractionDigits="0" /></td>
								<td class="text-right" title=<fmt:formatNumber value="${item.expectedImportance.importance}"/>><fmt:formatNumber value="${item.expectedImportance.importance*0.001}"
										maxFractionDigits="0" /></td>
								<td colspan="2"><fmt:message key="label.risk_register.strategy.${fn:toLowerCase(item.strategy)}" /></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:if>
		</div>
	</div>
</div>