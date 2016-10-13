<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<fmt:setLocale value="fr" scope="session" />
<spring:message code="label.assessment.for.asset" arguments="${asset.name}" var="title" />
<div class="section" id="section_asset_assessment" data-type='asset' data-trick-id='${asset.id}'
	data-trick-name='${title}'>
	<table class="table table-hover table-condensed table-fixed-header-analysis">
		<thead>
			<tr>
				<th style="width: 10%" title='<spring:message code="label.assessment.scenario" />'><spring:message code="label.assessment.scenario" /></th>
				<c:choose>
					<c:when test="${type=='QUALITATIVE'}">
						<th style="width: 2%" title='<spring:message code="label.title.assessment.impact_rep" />'><spring:message code="label.assessment.impact_rep" /></th>
						<th style="width: 2%" title='<spring:message code="label.title.assessment.impact_op" />'><spring:message code="label.assessment.impact_op" /></th>
						<th style="width: 2%" title='<spring:message code="label.title.assessment.impact_leg" />'><spring:message code="label.assessment.impact_leg" /></th>
						<th style="width: 2%" title='<spring:message code="label.title.assessment.impact_fin" />'><spring:message code="label.assessment.impact_fin" /></th>
						<th style="width: 2%" title='<spring:message code="label.title.likelihood" />'><spring:message code="label.assessment.likelihood" /></th>
					</c:when>
					<c:otherwise>
						<th style="width: 5%" title='<spring:message code="label.title.impact" />'><spring:message code="label.assessment.impact" /></th>
						<th style="width: 5%" title='<spring:message code="label.title.likelihood" />'><spring:message code="label.assessment.likelihood" /></th>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${show_uncertainty}">
						<th style="width: 2%" title='<spring:message code="label.title.uncertainty" />'><spring:message code="label.assessment.uncertainty" /></th>
						<th style="width: 2%" title='<spring:message code="label.title.alep" />'><spring:message code="label.assessment.alep" /></th>
						<th style="width: 2%" title='<spring:message code="label.title.ale" />'><spring:message code="label.assessment.ale" /></th>
						<th style="width: 2%" title='<spring:message code="label.title.aleo" />'><spring:message code="label.assessment.aleo" /></th>
					</c:when>
					<c:otherwise>
						<th style="width: 2%" title='<spring:message code="label.title.ale" />'><spring:message code="label.assessment.ale" /></th>
					</c:otherwise>
				</c:choose>
				<th width="2%"><spring:message code="label.title.owner" text="Owner" /></th>
				<th style="width: 30%" title='<spring:message code="label.assessment.comment" />'><spring:message code="label.assessment.comment" /></th>
				<th style="width: 30%" title='<spring:message code="label.assessment.hidden_comment" />'><spring:message code="label.assessment.hidden_comment" /></th>
			</tr>
		</thead>
		<tbody>
			<c:if test="${not empty asset}">
				<spring:eval expression="T(lu.itrust.business.TS.model.general.helper.AssessmentAndRiskProfileManager).Sort(assessments)" var="sortedAssessments" />
				<c:forEach items="${sortedAssessments}" var="assessment">
					<tr data-trick-class="Assessment" data-trick-id="${assessment.id}" data-trick-callback="chartALE()">
						<td style="height: 32px;"><spring:message text="${assessment.scenario.name}" /></td>
						<c:set var="impactFin" value="${valueFactory.findImpactFin(assessment.impactFin)}" />
						<c:set var="likelihood" value="${valueFactory.findExp(assessment.likelihood)}" />
						<c:choose>
							<c:when test="${type == 'QUALITATIVE'}">
								<c:set var="impactRep" value="${valueFactory.findImpactRep(assessment.impactRep)}" />
								<c:set var="impactOp" value="${valueFactory.findImpactOp(assessment.impactOp)}" />
								<c:set var="impactLeg" value="${valueFactory.findImpactLeg(assessment.impactLeg)}" />
								<c:choose>
									<c:when test="${empty impactRep}">
										<spring:message text="${assessment.impactRep}" var="impactRep" />
										<td data-trick-field="impactRep" data-trick-field-type="string" class="success" title='${impactRep}' onclick="return editField(this);">${impactRep}</td>
									</c:when>
									<c:otherwise>
										<td data-trick-field="impactRep" data-trick-field-type="string" class="success" onclick="return editField(this);"
											title='<fmt:formatNumber value="${fct:round(impactRep.real,0)}" /> &euro;'><spring:message text="${impactRep.variable}" /></td>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${empty impactOp}">
										<spring:message text="${assessment.impactOp}" var="impactOp" />
										<td data-trick-field="impactOp" data-trick-field-type="string" class="success" title='${impactOp}' onclick="return editField(this);">${impactOp}</td>
									</c:when>
									<c:otherwise>
										<td data-trick-field="impactOp" data-trick-field-type="string" class="success" onclick="return editField(this);"
											title='<fmt:formatNumber value="${fct:round(impactOp.real,0)}" /> &euro;'><spring:message text="${impactOp.variable}" /></td>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${empty impactLeg}">
										<spring:message text="${assessment.impactLeg}" var="impactLeg" />
										<td data-trick-field="impactLeg" data-trick-field-type="string" class="success" title='${impactLeg}' onclick="return editField(this);">${impactLeg}</td>
									</c:when>
									<c:otherwise>
										<td data-trick-field="impactLeg" data-trick-field-type="string" class="success" onclick="return editField(this);"
											title='<fmt:formatNumber value="${fct:round(impactLeg.real,0)}" /> &euro;'><spring:message text="${impactLeg.variable}" /></td>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${empty impactFin}">
										<spring:message text="${assessment.impactFin}" var="impactFin" />
										<td data-trick-field="impactFin" data-trick-field-type="string" class="success" title='${impactFin}' onclick="return editField(this);">${impactFin}</td>
									</c:when>
									<c:otherwise>
										<td data-trick-field="impactFin" data-trick-field-type="string" class="success" onclick="return editField(this);"
											title='<fmt:formatNumber value="${fct:round(impactFin.real,0)}" /> &euro;'><spring:message text="${impactFin.variable}" /></td>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${empty likelihood}">
										<spring:message text="${assessment.likelihood}" var="likelihood" />
										<td data-trick-field="likelihood" data-trick-field-type="string" class="success" title='${likelihood}' onclick="return editField(this);">${likelihood}</td>
									</c:when>
									<c:otherwise>
										<td data-trick-field="likelihood" data-trick-field-type="string" class="success" onclick="return editField(this);"
											title='<fmt:formatNumber value="${fct:round(likelihood.real,2)}" /> <spring:message code="label.assessment.likelihood.unit" />'><spring:message
												text="${likelihood.variable}" /></td>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${empty impactFin}">
										<spring:message text="${assessment.impactRep}" var="impactFin" />
										<td data-trick-field="impactFin" data-trick-field-type="string" class="success" title='${impactFin}' onclick="return editField(this);">${impactFin}</td>
									</c:when>
									<c:otherwise>
										<td data-trick-field="impactFin" data-trick-field-type="string" class="success" onclick="return editField(this);"
											title='<fmt:formatNumber value="${fct:round(impactFin.real,0)}" /> &euro;'><fmt:formatNumber value="${fct:round(impactFin.real*0.001,0)}" /></td>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${empty likelihood}">
										<spring:message text="${assessment.likelihood}" var="likelihood" />
										<td data-trick-field="likelihood" data-trick-field-type="string" class="success" title='${likelihood}' onclick="return editField(this);">${likelihood}</td>
									</c:when>
									<c:otherwise>
										<td data-trick-field="likelihood" data-trick-field-type="string" class="success" onclick="return editField(this);"
											title='<spring:message text="${likelihood.variable}" />'><fmt:formatNumber value="${fct:round(likelihood.real,2)}" /></td>
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
						<c:if test="${show_uncertainty}">
							<td data-trick-field="uncertainty" data-trick-field-type="double" class="success" data-trick-min-value="1.0000000000001"
								data-real-value='<fmt:formatNumber value="${assessment.uncertainty}" maxFractionDigits="2" />' onclick="return editField(this);"><fmt:formatNumber
									value="${assessment.uncertainty}" maxFractionDigits="2" /></td>
							<td title="<fmt:formatNumber value="${assessment.ALEO}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(assessment.ALEO*0.001,1)}" /></td>
						</c:if>
						<td title="<fmt:formatNumber value="${assessment.ALE}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(assessment.ALE*0.001,1)}" /></td>
						<c:if test="${show_uncertainty}">
							<td title="<fmt:formatNumber value="${assessment.ALEP}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(assessment.ALEP*0.001,1)}" /></td>
						</c:if>
						<td class="success" onclick="return editField(this);" data-trick-field="owner" data-trick-field-type="string"><spring:message text="${assessment.owner}" /></td>
						<td onclick="return editField(this.firstChild);" class="success"><pre data-trick-field="comment" data-trick-field-type="string" data-trick-content="text"><spring:message text="${assessment.comment}" /></pre></td>
						<td onclick="return editField(this.firstChild);" class="success"><pre data-trick-field="hiddenComment" data-trick-field-type="string" data-trick-content="text"><spring:message text="${assessment.hiddenComment}" /></pre></td>
					</tr>
				</c:forEach>
				<tr class="panel-footer" style="font-weight: bold;">
					<c:choose>
						<c:when test="${show_uncertainty}">
							<c:choose>
								<c:when test="${type == 'QUALITATIVE'}">
									<td colspan="7"><spring:message code="label.total.ale" /></td>
								</c:when>
								<c:otherwise>
									<td colspan="4"><spring:message code="label.total.ale" /></td>
								</c:otherwise>
							</c:choose>
							<td title="<fmt:formatNumber value="${aleo.value}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(aleo.value*0.001,1)}" /></td>
							<td title="<fmt:formatNumber value="${ale.value}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(ale.value*0.001,1)}" /></td>
							<td title="<fmt:formatNumber value="${alep.value}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(alep.value*0.001,1)}" /></td>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${type == 'QUALITATIVE'}">
									<td colspan="6"><spring:message code="label.total.ale" /></td>
								</c:when>
								<c:otherwise>
									<td colspan="3"><spring:message code="label.total.ale" /></td>
								</c:otherwise>
							</c:choose>
							<td title="<fmt:formatNumber value="${ale.value}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(ale.value*0.001,1)}" /></td>
						</c:otherwise>
					</c:choose>
					<td colspan="3">&nbsp;</td>
				</tr>
			</c:if>
		</tbody>
	</table>
</div>
