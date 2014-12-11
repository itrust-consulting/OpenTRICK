<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<div class="tab-pane" id="tabRiskInformation">
	<div class="section" id="section_risk_information">
		<div class="page-header">
			<h3>
				<fmt:message key="label.title.risk_information"  />
			</h3>
		</div>
		<c:if test="${empty(riskInformationSplited)}">
			<spring:eval expression="T(lu.itrust.business.TS.data.riskinformation.helper.RiskInformationManager).Split(riskInformation)" var="riskInformationSplited" />
		</c:if>
		<c:set var="chapterRegex">^\d(\.0)*$</c:set>
		<c:forEach items="${riskInformationSplited.keySet()}" var="categoryRiskInformation">
			<span class="anchor" id="anchorRiskInformation_${categoryRiskInformation}"></span>
			<div class="panel panel-default">
				<div class="panel-heading">
					<c:choose>
						<c:when test="${categoryRiskInformation == 'Threat'}">
							<fmt:message key="label.title.risk_information.threat"  />
						</c:when>
						<c:when test="${categoryRiskInformation == 'Vul'}">
							<fmt:message key="label.title.risk_information.vulnerabilities"  />
						</c:when>
						<c:otherwise>
							<fmt:message key="label.title.risk_information.risks"  />
						</c:otherwise>
					</c:choose>
				</div>
				<div class="panel-body">
					<table id="${categoryRiskInformation}table" class="table table-condensed table-hover">
						<thead>
							<tr>
								<th><fmt:message key="label.risk_information.id"  /></th>
								<c:choose>
									<c:when test="${categoryRiskInformation == 'Threat'}">
										<th colspan="3"><fmt:message key="label.risk_information.threats"  /></th>
										<th title='<fmt:message key="label.risk_information.acronym" />'><fmt:message key="label.risk_information.acro"  /></th>
									</c:when>
									<c:when test="${categoryRiskInformation == 'Vul'}">
										<th colspan="3"><fmt:message key="label.risk_information.vulnerabilities"  /></th>
									</c:when>
									<c:otherwise>
										<th colspan="3"><fmt:message key="label.risk_information.risks"  /></th>
									</c:otherwise>
								</c:choose>
								<th title='<fmt:message key="label.risk_information.exposed" />'><fmt:message key="label.risk_information.expo"  /></th>
								<th colspan="4"><fmt:message key="label.risk_information.comment"  /></th>
								<th colspan="4"><fmt:message key="label.risk_information.comment_hidden"  /></th>
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
													<td colspan="12"></td>
												</tr>
											</c:if>
										</c:otherwise>
									</c:choose>
								</c:if>
								<tr trick-class="RiskInformation" trick-id="${risk_information.id}">
									<c:set var="codeText" value="${risk_information.label}"/>
									<c:choose>
										<c:when test="${risk_information.category == 'Risk_TBA'}">
											<c:set var="codeLabel">label.risk_information.${fn:toLowerCase(categoryRiskInformation)}_tba.${fn:replace(risk_information.chapter,'.','_')}</c:set>
											
										</c:when>
										<c:when test="${risk_information.category == 'Risk_TBS'}">
											<c:set var="codeLabel">label.risk_information.${fn:toLowerCase(categoryRiskInformation)}_tbs.${fn:replace(risk_information.chapter,'.','_')}</c:set>
											
										</c:when>
										<c:otherwise>
											<c:set var="codeLabel">label.risk_information.${fn:toLowerCase(categoryRiskInformation)}.${fn:replace(risk_information.chapter,'.','_')}</c:set>
										</c:otherwise>
									</c:choose>
									<c:choose>
										<c:when test='${fct:matches(risk_information.chapter,chapterRegex)}'>
											<c:choose>
												<c:when test="${categoryRiskInformation == 'Threat' || categoryRiskInformation == 'Risk' }">
													<td><strong><spring:message text="${risk_information.chapter}" /></strong></td>
													<td colspan="12"><strong><fmt:message key="${codeLabel}"  /></strong></td>
												</c:when>
												<c:otherwise>
													<td><strong><spring:message text="${risk_information.chapter}" /></strong></td>
													<td colspan="3"><strong><fmt:message key="${codeLabel}"  /></strong></td>
													<td class="success" trick-field="exposed" trick-choose=",++,+,N,-,--" trick-field-type="string" ondblclick="return editField(this);"><spring:message
															text="${risk_information.exposed}" /></td>
													<td colspan="4" class="success" trick-field="comment" trick-content="text" trick-field-type="string" ondblclick="return editField(this);"><spring:message
															text="${risk_information.comment}" /></td>
													<td colspan="4" class="success" trick-field="hiddenComment" trick-content="text" trick-field-type="string" ondblclick="return editField(this);"><spring:message
															text="${risk_information.hiddenComment}" /></td>
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											<td><spring:message text="${risk_information.chapter}" /></td>
											<td colspan="3"><fmt:message key="${codeLabel}"  /></td>
											<c:if test="${categoryRiskInformation == 'Threat'}">
												<td><spring:message text="${risk_information.acronym}" /></td>
											</c:if>
											<td class="success" trick-field="exposed" trick-choose=",++,+,N,-,--" trick-field-type="string" ondblclick="return editField(this);"><spring:message
													text="${risk_information.exposed}" /></td>
											<td colspan="4" class="success" trick-field="comment" trick-content="text" trick-field-type="string" ondblclick="return editField(this);"><spring:message
													text="${risk_information.comment}" /></td>
											<td colspan="4" class="success" trick-field="hiddenComment" trick-content="text" trick-field-type="string" ondblclick="return editField(this);"><spring:message
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
</div>