<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<span class="anchor" id="anchorActionPlan"></span>
<div class="section" id="section_actionplans">
	<div class="page-header">
		<h3 id="ActionPlan">
			<fmt:message key="label.title.action_plan" />
		</h3>
	</div>
	<spring:eval expression="T(lu.itrust.business.component.ActionPlanManager).SplitByType(actionplans)" var="actionplansplitted" />
	<div class="panel panel-default">
		<div class="panel-heading" style="min-height: 60px;">
			<div class="col-md-10">
				<ul class="nav nav-pills">
					<c:forEach items="${actionplansplitted.keySet()}" var="apt" varStatus="status">
						<li ${status.index==0? "class='disabled'" : ""} trick-nav-control="${apt}">
							<a href="#" onclick="hideActionplanAssets('#section_actionplans', '#menu_actionplan'); return navToogled('section_actionplans','${apt}',true);"> 
								<fmt:message key="label.action_plan_type.${fn:toLowerCase(apt)}" />
							</a>
						</li>
					</c:forEach>
				</ul>
			</div>
			<div class="col-md-2">
				<ul class="nav nav-pills" id="menu_actionplan">
					<c:if test="${!actionplansplitted.isEmpty()}">
						<li class="pull-right">
							<a href="#" onclick="return toggleDisplayActionPlanAssets('#section_actionplans','#menu_actionplan');">
								<span class="glyphicon glyphicon-chevron-down"></span>&nbsp;<spring:message code="label.action_plan_assets.show"  />
							</a>
						</li>
					</c:if>
				</ul>
			</div>
		</div>
		<div class="panel-body autofitpanelbodydefinition">
			<c:forEach items="${actionplansplitted.keySet()}" var="apt" varStatus="status">
				<div trick-nav-data="${apt}" ${status.index!=0? "hidden='true'" : "" }>
					<table class="table table-hover ${status.index>0?'':'table-fixed-header' }" id="actionplantable_${apt}">
						<thead>
							<tr>
								<th><fmt:message key="label.table.index"  /></th>
								<th colspan="2"><fmt:message key="label.measure.norm"  /></th>
								<th colspan="3"><fmt:message key="label.measure.reference"  /></th>
								<th colspan="20"><fmt:message key="label.action_plan.todo"  /></th>
								<th colspan="3"><fmt:message key="label.action_plan.total_ale"  /></th>
								<th colspan="3"><fmt:message key="label.action_plan.delta_ale"  /></th>
								<th colspan="3"><fmt:message key="label.measure.cost"  /></th>
								<th colspan="3"><fmt:message key="label.action_plan.roi"  /></th>
								<th colspan="3"><fmt:message key="label.action_plan.internal_setup"  /></th>
								<th colspan="3"><fmt:message key="label.action_plan.external_setup"  /></th>
								<th colspan="3"><fmt:message key="label.action_plan.investment"  /></th>
								<th colspan="2"><fmt:message key="label.action_plan.phase"  /></th>
								<spring:eval expression="T(lu.itrust.business.component.ActionPlanManager).getAssetsByActionPlanType(actionplans)" var="actionplanassets" scope="request" />
								<c:forEach items="${actionplanassets}" var="asset">
									<th colspan="6" class="actionplanasset actionplanassethidden" >
										<spring:message text="${asset.name}" />
									</th>
								</c:forEach>
							</tr>
						</thead>
						<tbody>
							<c:if test="${actionplansplitted.get(apt).size()>0}">
								<tr>
									<td colspan="6">&nbsp;</td>
									<td colspan="20"><fmt:message key="label.action_plan.current_ale"  /></td>
									<spring:eval expression="${actionplansplitted.get(apt).get(0).totalALE+actionplansplitted.get(apt).get(0).deltaALE}" var="totalALE"></spring:eval>
									<td colspan="23" ${totalALE == 0? "class='danger'" : "" } title="${totalALE}"><fmt:formatNumber value="${totalALE*0.001}" maxFractionDigits="0" /></td>
									<c:forEach items="${actionplanassets}" var="asset">
										<c:choose>
											<c:when test="${apt == 'APPO'}">
												<td colspan="6" class="actionplanasset actionplanassethidden" title="${asset.ALEO}"><fmt:formatNumber value="${asset.ALEO*0.001}" maxFractionDigits="0" /></td>
											</c:when>
											<c:when test="${apt == 'APPP'}">
												<td colspan="6" class="actionplanasset actionplanassethidden" title="${asset.ALEP}"><fmt:formatNumber value="${asset.ALEP*0.001}" maxFractionDigits="0" /></td>
											</c:when>
											<c:otherwise>
												<td colspan="6" class="actionplanasset actionplanassethidden" title="${asset.ALE}"><fmt:formatNumber value="${asset.ALE*0.001}" maxFractionDigits="0" /></td>
											</c:otherwise>
										</c:choose>
									</c:forEach>
								</tr>
							</c:if>
							<c:forEach items="${actionplansplitted.get(apt)}" var="ape">
								<tr trick-class="ActionPlanEntry" trick-id="${ape.id}"
									trick-callback="reloadMeasureRow('${ape.measure.id}', '<spring:message text="${ape.measure.analysisStandard.standard.label}" />')">
									<td><spring:message text="${ape.order}" /></td>
									<td colspan="2"><spring:message text="${ape.measure.analysisStandard.standard.label}" /></td>
									<td colspan="3"><spring:message text="${ape.measure.measureDescription.reference}" /></td>
									<td colspan="20"><b><spring:message text="${ape.measure.measureDescription.getMeasureDescriptionTextByAlpha3(language).getDomain()}" /></b> <br /> <spring:message
											text="${ape.measure.getToDo()}" /></td>
									<td colspan="3" ${ape.totalALE == 0? "class='danger'" : "" } title="${ape.totalALE}"><fmt:formatNumber value="${ape.totalALE*0.001}" maxFractionDigits="0" /></td>
									<td colspan="3" ${ape.deltaALE == 0? "class='danger'" : "" } title="${ape.deltaALE}"><fmt:formatNumber value="${ape.deltaALE*0.001}" maxFractionDigits="0" /></td>
									<td colspan="3" ${ape.measure.cost == 0? "class='danger'" : "" } title="${ape.measure.cost}"><fmt:formatNumber value="${ape.measure.cost*0.001}" maxFractionDigits="0" /></td>
									<td colspan="3" ${ape.ROI == 0? "class='danger'" : "" } title="${ape.ROI}"><fmt:formatNumber value="${ape.ROI*0.001}" maxFractionDigits="0" /></td>
									<td colspan="3" ${ape.measure.internalWL == 0? "class='danger'" : "" } title="${ape.measure.internalWL}"><fmt:formatNumber value="${ape.measure.internalWL}" maxFractionDigits="1" /></td>
									<td colspan="3" ${ape.measure.externalWL == 0? "class='danger'" : "" } title="${ape.measure.externalWL}"><fmt:formatNumber value="${ape.measure.externalWL}" maxFractionDigits="1"/></td>
									<td colspan="3" ${ape.measure.investment == 0? "class='danger'" : "" } title="${ape.measure.investment}"><fmt:formatNumber value="${ape.measure.investment*0.001}"
											maxFractionDigits="0" /></td>
									<td colspan="2" class="success" trick-field="phase" trick-field-type="integer" ondblclick="return editField(this);" trick-callback-pre="extractPhase(this)"
										trick-real-value='${ape.measure.phase.number}'><c:choose>
											<c:when test="${ape.measure.phase.number == 0}">
												NA
											</c:when>
											<c:otherwise>
												${ape.measure.phase.number}
											</c:otherwise>
										</c:choose></td>
									<c:forEach items="${ape.actionPlanAssets}" var="apa">
										<td colspan="6" class="actionplanasset actionplanassethidden" title="${apa.currentALE}"><fmt:formatNumber value="${apa.currentALE*0.001}" maxFractionDigits="0" /></td>
									</c:forEach>
								</tr>
							</c:forEach>
						</tbody>
						<tfoot></tfoot>
					</table>
				</div>
			</c:forEach>
		</div>
	</div>
</div>