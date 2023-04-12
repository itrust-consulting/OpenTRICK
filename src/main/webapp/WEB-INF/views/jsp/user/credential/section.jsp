<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<div id="section_credential">
	<ul class="nav nav-pills bordered-bottom" id="menu_credential">
		<li data-trick-ignored="true"><a href="#" onclick="return addCredential();"><span class="glyphicon glyphicon-plus primary"></span> <spring:message
					code="label.action.add" text="Add" /> </a></li>
		<li class="disabled" data-trick-selectable="true"><a href="#" onclick="return editCredential();"><span class="glyphicon glyphicon-edit danger"></span> <spring:message
					code="label.action.edit" text="Edit" /> </a></li>
		<li class="disabled pull-right" data-trick-selectable="true"><a href="#" class="text-danger" onclick="return deleteCredential();"><span
				class="glyphicon glyphicon-remove"></span> <spring:message code="label.action.delete" text="Delete" /> </a></li>
	</ul>
	<table class="table table-hover table-condensed">
		<thead>
			<tr>
				<th width="1%" rowspan="2"></th>
				<th width="7%" rowspan="2"><spring:message code="label.type" /></th>
				<th width="10%" rowspan="2"><spring:message code="label.username" /></th>
				<th rowspan="2"><spring:message code="label.credential.secret" /></th>
				<th width="50%" class='text-center' colspan="4"><spring:message code="label.ticketing.system" /></th>
			</tr>
			<tr>
				<th width="8%"><spring:message code="label.customer" /></th>
				<th width="8%"><spring:message code="label.type" /></th>
				<th width="8%"><spring:message code="label.name" /></th>
				<th width="20%"><spring:message code="label.url" /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${credentials}" var="credential">
				<tr data-trick-id="${credential.id}" onclick="selectElement(this)" ondblclick="return editCredential('${credential.id}');">
					<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_credential','#menu_credential');"></td>
					<td><spring:message code="label.credential.type.${fn:toLowerCase(credential.type)}" text="${credential.type}" /></td>
					<c:choose>
						<c:when test="${fn:toLowerCase(credential.type) == 'token'}">
							<td>-</td>
							<td><spring:message text="${credential.value}" /></td>
						</c:when>
						<c:otherwise>
							<td><spring:message text="${credential.name}" /></td>
							<td>**************</td>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${not empty credential['ticketingSystem']}">
							<td><spring:message text="${credential.ticketingSystem.customer.organisation}" /></td>
							<td><spring:message code="label.ticketing.system.type.${fn:toLowerCase(credential.ticketingSystem.type)}" /></td>
							<td><spring:message text="${credential.ticketingSystem.name}" /></td>
							<td><spring:message text="${credential.ticketingSystem.url}" var="url" /> <a href="${url}" target="_blank">${url}</a></td>
						</c:when>
						<c:otherwise>
							<td colspan="4" />
						</c:otherwise>
					</c:choose>

				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
