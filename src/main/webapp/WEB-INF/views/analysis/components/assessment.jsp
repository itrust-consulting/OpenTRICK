<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="section" id="section_assessment">
	<div class="page-header">
		<h3 id="Assessment">
			<spring:message code="label.assessment" text="Assessment" />
		</h3>
	</div>
	<div class="panel panel-default">
		<div class="panel-heading">
			<button type="button" class="btn btn-default" onclick="return generateMissingAssessment();">
				<spring:message code="label.assessment.generate.missing" text="Generate missing" />
			</button>
		</div>
		<div class="panel-body carousel">
			<table class="table table-responsive" trick-table="1">
				<thead>
					<tr>
						<th colspan="3" trick-table-part="0"><spring:message code="label.assessment.scenario" text="Scenario" htmlEscape="true" /></th>
						<th trick-table-part="1"><spring:message code="label.assessment.impactRep" text="Rep." htmlEscape="true" /></th>
						<th><spring:message code="label.assessment.impactOp" text="Op." htmlEscape="true" /></th>
						<th><spring:message code="label.assessment.impactLeg" text="Leg." htmlEscape="true" /></th>
						<th><spring:message code="label.assessment.impactFin" text="Fin." htmlEscape="true" /></th>
						<th><spring:message code="label.assessment.likelihood" text="Pro." htmlEscape="true" /></th>
						<th><spring:message code="label.assessment.uncertainty" text="Unc." htmlEscape="true" /></th>
						<th trick-table-part="2"><spring:message code="label.assessment.ALEP" text="ALEP" htmlEscape="true" /></th>
						<th><spring:message code="label.assessment.ALE" text="ALE" htmlEscape="true" /></th>
						<th><spring:message code="label.assessment.ALEO" text="ALEO" htmlEscape="true" /></th>
						<th colspan="5" trick-table-part="3"><spring:message code="label.assessment.comment" text="Comment" htmlEscape="true" /></th>
						<th colspan="4"><spring:message code="label.assessment.hiddenComment" text="Hidden comment" htmlEscape="true" /></th>
					</tr>
				</thead>
				<tbody>
					<c:set var="prevAsset" value="null" />
					<spring:eval expression="T(lu.itrust.business.component.AssessmentManager).Sort(assessments)" var="sortedAssessments" />
					<c:forEach items="${sortedAssessments}" var="assessment">
						<c:if test="${empty(prevAsset) || assessment.asset !=prevAsset }">
							<tr class="warning">
								<td colspan="12" trick-table-part="0" title="${assessment.asset.name}">${assessment.asset.name}</td>
							</tr>
						</c:if>
						<tr>
							<td colspan="3" trick-table-part="0">${assessment.scenario.name}</td>
							<td trick-table-part="1" ondblclick="return editField(this, 'assessment','${assessment.id}', 'impactRep', 'string');">${assessment.impactRep}</td>
							<td ondblclick="return editField(this, 'assessment','${assessment.id}', 'impactOp', 'string');">${assessment.impactOp}</td>
							<td ondblclick="return editField(this, 'assessment','${assessment.id}', 'impactLeg', 'string');">${assessment.impactLeg}</td>
							<td ondblclick="return editField(this, 'assessment','${assessment.id}', 'impactFin', 'string');">${assessment.impactFin}</td>
							<td ondblclick="return editField(this, 'assessment','${assessment.id}', 'likelihood', 'string');">${assessment.likelihood}</td>
							<td ondblclick="return editField(this, 'assessment','${assessment.id}', 'uncertainty', 'double');" real-value="${assessment.uncertainty}"><fmt:formatNumber
									value="${assessment.uncertainty}" maxFractionDigits="3" minFractionDigits="1" /></td>
							<td trick-table-part="2">${assessment.ALEP}</td>
							<td>${assessment.ALE}</td>
							<td>${assessment.ALEO}</td>
							<td trick-table-part="3" colspan="5" ondblclick="return editField(this, 'assessment','${assessment.id}', 'comment', 'string');">${assessment.comment}</td>
							<td colspan="4" ondblclick="return editField(this, 'assessment','${assessment.id}', 'hiddenComment', 'string');">${assessment.hiddenComment}</td>
						</tr>
						<c:set var="prevAsset" value="${assessment.asset}" />
					</c:forEach>
				</tbody>
			</table>
			<a href="#" class="carousel-control no-background left" control-trick-table="left"> <span class="icon-prev"></span>
			</a> <a href="#" class="carousel-control no-background right" control-trick-table="right"><span class="icon-next"></span></a>
		</div>
	</div>
</div>