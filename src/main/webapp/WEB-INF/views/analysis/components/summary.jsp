<%@page import="lu.itrust.business.TS.actionplan.ActionPlanType"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<span class="anchor" id="anchorSummary"></span>
<div class="section" id="section_summary">
	<div class="page-header">
		<h3 id="Summary">
			<fmt:message key="label.title.summary" />
		</h3>
	</div>
	<spring:eval expression="T(lu.itrust.business.component.ActionPlanSummaryManager).getRows(summaries,phases)" var="summariesStages" />
	<div class="panel panel-default">
		<div class="panel-heading" style="min-height: 60px">
			<ul class="nav nav-pills">
				<c:forEach items="${summariesStages.keySet()}" var="actionPlanType" varStatus="status">
					<li ${status.index==0? "class='disabled'" : ""} trick-nav-control="${actionPlanType.name}"><a href="#"
						onclick="return navToogled('section_summary','${actionPlanType.name}', true);"><fmt:message key="label.action_plan_type.${fn:toLowerCase(actionPlanType.name)}" /></a></li>
				</c:forEach>
			</ul>
		</div>
		<div class="panel-body autofitpanelbodydefinition">
			<c:forEach items="${summariesStages.keySet()}" var="actionPlanType" varStatus="status">
				<c:set var="summaryStages" value="${summariesStages.get(actionPlanType)}" />
				<div trick-nav-data="<spring:message text='${actionPlanType.name}' />" ${status.index!=0? "hidden='true'" : "" }>
					<table class="table table-hover ${status.index>0?'':'table-fixed-header' }" id="summarytable_<spring:message text='${actionPlanType.name}' />">
						<thead>
							<tr>
								<th colspan="5"><fmt:message key="label.characteristic" /></th>
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
							<c:set var="totalales" value="${summaryStages.get('label.profitability.ale.until.end')}"></c:set>
							<c:set var="deltaales" value="${summaryStages.get('label.profitability.risk.reduction')}"></c:set>
							<c:set var="rosis" value="${summaryStages.get('label.profitability.rosi')}"></c:set>
							<c:set var="relativerosis" value="${summaryStages.get('label.profitability.rosi.relatif')}"></c:set>
							<c:set var="internalworkloads" value="${summaryStages.get('label.resource.planning.internal.workload')}"></c:set>
							<c:set var="externalworkloads" value="${summaryStages.get('label.resource.planning.external.workload')}"></c:set>
							<c:set var="investments" value="${summaryStages.get('label.resource.planning.investment')}"></c:set>
							<c:set var="internalmaintenances" value="${summaryStages.get('label.resource.planning.internal.maintenance')}"></c:set>
							<c:set var="externalmaintenances" value="${summaryStages.get('label.resource.planning.external.maintenance')}"></c:set>
							<c:set var="recurrentinvestments" value="${summaryStages.get('label.resource.planning.recurrent.investment')}"></c:set>
							<c:set var="recurrentcosts" value="${summaryStages.get('label.resource.planning.recurrent.cost')}"></c:set>
							<c:set var="totalcosts" value="${summaryStages.get('label.resource.planning.total.phase.cost')}"></c:set>
							<tr>
								<td colspan="5"><fmt:message key="label.phase.begin.date" /></td>
								<c:forEach var="i" begin="0" end="${columncount-1}">
									<td class="text-right"><spring:message text="${begindates.get(i)}" /></td>
								</c:forEach>
							</tr>
							<tr>
								<td colspan="5"><fmt:message key="label.phase.end.date" /></td>
								<c:forEach var="i" begin="0" end="${columncount-1}">
									<td class="text-right"><spring:message text="${enddates.get(i)}" /></td>
								</c:forEach>
							</tr>
							<c:forEach var="key" items="${summaryStages.keySet()}">
								<c:if test="${fn:startsWith(key, 'label.characteristic.compliance')}">
									<c:set var="normlabel" value="${fn:substring(key, 31, key.length())}" />
									<tr>
										<td colspan="5"><fmt:message key="label.characteristic.compliance" /> <spring:message text="${normlabel}" /> (%)</td>
										<c:set var="data" value="label.characteristic.compliance${normlabel}" />
										<c:set value="${summaryStages.get(data)}" var="compliances" />
										<c:forEach var="i" begin="0" end="${columncount-1}">
											<fmt:formatNumber value="${compliances.get(i)*100}" maxFractionDigits="0" var="val" />
											<td class="text-right"><spring:message text="${val}" /></td>
										</c:forEach>
									</tr>
								</c:if>
							</c:forEach>
							<tr>
								<td colspan="5"><fmt:message key="label.characteristic.count.measure.phase" /></td>
								<c:forEach var="i" begin="0" end="${columncount-1}">
									<fmt:formatNumber value="${measurecounts.get(i)}" maxFractionDigits="0" var="value" />
									<td class="text-right"><spring:message text="${value}" /></td>
								</c:forEach>
							</tr>
							<tr>
								<td colspan="5"><fmt:message key="label.characteristic.count.measure.implemented" /></td>
								<c:forEach var="i" begin="0" end="${columncount-1}">
									<fmt:formatNumber value="${implementedcounts.get(i)}" maxFractionDigits="0" var="value" />
									<td class="text-right"><spring:message text="${value}" /></td>
								</c:forEach>
							</tr>
							<tr>
								<td style="background-color: #F8F8F8;" colspan="${columncount+5}"><fmt:message key="label.profitability" /></td>
							</tr>
							<tr>
								<td colspan="5"><fmt:message key="label.profitability.ale.until.end" /></td>
								<c:forEach var="i" begin="0" end="${columncount-1}">
									<fmt:formatNumber value="${totalales.get(i)*0.001}" maxFractionDigits="0" var="value" />
									<td class="text-right" title="${totalales.get(i)}"><spring:message text="${value}" /></td>
								</c:forEach>
							</tr>
							<tr>
								<td colspan="5"><fmt:message key="label.profitability.risk.reduction" /></td>
								<c:forEach var="i" begin="0" end="${columncount-1}">
									<fmt:formatNumber value="${deltaales.get(i)*0.001}" maxFractionDigits="0" var="value" />
									<td class="text-right" title="${deltaales.get(i)}"><spring:message text="${value}" /></td>
								</c:forEach>
							</tr>
							<tr>
								<td colspan="5"><fmt:message key="label.profitability.rosi" /></td>
								<c:forEach var="i" begin="0" end="${columncount-1}">
									<fmt:formatNumber value="${rosis.get(i)*0.001}" maxFractionDigits="0" var="value" />
									<td class="text-right" title="${rosis.get(i)}"><spring:message text="${value}" /></td>
								</c:forEach>
							</tr>
							<tr>
								<td colspan="5"><fmt:message key="label.profitability.rosi.relatif" /></td>
								<c:forEach var="i" begin="0" end="${columncount-1}">
									<fmt:formatNumber value="${relativerosis.get(i)}" maxFractionDigits="0" var="value" />
									<td class="text-right" title="${relativerosis.get(i)}"><spring:message text="${value}" /></td>
								</c:forEach>
							</tr>
							<tr>
								<td style="background-color: #F8F8F8;" colspan="${columncount+5}"><fmt:message key="label.resource.planning" /></td>
							</tr>
							<tr>
								<td colspan="5"><fmt:message key="label.resource.planning.internal.workload" /></td>
								<c:forEach var="i" begin="0" end="${columncount-1}">
									<fmt:formatNumber value="${internalworkloads.get(i)}" maxFractionDigits="1" var="value" />
									<td class="text-right" title="${value}"><spring:message text="${value}" /></td>
								</c:forEach>
							</tr>
							<tr>
								<td colspan="5"><fmt:message key="label.resource.planning.external.workload" /></td>
								<c:forEach var="i" begin="0" end="${columncount-1}">
									<fmt:formatNumber value="${externalworkloads.get(i)}" maxFractionDigits="1" var="value" />
									<td class="text-right" title="${value}"><spring:message text="${value}" /></td>
								</c:forEach>
							</tr>
							<tr>
								<td colspan="5"><fmt:message key="label.resource.planning.investment" /></td>
								<c:forEach var="i" begin="0" end="${columncount-1}">
									<fmt:formatNumber value="${investments.get(i)*0.001}" maxFractionDigits="1" var="value" />
									<td class="text-right" title="${investments.get(i)}"><spring:message text="${value}" /></td>
								</c:forEach>
							</tr>
							<tr>
								<td colspan="5"><fmt:message key="label.resource.planning.internal.maintenance" /></td>
								<c:forEach var="i" begin="0" end="${columncount-1}">
									<fmt:formatNumber value="${internalmaintenances.get(i)}" maxFractionDigits="1" var="value" />
									<td class="text-right" title="${internalmaintenances.get(i)}"><spring:message text="${value}" /></td>
								</c:forEach>
							</tr>
							<tr>
								<td colspan="5"><fmt:message key="label.resource.planning.external.maintenance" /></td>
								<c:forEach var="i" begin="0" end="${columncount-1}">
									<fmt:formatNumber value="${externalmaintenances.get(i)}" maxFractionDigits="1" var="value" />
									<td class="text-right" title="${externalmaintenances.get(i)}"><spring:message text="${value}" /></td>
								</c:forEach>
							</tr>
							<tr>
								<td colspan="5"><fmt:message key="label.resource.planning.recurrent.investment" /></td>
								<c:forEach var="i" begin="0" end="${columncount-1}">
									<fmt:formatNumber value="${recurrentinvestments.get(i)*0.001}" maxFractionDigits="1" var="value" />
									<td class="text-right" title="${recurrentinvestments.get(i)}"><spring:message text="${value}" /></td>
								</c:forEach>
							</tr>
							<tr>
								<td colspan="5"><fmt:message key="label.resource.planning.recurrent.cost" /></td>
								<c:forEach var="i" begin="0" end="${columncount-1}">
									<fmt:formatNumber value="${recurrentcosts.get(i)*0.001}" maxFractionDigits="1" var="value" />
									<td class="text-right" title="${recurrentcosts.get(i)}"><spring:message text="${value}" /></td>
								</c:forEach>
							</tr>
							<tr>
								<td colspan="5"><fmt:message key="label.resource.planning.total.phase.cost" /></td>
								<c:forEach var="i" begin="0" end="${columncount-1}">
									<fmt:formatNumber value="${totalcosts.get(i)*0.001}" maxFractionDigits="1" var="value" />
									<td class="text-right" title="${totalcosts.get(i)}"><spring:message text="${value}" /></td>
								</c:forEach>
							</tr>
						</tbody>
						<tfoot>
						</tfoot>
					</table>
				</div>
			</c:forEach>
		</div>
	</div>
</div>