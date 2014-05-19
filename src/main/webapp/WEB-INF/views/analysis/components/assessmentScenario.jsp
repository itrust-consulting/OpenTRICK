<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="section" id="section_scenario_assessment"
	trick-name="<spring:message code="label.assessment.for.scenario" text="Assessment for ${scenario.name}" arguments="${scenario.name}" />">
	<div class="panel panel-default">
		<div class="panel-body" style="max-height: 700px; overflow: auto;">
			<table class="table table-hover">
				<thead>
					<tr>
						<th colspan="2"><spring:message code="label.assessment.asset" text="Asset" /></th>
						<th><spring:message code="label.assessment.asset.value" text="Asset value" /></th>
						<th><spring:message code="label.assessment.impactRep" text="Rep." /></th>
						<th><spring:message code="label.assessment.impactOp" text="Op." /></th>
						<th><spring:message code="label.assessment.impactLeg" text="Leg." /></th>
						<th><spring:message code="label.assessment.impactFin" text="Fin." /></th>
						<th><spring:message code="label.assessment.likelihood" text="Pro." /></th>
						<th><spring:message code="label.assessment.uncertainty" text="Unc." /></th>
						<th><spring:message code="label.assessment.ALEP" text="ALEP" /> (k&euro;)</th>
						<th><spring:message code="label.assessment.ALE" text="ALE" /> (k&euro;)</th>
						<th><spring:message code="label.assessment.ALEO" text="ALEO" /> (k&euro;)</th>
						<th colspan="3"><spring:message code="label.assessment.comment" text="Comment" /></th>
						<th colspan="3"><spring:message code="label.assessment.hiddenComment" text="Hidden comment" /></th>
					</tr>
				</thead>
				<tbody>
					<c:set var="prevAsset" value="null" />
					<spring:eval expression="T(lu.itrust.business.component.AssessmentManager).Sort(assessments)" var="sortedAssessments" />
					<c:forEach items="${sortedAssessments}" var="assessment">
						<tr trick-class="Assessment" trick-id="${assessment.id}">
							<td colspan="2"><spring:message text="${assessment.asset.name}" /></td>
							<td><fmt:formatNumber value="${assessment.asset.value}" maxFractionDigits="1" minFractionDigits="1" /></td>
							<c:choose>
								<c:when test="${parameters.containsKey(assessment.impactRep)}">
									<td trick-field="impactRep" trick-field-type="string" class="success" title='<fmt:formatNumber value="${parameters.get(assessment.impactRep)*0.001}" />'
										ondblclick="return editField(this);"><spring:message text="${assessment.impactRep}" /></td>
								</c:when>
								<c:otherwise>
									<td trick-field="impactRep" trick-field-type="string" class="success" ondblclick="return editField(this);" real-value="${assessment.impactRep}"><c:catch>
											<fmt:formatNumber value="${assessment.impactRep}" maxFractionDigits="1" minFractionDigits="1" var="impactRep" />
										</c:catch> <c:choose>
											<c:when test="${!empty impactRep}">
												<spring:message text="${impactRep}" />
											</c:when>
											<c:otherwise>
												<spring:message text="${assessment.impactRep}" />
											</c:otherwise>
										</c:choose></td>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${parameters.containsKey(assessment.impactOp)}">
									<td trick-field="impactOp" trick-field-type="string" class="success" title='<fmt:formatNumber value="${parameters.get(assessment.impactOp)*0.001}" />'
										ondblclick="return editField(this);"><spring:message text="${assessment.impactOp}" /></td>
								</c:when>
								<c:otherwise>
									<td trick-field="impactOp" trick-field-type="string" class="success" ondblclick="return editField(this);" real-value="${assessment.impactOp}"><c:catch>
											<fmt:formatNumber value="${assessment.impactOp}" maxFractionDigits="1" minFractionDigits="1" var="impactOp" />
										</c:catch> <c:choose>
											<c:when test="${!empty impactOp}">
												<spring:message text="${impactOp}" />
											</c:when>
											<c:otherwise>
												<spring:message text="${assessment.impactOp}" />
											</c:otherwise>
										</c:choose></td>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${parameters.containsKey(assessment.impactLeg)}">
									<td trick-field="impactLeg" trick-field-type="string" class="success" title='<fmt:formatNumber value="${parameters.get(assessment.impactLeg)*0.001}" />'
										ondblclick="return editField(this);"><spring:message text="${assessment.impactLeg}" /></td>
								</c:when>
								<c:otherwise>
									<td trick-field="impactLeg" trick-field-type="string" class="success" ondblclick="return editField(this);" real-value="${assessment.impactLeg}"><c:catch>
											<fmt:formatNumber value="${assessment.impactLeg}" maxFractionDigits="1" minFractionDigits="1" var="impactLeg" />
										</c:catch> <c:choose>
											<c:when test="${!empty impactLeg}">
												<spring:message text="${impactLeg}" />
											</c:when>
											<c:otherwise>
												<spring:message text="${assessment.impactLeg}" />
											</c:otherwise>
										</c:choose></td>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${parameters.containsKey(assessment.impactFin)}">
									<td trick-field="impactFin" trick-field-type="string" class="success" title='<fmt:formatNumber value="${parameters.get(assessment.impactFin)*0.001}" />'
										ondblclick="return editField(this);"><spring:message text="${assessment.impactFin}" /></td>
								</c:when>
								<c:otherwise>
									<td trick-field="impactFin" trick-field-type="string" class="success" ondblclick="return editField(this);" real-value="${assessment.impactFin}"><c:catch>
											<fmt:formatNumber value="${assessment.impactFin}" var="impactFin" maxFractionDigits="1" minFractionDigits="1" />
										</c:catch> <c:choose>
											<c:when test="${not empty impactFin}">
												<spring:message text="${impactFin}" />
											</c:when>
											<c:otherwise>
												<spring:message text="${assessment.impactFin}" />
											</c:otherwise>
										</c:choose></td>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${parameters.containsKey(assessment.likelihood)}">
									<td trick-field="likelihood" trick-field-type="string" class="success" title='<fmt:formatNumber value="${parameters.get(assessment.likelihood)}" />'
										ondblclick="return editField(this);"><spring:message text="${assessment.likelihood}" /></td>
								</c:when>
								<c:otherwise>
									<td trick-field="likelihood" trick-field-type="string" class="success" ondblclick="return editField(this);" real-value="${assessment.likelihood}"><c:catch>
											<fmt:formatNumber value="${parameters.get(assessment.likelihood)}" var="likelihood" />
										</c:catch> <c:choose>
											<c:when test="${not empty likelihood }">
												<spring:message text="${likelihood}" />
											</c:when>
											<c:otherwise>
												<spring:message text="${assessment.likelihood}" />
											</c:otherwise>
										</c:choose></td>
								</c:otherwise>
							</c:choose>
							<td trick-field="uncertainty" trick-field-type="double" class="success" trick-real-value="${assessment.uncertainty}" ondblclick="return editField(this);"><fmt:formatNumber
									value="${assessment.uncertainty}" maxFractionDigits="3" minFractionDigits="1" /></td>
							<td title="<fmt:formatNumber value="${assessment.ALEO}" maxFractionDigits="3" minFractionDigits="3" />"><fmt:formatNumber value="${assessment.ALEO*0.001}"
									maxFractionDigits="0" minFractionDigits="0" /></td>
							<td title="<fmt:formatNumber value="${assessment.ALE}" maxFractionDigits="3" minFractionDigits="3" />"><fmt:formatNumber value="${assessment.ALE*0.001}"
									maxFractionDigits="0" minFractionDigits="0" /></td>
							<td title="<fmt:formatNumber value="${assessment.ALEP}" maxFractionDigits="3" minFractionDigits="3" />"><fmt:formatNumber value="${assessment.ALEP*0.001}"
									maxFractionDigits="0" minFractionDigits="0" /></td>
							<td class="success" trick-field="comment" trick-field-type="string" trick-content="text" colspan="3" ondblclick="return editField(this);"><spring:message
									text="${assessment.comment}" /></td>
							<td class="success" trick-field="hiddenComment" trick-content="text" trick-field-type="string" colspan="3" ondblclick="return editField(this);"><spring:message
									text="${assessment.hiddenComment}" /></td>
						</tr>
					</c:forEach>
					<tr class="panel-footer" style="font-weight: bold;">
						<td colspan="9"><spring:message code="label.assessment.total.ale" text="Total" /></td>
						<td title="<fmt:formatNumber value="${aleo.value}" maxFractionDigits="3" minFractionDigits="3" />"><spring:htmlEscape defaultHtmlEscape="true">
								<fmt:formatNumber value="${aleo.value*0.001}" maxFractionDigits="0" minFractionDigits="0" />
							</spring:htmlEscape></td>
						<td title="<fmt:formatNumber value="${ale.value}" maxFractionDigits="3" minFractionDigits="3" />"><spring:htmlEscape defaultHtmlEscape="true">
								<fmt:formatNumber value="${ale.value*0.001}" maxFractionDigits="0" minFractionDigits="0" />
							</spring:htmlEscape></td>
						<td title="<fmt:formatNumber value="${alep.value}" maxFractionDigits="3" minFractionDigits="3" />"><spring:htmlEscape defaultHtmlEscape="true">
								<fmt:formatNumber value="${alep.value*0.001}" maxFractionDigits="0" minFractionDigits="0" />
							</spring:htmlEscape></td>
						<td colspan="6" />
					</tr>
				</tbody>
			</table>
		</div>
	</div>
</div>