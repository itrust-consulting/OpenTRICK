<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="lu.itrust.business.TS.model.actionplan.ActionPlanType"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<fmt:setLocale value="fr" scope="session" />
<div class="tab-pane" id="tab-summary">
	<div class="section" id="section_summary">
		<spring:eval expression="T(lu.itrust.business.TS.model.actionplan.summary.helper.ActionPlanSummaryManager).getRows(summaries,phases)" var="summariesStages" />
		<div class="page-header tab-content-header">
			<div class="container">
				<div class="row-fluid">
					<h3>
						<spring:message code="label.title.action_plan.summary" />
					</h3>
				</div>
			</div>
		</div>
		<ul id="menu_summary" class="nav nav-pills bordered-bottom">
			<c:choose>
				<c:when test="${type.quantitative }">
					<c:forEach items="${summariesStages.keySet()}" var="actionPlanType" varStatus="status">
						<li ${status.index==0? "class='disabled'" : ""} data-trick-nav-control="${actionPlanType.name}"><a href="#"
							onclick="return navToogled('#section_summary','#menu_summary,#tabOption','${actionPlanType.name}', true);"><spring:message
									code="label.title.plan_type.${fn:toLowerCase(actionPlanType.name)}" /></a></li>
					</c:forEach>
					<li class="pull-right"><a href="#" onclick="return displayActionPlanOptions('${empty analysisId? analysis.id : analysisId}')"><i class="glyphicon glyphicon-expand"></i>
							<spring:message code="label.action.compute" /></a></li>
				</c:when>
				<c:otherwise>
					<li><a href="#" onclick="return calculateAction({'id':'${empty analysisId? analysis.id : analysisId}'})"><i class="glyphicon glyphicon-expand"></i> <spring:message
								code="label.action.compute" /></a></li>
				</c:otherwise>
			</c:choose>
		</ul>
		<c:set var="euroByYear">
			<spring:message code="label.metric.euro_by_year" />
		</c:set>
		<c:forEach items="${summariesStages.keySet()}" var="actionPlanType" varStatus="status">
			<c:set var="summaryStages" value="${summariesStages.get(actionPlanType)}" />
			<div data-trick-nav-content="<spring:message text='${actionPlanType.name}' />" ${status.index!=0? "hidden='true'" : "" }>
				<table class="table table-hover table-condensed table-fixed-header-analysis" id="summarytable_<spring:message text='${actionPlanType.name}' />">
					<thead>
						<tr>
							<th style="width: 30%;"><spring:message code="label.characteristic" /></th>
							<c:set var="stages" value="${summaryStages.get('label.characteristic')}" />
							<c:set var="columncount" value="${stages.size()}" />
							<c:forEach var="i" begin="0" end="${columncount-1}">
								<th class="text-right"><spring:message text="${stages.get(i)}" /></th>
							</c:forEach>
						</tr>
					</thead>
					<tbody>
						<c:set var="begindates" value="${summaryStages.get('label.phase.begin.date')}"></c:set>
						<c:set var="enddates" value="${summaryStages.get('label.phase.end.date')}"></c:set>
						<c:set var="measurecounts" value="${summaryStages.get('label.characteristic.count.measure.phase')}"></c:set>
						<c:set var="implementedcounts" value="${summaryStages.get('label.characteristic.count.measure.implemented')}"></c:set>
						<c:set var="nonCompliantMeasure27001" value="${summaryStages.get('label.characteristic.count.not_compliant_measure_27001')}"></c:set>
						<c:set var="nonCompliantMeasure27002" value="${summaryStages.get('label.characteristic.count.not_compliant_measure_27002')}"></c:set>
						<c:if test="${actionPlanType.name!='APQ'}">
							<c:set var="totalales" value="${summaryStages.get('label.profitability.ale.until.end')}"></c:set>
							<c:set var="deltaales" value="${summaryStages.get('label.profitability.risk.reduction')}"></c:set>
							<c:set var="costOfMeasures" value="${summaryStages.get('label.profitability.average_yearly_cost_of_phase')}"></c:set>
							<c:set var="rosis" value="${summaryStages.get('label.profitability.rosi')}"></c:set>
							<c:set var="relativerosis" value="${summaryStages.get('label.profitability.rosi.relatif')}"></c:set>
						</c:if>
						<c:set var="internalworkloads" value="${summaryStages.get('label.resource.planning.internal.workload')}"></c:set>
						<c:set var="externalworkloads" value="${summaryStages.get('label.resource.planning.external.workload')}"></c:set>
						<c:set var="investments" value="${summaryStages.get('label.resource.planning.investment')}"></c:set>
						<c:set var="internalmaintenances" value="${summaryStages.get('label.resource.planning.internal.maintenance')}"></c:set>
						<c:set var="externalmaintenances" value="${summaryStages.get('label.resource.planning.external.maintenance')}"></c:set>
						<c:set var="recurrentinvestments" value="${summaryStages.get('label.resource.planning.recurrent.investment')}"></c:set>
						<c:set var="implementPhaseCost" value="${summaryStages.get('label.resource.planning.total.implement.phase.cost')}"></c:set>
						<c:set var="recurrentcosts" value="${summaryStages.get('label.resource.planning.total.recurrent.cost')}"></c:set>
						<c:set var="totalcosts" value="${summaryStages.get('label.resource.planning.total.phase.cost')}"></c:set>
						<tr>
							<td><spring:message code="label.phase.begin.date" /></td>
							<c:forEach var="i" begin="0" end="${columncount-1}">
								<td class="text-right"><spring:message text="${begindates.get(i)}" /></td>
							</c:forEach>
						</tr>
						<tr>
							<td><spring:message code="label.phase.end.date" /></td>
							<c:forEach var="i" begin="0" end="${columncount-1}">
								<td class="text-right"><spring:message text="${enddates.get(i)}" /></td>
							</c:forEach>
						</tr>
						<c:forEach var="key" items="${summaryStages.keySet()}">
							<c:if test="${fn:startsWith(key, 'label.characteristic.compliance')}">
								<c:set var="standardLabel" value="${fn:substring(key, 31, key.length())}" />
								<tr>
									<td><spring:message code="label.characteristic.compliance" /> <spring:message text="${standardLabel}" /> (%)</td>
									<c:set var="data" value="label.characteristic.compliance${standardLabel}" />
									<c:set value="${summaryStages.get(data)}" var="compliances" />
									<c:forEach var="i" begin="0" end="${columncount-1}">
										<fmt:formatNumber value="${compliances.get(i)*100}" maxFractionDigits="0" var="val" />
										<td class="text-right"><spring:message text="${val}" /></td>
									</c:forEach>
								</tr>
							</c:if>
						</c:forEach>

						<tr>
							<td><spring:message code="label.characteristic.count.not_compliant_measure_27001" /></td>
							<c:forEach var="i" begin="0" end="${columncount-1}">
								<fmt:formatNumber value="${nonCompliantMeasure27001[i]}" maxFractionDigits="0" var="value" />
								<td class="text-right"><spring:message text="${value}" /></td>
							</c:forEach>
						</tr>


						<tr>
							<td><spring:message code="label.characteristic.count.not_compliant_measure_27002" /></td>
							<c:forEach var="i" begin="0" end="${columncount-1}">
								<fmt:formatNumber value="${nonCompliantMeasure27002[i]}" maxFractionDigits="0" var="value" />
								<td class="text-right"><spring:message text="${value}" /></td>
							</c:forEach>
						</tr>

						<tr>
							<td><spring:message code="label.characteristic.count.measure.phase" /></td>
							<c:forEach var="i" begin="0" end="${columncount-1}">
								<fmt:formatNumber value="${measurecounts.get(i)}" maxFractionDigits="0" var="value" />
								<td class="text-right"><spring:message text="${value}" /></td>
							</c:forEach>
						</tr>
						<tr>
							<td><spring:message code="label.characteristic.count.measure.implemented" /></td>
							<c:forEach var="i" begin="0" end="${columncount-1}">
								<fmt:formatNumber value="${implementedcounts.get(i)}" maxFractionDigits="0" var="value" />
								<td class="text-right"><spring:message text="${value}" /></td>
							</c:forEach>
						</tr>
						<c:set var="currentIndex" value="1" />
						<c:if test="${actionPlanType.name!='APQ'}">
							<tr class="active">
								<td colspan="${columncount+5}">${currentIndex}.<spring:message code="label.profitability" /></td>
							</tr>
							<tr>
								<td style="padding-left: 15px">${currentIndex}.1.<spring:message code="label.profitability.ale.until.end" /></td>
								<c:forEach var="i" begin="0" end="${columncount-1}">
									<fmt:formatNumber value="${fct:round(totalales.get(i)*0.001,0)}" maxFractionDigits="0" var="value" />
									<td class="text-right" title='<fmt:formatNumber value="${totalales.get(i)}" maxFractionDigits="2" /> &euro;'><spring:message text="${value}" /></td>
								</c:forEach>
							</tr>
							<tr>
								<td style="padding-left: 15px">${currentIndex}.2.<spring:message code="label.profitability.risk.reduction" /></td>
								<c:forEach var="i" begin="0" end="${columncount-1}">
									<fmt:formatNumber value="${fct:round(deltaales.get(i)*0.001,0)}" maxFractionDigits="0" var="value" />
									<td class="text-right" title='<fmt:formatNumber value="${deltaales.get(i)}" maxFractionDigits="2" /> ${euroByYear}'><spring:message text="${value}" /></td>
								</c:forEach>
							</tr>
							<tr>
								<td style="padding-left: 15px">${currentIndex}.3.<spring:message code="label.profitability.average_yearly_cost_of_phase" /></td>
								<c:forEach var="i" begin="0" end="${columncount-1}">
									<fmt:formatNumber value="${fct:round(costOfMeasures.get(i)*0.001,0)}" maxFractionDigits="0" var="value" />
									<td class="text-right" title='<fmt:formatNumber value="${costOfMeasures.get(i)}" maxFractionDigits="2" /> ${euroByYear}'><spring:message text="${value}" /></td>
								</c:forEach>
							</tr>
							<tr>
								<td style="padding-left: 15px">${currentIndex}.4.<spring:message code="label.profitability.rosi" /></td>

								<c:forEach var="i" begin="0" end="${columncount-1}">
									<fmt:formatNumber value="${fct:round(rosis.get(i)*0.001,0)}" maxFractionDigits="0" var="value" />
									<td class="text-right" title='<fmt:formatNumber value="${rosis.get(i)}" maxFractionDigits="2" /> ${euroByYear}'><spring:message text="${value}" /></td>
								</c:forEach>
							</tr>
							<tr>
								<td style="padding-left: 15px">${currentIndex}.5.<spring:message code="label.profitability.rosi.relatif" /></td>
								<c:forEach var="i" begin="0" end="${columncount-1}">
									<fmt:formatNumber value="${fct:round(relativerosis.get(i),2)}" var="value" />
									<td class="text-right" title='<fmt:formatNumber value="${relativerosis.get(i)}" />'><spring:message text="${value}" /></td>
								</c:forEach>

							</tr>
							<c:set var="currentIndex" value="${currentIndex+1}" />
						</c:if>
						<tr class="active">
							<td colspan="${columncount+5}">${currentIndex}.<spring:message code="label.resource.planning" /></td>
						</tr>
						<tr class="warning">
							<td style="padding-left: 15px" colspan="${columncount+5}">${currentIndex}.1.<spring:message code="label.resource.implementation.cost" /></td>
						</tr>
						<tr>
							<td style="padding-left: 30px">${currentIndex}.1.1.<spring:message code="label.resource.planning.internal.workload" /></td>

							<c:forEach var="i" begin="0" end="${columncount-1}">
								<fmt:formatNumber value="${internalworkloads.get(i)}" maxFractionDigits="2" var="value" />
								<td class="text-right"><spring:message text="${value}" /></td>
							</c:forEach>
						</tr>
						<tr>
							<td style="padding-left: 30px">${currentIndex}.1.2.<spring:message code="label.resource.planning.external.workload" /></td>

							<c:forEach var="i" begin="0" end="${columncount-1}">
								<fmt:formatNumber value="${externalworkloads.get(i)}" maxFractionDigits="2" var="value" />
								<td class="text-right"><spring:message text="${value}" /></td>
							</c:forEach>

						</tr>
						<tr>
							<td style="padding-left: 30px">${currentIndex}.1.3.<spring:message code="label.resource.planning.investment" /></td>
							<c:forEach var="i" begin="0" end="${columncount-1}">
								<fmt:formatNumber value="${fct:round(investments.get(i)*0.001,0)}" maxFractionDigits="0" var="value" />
								<td class="text-right" title='<fmt:formatNumber value="${investments.get(i)}" maxFractionDigits="2" /> &euro;'><spring:message text="${value}" /></td>
							</c:forEach>
						</tr>
						<tr style="font-weight: bold;">
							<td style="padding-left: 15px"><spring:message code="label.resource.planning.total.implement.phase.cost" /></td>

							<c:forEach var="i" begin="0" end="${columncount-1}">
								<fmt:formatNumber value="${fct:round(implementPhaseCost.get(i)*0.001,0)}" maxFractionDigits="0" var="value" />
								<td class="text-right" title='<fmt:formatNumber value="${implementPhaseCost.get(i)}" maxFractionDigits="2" /> &euro;'><spring:message text="${value}" /></td>
							</c:forEach>

						</tr>
						<tr class="warning">
							<td style="padding-left: 15px" colspan="${columncount+5}">${currentIndex}.2.<spring:message code="label.resource.planning.recurrent.cost" /></td>
						</tr>
						<tr>
							<td style="padding-left: 30px">${currentIndex}.2.1.<spring:message code="label.resource.planning.internal.maintenance" /></td>

							<c:forEach var="i" begin="0" end="${columncount-1}">
								<fmt:formatNumber value="${fct:round(internalmaintenances.get(i),0)}" maxFractionDigits="0" var="value" />
								<td class="text-right" title='<fmt:formatNumber value="${internalmaintenances.get(i)}" maxFractionDigits="2" />'><spring:message text="${value}" /></td>
							</c:forEach>
						</tr>
						<tr>
							<td style="padding-left: 30px">${currentIndex}.2.2.<spring:message code="label.resource.planning.external.maintenance" /></td>

							<c:forEach var="i" begin="0" end="${columncount-1}">
								<fmt:formatNumber value="${fct:round(externalmaintenances.get(i),0)}" maxFractionDigits="0" var="value" />
								<td class="text-right" title='<fmt:formatNumber value="${externalmaintenances.get(i)}" maxFractionDigits="2" />'><spring:message text="${value}" /></td>
							</c:forEach>
						</tr>
						<tr>
							<td style="padding-left: 30px">${currentIndex}.2.3.<spring:message code="label.resource.planning.recurrent.investment" /></td>

							<c:forEach var="i" begin="0" end="${columncount-1}">
								<fmt:formatNumber value="${fct:round(recurrentinvestments.get(i)*0.001,0)}" maxFractionDigits="0" var="value" />
								<td class="text-right" title='<fmt:formatNumber value="${recurrentinvestments.get(i)}" maxFractionDigits="2" /> &euro;'><spring:message text="${value}" /></td>
							</c:forEach>
						</tr>
						<tr style="font-weight: bold;">
							<td style="padding-left: 15px"><spring:message code="label.resource.planning.total.recurrent.cost" /></td>
							<c:forEach var="i" begin="0" end="${columncount-1}">
								<fmt:formatNumber value="${fct:round(recurrentcosts.get(i)*0.001,0)}" maxFractionDigits="0" var="value" />
								<td class="text-right" title='<fmt:formatNumber value="${recurrentcosts.get(i)}" maxFractionDigits="2" /> &euro;'><spring:message text="${value}" /></td>
							</c:forEach>
						</tr>
					</tbody>
					<tfoot style="font-weight: bold; border-top: #dddddd solid;">
						<tr class="active">
							<td><spring:message code="label.resource.planning.total.phase.cost" /></td>

							<c:forEach var="i" begin="0" end="${columncount-1}">
								<fmt:formatNumber value="${fct:round(totalcosts.get(i)*0.001,0)}" maxFractionDigits="0" var="value" />
								<td class="text-right" title='<fmt:formatNumber value="${totalcosts.get(i)}" maxFractionDigits="2" /> &euro;'><spring:message text="${value}" /></td>
							</c:forEach>
						</tr>
					</tfoot>
				</table>
			</div>
		</c:forEach>
	</div>
</div>