<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<fmt:setLocale value="fr_FR" scope="session"/>
<div class="section" id="section_asset_assessment"
	trick-name="<spring:message code="label.assessment.for.asset" text="Assessment for ${asset.name}" arguments="${asset.name}" htmlEscape="true" />">
	<div class="panel panel-default">
		<div class="panel-body" style="max-height: 700px; overflow: auto;">
			<table class="table table-hover">
				<thead>
					<tr>
						<th colspan="3"><spring:message code="label.assessment.scenario" text="Scenario" htmlEscape="true" /></th>
						<th><spring:message code="label.assessment.impactRep" text="Rep." htmlEscape="true" /></th>
						<th><spring:message code="label.assessment.impactOp" text="Op." htmlEscape="true" /></th>
						<th><spring:message code="label.assessment.impactLeg" text="Leg." htmlEscape="true" /></th>
						<th><spring:message code="label.assessment.impactFin" text="Fin." htmlEscape="true" /></th>
						<th><spring:message code="label.assessment.likelihood" text="Pro." htmlEscape="true" /></th>
						<th><spring:message code="label.assessment.uncertainty" text="Unc." htmlEscape="true" /></th>
						<th><spring:message code="label.assessment.ALEP" text="ALEP" htmlEscape="true" /> (k&euro;)</th>
						<th><spring:message code="label.assessment.ALE" text="ALE" htmlEscape="true" /> (k&euro;)</th>
						<th><spring:message code="label.assessment.ALEO" text="ALEO" htmlEscape="true" /> (k&euro;)</th>
						<th colspan="3"><spring:message code="label.assessment.comment" text="Comment" htmlEscape="true" /></th>
						<th colspan="3"><spring:message code="label.assessment.hiddenComment" text="Hidden comment" htmlEscape="true" /></th>
					</tr>
				</thead>
				<tbody>
					<c:set var="prevAsset" value="null" />
					<spring:eval expression="T(lu.itrust.business.component.AssessmentManager).Sort(assessments)" var="sortedAssessments" />
					<c:forEach items="${sortedAssessments}" var="assessment">
						<tr trick-class="Assessment" trick-id="${assessment.id}" trick-callback="chartALE()">
							<td colspan="3">${assessment.scenario.name}</td>
							<c:choose>
								<c:when test="${parameters.containsKey(assessment.impactRep)}">
									<td trick-field="impactRep" trick-field-type="string" class="success" title='<fmt:formatNumber value="${parameters.get(assessment.impactRep)*0.001}" />'
										ondblclick="return editField(this);"><spring:message text="${assessment.impactRep}" htmlEscape="true" /></td>
								</c:when>
								<c:otherwise>
									<td trick-field="impactRep" trick-field-type="string" class="success" ondblclick="return editField(this);" real-value="${assessment.impactRep}"><c:catch>
											<fmt:formatNumber value="${assessment.impactRep}" maxFractionDigits="1" minFractionDigits="1" var="impactRep" />
										</c:catch> <c:choose>
											<c:when test="${!empty impactRep}">
												<spring:message text="${impactRep}" htmlEscape="true" />
											</c:when>
											<c:otherwise>

												<spring:message text="${assessment.impactRep}" htmlEscape="true" />
											</c:otherwise>
										</c:choose></td>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${parameters.containsKey(assessment.impactOp)}">
									<td trick-field="impactOp" trick-field-type="string" class="success" title='<fmt:formatNumber value="${parameters.get(assessment.impactOp)*0.001}" />'
										ondblclick="return editField(this);"><spring:message text="${assessment.impactOp}" htmlEscape="true" /></td>
								</c:when>
								<c:otherwise>
									<td trick-field="impactOp" trick-field-type="string" class="success" ondblclick="return editField(this);" real-value="${assessment.impactOp}"><c:catch>
											<fmt:formatNumber value="${assessment.impactOp}" maxFractionDigits="1" minFractionDigits="1" var="impactOp" />
										</c:catch> <c:choose>
											<c:when test="${!empty impactOp}">
												<spring:message text="${impactOp}" htmlEscape="true" />
											</c:when>
											<c:otherwise>
												<spring:message text="${assessment.impactOp}" htmlEscape="true" />
											</c:otherwise>
										</c:choose></td>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${parameters.containsKey(assessment.impactLeg)}">
									<td trick-field="impactLeg" trick-field-type="string" class="success" title='<fmt:formatNumber value="${parameters.get(assessment.impactLeg)*0.001}" />'
										ondblclick="return editField(this);"><spring:message text="${assessment.impactLeg}" htmlEscape="true" /></td>
								</c:when>
								<c:otherwise>
									<td trick-field="impactLeg" trick-field-type="string" class="success" ondblclick="return editField(this);" real-value="${assessment.impactLeg}"><c:catch>
											<fmt:formatNumber value="${assessment.impactLeg}" maxFractionDigits="1" minFractionDigits="1" var="impactLeg" />
										</c:catch> <c:choose>
											<c:when test="${!empty impactLeg}">
												<spring:message text="${impactLeg}" htmlEscape="true" />
											</c:when>
											<c:otherwise>
												<spring:message text="${assessment.impactLeg}" htmlEscape="true" />
											</c:otherwise>
										</c:choose></td>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${parameters.containsKey(assessment.impactFin)}">
									<td trick-field="impactFin" trick-field-type="string" class="success" title='<fmt:formatNumber value="${parameters.get(assessment.impactFin)*0.001}" />'
										ondblclick="return editField(this);"><spring:message text="${assessment.impactFin}" htmlEscape="true" /></td>
								</c:when>
								<c:otherwise>
									<td trick-field="impactFin" trick-field-type="string" class="success" ondblclick="return editField(this);" real-value="${assessment.impactFin}"><c:catch>
											<fmt:formatNumber value="${assessment.impactFin}" var="impactFin" maxFractionDigits="1" minFractionDigits="1" />
										</c:catch> <c:choose>
											<c:when test="${not empty impactFin}">
												<spring:message text="${impactFin}" htmlEscape="true" />
											</c:when>
											<c:otherwise>
												<spring:message text="${assessment.impactFin}" htmlEscape="true" />
											</c:otherwise>
										</c:choose></td>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${parameters.containsKey(assessment.likelihood)}">
									<td trick-field="likelihood" trick-field-type="string" class="success" title='<fmt:formatNumber value="${parameters.get(assessment.likelihood)}" />'
										ondblclick="return editField(this);"><spring:message text="${assessment.likelihood}" htmlEscape="true" /></td>
								</c:when>
								<c:otherwise>
									<td trick-field="likelihood" trick-field-type="string" class="success" ondblclick="return editField(this);" real-value="${assessment.likelihood}"><c:catch>
											<fmt:formatNumber value="${parameters.get(assessment.likelihood)}" var="likelihood" />
										</c:catch> <c:choose>
											<c:when test="${not empty likelihood }">
												<spring:message text="${likelihood}" htmlEscape="true" />
											</c:when>
											<c:otherwise>
												<spring:message text="${assessment.likelihood}" htmlEscape="true" />
											</c:otherwise>
										</c:choose></td>
								</c:otherwise>
							</c:choose>
							<td trick-field="uncertainty" trick-field-type="double" class="success" trick-real-value="${assessment.uncertainty}" ondblclick="return editField(this);"><fmt:formatNumber
									value="${assessment.uncertainty}" maxFractionDigits="3" minFractionDigits="1" /></td>
							<td title="<fmt:formatNumber value="${assessment.ALEP}" maxFractionDigits="3" minFractionDigits="3" />"><fmt:formatNumber value="${assessment.ALEP*0.001}"
									maxFractionDigits="0" minFractionDigits="0" /></td>
							<td title="<fmt:formatNumber value="${assessment.ALE}" maxFractionDigits="3" minFractionDigits="3" />"><fmt:formatNumber value="${assessment.ALE*0.001}" maxFractionDigits="0"
									minFractionDigits="0" /></td>
							<td title="<fmt:formatNumber value="${assessment.ALEO}" maxFractionDigits="3" minFractionDigits="3" />"><fmt:formatNumber value="${assessment.ALEO*0.001}"
									maxFractionDigits="0" minFractionDigits="0" /></td>
							<td trick-field="comment" trick-field-type="string" colspan="3" trick-content="text" ondblclick="return editField(this);">${assessment.comment}</td>
							<td trick-field="hiddenComment" trick-field-type="string" trick-content="text" colspan="3" ondblclick="return editField(this);">${assessment.hiddenComment}</td>
						</tr>
						<c:set var="prevAsset" value="${assessment.asset}" />
					</c:forEach>
					<tr class="panel-footer" style="font-weight: bold;">
						<td colspan="9"><spring:message code="label.assessment.total.ale" text="Total" /></td>
						<td title="<fmt:formatNumber value="${alep.value}" maxFractionDigits="3" minFractionDigits="3" />"><spring:htmlEscape defaultHtmlEscape="true">
								<fmt:formatNumber value="${alep.value*0.001}" maxFractionDigits="0" minFractionDigits="0" />
							</spring:htmlEscape></td>
						<td title="<fmt:formatNumber value="${ale.value}" maxFractionDigits="3" minFractionDigits="3" />"><spring:htmlEscape defaultHtmlEscape="true">
								<fmt:formatNumber value="${ale.value*0.001}" maxFractionDigits="0" minFractionDigits="0" />
							</spring:htmlEscape></td>
						<td title="<fmt:formatNumber value="${aleo.value}" maxFractionDigits="3" minFractionDigits="3" />"><spring:htmlEscape defaultHtmlEscape="true">
								<fmt:formatNumber value="${aleo.value*0.001}" maxFractionDigits="0" minFractionDigits="0" />
							</spring:htmlEscape></td>
						<td colspan="6" />
					</tr>
				</tbody>
			</table>
		</div>
	</div>
</div>
