<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fct" uri="http://itrust.lu/function"%>
<div class="section" id="section_risk_information" style="z-index: 3">
	<div class="page-header">
		<h3>
			<spring:message code="label.risk_information" text="Risk information" />
		</h3>
	</div>
	<c:if test="${empty(riskInformationSplited)}">
		<spring:eval expression="T(lu.itrust.business.component.RiskInformationManager).Split(riskInformation)" var="riskInformationSplited" />
	</c:if>
	<c:set var="chapterRegex">^\d(\.0)*$</c:set>
	<c:forEach items="${riskInformationSplited.keySet()}" var="categoryRiskInformation">
		<span class="anchor" id="anchorRiskInformation_${categoryRiskInformation}"></span>
		<div class="panel panel-default">
			<div class="panel-heading">
				<spring:message code="label.risk_information.${categoryRiskInformation}" text="${categoryRiskInformation}" />
			</div>
			<div class="panel-body autofitpanelbodydefinition">
				<table id="${categoryRiskInformation}table" class="table table-condensed table-hover headertofixtable">
					<thead>
						<tr>
							<th><spring:message code="label.risk_information.id" text="Id" /></th>
							<c:choose>
								<c:when test="${categoryRiskInformation == 'Threat'}">
									<th><spring:message code="label.risk_information.threat" text="Threat" /></th>
								</c:when>
								<c:when test="${categoryRiskInformation == 'Vul'}">
									<th><spring:message code="label.risk_information.vulnerabilities" text="Vulnerabilies" /></th>
								</c:when>
								<c:otherwise>
									<th><spring:message code="label.risk_information.risks" text="Risks" /></th>
								</c:otherwise>
							</c:choose>
							<c:if test="${categoryRiskInformation == 'Threat'}">
								<th title='<spring:message code="label.risk_information.acronym" text="Acronym" />'><spring:message code="label.risk_information.acro" text="Acro" /></th>
							</c:if>
							<th title='<spring:message code="label.risk_information.exposed" text="Exposed" />'><spring:message code="label.risk_information.expo" text="Expo" /></th>
							<th><spring:message code="label.risk_information.comment" text="Comment" /></th>
							<th><spring:message code="label.risk_information.comment_hidden" text="Hidden Comment" /></th>
						</tr>
					</thead>
					<tfoot></tfoot>
					<tbody>
						<c:forEach items="${riskInformationSplited.get(categoryRiskInformation)}" var="risk_information">
							<c:if test="${categoryRiskInformation == 'Risk'}">
								<c:choose>
									<c:when test="${empty previewRisk}">
										<c:set var="previewRisk" value="${risk_information}" />
									</c:when>
									<c:otherwise>
										<c:if test="${previewRisk.category != risk_information.category}">
											<c:set var="previewRisk" value="${risk_information}" />
											<tr>
												<td colspan="5"></td>
											</tr>
										</c:if>
									</c:otherwise>
								</c:choose>
							</c:if>
							<tr trick-class="RiskInformation" trick-id="${risk_information.id}">
								<c:choose>
									<c:when test='${fct:matches(risk_information.chapter,chapterRegex)}'>
										<c:choose>
											<c:when test="${categoryRiskInformation == 'Threat'}">
												<td><strong><spring:message text="${risk_information.chapter}" /></strong></td>
												<td colspan="5"><strong><spring:message text="${risk_information.label}" /></strong></td>
											</c:when>
											<c:otherwise>
												<td><strong><spring:message text="${risk_information.chapter}" /></strong></td>
												<td><strong><spring:message text="${risk_information.label}" /></strong></td>
												<td class="success" trick-field="exposed" trick-choose=",++,+,N,-,--" trick-field-type="string" ondblclick="return editField(this);"><spring:message
														text="${risk_information.exposed}" /></td>
												<td class="success" trick-field="comment" trick-content="text" trick-field-type="string" ondblclick="return editField(this);"><spring:message
														text="${risk_information.comment}" /></td>
												<td class="success" trick-field="hiddenComment" trick-content="text" trick-field-type="string" ondblclick="return editField(this);"><spring:message
														text="${risk_information.hiddenComment}" /></td>
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<td><spring:message text="${risk_information.chapter}" /></td>
										<td><spring:message text="${risk_information.label}" /></td>
										<c:if test="${categoryRiskInformation == 'Threat'}">
											<td><spring:message text="${risk_information.acronym}" /></td>
										</c:if>
										<td class="success" trick-field="exposed" trick-choose=",++,+,N,-,--" trick-field-type="string" ondblclick="return editField(this);"><spring:message
												text="${risk_information.exposed}" /></td>
										<td class="success" trick-field="comment" trick-content="text" trick-field-type="string" ondblclick="return editField(this);"><spring:message
												text="${risk_information.comment}" /></td>
										<td class="success" trick-field="hiddenComment" trick-content="text" trick-field-type="string" ondblclick="return editField(this);"><spring:message
												text="${risk_information.hiddenComment}" /></td>
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