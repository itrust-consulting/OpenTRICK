<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<span class="anchor" id="anchorActionPlan"></span>
<div class="section" id="section_actionplans">
	<div class="page-header">
		<h3 id="ActionPlan">
			<spring:message code="label.actionplans" text="Actionplans" />
		</h3>
	</div>
	<spring:eval expression="T(lu.itrust.business.component.ActionPlanManager).SplitByType(actionplans)" var="actionplansplitted" />
	<div class="panel panel-default">
		<div class="panel-heading" style="min-height: 60px;">
			<div class="col-md-10">
				<ul class="nav nav-pills">
					<c:forEach items="${actionplansplitted.keySet()}" var="apt" varStatus="status">
						<li ${status.index==0? "class='disabled'" : ""} trick-nav-control="${apt}"><a href="#"
							onclick="hideActionplanAssets('#section_actionplans', '#menu_actionplan');return navToogled('section_actionplans','${apt}');initialiseTableFixedHeaderRows('#actionplantable_${apt}');"> <spring:message
									code="label.actionPlanType.${apt}" text="${apt}" htmlEscape="true" />
						</a></li>
					</c:forEach>
				</ul>
			</div>
			<div class="col-md-2">
				<ul class="nav nav-pills" id="menu_actionplan">
					<c:if test="${!actionplansplitted.isEmpty()}">
						<li><a href="#" onclick="return toggleDisplayActionPlanAssets('#section_actionplans','#menu_actionplan');"> <span class="glyphicon glyphicon-chevron-down"></span>&nbsp;<spring:message
									code="action.actionplanassets.show" text="Show Assets" />
						</a></li>
					</c:if>
				</ul>
			</div>
		</div>
		<div class="panel-body panelbodydefinition">
			<c:forEach items="${actionplansplitted.keySet()}" var="apt" varStatus="status">
				<div trick-nav-data="${apt}" ${status.index!=0? "hidden='true'" : "" }>
					<h4 class="text-center">
						<spring:message code="label.actionPlanType.${apt}" text="${apt}" htmlEscape="true" />
					</h4>
					<table class="fixedheadertable table table-hover" id="actionplantable_${apt}">
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
								<th><spring:message code="label.actionplan.internal_setup" text="Internal Setup" /> (md)</th>
								<th><spring:message code="label.actionplan.external_setup" text="External Setup" /> (md)</th>
								<th><spring:message code="label.actionplan.investment" text="Investment" /> (k&euro;)</th>
								<th><spring:message code="label.actionplan.phase" text="Phase" /></th>
								<spring:eval expression="T(lu.itrust.business.component.ActionPlanManager).getAssetsByActionPlanType(actionplans)" var="actionplanassets" scope="request" />
								<c:forEach items="${actionplanassets}" var="asset">
									<th class="actionplanasset actionplanassethidden">${asset.name}</th>
								</c:forEach>
							</tr>
						</thead>
						<tbody>
							<c:if test="${actionplansplitted.get(apt).size()>0}">
								<tr>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
									<td><spring:message code="label.actionplan.totalALE" text="Current ALE" /></td>
									<spring:eval expression="${actionplansplitted.get(apt).get(0).totalALE+actionplansplitted.get(apt).get(0).deltaALE}" var="totalALE"></spring:eval>
									<td ${totalALE == 0? "class='danger'" : "" } title="${totalALE}"><fmt:formatNumber value="${totalALE*0.001}" maxFractionDigits="0" /></td>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
									<c:forEach items="${actionplanassets}" var="asset">
										<c:choose>
											<c:when test="${apt == 'APPO'}">
												<td class="actionplanasset actionplanassethidden" title="${asset.ALEO}"><fmt:formatNumber value="${asset.ALEO*0.001}" maxFractionDigits="0" /></td>
											</c:when>
											<c:when test="${apt == 'APPP'}">
												<td class="actionplanasset actionplanassethidden" title="${asset.ALEP}"><fmt:formatNumber value="${asset.ALEP*0.001}" maxFractionDigits="0" /></td>
											</c:when>
											<c:otherwise>
												<td class="actionplanasset actionplanassethidden" title="${asset.ALE}"><fmt:formatNumber value="${asset.ALE*0.001}" maxFractionDigits="0" /></td>
											</c:otherwise>
										</c:choose>
									</c:forEach>
								</tr>
							</c:if>
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
									<td ${ape.measure.internalWL == 0? "class='danger'" : "" } title="${ape.measure.internalWL}">${ape.measure.internalWL}</td>
									<td ${ape.measure.externalWL == 0? "class='danger'" : "" } title="${ape.measure.externalWL}">${ape.measure.internalWL}</td>
									<td ${ape.measure.investment == 0? "class='danger'" : "" } title="${ape.measure.investment}"><fmt:formatNumber value="${ape.measure.investment*0.001}"
											maxFractionDigits="0" /></td>
									<td class="success" trick-field="phase" trick-field-type="integer" ondblclick="return editField(this);" trick-callback-pre="extractPhase(this)"
										trick-real-value='${ape.measure.phase.number}'><c:choose>
											<c:when test="${ape.measure.phase.number == 0}">
												NA
											</c:when>
											<c:otherwise>
												${ape.measure.phase.number}
											</c:otherwise>
										</c:choose></td>
									<c:forEach items="${ape.actionPlanAssets}" var="apa">
										<td class="actionplanasset actionplanassethidden" title="${apa.currentALE}"><fmt:formatNumber value="${apa.currentALE*0.001}" maxFractionDigits="0" /></td>
									</c:forEach>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</c:forEach>
		</div>
	</div>
</div>