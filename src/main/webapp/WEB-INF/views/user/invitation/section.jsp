<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="col-md-9" id="section_invitation">
	<table class="table table-hover table-condensed">
		<thead>
			<tr>
				<th><spring:message code="label.invitation.host" text="Host" /></th>
				<th><spring:message code="label.analysis.identifier" text="TRICK name" /></th>
				<th><spring:message code="label.analysis.label" text="Name" /></th>
				<th><spring:message code="label.analysis.version" text="Version" /></th>
				<th><spring:message code="label.analysis.right" text="Access" /></th>
				<th><spring:message code="label.action" /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${invitations}" var="invitation">
				<tr data-trick-id="${invitation.id}">
					<td><spring:message text="${invitation.host.firstName} ${invitation.host.lastName}" /></td>
					<td><spring:message text="${invitation.analysis.identifier}" /></td>
					<td><spring:message text="${invitation.analysis.label}" /></td>
					<td><spring:message text="${invitation.analysis.version}" /></td>
					<td><spring:message code="label.analysis.right.${fn:toLowerCase(invitation.right)}" text="${fn:replace(invitation.right,'_', ' ')}" /></td>
					<td><a class="btn btn-primary" href="${pageContext.request.contextPath}/Account/Invitation/${invitation.id}/Accept" onclick="return acceptInvitation('${invitation.id}')"
						title='<spring:message code="label.action.accept" />'><i class="fa fa-check-square-o"></i></a> <a class="btn btn-danger"
						href="${pageContext.request.contextPath}/Account/Invitation/${invitation.id}/Reject" onclick="return rejectInvitation('${invitation.id}')"
						title='<spring:message code="label.action.reject" />'><i class="fa fa-ban"></i></a></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
