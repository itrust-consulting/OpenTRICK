<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:if test="${empty locale }">
	<spring:eval expression="T(org.springframework.web.servlet.support.RequestContextUtils).getLocale(pageContext.request)" var="locale" scope="request" />
</c:if>
<c:set var="language" value="${locale.language}" scope="request" />
<fmt:setLocale value="fr" scope="session" />
<div class="tab-pane" id="tab-action-plan">
	<div class="section" id="section_actionplans">
		<spring:eval expression="T(lu.itrust.business.TS.model.actionplan.helper.ActionPlanManager).SplitByType(actionplans)" var="actionplansplitted" />
		<div class="page-header tab-content-header">
			<div class="container">
				<div class="row-fluid">
					<h3>
						<spring:message code="label.title.action_plan" />
					</h3>
				</div>
			</div>
		</div>
		<ul class="nav nav-pills bordered-bottom" id="menu_actionplans">
			<c:if test="${not empty actionplansplitted}">
				<c:if test="${actionplansplitted.size()>1}">
					<c:forEach items="${actionplansplitted.keySet()}" var="apt" varStatus="status">
						<li ${status.index==0? "class='disabled'" : ""} data-trick-nav-control="${apt}"><a href="#"
							onclick="return navToogled('#section_actionplans','#menu_actionplans,#tabOption','${apt}',true);"> <spring:message code="label.action_plan_type.${fn:toLowerCase(apt)}" />
						</a></li>
					</c:forEach>
				</c:if>
				<c:if test="${isLinkedToProject}">
					<c:set var="ttSysName" value="${fn:toLowerCase(ticketingName)}"/>
					<c:choose>
						<c:when test="${isEditable}">
							<li class="disabled" data-trick-selectable="multi" data-trick-single-check="isLinkedTicketingSystem('#section_actionplans')"><a href="#"
								onclick="return synchroniseWithTicketingSystem('#section_actionplans')"><spring:message code="label.open.ticket_measure"
										text="Open Measure/Ticket" /></a></li>
							<li class="disabled" data-trick-selectable="multi"><a href="#"
								onclick="return generateTickets('#section_actionplans')"><spring:message code="label.action.create_or_update.tickets" text="Generate/Update Tickets" /></a></li>
							<li class="disabled" data-trick-selectable="multi" data-trick-single-check="isUnLinkedTicketingSystem('#section_actionplans')"><a href="#"
								onclick="return linkToTicketingSystem('#section_actionplans')"><spring:message code="label.link.to.ticketing.system" arguments="${ticketingName}"
										text="Link to ${ticketingName}" /></a></li>
							<li class="disabled" data-trick-selectable="multi" data-trick-single-check="isLinkedTicketingSystem('#section_actionplans')"><a href="#"
								onclick="return unLinkToTicketingSystem('#section_actionplans')"><spring:message code="label.unlink.from.ticketing.system" arguments="${ticketingName}"
										text="Unlink from ${ticketingName}" /></a></li>
						</c:when>
						<c:otherwise>
							<li class="disabled" data-trick-selectable="multi" data-trick-single-check="isLinkedTicketingSystem('#section_actionplans')"><a href="#"
								onclick="return openTicket('#section_actionplans')"><spring:message code="label.open.ticket" text="Open ticket" /></a></li>
						</c:otherwise>
					</c:choose>
				</c:if>
				<c:if test="${isLinkedToProject or  actionplansplitted.size()>1}">
					<li style="display: none;" class="dropdown-header"><spring:message code="label.menu.advanced" /></li>
				</c:if>
				<c:if test="${type.quantitative}">
					<li class="pull-right"><a href="#" onclick="return displayActionPlanAssets();"><span class="glyphicon glyphicon-new-window"></span> <spring:message
								code="label.action_plan_assets.show" /></a></li>
				</c:if>
			</c:if>
			<c:choose>
				<c:when test="${type.quantitative}">
					<li class="pull-right"><a href="#" onclick="return displayActionPlanOptions('${empty analysisId? analysis.id : analysisId}')"><i class="glyphicon glyphicon-expand"></i> <spring:message
						code="label.action.compute" /></a></li>
				</c:when>
				<c:otherwise>
					<li ${isLinkedToProject? 'class="pull-right"' :''}><a href="#" onclick="return calculateAction({'id':'${empty analysisId? analysis.id : analysisId}'})"><i class="glyphicon glyphicon-expand"></i> <spring:message
						code="label.action.compute" /></a></li>
				</c:otherwise>
			</c:choose>
			
		</ul>
		<c:choose>
			<c:when test="${not empty actionplansplitted}">
				<c:forEach items="${actionplansplitted.keySet()}" var="apt" varStatus="status">
					<div data-trick-nav-content="${apt}" ${status.index!=0? "hidden='true'" : "" }>
						<table class="table table-hover table-condensed table-fixed-header-analysis" id="actionplantable_${apt}">
							<thead>
								<tr>
									<c:if test="${isLinkedToProject && apt=='APPN'}">
										<th width="1%"><input type="checkbox" class="checkbox" onchange="return checkControlChange(this,'actionplans')"></th>
									</c:if>
									<th style="width: 1%;"><spring:message code="label.table.index" /></th>
									<th style="width: 5%;" title='<spring:message code="label.measure.norm" />'><spring:message code="label.measure.norm" /></th>
									<th style="width: 5%;" title='<spring:message code="label.reference" />'><spring:message code="label.reference" /></th>
									<th title='<spring:message code="label.measure.todo" />'><spring:message code="label.measure.todo" /></th>
									<c:choose>
										<c:when test="${type.quantitative}">
											<th style="width: 2%;" title='<spring:message code="label.title.ale" />'><spring:message code="label.action_plan.total_ale" /></th>
											<th style="width: 2%;" title='<spring:message code="label.title.delta_ale" />'><spring:message code="label.action_plan.delta_ale" /></th>
											<th style="width: 2%;" title='<spring:message code="label.title.measure.cost" />'><spring:message code="label.measure.cost" /></th>
											<th style="width: 2%;" title='<spring:message code="label.title.action_plan.roi" />'><spring:message code="label.action_plan.roi" /></th>
										</c:when>
										<c:otherwise>
											<th style="width: 2%;" title='<spring:message code="label.title.measure.risk_count" />'><spring:message code="label.measure.risk_count" /></th>
											<th style="width: 2%;" title='<spring:message code="label.title.measure.cost" />'><spring:message code="label.measure.cost" /></th>
										</c:otherwise>
									</c:choose>
									<th style="width: 2%;" title='<spring:message code="label.title.measure.iw" />'><spring:message code="label.measure.iw" /></th>
									<th style="width: 2%;" title='<spring:message code="label.title.measure.ew" />'><spring:message code="label.measure.ew" /></th>
									<th style="width: 2%;" title='<spring:message code="label.title.measure.inv" />'><spring:message code="label.measure.inv" /></th>
									<th style="width: 2%;" title='<spring:message code="label.title.measure.phase" />'><spring:message code="label.measure.phase" /></th>
								</tr>
							</thead>
							<tbody>
								<c:if test="${type.quantitative and actionplansplitted.get(apt).size()>0}">
									<tr>
										<td colspan="${isLinkedToProject && apt=='APPN'?'2':'1'}">&nbsp;</td>
										<td colspan="3"><spring:message code="label.action_plan.current_ale" /></td>
										<c:set var="totalALE">
											${fct:round(actionplansplitted.get(apt).get(0).totalALE,2)+ fct:round(actionplansplitted.get(apt).get(0).deltaALE,2)}
										</c:set>
										<fmt:parseNumber var="computedALE" type="number" value="${totalALE}" />
										<td align="right" ${computedALE == 0? "class='danger'" : "" } title='<fmt:formatNumber value="${computedALE}" maxFractionDigits="2" /> &euro;'><fmt:formatNumber
												value="${fct:round(computedALE*0.001,0)}" maxFractionDigits="0" /></td>
										<td colspan="7">&nbsp;</td>
									</tr>
								</c:if>
								<c:forEach items="${actionplansplitted.get(apt)}" var="ape">
									<tr data-trick-class="ActionPlanEntry" onclick="selectElement(this)" data-trick-id="${ape.id}" data-measure-id='${ape.measure.id}' data-is-linked='${isLinkedToProject and not empty ape.measure.ticket}' data-trick-callback="reloadMeasureRow('${ape.measure.id}','${ape.measure.analysisStandard.standard.id}')">
										<c:if test="${isLinkedToProject && apt=='APPN'}">
											<td><input type="checkbox" ${measure.status=='NA'?'disabled':''} class="checkbox" onchange="return updateMenu(this,'#section_actionplans','#menu_actionplans');"></td>
										</c:if>
										<td><spring:message text="${ape.order}" /></td>
										<td><spring:message text="${ape.measure.analysisStandard.standard.label}" /></td>
										<td>
											<c:choose>
												<c:when test="${isLinkedToProject and not empty ape.measure.ticket}">
													<spring:eval expression="T(lu.itrust.business.TS.model.ticketing.builder.ClientBuilder).TicketLink(ttSysName,ticketingURL,ape.measure.ticket)" var="ticketLink" />
													<a  href="${ticketLink}" target="_titck_ts" class="btn btn-default btn-xs"><spring:message text="${ape.measure.measureDescription.reference}" /></a>
												</c:when>
												<c:otherwise><spring:message text="${ape.measure.measureDescription.reference}" /></c:otherwise>
											</c:choose>
										</td>
										<td><b><spring:message text="${ape.measure.measureDescription.getMeasureDescriptionTextByAlpha2(language).getDomain()}" /></b> <br /> <spring:message
												text="${ape.measure.getToDo()}" /></td>
										<c:choose>
											<c:when test="${type.quantitative}">
												<td align="right" ${ape.totalALE == 0? "class='danger'" : "" } title='<fmt:formatNumber value="${ape.totalALE}" maxFractionDigits="2" /> &euro;'><fmt:formatNumber
														value="${fct:round(ape.totalALE*0.001,0)}" maxFractionDigits="0" /></td>
												<td align="right" ${ape.deltaALE == 0? "class='danger'" : "" } title='<fmt:formatNumber value="${ape.deltaALE}" maxFractionDigits="2" /> &euro;'><fmt:formatNumber
														value="${fct:round(ape.deltaALE*0.001,0)}" maxFractionDigits="0" /></td>
												<td align="right" ${ape.measure.cost == 0? "class='danger'" : "" } title='<fmt:formatNumber value="${ape.measure.cost}" maxFractionDigits="2" /> &euro;'><fmt:formatNumber
														value="${fct:round(ape.measure.cost*0.001,0)}" maxFractionDigits="0" /></td>
												<td align="right" ${ape.ROI == 0? "class='danger'" : "" } title='<fmt:formatNumber value="${ape.ROI}" maxFractionDigits="2" /> &euro;'><fmt:formatNumber
														value="${fct:round(ape.ROI*0.001,0)}" maxFractionDigits="0" /></td>
											</c:when>
											<c:otherwise>
												<td align="right" title='<fmt:formatNumber value="${ape.riskCount}" maxFractionDigits="0" />'><fmt:formatNumber value="${ape.riskCount}" maxFractionDigits="0" /></td>
												<td align="right" ${ape.measure.cost == 0? "class='danger'" : "" } title='<fmt:formatNumber value="${ape.measure.cost}" maxFractionDigits="2" /> &euro;'><fmt:formatNumber
														value="${fct:round(ape.measure.cost*0.001,0)}" maxFractionDigits="0" /></td>
											</c:otherwise>
										</c:choose>

										<td align="right" ${ape.measure.internalWL == 0? "class='danger'" : "" } title="${ape.measure.internalWL}"><fmt:formatNumber value="${ape.measure.internalWL}"
												maxFractionDigits="1" /></td>
										<td align="right" ${ape.measure.externalWL == 0? "class='danger'" : "" } title="${ape.measure.externalWL}"><fmt:formatNumber value="${ape.measure.externalWL}"
												maxFractionDigits="1" /></td>
										<td align="right" ${ape.measure.investment == 0? "class='danger'" : "" } title='<fmt:formatNumber value="${ape.measure.investment}" maxFractionDigits="2" /> &euro;'>
											<fmt:formatNumber value="${fct:round(ape.measure.investment*0.001,0)}" maxFractionDigits="0" />
										</td>
										<td align="right" class="editable" data-trick-field="phase" data-trick-field-type="integer" onclick="return editField(this);" data-trick-callback-pre="extractPhase(this)"
											data-real-value='${ape.measure.phase.number}'><c:choose>
												<c:when test="${ape.measure.phase.number == 0}">
													NA
												</c:when>
												<c:otherwise>
													${ape.measure.phase.number}
												</c:otherwise>
											</c:choose></td>
									</tr>
								</c:forEach>
							</tbody>
							<tfoot></tfoot>
						</table>
					</div>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<div style="padding: 20px;">
					<spring:message code="info.action_plan.empty" />
				</div>
			</c:otherwise>
		</c:choose>
	</div>
</div>