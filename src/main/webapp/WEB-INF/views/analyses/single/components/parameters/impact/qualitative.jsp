<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<fmt:setLocale value="fr" scope="session" />
<c:set var="impactLanguage" value="${fn:toUpperCase(language)}" />
<c:choose>
		<c:when test="${not type.quantitative and type.qualitative}"> 
				<div class="row">
				    <jsp:include page="../probability/likelihood.jsp" />
				<c:set var="writeIndex" value="${1}" />
		</c:when>
		<c:otherwise>
		    <c:set var="writeIndex" value="${0}" />
		</c:otherwise>
</c:choose>

<c:set var="writtenDynamic" value="${false}" />

<c:forEach items="${impactTypes}" var="impactType" varStatus="status">
    <c:choose>
		<c:when test="${impactType.name=='IMPACT'}">
			<c:if test="${writeIndex % 2 != 0 and status.index == impactTypes.size()-1}">
			    	<jsp:include page="../probability/dynamic.jsp" />
					<c:set var="writtenDynamic" value="${true}" />
				</div>
			</c:if>
		</c:when>
		<c:otherwise>
			<c:if test="${writeIndex % 2 == 0}">
				<div class="row">
			</c:if>
			<spring:message text="${impactType.name}" var="impactName" />
			<c:set var="displayName" value="${empty impactType.translations[impactLanguage]? impactType.displayName  :  impactType.translations[impactLanguage].name}" />
			<div class="col-sm-6 impact-qualitaitve">
				<fieldset id="Scale_Impact_${impactName}">
					<legend>
						<spring:message code="label.title.parameter.impact.name" arguments="${displayName},${fn:toLowerCase(displayName)}" />
					</legend>
					<table class="table table-hover table-fixed-header-analysis table-condensed">
						<thead>
							<tr>
								<th width='1%' class="textaligncenter"><spring:message code="label.parameter.level" /></th>
								<th width='20%' class="textaligncenter"><spring:message code="label.parameter.label" /></th>
								<th data-th-name='qualification'><spring:message code="label.parameter.qualification" /></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${mappedParameters[impactName]}" var="parameter">
								<tr data-trick-class="ImpactParameter" data-trick-id="${parameter.id}" ${parameter.level ==0? 'style="display: none"' : ''}>
									<c:choose>
										<c:when test="${parameter.level == 0 }">
											<td data-trick-field="level" class="textaligncenter"><spring:message code='label.status.na' /></td>
											<td data-trick-field="label" data-trick-acronym-value='<spring:message text="${parameter.acronym}" />' class="textaligncenter"><spring:message code='label.parameter.label.na' text="${parameter.label}" /></td>
										</c:when>
										<c:otherwise>
											<td data-trick-field="level" class="textaligncenter"><spring:message text="${parameter.level}" /></td>
											<td data-trick-field="label" data-trick-acronym-value='<spring:message text="${parameter.acronym}" />' class="textaligncenter"><spring:message
													text="${parameter.label}" /></td>
										</c:otherwise>
									</c:choose>


									<td data-trick-field="description" data-trick-content="text" data-trick-field-type="string" class="editable" onclick="return editField(this);"><spring:message
											text="${parameter.description}" /></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</fieldset>
			</div>
			<c:if test="${writeIndex % 2 != 0 or status.index == impactTypes.size()-1}">
			      <c:if test="${status.index == impactTypes.size()-1}">
				   	 <jsp:include page="../probability/dynamic.jsp" />
					 <c:set var="writtenDynamic" value="${true}" />
				  </c:if>
				</div>
			</c:if>
		 	<c:set var="writeIndex" value="${writeIndex+1}" />
		</c:otherwise>
	</c:choose>

</c:forEach>
<c:if test="${not writtenDynamic and type.quantitative and showDynamicAnalysis}">
	<div class="row">
		<jsp:include page="../probability/dynamic.jsp" />
	</div>
</c:if>
