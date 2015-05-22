<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<c:if test="${empty(riskInformationSplited)}">
	<spring:eval expression="T(lu.itrust.business.TS.model.riskinformation.helper.RiskInformationManager).Split(riskInformation)" var="riskInformationSplited" />
</c:if>
<c:set var="chapterRegex">^\d(\.0)*$</c:set>
<c:forEach items="${riskInformationSplited.keySet()}" var="categoryRiskInformation">
	<div class="tab-pane" id="tabRiskInformation_${categoryRiskInformation}">
		<div class="page-header tab-content-header">
			<div class="container">
				<div class="row-fluid">
					<h3>
						<c:choose>
							<c:when test="${categoryRiskInformation == 'Threat'}">
								<fmt:message key="label.title.risk_information.threat" />
							</c:when>
							<c:when test="${categoryRiskInformation == 'Vul'}">
								<fmt:message key="label.title.risk_information.vulnerabilities" />
							</c:when>
							<c:otherwise>
								<fmt:message key="label.title.risk_information.risks" />
							</c:otherwise>
						</c:choose>
					</h3>
				</div>
			</div>
		</div>
		<table id="${categoryRiskInformation}table" class="table table-condensed table-hover table-fixed-header-analysis">
			<thead>
				<tr>
					<th style="width: 5%"><fmt:message key="label.risk_information.id" /></th>
					<c:choose>
						<c:when test="${categoryRiskInformation == 'Threat'}">
							<th style="width: 15%"><fmt:message key="label.risk_information.threats" /></th>
							<th style="width: 5%" title='<fmt:message key="label.risk_information.acronym" />'><fmt:message key="label.risk_information.acro" /></th>
						</c:when>
						<c:when test="${categoryRiskInformation == 'Vul'}">
							<th style="width: 25%"><fmt:message key="label.risk_information.vulnerabilities" /></th>
						</c:when>
						<c:otherwise>
							<th style="width: 15%"><fmt:message key="label.risk_information.risks" /></th>
						</c:otherwise>
					</c:choose>
					<th style="width: 5%" title='<fmt:message key="label.risk_information.exposed" />'><fmt:message key="label.risk_information.expo" /></th>
					<th><fmt:message key="label.risk_information.comment" /></th>
					<th><fmt:message key="label.risk_information.comment_hidden" /></th>
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
					<tr data-trick-class="RiskInformation" data-trick-id="${risk_information.id}">
						<c:set var="codeText" value="${risk_information.label}" />
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
										<td style="height:32px;"><strong><spring:message text="${risk_information.chapter}" /></strong></td>
										<td colspan="12"><strong><fmt:message key="${codeLabel}" /></strong></td>
									</c:when>
									<c:otherwise>
										<td style="height:32px;"><strong><spring:message text="${risk_information.chapter}" /></strong></td>
										<td><strong><fmt:message key="${codeLabel}" /></strong></td>
										<td class="success" data-trick-field="exposed" data-trick-choose=",++,+,N,-,--" data-trick-field-type="string" onclick="return editField(this);"><spring:message
												text="${risk_information.exposed}" /></td>
										<td class="success" onclick="return editField(this.firstElementChild);"><pre data-trick-field="comment" data-trick-content="text" data-trick-field-type="string">
												<spring:message text="${risk_information.comment}" />
											</pre></td>
										<td class="success" onclick="return editField(this.firstElementChild);"><pre data-trick-field="hiddenComment" data-trick-content="text" data-trick-field-type="string">
												<spring:message text="${risk_information.hiddenComment}" />
											</pre></td>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								<td style="height:32px;"><spring:message text="${risk_information.chapter}" /></td>
								<td><fmt:message key="${codeLabel}" /></td>
								<c:if test="${categoryRiskInformation == 'Threat'}">
									<td><spring:message text="${risk_information.acronym}" /></td>
								</c:if>
								<td class="success" data-trick-field="exposed" data-trick-choose=",++,+,N,-,--" data-trick-field-type="string" onclick="return editField(this);"><spring:message
										text="${risk_information.exposed}" /></td>
								<td class="success" onclick="return editField(this.firstElementChild);"><pre data-trick-field="comment" data-trick-content="text" data-trick-field-type="string">
										<spring:message text="${risk_information.comment}" />
									</pre></td>
								<td class="success" onclick="return editField(this.firstElementChild);"><pre data-trick-field="hiddenComment" data-trick-content="text" data-trick-field-type="string">
										<spring:message text="${risk_information.hiddenComment}" />
									</pre></td>
							</c:otherwise>
						</c:choose>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</c:forEach>