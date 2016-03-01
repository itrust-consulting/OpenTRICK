<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<fmt:setLocale value="fr" scope="session" />
<c:choose>
	<c:when test="${not empty assessments}">
		<div id="estimation-ui" class='col-lg-10 trick-ui' data-trick-id='-1' data-trick-content='asset'>
			<fieldset style="display: block; width: 100%; clear: left;">
				<legend>
					<spring:message text='${asset.name}' />
				</legend>
				<div id="description" class='well well-sm' style="word-wrap: break-word; white-space: pre-wrap; resize: vertical; overflow: auto; height: 40px;"
				><spring:message text="${fn:trim(asset.comment)}" /></div>
			</fieldset>
			<table class="table table-hover table-fixed-header-analysis">
				<thead>
					<tr>
						<th style="width: 10%" title='<spring:message code="label.assessment.scenario" />'><spring:message code="label.assessment.scenario" /></th>
						<c:if test="${show_cssf}">
							<th style="width: 2.6%" title='<spring:message code="label.title.assessment.impact_rep" />'><spring:message code="label.assessment.impact_rep" /></th>
							<th style="width: 2.6%" title='<spring:message code="label.title.assessment.impact_op" />'><spring:message code="label.assessment.impact_op" /></th>
							<th style="width: 2.6%" title='<spring:message code="label.title.assessment.impact_leg" />'><spring:message code="label.assessment.impact_leg" /></th>
							<th style="width: 2.6%" title='<spring:message code="label.title.assessment.impact_fin" />'><spring:message code="label.assessment.impact_fin" /></th>
						</c:if>
						<c:if test="${!show_cssf}">
							<th style="width: 2.6%" title='<spring:message code="label.title.impact" />'><spring:message code="label.assessment.impact" /></th>
						</c:if>
						<th style="width: 2.6%" title='<spring:message code="label.title.likelihood" />'><spring:message code="label.assessment.likelihood" /></th>
						<c:choose>
							<c:when test="${show_uncertainty}">
								<th style="width: 2.6%" title='<spring:message code="label.title.uncertainty" />'><spring:message code="label.assessment.uncertainty" /></th>
								<th style="width: 2.6%" title='<spring:message code="label.title.alep" />'><spring:message code="label.assessment.alep" /></th>
								<th style="width: 2.6%" title='<spring:message code="label.title.ale" />'><spring:message code="label.assessment.ale" /></th>
								<th style="width: 2.6%" title='<spring:message code="label.title.aleo" />'><spring:message code="label.assessment.aleo" /></th>
							</c:when>
							<c:otherwise>
								<th style="width: 2.6%" title='<spring:message code="label.title.ale" />'><spring:message code="label.assessment.ale" /></th>
							</c:otherwise>
						</c:choose>
						<th width="2%" style="text-align: center;"><spring:message code="label.title.owner" text="Owner" /></th>
						<th style="width: 30%" title='<spring:message code="label.assessment.comment" />'><spring:message code="label.assessment.comment" /></th>
						<th style="width: 30%" title='<spring:message code="label.assessment.hidden_comment" />'><spring:message code="label.assessment.hidden_comment" /></th>
					</tr>
				</thead>
				<tbody>
					<spring:eval expression="T(lu.itrust.business.TS.model.assessment.helper.AssessmentManager).Sort(assessments)" var="sortedAssessments" />
					<c:forEach items="${sortedAssessments}" var="assessment">
						<spring:eval expression="T(lu.itrust.business.TS.model.cssf.RiskProfile).key(assessment.asset,assessment.scenario)" var="riskProfileKey" />
						<tr data-trick-id="${assessment.id}">
							<td style="height: 32px;"><spring:message text="${assessment.scenario.name}" /></td>
							<c:if test="${show_cssf}">
								<c:choose>
									<c:when test="${parameters.containsKey(assessment.impactRep)}">
										<td title='<fmt:formatNumber value="${fct:round(parameters.get(assessment.impactRep),0)}" /> &euro;'><spring:message text="${assessment.impactRep}" /></td>
									</c:when>
									<c:otherwise>
										<td title='<fmt:formatNumber value="${fct:round(assessment.impactRep,0)}" /> &euro;'><c:catch>
												<fmt:formatNumber value="${fct:round(assessment.impactRep*0.001,0)}" var="impactRep" />
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
										<td title='<fmt:formatNumber value="${fct:round(parameters.get(assessment.impactOp),0)}" /> &euro;'><spring:message text="${assessment.impactOp}" /></td>
									</c:when>
									<c:otherwise>
										<td title='<fmt:formatNumber value="${fct:round(assessment.impactOp,0)}" /> &euro;'><c:catch>
												<fmt:formatNumber value="${fct:round(assessment.impactOp*0.001,0)}" var="impactOp" />
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
										<td title='<fmt:formatNumber value="${fct:round(parameters.get(assessment.impactLeg),0)}" /> &euro;'><spring:message text="${assessment.impactLeg}" /></td>
									</c:when>
									<c:otherwise>
										<td title='<fmt:formatNumber value="${fct:round(assessment.impactLeg,0)}" /> &euro;'><c:catch>
												<fmt:formatNumber value="${fct:round(assessment.impactLeg*0.001,0)}" var="impactLeg" />
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
									<td title='<fmt:formatNumber value="${fct:round(parameters.get(assessment.impactFin),0)}" /> &euro;'><spring:message text="${assessment.impactFin}" /></td>
								</c:when>
								<c:otherwise>
									<td title='<fmt:formatNumber value="${fct:round(assessment.impactFin,0)}" /> &euro;'><c:catch>
											<fmt:formatNumber value="${fct:round(assessment.impactFin*0.001,0)}" var="impactFin" />
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
									<td title='<fmt:formatNumber value="${fct:round(parameters.get(assessment.likelihood),2)}" maxFractionDigits="2" /> <spring:message code="label.assessment.likelihood.unit" />'><spring:message
											text="${assessment.likelihood}" /></td>
								</c:when>
								<c:otherwise>
									<td><c:catch>
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
								<td><fmt:formatNumber value="${assessment.uncertainty}" maxFractionDigits="2" /></td>
								<td title="<fmt:formatNumber value="${assessment.ALEP}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(assessment.ALEP*0.001,1)}" /></td>
							</c:if>
							<td title="<fmt:formatNumber value="${assessment.ALE}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(assessment.ALE*0.001,1)}" /></td>
							<c:if test="${show_uncertainty}">
								<td title="<fmt:formatNumber value="${assessment.ALEO}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(assessment.ALEO*0.001,1)}" /></td>
							</c:if>
							<td><spring:message text="${riskProfiles[riskProfileKey].owner}" /></td>
							<td><pre><spring:message text="${assessment.comment}" /></pre></td>
							<td><pre><spring:message text="${assessment.hiddenComment}" /></pre></td>
						</tr>
					</c:forEach>
					<tr class="panel-footer" style="font-weight: bold;">
						<c:choose>
							<c:when test="${show_uncertainty}">
								<c:choose>
									<c:when test="${show_cssf}">
										<td colspan="7"><spring:message code="label.total.ale" /></td>
									</c:when>
									<c:otherwise>
										<td colspan="4"><spring:message code="label.total.ale" /></td>
									</c:otherwise>
								</c:choose>
								<td title="<fmt:formatNumber value="${alep.value}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(alep.value*0.001,1)}" /></td>
								<td title="<fmt:formatNumber value="${ale.value}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(ale.value*0.001,1)}" /></td>
								<td title="<fmt:formatNumber value="${aleo.value}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(aleo.value*0.001,1)}" /></td>
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${show_cssf}">
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
				</tbody>
			</table>
		</div>
	</c:when>
	<c:when test="${not empty assessment}">
		<div id="estimation-ui" class='col-lg-10 trick-ui' data-trick-id='${scenario.id}' data-trick-content='asset'>
			<fieldset style="display: block; width: 100%; clear: left;">
				<legend>
					<spring:message text='${scenario.name}' />
				</legend>
				<div id="description" class='well well-sm' 
				style="word-wrap: break-word; white-space: pre-wrap; resize: vertical; overflow: auto; height: 40px;"><spring:message text="${fn:trim(scenario.description)}" /></div>
			</fieldset>
			<c:choose>
				<c:when test="${show_cssf}">
					<jsp:include page="form-cssf.jsp" />
				</c:when>
				<c:otherwise>
					<c:set var="rowLength" value="${show_uncertainty? '12' : '13'}" scope="request"/>
					<jsp:include page="form-normal.jsp" />
				</c:otherwise>
			</c:choose>
		</div>
	</c:when>
	<c:otherwise>
		<div id="estimation-ui" class='col-lg-10' data-trick-id='-2' data-trick-content='asset'></div>
	</c:otherwise>
</c:choose>
