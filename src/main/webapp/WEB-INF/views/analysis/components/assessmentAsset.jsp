<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<div class="section" id="section_asset_assessment"
	trick-name="<spring:message code="label.assessment.for.asset" text="Assessment for ${asset.name}" arguments="${asset.name}" htmlEscape="true" />">
	<div class="panel panel-default">
		<div class="panel-body" style="max-height: 700px; overflow: auto;">
			<table class="table table-hover">
				<thead>
					<tr>
						<th colspan="3"><spring:message
								code="label.assessment.scenario" text="Scenario"
								htmlEscape="true" /></th>
						<th><spring:message code="label.assessment.impactRep"
								text="Rep." htmlEscape="true" /></th>
						<th><spring:message code="label.assessment.impactOp"
								text="Op." htmlEscape="true" /></th>
						<th><spring:message code="label.assessment.impactLeg"
								text="Leg." htmlEscape="true" /></th>
						<th><spring:message code="label.assessment.impactFin"
								text="Fin." htmlEscape="true" /></th>
						<th><spring:message code="label.assessment.likelihood"
								text="Pro." htmlEscape="true" /></th>
						<th><spring:message code="label.assessment.uncertainty"
								text="Unc." htmlEscape="true" /></th>
						<th><spring:message code="label.assessment.ALEP" text="ALEP"
								htmlEscape="true" /></th>
						<th><spring:message code="label.assessment.ALE" text="ALE"
								htmlEscape="true" /></th>
						<th><spring:message code="label.assessment.ALEO" text="ALEO"
								htmlEscape="true" /></th>
						<th colspan="3"><spring:message
								code="label.assessment.comment" text="Comment" htmlEscape="true" /></th>
						<th colspan="3"><spring:message
								code="label.assessment.hiddenComment" text="Hidden comment"
								htmlEscape="true" /></th>
					</tr>
				</thead>
				<tbody>
					<c:set var="prevAsset" value="null" />
					<spring:eval
						expression="T(lu.itrust.business.component.AssessmentManager).Sort(assessments)"
						var="sortedAssessments" />
					<c:forEach items="${sortedAssessments}" var="assessment">
						<tr trick-class="Assessment" trick-id="${assessment.id}" trick-callback="chartALE()">
							<td colspan="3">${assessment.scenario.name}</td>
							<td trick-field="impactRep" trick-field-type="string" class="success"
								ondblclick="return editField(this);"><spring:message
									text="${assessment.impactRep}" htmlEscape="true" /></td>
							<td trick-field="impactOp" trick-field-type="string" class="success"
								ondblclick="return editField(this);"><spring:message
									text="${assessment.impactOp}" htmlEscape="true" /></td>
							<td trick-field="impactLeg" trick-field-type="string" class="success"
								ondblclick="return editField(this);"><spring:message
									text="${assessment.impactLeg}" htmlEscape="true" /></td>
							<td trick-field="impactFin" trick-field-type="string" class="success"
								ondblclick="return editField(this);"><spring:message
									text="${assessment.impactFin}" htmlEscape="true" /></td>
							<td trick-field="likelihood" trick-field-type="string" class="success"
								ondblclick="return editField(this);"><spring:message
									text="${assessment.likelihood}" htmlEscape="true" /></td>
							<td trick-field="uncertainty" trick-field-type="double" class="success"
								trick-real-value="${assessment.uncertainty}"
								ondblclick="return editField(this);"><fmt:formatNumber
									value="${assessment.uncertainty}" maxFractionDigits="3"
									minFractionDigits="1" /></td>
							<td title="${assessment.ALEP}"><fmt:formatNumber value="${assessment.ALEP}" maxFractionDigits="3" minFractionDigits="3" /></td>
							<td title="${assessment.ALE}"><fmt:formatNumber value="${assessment.ALE}" maxFractionDigits="3" minFractionDigits="3" /></td>
							<td title="${assessment.ALEO}"><fmt:formatNumber value="${assessment.ALEO}" maxFractionDigits="3" minFractionDigits="3" /></td>
							<td trick-field="comment" trick-field-type="string" colspan="3"
								ondblclick="return editField(this);">${assessment.comment}</td>
							<td trick-field="hiddenComment" trick-field-type="string"
								colspan="3" ondblclick="return editField(this);">${assessment.hiddenComment}</td>
						</tr>
						<c:set var="prevAsset" value="${assessment.asset}" />
					</c:forEach>
					<tr class="panel-footer" style="font-weight:bold;">
						<td colspan="9"><spring:message
								code="label.assessment.total.ale" text="Total" /></td>
						<td><spring:htmlEscape defaultHtmlEscape="true">
						<fmt:formatNumber value="${alep.value}" maxFractionDigits="3" minFractionDigits="3" />
					</spring:htmlEscape></td>
						<td><spring:htmlEscape defaultHtmlEscape="true">
						<fmt:formatNumber value="${ale.value}" maxFractionDigits="3" minFractionDigits="3" />
					</spring:htmlEscape></td>
						<td><spring:htmlEscape defaultHtmlEscape="true">
						<fmt:formatNumber value="${aleo.value}" maxFractionDigits="3" minFractionDigits="3" />
					</spring:htmlEscape></td>
						<td colspan="6" />
					</tr>
				</tbody>
			</table>
		</div>
	</div>
</div>