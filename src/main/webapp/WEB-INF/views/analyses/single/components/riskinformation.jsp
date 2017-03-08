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
	<div class="tab-pane" id="tab-risk-information-${fn:toLowerCase(categoryRiskInformation)}">
		<div class="page-header tab-content-header table-condensed">
			<div class="container">
				<div class="row-fluid">
					<h3>
						<c:choose>
							<c:when test="${categoryRiskInformation == 'Threat'}">
								<spring:message code="label.title.risk_information.threat" />
							</c:when>
							<c:when test="${categoryRiskInformation == 'Vul'}">
								<spring:message code="label.title.risk_information.vulnerabilities" />
							</c:when>
							<c:otherwise>
								<spring:message code="label.title.risk_information.risks" />
							</c:otherwise>
						</c:choose>
					</h3>
				</div>
			</div>
		</div>
		<table id="${categoryRiskInformation}table" class="table table-condensed table-hover table-fixed-header-analysis">
			<thead>
				<tr>
					<th width="5%" title='<spring:message code="label.title.id" />'><spring:message code="label.risk_information.id" /></th>
					<c:choose>
						<c:when test="${categoryRiskInformation == 'Threat'}">
							<th width="15%" title='<spring:message code="label.name" />'><spring:message code="label.name" /></th>
							<th width="5%" title='<spring:message code="label.risk_information.acronym" />'><spring:message code="label.risk_information.acro" /></th>
						</c:when>
						<c:when test="${categoryRiskInformation == 'Vul'}">
							<th width="25%" title='<spring:message code="label.name" />'><spring:message code="label.name" /></th>
						</c:when>
						<c:otherwise>
							<th width="15%" title='<spring:message code="label.name" />'><spring:message code="label.name" /></th>
						</c:otherwise>
					</c:choose>
					<th width="5%" title='<spring:message code="label.risk_information.exposed" />'><spring:message code="label.risk_information.expo" /></th>
					<th width="5%" title='<spring:message code="label.title.risk_information.owner" />'><spring:message code="label.risk_information.owner" /></th>
					<th title='<spring:message code="label.risk_information.comment" />'><spring:message code="label.risk_information.comment" /></th>
					<c:if test="${showRawColumn}">
						<th title='<spring:message code="label.risk_information.comment_hidden" />'><spring:message code="label.risk_information.comment_hidden" /></th>
					</c:if>
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
										<td colspan="6"></td>
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
								<td style="height: 32px;"><strong><spring:message text="${risk_information.chapter}" /></strong></td>
								<c:choose>
									<c:when test="${categoryRiskInformation == 'Threat'}">
										<td colspan="2"><strong><spring:message code="${codeLabel}" /></strong></td>
									</c:when>
									<c:otherwise>
										<td><strong><spring:message code="${codeLabel}" /></strong></td>
									</c:otherwise>
								</c:choose>
								<td class="editable" data-trick-field="exposed" data-trick-choose=",++,+,N,-,--" data-trick-field-type="string" onclick="return editField(this);"><spring:message
										text="${risk_information.exposed}" /></td>
								<td class="editable" onclick="return editField(this);" data-trick-field="owner" data-trick-field-type="string"><spring:message text="${risk_information.owner}" /></td>
								<td class="editable" onclick="return editField(this);" data-trick-field="comment" data-trick-content="text" data-trick-field-type="string"><spring:message
										text="${risk_information.comment}" /></td>
								<c:if test="${showRawColumn}">
									<td class="editable" onclick="return editField(this);" data-trick-field="hiddenComment" data-trick-content="text" data-trick-field-type="string"><spring:message
											text="${risk_information.hiddenComment}" /></td>
								</c:if>
							</c:when>
							<c:otherwise>
								<td style="height: 32px;"><spring:message text="${risk_information.chapter}" /></td>
								<td><spring:message code="${codeLabel}" /></td>
								<c:if test="${categoryRiskInformation == 'Threat'}">
									<td><spring:message text="${risk_information.acronym}" /></td>
								</c:if>
								<td class="editable" data-trick-field="exposed" data-trick-choose=",++,+,N,-,--" data-trick-field-type="string" onclick="return editField(this);"><spring:message
										text="${risk_information.exposed}" /></td>
								<td class="editable" onclick="return editField(this);" data-trick-field="owner" data-trick-field-type="string"><spring:message text="${risk_information.owner}" /></td>
								<td class="editable" onclick="return editField(this);" data-trick-field="comment" data-trick-content="text" data-trick-field-type="string"><spring:message
										text="${risk_information.comment}" /></td>
								<c:if test="${showRawColumn}">
									<td class="editable" onclick="return editField(this);" data-trick-field="hiddenComment" data-trick-content="text" data-trick-field-type="string"><spring:message
											text="${risk_information.hiddenComment}" /></td>
								</c:if>
							</c:otherwise>
						</c:choose>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</c:forEach>