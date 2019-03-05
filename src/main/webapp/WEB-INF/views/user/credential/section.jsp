<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<div id="section_credential">
	<table class="table table-hover table-condensed">
		<thead>
			<tr>
				<th><spring:message code="label.customer" text="Customer"/></th>
				<th><spring:message code="label.type" text="Type"/></th>
				<th><spring:message code="label.name" text="Name"/></th>
				<th><spring:message code="label.value" text="Value"/></th>
				<th><spring:message code="label.action"/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${credentials}" var="credential">
				<tr data-trick-id="${credential.id}">
					<td>
						<c:choose>
							<c:when test="${empty credential['ticketingSystem']}">
								<spring:message text="${credential.ticketingSystem.customer.organisation}"/>
							</c:when>
							<c:otherwise>
							</c:otherwise>
						</c:choose>
					</td>
					<td><spring:message code="label.credential.type.${fn:toLowerCase(credential.type)}" text="${credential.type}"/></td>
					<td><spring:message text="${credential.name}"/></td>
					<td>
						<c:choose>
							<c:when test="${credential.type == 'TOKEN'}">
								<spring:message text="${credential.value}"/>
							</c:when>
							<c:otherwise>
								**************
							</c:otherwise>
						</c:choose>
					</td>
					<td>
						<a class="btn btn-danger" href="${pageContext.request.contextPath}/Account/Credential/${credential.id}/Delete" onclick="return deleteCredential('${credential.id}')" title='<spring:message code="label.action.delete" text="Delete"/>'><i class="fa fa-trash"></i></a>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
