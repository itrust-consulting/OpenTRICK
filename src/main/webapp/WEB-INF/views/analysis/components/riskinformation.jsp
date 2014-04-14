<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="section" id="section_risk_information" style="z-index: 3">
	<div class="page-header">
		<h3 id="RiskInformation">
			<spring:message code="label.risk_information" text="Risk information" />
		</h3>
	</div>
	<c:if test="${empty(riskInformationSplited)}">
		<spring:eval expression="T(lu.itrust.business.component.RiskInformationManager).Split(riskInformation)" var="riskInformationSplited" />
	</c:if>
	<c:forEach items="${riskInformationSplited.keySet()}" varStatus="categoryRiskInformation">
		<div class="panel">
			<div class="panel-heading">
				<spring:message code="label.risk_information.${categoryRiskInformation}" text="${categoryRiskInformation}" />
			</div>
			<div class="panel-body">
				<table>
					<thead>
						<tr>
							<th><spring:message code="label.risk_information.id" text="Id" /></th>
							<c:choose>
								<c:when test="${categoryRiskInformation == 'Threat'}">
									<th><spring:message code="label.risk_information.threat.characteristic" text="Threat characteristic" /></th>
								</c:when>
								<c:when test="${categoryRiskInformation == 'Vul'}">
									<th><spring:message code="label.risk_information.vulnerabilities" text="Vulnerabilies" /></th>
								</c:when>
								<c:otherwise>
									<th><spring:message code="label.risk_information.risks" text="Risks" /></th>
								</c:otherwise>
							</c:choose>
							<th title='<spring:message code="label.risk_information.exposed" text="Exposed" />'><spring:message code="label.risk_information.expo" text="Expo" /></th>
							<c:if test="${categoryRiskInformation == 'Threat'}">
								<th title='<spring:message code="label.risk_information.acronym" text="Acronym" />'><spring:message code="label.risk_information.acro" text="Acro" /></th>
							</c:if>
							<th><spring:message code="label.risk_information.comment" text="Comment" /></th>
							<th><spring:message code="label.risk_information.comment_hidden" text="Hidden Comment" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${riskInformationList}" var="risk_information">
							<tr>
								<c:choose>
									<c:when test='${risk_information.chapter.matches("^\\d(\\.0)*")}'>
										<c:choose>
											<c:when test="${categoryRiskInformation == 'Threat'}">
												<td><strong><spring:message text="${risk_information.chapter}" /></strong></td>
												<td colspan="5"><strong><spring:message text="${risk_information.label}" /></strong></td>
											</c:when>
											<c:otherwise>
												<td><strong><spring:message text="${risk_information.chapter}" /></strong></td>
												<td colspan="4"><strong><spring:message text="${risk_information.label}" /></strong></td>
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<td><spring:message text="${risk_information.chapter}" /></td>
										<td><spring:message text="${risk_information.label}" /></td>
										<td><spring:message text="${risk_information.exposed}" /></td>
										<c:if test="${categoryRiskInformation == 'Threat'}">
											<td><spring:message text="${risk_information.acronym}" /></td>
										</c:if>
										<td><spring:message text="${risk_information.comment}" /></td>
										<td><spring:message text="${risk_information.hiddenComment}" /></td>
									</c:otherwise>
								</c:choose>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</c:forEach>
</div>