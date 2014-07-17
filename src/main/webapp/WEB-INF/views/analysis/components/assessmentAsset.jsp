<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="section" id="section_asset_assessment" trick-name="<spring:message code="label.assessment.for.asset" text="Assessment for ${asset.name}" arguments="${asset.name}" />">
	<div class="panel panel-default">
		<div class="panel-body" style="max-height: 700px; overflow: auto;">
			<table class="table table-hover table-fixed-header">
				<thead>
					<tr>
						<th colspan="4"><spring:message code="label.assessment.scenario" text="Scenario" /></th>
						<th><spring:message code="label.assessment.impact_rep" text="Rep." /> (k&euro;)</th>
						<th><spring:message code="label.assessment.impact_op" text="Op." /> (k&euro;)</th>
						<th><spring:message code="label.assessment.impact_leg" text="Leg." /> (k&euro;)</th>
						<th><spring:message code="label.assessment.impact_fin" text="Fin." /> (k&euro;)</th>
						<th><spring:message code="label.assessment.likelihood" text="Pro." /> (<spring:message code="label.assessment.likelihood.unit" text="/y"/>)</th>
						<th><spring:message code="label.assessment.uncertainty" text="Unc." /></th>
						<th><spring:message code="label.assessment.alep" text="ALEP" /> (k&euro;)</th>
						<th><spring:message code="label.assessment.ale" text="ALE" /> (k&euro;)</th>
						<th><spring:message code="label.assessment.aleo" text="ALEO" /> (k&euro;)</th>
						<th colspan="6"><spring:message code="label.assessment.comment" text="Comment" /></th>
						<th colspan="6"><spring:message code="label.assessment.hidden_comment" text="Hidden comment" /></th>
					</tr>
				</thead>
				<tbody>
					<spring:eval expression="T(lu.itrust.business.component.AssessmentManager).Sort(assessments)" var="sortedAssessments" />
					<c:forEach items="${sortedAssessments}" var="assessment">
						<tr trick-class="Assessment" trick-id="${assessment.id}" trick-callback="chartALE()">
							<td colspan="4"><spring:message text="${assessment.scenario.name}" /></td>
							<c:choose>
								<c:when test="${parameters.containsKey(assessment.impactRep)}">
									<td trick-field="impactRep" trick-field-type="string" class="success" title='<fmt:formatNumber value="${parameters.get(assessment.impactRep)}" />'
										ondblclick="return editField(this);"><spring:message text="${assessment.impactRep}" /></td>
								</c:when>
								<c:otherwise>
									<td trick-field="impactRep" trick-field-type="string" class="success" ondblclick="return editField(this);" title='<fmt:formatNumber value="${assessment.impactRep}" />&euro;'><c:catch>
											<fmt:formatNumber value="${assessment.impactRep*0.001}" maxFractionDigits="1" minFractionDigits="0" var="impactRep" />
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
									<td trick-field="impactOp" trick-field-type="string" class="success" title='<fmt:formatNumber value="${parameters.get(assessment.impactOp)}" />&euro;'
										ondblclick="return editField(this);"><spring:message text="${assessment.impactOp}" /></td>
								</c:when>
								<c:otherwise>
									<td trick-field="impactOp" trick-field-type="string" class="success" ondblclick="return editField(this);" title='<fmt:formatNumber value="${assessment.impactOp}" />&euro;'><c:catch>
											<fmt:formatNumber value="${assessment.impactOp*0.001}" maxFractionDigits="1" minFractionDigits="0" var="impactOp" />
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
									<td trick-field="impactLeg" trick-field-type="string" class="success" title='<fmt:formatNumber value="${parameters.get(assessment.impactLeg)}" />&euro;'
										ondblclick="return editField(this);"><spring:message text="${assessment.impactLeg}" /></td>
								</c:when>
								<c:otherwise>
									<td trick-field="impactLeg" trick-field-type="string" class="success" ondblclick="return editField(this);" title='<fmt:formatNumber value="${assessment.impactLeg}" />&euro;' ><c:catch>
											<fmt:formatNumber value="${assessment.impactLeg*0.001}" maxFractionDigits="1" minFractionDigits="0" var="impactLeg" />
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
									<td trick-field="impactFin" trick-field-type="string" class="success" title='<fmt:formatNumber value="${parameters.get(assessment.impactFin)}" />&euro;'
										ondblclick="return editField(this);"><spring:message text="${assessment.impactFin}" /></td>
								</c:when>
								<c:otherwise>
									<td trick-field="impactFin" trick-field-type="string" class="success" ondblclick="return editField(this);" title='<fmt:formatNumber value="${assessment.impactFin}" />&euro;'><c:catch>
											<fmt:formatNumber value="${assessment.impactFin*0.001}" var="impactFin" maxFractionDigits="1" minFractionDigits="0" />
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
									<td trick-field="likelihood" trick-field-type="string" class="success" title='<fmt:formatNumber value="${parameters.get(assessment.likelihood)}" /><spring:message code="label.assessment.likelihood.unit" text="/y"/>'
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
									value="${assessment.uncertainty}" maxFractionDigits="2" minFractionDigits="0" /></td>
							<td title="<fmt:formatNumber value="${assessment.ALEO}" maxFractionDigits="2" minFractionDigits="0" />&euro;"><fmt:formatNumber value="${assessment.ALEO*0.001}"
									maxFractionDigits="0" minFractionDigits="0" /></td>
							<td title="<fmt:formatNumber value="${assessment.ALE}" maxFractionDigits="2" minFractionDigits="0" />&euro;"><fmt:formatNumber value="${assessment.ALE*0.001}"
									maxFractionDigits="0" minFractionDigits="0" /></td>
							<td title="<fmt:formatNumber value="${assessment.ALEP}" maxFractionDigits="2" minFractionDigits="0" />&euro;"><fmt:formatNumber value="${assessment.ALEP*0.001}"
									maxFractionDigits="0" minFractionDigits="0" /></td>
							<td colspan="6" ondblclick="return editField(this);" class="success" trick-field="comment" trick-field-type="string" colspan="3" trick-content="text"><spring:message text="${assessment.comment}" /></td>
							<td colspan="6" ondblclick="return editField(this);" class="success" trick-field="hiddenComment" trick-field-type="string" trick-content="text"><spring:message text="${assessment.hiddenComment}" /></td>
						</tr>
					</c:forEach>
					<tr class="panel-footer" style="font-weight: bold;">
						<td colspan="10"><spring:message code="label.assessment.total.ale" text="Total" /></td>
						<td title="<fmt:formatNumber value="${aleo.value}" maxFractionDigits="2" minFractionDigits="0" />&euro;"><fmt:formatNumber value="${aleo.value*0.001}" maxFractionDigits="0"
								minFractionDigits="0" /></td>
						<td title="<fmt:formatNumber value="${ale.value}" maxFractionDigits="2" minFractionDigits="0" />&euro;"><fmt:formatNumber value="${ale.value*0.001}" maxFractionDigits="0"
								minFractionDigits="0" /></td>
						<td title="<fmt:formatNumber value="${alep.value}" maxFractionDigits="2" minFractionDigits="0" />&euro;"><fmt:formatNumber value="${alep.value*0.001}" maxFractionDigits="0"
								minFractionDigits="0" /></td>
						<td colspan="12" />
					</tr>
				</tbody>
			</table>
		</div>
	</div>
</div>
