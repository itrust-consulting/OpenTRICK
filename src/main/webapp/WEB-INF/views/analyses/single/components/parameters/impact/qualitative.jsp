<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<fmt:setLocale value="fr" scope="session" />
<c:set var="impactLanguage" value="${fn:toUpperCase(language)}" />
<c:if test="${type.quantitative}">
	<jsp:include page="financial.jsp" />
</c:if>
<c:forEach items="${impactTypes}" var="impactType">
	<c:if test="${impactType.name!='IMPACT'}">
		<spring:message text="${impactType.name}" var="impactName" />
		<c:set var="displayName" value="${empty impactType.translations[impactLanguage]? impactType.displayName  :  impactType.translations[impactLanguage].name}" />
		<div class="col-md-6">
			<fieldset id="Scale_Impact_${impactName}">
				<legend>
					<spring:message code="label.title.parameter.impact.name" arguments="${displayName},${fn:toLowerCase(displayName)}" />
				</legend>
				<table class="table table-hover table-fixed-header-analysis table-condensed">
					<thead>
						<tr>
							<th width='5%' class="textaligncenter"><spring:message code="label.parameter.level" /></th>
							<th width='20%' class="textaligncenter"><spring:message code="label.parameter.label" /></th>
							<th class="textaligncenter"><spring:message code="label.parameter.qualification" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${mappedParameters[impactName]}" var="parameter">
							<tr data-trick-class="ImpactParameter" data-trick-id="${parameter.id}" ${parameter.level ==0? 'style="display: none"' : ''}>
								<td data-trick-field="level" class="textaligncenter"><c:choose>
										<c:when test="${parameter.level == 0 }">
											<spring:message code='label.status.na' />
										</c:when>
										<c:otherwise>
											<spring:message text="${parameter.level}" />
										</c:otherwise>
									</c:choose></td>
								<td data-trick-field="label" data-trick-acronym-value='<spring:message text="${parameter.acronym}" />' class="textaligncenter"><spring:message
										text="${parameter.label}" /></td>
								<td data-trick-field="description" data-trick-field-type="string" class="editable textaligncenter" onclick="return editField(this);"><spring:message
										text="${parameter.description}" /></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</fieldset>
		</div>
	</c:if>
</c:forEach>
