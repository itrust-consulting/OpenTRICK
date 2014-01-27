<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="section" id="section_actionplans">
	<div class="page-header">
		<h3 id="ActionPlan">
			<spring:message code="label.actionplans" text="Actionplans" />
		</h3>
	</div>
	<spring:eval expression="T(lu.itrust.business.component.ActionPlanManager).SplitByType(actionplans)" var="actionplansplitted" />
	<c:forEach items="${actionplansplitted.keySet()}" var="apt">
		<div id="section_actionplan_${apt}" class="panel panel-default" onmouseover="if(!$('#menu_actionplan_${apt}').is(':visible')) {$('#menu_actionplan_${apt}').show();}"
			onmouseout="$('#menu_actionplan_${apt}').hide();">
			<div class="panel-heading" style="min-height: 60px">
				<div class="row">
					<div class="col-md-1">
						<div style="display: block; padding: 10px 15px; position: relative;">
							<spring:message code="label.actionplan.${apt}" text="${apt}" />
						</div>
					</div>
					<div class="col-md-11">
						<ul class="nav nav-pills" hidden="true" id="menu_actionplan_${apt}">
							<li>
								<a href="#" onclick="return toggleDisplayAsctionPlanAssets('#actionplantable_${apt}','#menu_actionplan_${apt}');">
									<span class="glyphicon glyphicon-chevron-down"></span>&nbsp;<spring:message	code="action.actionplanassets.show" text="Show Assets" />
								</a>
							</li>
						</ul>
					</div>
				</div>
			</div>
			<div class="panel-body" style="max-height: 700px; overflow: auto;">
				<table class="table table-hover" id="actionplantable_${apt}">
					<thead>
						<tr>
							<th><spring:message code="label.table.index" text="#" /></th>
							<th><spring:message code="label.measure.norm" text="Norm" /></th>
							<th><spring:message code="label.measure.reference" text="Reference" /></th>
							<th><spring:message code="label.actionplan.todo" text="To Do" /></th>
							<th><spring:message code="label.actionplan.totalale" text="ALE" /> (k&euro;)</th>
							<th><spring:message code="label.actionplan.deltaale" text="DeltaALE" /> (k&euro;)</th>
							<th><spring:message code="label.measure.cs" text="Cost" /> (k&euro;)</th>
							<th><spring:message code="label.actionplan.roi" text="ROI" /> (k&euro;)</th>
							<th><spring:message code="label.actionplan.phase" text="Phase" /></th>
							<spring:eval expression="T(lu.itrust.business.component.ActionPlanManager).getAssetsByActionPlanType(actionplans)" var="actionplanassets" scope="request" />
							<c:forEach items="${actionplanassets}" var="asset">
								<th class="actionplanasset actionplanassethidden">${asset.name}</th>
							</c:forEach>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${actionplansplitted.get(apt)}" var="ape">
							<tr trick-class="ActionPlanEntry" trick-id="${ape.id}"
								trick-callback="reloadActionPlanEntryRow('${ape.id}','${apt}', '${ape.measure.id}', '${ape.measure.analysisNorm.norm.label}')">
								<td><spring:message text="${ape.position}" /></td>
								<td><spring:message text="${ape.measure.analysisNorm.norm.label}" /></td>
								<td><spring:message text="${ape.measure.measureDescription.reference}" /></td>
								<td><b><spring:message text="${ape.measure.measureDescription.getMeasureDescriptionTextByAlpha3(language).getDomain()}" /></b> <br /> <spring:message
										text="${ape.measure.getToDo()}" /></td>
								<td ${ape.totalALE == 0? "class='danger'" : "" } title="${ape.totalALE}"><fmt:formatNumber value="${ape.totalALE*0.001}" maxFractionDigits="0" /></td>
								<td ${ape.deltaALE == 0? "class='danger'" : "" } title="${ape.deltaALE}"><fmt:formatNumber value="${ape.deltaALE*0.001}" maxFractionDigits="0" /></td>
								<td ${ape.measure.cost == 0? "class='danger'" : "" } title="${ape.measure.cost}"><fmt:formatNumber value="${ape.measure.cost*0.001}" maxFractionDigits="0" /></td>
								<td ${ape.ROI == 0? "class='danger'" : "" } title="${ape.ROI}"><fmt:formatNumber value="${ape.ROI*0.001}" maxFractionDigits="0" /></td>
								<td class="success" trick-field="phase" trick-field-type="integer" ondblclick="return editField(this);" trick-callback-pre="extractPhase(this)"
									trick-real-value='${ape.measure.phase.number}'><c:choose>
										<c:when test="${ape.measure.phase.number == 0}">
										NA
								</c:when>
										<c:otherwise>
								${ape.measure.phase.number}
								</c:otherwise>
									</c:choose>
								</td>
								<c:forEach items="${ape.actionPlanAssets}" var="apa">
									<td class="actionplanasset actionplanassethidden" title="${apa.currentALE}"><fmt:formatNumber value="${apa.currentALE*0.001}" maxFractionDigits="0" /></td>
								</c:forEach>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</c:forEach>
</div>