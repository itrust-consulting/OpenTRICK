<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="section" id="section_asset_assessment" trick-name="<fmt:message key="label.assessment.for.asset"><fmt:param value="${asset.name}" /></fmt:message>">
	<div class="panel panel-default">
		<div class="panel-body autofitpanelbodydefinition" style="max-height: 700px; overflow: show;">
			<table class="table table-hover table-condensed">
				<thead>
					<tr>
						<th style="width: 25%"><fmt:message key="label.assessment.scenario" /></th>
						<c:if test="${show_cssf}">
							<th style="width: 5%"><fmt:message key="label.assessment.impact_rep" /></th>
							<th style="width: 5%"><fmt:message key="label.assessment.impact_op" /></th>
							<th style="width: 5%"><fmt:message key="label.assessment.impact_leg" /></th>
							<th style="width: 5%"><fmt:message key="label.assessment.impact_fin" /></th>
						</c:if>
						<c:if test="${!show_cssf}">
							<th style="width: 5%"><fmt:message key="label.assessment.impact" /></th>
						</c:if>
						<th style="width: 5%"><fmt:message key="label.assessment.likelihood" /></th>
						<c:choose>
							<c:when test="${show_uncertainty}">
								<th style="width: 5%"><fmt:message key="label.assessment.uncertainty" /></th>
								<th style="width: 5%"><fmt:message key="label.assessment.alep" /></th>
								<th style="width: 5%"><fmt:message key="label.assessment.ale" /></th>
								<th style="width: 5%"><fmt:message key="label.assessment.aleo" /></th>
							</c:when>
							<c:otherwise>
								<th style="width: 5%"><fmt:message key="label.assessment.ale" /></th>
							</c:otherwise>
						</c:choose>
						<th><fmt:message key="label.assessment.comment" /></th>
						<th><fmt:message key="label.assessment.hidden_comment" /></th>
					</tr>
				</thead>
				<tbody>
					<spring:eval expression="T(lu.itrust.business.TS.data.assessment.helper.AssessmentManager).Sort(assessments)" var="sortedAssessments" />
					<c:forEach items="${sortedAssessments}" var="assessment">
						<tr trick-class="Assessment" trick-id="${assessment.id}" trick-callback="chartALE()">
							<td style="height:32px;"><spring:message text="${assessment.scenario.name}" /></td>
							<fmt:setLocale value="fr" scope="session" />
							<c:if test="${show_cssf}">
								<c:choose>
									<c:when test="${parameters.containsKey(assessment.impactRep)}">
										<td trick-field="impactRep" trick-field-type="string" class="success"
											title='<fmt:formatNumber value="${fct:round(parameters.get(assessment.impactRep),0)}" maxFractionDigits="0"/> &euro;' onclick="return editField(this);"><spring:message
												text="${assessment.impactRep}" /></td>
									</c:when>
									<c:otherwise>
										<td trick-field="impactRep" trick-field-type="string" class="success" onclick="return editField(this);"
											title='<fmt:formatNumber value="${fct:round(assessment.impactRep,0)}" maxFractionDigits="0"/> &euro;'><c:catch>
												<fmt:formatNumber value="${fct:round(assessment.impactRep*0.001,0)}" maxFractionDigits="0" var="impactRep" />
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
										<td trick-field="impactOp" trick-field-type="string" class="success"
											title='<fmt:formatNumber value="${fct:round(parameters.get(assessment.impactOp),0)}" maxFractionDigits="0"/> &euro;' onclick="return editField(this);"><spring:message
												text="${assessment.impactOp}" /></td>
									</c:when>
									<c:otherwise>
										<td trick-field="impactOp" trick-field-type="string" class="success" onclick="return editField(this);"
											title='<fmt:formatNumber value="${fct:round(assessment.impactOp,0)}" maxFractionDigits="0"/> &euro;'><c:catch>
												<fmt:formatNumber value="${fct:round(assessment.impactOp*0.001,0)}" maxFractionDigits="0" var="impactOp" />
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
										<td trick-field="impactLeg" trick-field-type="string" class="success"
											title='<fmt:formatNumber value="${fct:round(parameters.get(assessment.impactLeg),0)}" maxFractionDigits="0"/> &euro;' onclick="return editField(this);"><spring:message
												text="${assessment.impactLeg}" /></td>
									</c:when>
									<c:otherwise>
										<td trick-field="impactLeg" trick-field-type="string" class="success" onclick="return editField(this);"
											title='<fmt:formatNumber value="${fct:round(assessment.impactLeg,0)}" maxFractionDigits="0"/> &euro;'><c:catch>
												<fmt:formatNumber value="${fct:round(assessment.impactLeg*0.001,0)}" maxFractionDigits="0" var="impactLeg" />
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
							</c:if>
							<c:choose>
								<c:when test="${parameters.containsKey(assessment.impactFin)}">
									<td trick-field="impactFin" trick-field-type="string" class="success"
										title='<fmt:formatNumber value="${fct:round(parameters.get(assessment.impactFin),0)}" maxFractionDigits="0"/> &euro;' onclick="return editField(this);"><spring:message
											text="${assessment.impactFin}" /></td>
								</c:when>
								<c:otherwise>
									<td trick-field="impactFin" trick-field-type="string" class="success" onclick="return editField(this);"
										title='<fmt:formatNumber value="${fct:round(assessment.impactFin,0)}" maxFractionDigits="0"/> &euro;'><c:catch>
											<fmt:formatNumber value="${fct:round(assessment.impactFin*0.001,0)}" maxFractionDigits="0" var="impactFin" />
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
									<td trick-field="likelihood" trick-field-type="string" class="success"
										title='<fmt:formatNumber value="${fct:round(parameters.get(assessment.likelihood),2)}" maxFractionDigits="2" /> <fmt:message key="label.assessment.likelihood.unit" />'
										onclick="return editField(this);"><spring:message text="${assessment.likelihood}" /></td>
								</c:when>
								<c:otherwise>
									<td trick-field="likelihood" trick-field-type="string" class="success" onclick="return editField(this);" real-value="${assessment.likelihood}"><c:catch>
											<fmt:formatNumber value="${fct:round(assessment.likelihood,2)}" maxFractionDigits="2" var="likelihood" />
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
							<c:if test="${show_uncertainty}">
								<td trick-field="uncertainty" trick-field-type="double" class="success" trick-real-value='<fmt:formatNumber value="${assessment.uncertainty}" maxFractionDigits="2" />'
									onclick="return editField(this);"><fmt:formatNumber value="${assessment.uncertainty}" maxFractionDigits="2" /></td>
								<td title="<fmt:formatNumber value="${assessment.ALEO}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(assessment.ALEO*0.001,0)}"
										maxFractionDigits="0" /></td>
							</c:if>
							<td title="<fmt:formatNumber value="${assessment.ALE}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(assessment.ALE*0.001,0)}"
									maxFractionDigits="0" /></td>
							<c:if test="${show_uncertainty}">
								<td title="<fmt:formatNumber value="${assessment.ALEP}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(assessment.ALEP*0.001,0)}"
										maxFractionDigits="0" /></td>
							</c:if>
							<fmt:setLocale value="${fn:substring(analysis.language.alpha3,0, 2)}" scope="session" />
							<td onclick="return editField(this.firstChild);" class="success"><pre trick-field="comment" trick-field-type="string" trick-content="text"><spring:message
									text="${assessment.comment}" /></pre></td>
							<td onclick="return editField(this.firstChild);" class="success"><pre trick-field="hiddenComment" trick-field-type="string" trick-content="text"><spring:message
									text="${assessment.hiddenComment}" /></pre></td>
						</tr>
					</c:forEach>
					<tr class="panel-footer" style="font-weight: bold;">
						<c:choose>
							<c:when test="${show_uncertainty}">
								<c:choose>
									<c:when test="${show_cssf}">
										<td colspan="6"><fmt:message key="label.total.ale" /></td>
									</c:when>
									<c:otherwise>
										<td colspan="3"><fmt:message key="label.total.ale" /></td>
									</c:otherwise>
								</c:choose>
								<td title="<fmt:formatNumber value="${aleo.value}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(aleo.value*0.001,0)}" maxFractionDigits="0" /></td>
								<td title="<fmt:formatNumber value="${ale.value}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(ale.value*0.001,0)}" maxFractionDigits="0" /></td>
								<td title="<fmt:formatNumber value="${alep.value}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(alep.value*0.001,0)}" maxFractionDigits="0" /></td>
								<fmt:setLocale value="${fn:substring(analysis.language.alpha3,0, 2)}" scope="session" />
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${show_cssf}">
										<td colspan="6"><fmt:message key="label.total.ale" /></td>
									</c:when>
									<c:otherwise>
										<td colspan="3"><fmt:message key="label.total.ale" /></td>
									</c:otherwise>
								</c:choose>
								<td title="<fmt:formatNumber value="${ale.value}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(ale.value*0.001,0)}" maxFractionDigits="0" /></td>
							</c:otherwise>
						</c:choose>
						<td colspan="2">&nbsp;</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
</div>
