<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="col-md-9 col-lg-10" id="section_log" >
	<table class="table table-striped table-hover table-condensed table-fixed-header-analysis">
		<thead>
			<tr>
				<th width="14%"><spring:message code="label.log.date.created" text="Created" /></th>
				<th width="5%"><spring:message code="label.log.level" text="Level" /></th>
				<th width="8%"><spring:message code="label.log.type" text="Type" /></th>
				<th width="8%"><spring:message code="label.log.action" text="Action" /></th>
				<th width="8%"><spring:message code="label.log.author" text="Author" /></th>
				<th><spring:message code="label.log.message" text="Message" /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${trickLogs}" var="trickLog">
				<c:set var="cssClass">
					<c:choose>
						<c:when test="${trickLog.level=='INFO'}">
							<c:if test="${trickLog.type == 'AUTHENTICATION'}">
								class="info"
							</c:if>
						</c:when>
						<c:when test="${trickLog.level=='ERROR'}">
							class="danger"
						</c:when>
						<c:otherwise>
							class="${fn:toLowerCase(trickLog.level)}"
						</c:otherwise>
					</c:choose>
				</c:set>
				<tr data-trick-id="${trickLog.id}" ${cssClass}>
					<td><fmt:formatDate value="${trickLog.created}" type="BOTH"/></td>
					<td><c:if test="${!empty trickLog.level}">
							<spring:message code="label.log.level.${fn:toLowerCase(trickLog.level)}" text="${fn:toLowerCase(trickLog.level)}" />
						</c:if>
					</td>
					<td><c:if test="${!empty trickLog.type}">
							<spring:message code="label.log.type.${fn:toLowerCase(trickLog.type)}" text="${fn:replace(fn:toLowerCase(trickLog.type),'_',' ')}" />
						</c:if>
					</td>
					<td>
						<c:choose>
							<c:when test="${empty trickLog.action}">
								-
							</c:when>
							<c:otherwise>
								<spring:message code="label.action.${fn:toLowerCase(trickLog.action)}" text="${fn:replace(fn:toLowerCase(trickLog.action),'_',' ')}"/>
							</c:otherwise>
						</c:choose>
						
					</td>
					<td><spring:message  text="${trickLog.author}"/></td>
					<td>
						<spring:message code="${trickLog.code}" arguments="${trickLog.parameters}" text="${trickLog.message}"/>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>