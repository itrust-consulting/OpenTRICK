<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="modal fade" id="actionPlanAssets" tabindex="-1" role="dialog" data-aria-labelledby="actionPlanAssets" data-aria-hidden="true" data-backdrop="static">
	<div class="modal-dialog" style="width: 100%;">
		<div class="modal-content" style="padding:0 5px 20px 5px">
			<div class="modal-header" style="padding-bottom: 2px">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<div class="modal-title">
					<h4>
						<fmt:message key="label.title.actionplan.assets" />
					</h4>
					<spring:eval expression="T(lu.itrust.business.TS.model.actionplan.helper.ActionPlanManager).SplitByType(actionplans)" var="actionplansplitted" />
					<ul class="nav nav-pills" id="menu_asset_actionplan">
						<c:forEach items="${actionplansplitted.keySet()}" var="apt" varStatus="status">
							<li ${selectedApt == apt? "class='disabled'" : ""} data-trick-nav-control="${apt}"><a href="#"
								onclick="return navToogled('#section_actionplans,#actionPlanAssets','#menu_actionplan,#tabOption,#menu_asset_actionplan','${apt}',true);"> <fmt:message
										key="label.action_plan_type.${fn:toLowerCase(apt)}" />
							</a></li>
						</c:forEach>
					</ul>
				</div>
			</div>
			<div class="modal-body" style="padding-top: 0;overflow-x: auto;">
				<c:forEach items="${actionplansplitted.keySet()}" var="apt" varStatus="status">
					<div data-trick-nav-content="${apt}" ${selectedApt == apt? "" : "hidden='true'"}>
						<table class="table table-hover table-condensed table-fixed-header-analysis" id="actionplantable_${apt}">
							<thead>
								<tr>
									<th style="width: 1%;"><fmt:message key="label.table.index" /></th>
									<th style="width: 4%;"><fmt:message key="label.measure.norm" /></th>
									<th style="width: 3%;"><fmt:message key="label.reference" /></th>
									<th style="width: 4%;"><fmt:message key="label.action_plan.total_ale" /></th>
									<spring:eval expression="T(lu.itrust.business.TS.model.actionplan.helper.ActionPlanManager).getAssetsByActionPlanType(actionplans)" var="actionplanassets" scope="request" />
									<c:forEach items="${actionplanassets}" var="asset">
										<th><spring:message text="${asset.name}" /></th>
									</c:forEach>
								</tr>
							</thead>
							<tbody>
								<c:if test="${actionplansplitted.get(apt).size()>0}">
									<tr>
										<td colspan="3"><fmt:message key="label.action_plan.current_ale" /></td>
										<fmt:setLocale value="fr" scope="session" />
										<c:set var="totalALE">
											${fct:round(actionplansplitted.get(apt).get(0).totalALE,2) + fct:round(actionplansplitted.get(apt).get(0).deltaALE,2)}
										</c:set>
										<fmt:parseNumber var="computedALE" type="number" value="${totalALE}" />
										<td  ${computedALE == 0? "class='danger'" : "" } title='<fmt:formatNumber value="${computedALE}" maxFractionDigits="2" /> &euro;'><fmt:formatNumber
												value="${fct:round(computedALE*0.001,0)}" maxFractionDigits="0" /></td>
										<c:forEach items="${actionplanassets}" var="asset">
											<c:choose>
												<c:when test="${apt == 'APPO'}">
													<td title='<fmt:formatNumber value="${asset.ALEO}" maxFractionDigits="2" /> &euro;'><fmt:formatNumber value="${asset.ALEO*0.001}" maxFractionDigits="2" /></td>
												</c:when>
												<c:when test="${apt == 'APPP'}">
													<td title='<fmt:formatNumber value="${asset.ALEP}" maxFractionDigits="2" /> &euro;'><fmt:formatNumber value="${asset.ALEP*0.001}" maxFractionDigits="0" /></td>
												</c:when>
												<c:otherwise>
													<td title='<fmt:formatNumber value="${asset.ALE}" maxFractionDigits="2" /> &euro;'><fmt:formatNumber value="${asset.ALE*0.001}" maxFractionDigits="0" /></td>
												</c:otherwise>
											</c:choose>
										</c:forEach>
									</tr>
								</c:if>
								<c:forEach items="${actionplansplitted.get(apt)}" var="ape">
									<tr data-trick-class="ActionPlanEntry" data-trick-id="${ape.id}"
										data-trick-callback="reloadMeasureRow('${ape.measure.id}', '<spring:message text="${ape.measure.analysisStandard.standard.label}" />')">
										<td><spring:message text="${ape.order}" /></td>
										<td><spring:message text="${ape.measure.analysisStandard.standard.label}" /></td>
										<td><spring:message text="${ape.measure.measureDescription.reference}" /></td>
										<td ${ape.totalALE == 0? "class='danger'" : "" } title='<fmt:formatNumber value="${ape.totalALE}" maxFractionDigits="2" /> &euro;'><fmt:formatNumber
												value="${fct:round(ape.totalALE*0.001,0)}" maxFractionDigits="0" /></td>
										<spring:eval expression="T(lu.itrust.business.TS.model.actionplan.helper.ActionPlanManager).orderActionPlanAssetsByAssetList(ape, actionplanassets)"
											var="actionPlanAssets" />
										<c:forEach items="${actionPlanAssets}" var="apa">
											<td title='<fmt:formatNumber value="${apa.currentALE}" maxFractionDigits="2" /> &euro;'><fmt:formatNumber value="${fct:round(apa.currentALE*0.001,0)}"
													maxFractionDigits="0" /></td>
										</c:forEach>
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
	</div>
</div>