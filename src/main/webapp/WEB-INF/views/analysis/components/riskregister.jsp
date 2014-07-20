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
			<spring:message code="label.title.risk_register" text="Risk Register" />
		</h3>
	</div>
	<div class="panel panel-default">
		<div class="panel-heading" style="min-height: 60px;">&nbsp;</div>
		<div class="panel-body autofitpanelbodydefinition">
			<c:if test="${!empty(riskregister)}">
				<table class="table table-hover table-fixed-header">
					<thead>
						<tr>
							<th rowspan="2" colspan="2"><spring:message code="label.risk_register.category" text="Category" /></th>
							<th rowspan="2"><spring:message code="label.risk_register.id" text="ID" /></th>
							<th rowspan="2" colspan="8"><spring:message code="label.risk_register.risk_title" text="Risk Title" /></th>
							<th rowspan="2" colspan="6"><spring:message code="label.risk_register.asset" text="Asset" /></th>
							<th colspan="3"><spring:message code="label.risk_register.raw_eval" text="Raw Eval." /></th>
							<th colspan="3"><spring:message code="label.risk_register.net_eval" text="Net Eval." /></th>
							<th colspan="3"><spring:message code="label.risk_register.exp_eval" text="Exp Eval." /></th>
							<th rowspan="2" colspan="2"><spring:message code="label.risk_register.strategy" text="Response strategy" /></th>
						</tr>
						<tr>
							<th class="text-center" title='<spring:message code="label.risk_register.probability" text="Probability" />'><spring:message code="label.risk_register.acro.probability"
									text="P." /></th>
							<th class="text-center" title='<spring:message code="label.risk_register.impact" text="Impact" />'><spring:message code="label.risk_register.acro.impact" text="I." /></th>
							<th class="text-center" title='<spring:message code="label.risk_register.importance" text="Importance" />'><spring:message code="label.risk_register.importance"
									text="Imp." /></th>
							<th class="text-center" title='<spring:message code="label.risk_register.probability" text="Probability" />'><spring:message code="label.risk_register.acro.probability"
									text="P." /></th>
							<th class="text-center" title='<spring:message code="label.risk_register.impact" text="Impact" />'><spring:message code="label.risk_register.acro.impact" text="I." /></th>
							<th class="text-center" title='<spring:message code="label.risk_register.importance" text="Importance" />'><spring:message code="label.risk_register.importance"
									text="Imp." /></th>
							<th class="text-center" title='<spring:message code="label.risk_register.probability" text="Probability" />'><spring:message code="label.risk_register.acro.probability"
									text="P." /></th>
							<th class="text-center" title='<spring:message code="label.risk_register.impact" text="Impact" />'><spring:message code="label.risk_register.acro.impact" text="I." /></th>
							<th class="text-center" title='<spring:message code="label.risk_register.importance" text="Importance" />'><spring:message code="label.risk_register.importance"
									text="Imp." /></th>
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
								<td colspan="2"><spring:message code="label.risk_register.strategy.${fn:toLowerCase(item.strategy)}" text="${item.strategy}" /></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:if>
		</div>
	</div>
</div>