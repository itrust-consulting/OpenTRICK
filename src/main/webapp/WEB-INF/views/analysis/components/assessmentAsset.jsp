<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="section" id="section_asset_assessment" trick-name="<spring:message code="label.assessment.for.asset" text="Assessment for ${asset.name}" arguments="${asset.name}" />">
	<div class="panel panel-default">
		<div class="panel-body" style="max-height: 700px; overflow: auto;">
			<table class="table table-hover">
				<thead>
					<tr>
						<th colspan="3"><spring:message code="label.assessment.scenario" text="Scenario" /></th>
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
					<spring:eval expression="T(lu.itrust.business.component.AssessmentManager).Sort(assessments)" var="sortedAssessments" />
					<c:forEach items="${sortedAssessments}" var="assessment">
						<tr trick-class="Assessment" trick-id="${assessment.id}" trick-callback="chartALE()">
							<td colspan="3"><spring:message text="${assessment.scenario.name}" /></td>
							<c:choose>
								<c:when test="${parameters.containsKey(assessment.impactRep)}">
									<td trick-field="impactRep" trick-field-type="string" class="success"
										title='<fmt:formatNumber value="${parameters.get(assessment.impactRep)*0.001}" maxFractionDigits="2" minFractionDigits="0" /> k&euro;'
										ondblclick="return editField(this);"><spring:message text="${assessment.impactRep}" /></td>
								</c:when>
								<c:otherwise>
									<c:catch>
										<fmt:formatNumber value="${assessment.impactRep*0.001}" maxFractionDigits="2" minFractionDigits="0" var="impactRep" />
									</c:catch>
									<td trick-field="impactRep" trick-field-type="string" class="success" ondblclick="return editField(this);" title="<spring:message text="${impactRep}" /> k&euro;"
										real-value="<spring:message text="${impactRep}" />"><spring:message text="${impactRep}" /></td>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${parameters.containsKey(assessment.impactOp)}">
									<td trick-field="impactOp" trick-field-type="string" class="success"
										title='<fmt:formatNumber value="${parameters.get(assessment.impactOp)*0.001}" maxFractionDigits="2" minFractionDigits="0" /> k&euro;' ondblclick="return editField(this);"><spring:message
											text="${assessment.impactOp}" /></td>
								</c:when>
								<c:otherwise>
									<c:catch>
										<fmt:formatNumber value="${assessment.impactOp*0.001}" maxFractionDigits="2" minFractionDigits="0" var="impactOp" />
									</c:catch>
									<td trick-field="impactOp" trick-field-type="string" class="success" ondblclick="return editField(this);" title="<spring:message text="${impactOp}" /> k&euro;"
										real-value="<spring:message text="${impactOp}" />"><spring:message text="${impactOp}" /></td>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${parameters.containsKey(assessment.impactLeg)}">
									<td trick-field="impactLeg" trick-field-type="string" class="success"
										title='<fmt:formatNumber value="${parameters.get(assessment.impactLeg)*0.001}" maxFractionDigits="2" minFractionDigits="0" /> k&euro;'
										ondblclick="return editField(this);"><spring:message text="${assessment.impactLeg}" /></td>
								</c:when>
								<c:otherwise>
									<c:catch>
										<fmt:formatNumber value="${assessment.impactLeg*0.001}" maxFractionDigits="2" minFractionDigits="0" var="impactLeg" />
									</c:catch>
									<td trick-field="impactLeg" trick-field-type="string" class="success" ondblclick="return editField(this);" title="<spring:message text="${impactLeg}" /> k&euro;"
										real-value="<spring:message text="${impactLeg}" />"><spring:message text="${impactLeg}" /></td>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${parameters.containsKey(assessment.impactFin)}">
									<td trick-field="impactFin" trick-field-type="string" class="success"
										title='<fmt:formatNumber value="${parameters.get(assessment.impactFin)*0.001}" maxFractionDigits="2" minFractionDigits="0" /> k&euro;'
										ondblclick="return editField(this);"><spring:message text="${assessment.impactFin}" /></td>
								</c:when>
								<c:otherwise>
									<c:catch>
										<fmt:formatNumber value="${assessment.impactFin*0.001}" var="impactFin" maxFractionDigits="2" minFractionDigits="0" />
									</c:catch>
									<td trick-field="impactFin" trick-field-type="string" class="success" ondblclick="return editField(this);" title='<spring:message text="${impactFin}" /> k&euro;'
										real-value="<spring:message text="${impactFin}" />"><spring:message text="${impactFin}" /></td>
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
							<td title="<fmt:formatNumber value="${assessment.ALEO}" maxFractionDigits="2" minFractionDigits="0" /> &euro;"><fmt:formatNumber value="${assessment.ALEO*0.001}"
									maxFractionDigits="0" minFractionDigits="0" /></td>
							<td title="<fmt:formatNumber value="${assessment.ALE}" maxFractionDigits="2" minFractionDigits="0" /> &euro;"><fmt:formatNumber value="${assessment.ALE*0.001}"
									maxFractionDigits="0" minFractionDigits="0" /></td>
							<td title="<fmt:formatNumber value="${assessment.ALEP}" maxFractionDigits="2" minFractionDigits="0" /> &euro;"><fmt:formatNumber value="${assessment.ALEP*0.001}"
									maxFractionDigits="0" minFractionDigits="0" /></td>
							<td trick-field="comment" trick-field-type="string" colspan="3" trick-content="text" ondblclick="return editField(this);" class="success"><spring:message
									text="${assessment.comment}" /></td>
							<td trick-field="hiddenComment" trick-field-type="string" trick-content="text" colspan="3" ondblclick="return editField(this);" class="success"><spring:message
									text="${assessment.hiddenComment}" /></td>
						</tr>
					</c:forEach>
					<tr class="panel-footer" style="font-weight: bold;">
						<td colspan="9"><spring:message code="label.assessment.total.ale" text="Total" /></td>
						<td title="<fmt:formatNumber value="${aleo.value}" maxFractionDigits="2" minFractionDigits="0" /> &euro;"><fmt:formatNumber value="${aleo.value*0.001}"
								maxFractionDigits="0" minFractionDigits="0" /></td>
						<td title="<fmt:formatNumber value="${ale.value}" maxFractionDigits="2" minFractionDigits="0" /> &euro;"><fmt:formatNumber value="${ale.value*0.001}"
								maxFractionDigits="0" minFractionDigits="0" /></td>
						<td title="<fmt:formatNumber value="${alep.value}" maxFractionDigits="2" minFractionDigits="0" /> &euro;"><fmt:formatNumber value="${alep.value*0.001}"
								maxFractionDigits="0" minFractionDigits="0" /></td>
						<td colspan="6" />
					</tr>
				</tbody>
			</table>
		</div>
	</div>
</div>
