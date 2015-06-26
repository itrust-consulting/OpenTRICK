<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="tab-pane" id="tabActionPlan">
	<div class="section" id="section_actionplans">
		<spring:eval expression="T(lu.itrust.business.TS.model.actionplan.helper.ActionPlanManager).SplitByType(actionplans)" var="actionplansplitted" />
		<ul class="nav nav-pills bordered-bottom" id="menu_actionplan">
			<c:forEach items="${actionplansplitted.keySet()}" var="apt" varStatus="status">
				<li ${status.index==0? "class='disabled'" : ""} data-trick-nav-control="${apt}"><a href="#"
					onclick="return navToogled('#section_actionplans','#menu_actionplan,#tabOption','${apt}',true);"> <fmt:message
							key="label.action_plan_type.${fn:toLowerCase(apt)}" />
				</a></li>
			</c:forEach>
			<li class="dropdown-submenu pull-right"><a href="#" class="dropdown-toggle" data-toggle="dropdown"> <fmt:message key="label.menu.advanced" /><span class="caret"></span></a>
				<ul class="dropdown-menu">
					<c:if test="${!actionplansplitted.isEmpty()}">
						<li><a href="#" onclick="return displayActionPlanAssets();"><span class="glyphicon glyphicon-new-window"></span> <fmt:message
									key="label.action_plan_assets.show" />
						</a></li>
					</c:if>
					<li><a href="#" class="text-danger" onclick="return displayActionPlanOptions('${analysis.id}')"><i class="glyphicon glyphicon-expand"></i> <fmt:message key="label.action.compute.action_plan" /></a></li>
				</ul>
			</li>
		</ul>
		<c:forEach items="${actionplansplitted.keySet()}" var="apt" varStatus="status">
			<div data-trick-nav-content="${apt}" ${status.index!=0? "hidden='true'" : "" }>
				<table class="table table-hover table-condensed table-fixed-header-analysis" id="actionplantable_${apt}">
					<thead>
						<tr>
							<th style="width:1%;"><fmt:message key="label.table.index" /></th>
							<th style="width:4%;"><fmt:message key="label.measure.norm" /></th>
							<th style="width:3%;"><fmt:message key="label.measure.reference" /></th>
							<th><fmt:message key="label.action_plan.todo" /></th>
							<th style="width:3%;"><fmt:message key="label.action_plan.total_ale" /></th>
							<th style="width:4%;"><fmt:message key="label.action_plan.delta_ale" /></th>
							<th style="width:3%;"><fmt:message key="label.measure.cost" /></th>
							<th style="width:3%;"><fmt:message key="label.action_plan.roi" /></th>
							<th style="width:3%;"><fmt:message key="label.measure.iw" /></th>
							<th style="width:3%;"><fmt:message key="label.measure.ew" /></th>
							<th style="width:3%;"><fmt:message key="label.measure.inv" /></th>
							<th style="width:3%;"><fmt:message key="label.action_plan.phase" /></th>
						</tr>
					</thead>
					<tbody>
						<c:if test="${actionplansplitted.get(apt).size()>0}">
							<tr>
								<td>&nbsp;</td>
								<td colspan="3"><fmt:message key="label.action_plan.current_ale" /></td>
								<fmt:setLocale value="fr" scope="session" />
								<c:set var="totalALE">
									${fct:round(actionplansplitted.get(apt).get(0).totalALE,2)+ fct:round(actionplansplitted.get(apt).get(0).deltaALE,2)}
								</c:set>
								<fmt:parseNumber var="computedALE" type="number" value="${totalALE}" />
								<td  align="right" ${computedALE == 0? "class='danger'" : "" } title='<fmt:formatNumber value="${computedALE}" maxFractionDigits="2" /> &euro;'>
									<fmt:formatNumber value="${fct:round(computedALE*0.001,0)}" maxFractionDigits="0" />
								</td>
								<td colspan="7">&nbsp;</td>
							</tr>
						</c:if>
						<c:forEach items="${actionplansplitted.get(apt)}" var="ape">
							<tr data-trick-class="ActionPlanEntry" data-trick-id="${ape.id}"
								data-trick-callback="reloadMeasureRow('${ape.measure.id}', '<spring:message text="${ape.measure.analysisStandard.standard.label}" />')">
								<td><spring:message text="${ape.order}" /></td>
								<td><spring:message text="${ape.measure.analysisStandard.standard.label}" /></td>
								<td><spring:message text="${ape.measure.measureDescription.reference}" /></td>
								<td><b><spring:message text="${ape.measure.measureDescription.getMeasureDescriptionTextByAlpha2(language).getDomain()}" /></b> <br /> <spring:message
										text="${ape.measure.getToDo()}" /></td>
								<td  align="right" ${ape.totalALE == 0? "class='danger'" : "" } title='<fmt:formatNumber value="${ape.totalALE}" maxFractionDigits="2" /> &euro;'>
									<fmt:formatNumber value="${fct:round(ape.totalALE*0.001,0)}" maxFractionDigits="0" />
								</td>
								<td  align="right" ${ape.deltaALE == 0? "class='danger'" : "" } title='<fmt:formatNumber value="${ape.deltaALE}" maxFractionDigits="2" /> &euro;'>
									<fmt:formatNumber value="${fct:round(ape.deltaALE*0.001,0)}" maxFractionDigits="0" />
								</td>
								<td  align="right" ${ape.measure.cost == 0? "class='danger'" : "" } title='<fmt:formatNumber value="${ape.measure.cost}" maxFractionDigits="2" /> &euro;'>
									<fmt:formatNumber value="${fct:round(ape.measure.cost*0.001,0)}" maxFractionDigits="0" />
								</td>
								<td  align="right" ${ape.ROI == 0? "class='danger'" : "" } title='<fmt:formatNumber value="${ape.ROI}" maxFractionDigits="2" /> &euro;'>
									<fmt:formatNumber value="${fct:round(ape.ROI*0.001,0)}" maxFractionDigits="0" />
								</td>
								<td  align="right" ${ape.measure.internalWL == 0? "class='danger'" : "" } title="${ape.measure.internalWL}"><fmt:formatNumber value="${ape.measure.internalWL}"
										maxFractionDigits="1" /></td>
								<td  align="right" ${ape.measure.externalWL == 0? "class='danger'" : "" } title="${ape.measure.externalWL}"><fmt:formatNumber value="${ape.measure.externalWL}"
										maxFractionDigits="1" /></td>
								<td  align="right" ${ape.measure.investment == 0? "class='danger'" : "" } title='<fmt:formatNumber value="${ape.measure.investment}" maxFractionDigits="2" /> &euro;'>
									<fmt:formatNumber value="${fct:round(ape.measure.investment*0.001,0)}" maxFractionDigits="0" />
								</td>
								<td  align="right" class="success" data-trick-field="phase" data-trick-field-type="integer" onclick="return editField(this);" data-trick-callback-pre="extractPhase(this)"
									data-real-value='${ape.measure.phase.number}'><c:choose>
										<c:when test="${ape.measure.phase.number == 0}">
											NA
										</c:when>
										<c:otherwise>
											${ape.measure.phase.number}
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
						</c:forEach>
						<fmt:setLocale value="${language}" scope="session" />
					</tbody>
					<tfoot></tfoot>
				</table>
			</div>
		</c:forEach>
	</div>
</div>