<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<div class="col-md-9 col-lg-10" id="section_invitation">
	<table class="table table-hover table-condensed">
		<thead>
			<tr>
				<th width="10%"><spring:message code="label.invitation.host" text="Host" /></th>
				<th width="8%"><spring:message code="label.analysis.customer" text="Customer" /></th>
				<th width="15%"><spring:message code="label.analysis.label" text="Name" /></th>
				<th width="5%"><spring:message code="label.analysis.version" text="Version" /></th>
				<th><spring:message code="label.analysis.description" text="Description" /></th>
				<th width="5%"><spring:message code="label.analysis.right" text="Access" /></th>
				<th width="10%" style="min-width: 100px;"><spring:message code="label.action" /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${invitations}" var="invitation">
				<tr data-trick-id="${invitation.id}">
					<td><spring:message text="${invitation.host.firstName} ${invitation.host.lastName}" /></td>
					<td><spring:message text="${invitation.analysis.customer.organisation}" /></td>
					<td><spring:message text="${invitation.analysis.label}" /></td>
					<td><spring:message text="${invitation.analysis.version}" /></td>
					<td data-trick-content='text'><spring:message text="${invitation.analysis.lastHistory.comment}" /></td>
					<td><spring:message code="label.analysis.right.${fn:toLowerCase(invitation.right)}" text="${fn:replace(invitation.right,'_', ' ')}" /></td>
					<td><div class='btn-group' style="display: inline-block;"><a class="btn btn-primary" href="${pageContext.request.contextPath}/Account/Invitation/${invitation.id}/Accept" onclick="return acceptInvitation('${invitation.id}')"
						title='<spring:message code="label.action.accept" />'><i class="fa fa-check-square-o"></i></a> <a class="btn btn-danger"
						href="${pageContext.request.contextPath}/Account/Invitation/${invitation.id}/Reject" onclick="return rejectInvitation('${invitation.id}')"
						title='<spring:message code="label.action.reject" />'><i class="fa fa-ban"></i></a></div> </td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
