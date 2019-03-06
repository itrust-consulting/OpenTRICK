<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<div id="section_credential">
	<ul class="nav nav-pills bordered-bottom" id="menu_credential">
			<li data-trick-ignored="true"><a href="#" onclick="return addCredential();"><span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.action.add" text="Add" /> </a></li>
			<li class="disabled" data-trick-selectable="true"><a href="#" onclick="return editCredential();"><span class="glyphicon glyphicon-edit danger"></span> <spring:message
						code="label.action.edit" text="Edit" /> </a></li>
			<li class="disabled pull-right" data-trick-selectable="true"><a href="#" class="text-danger" onclick="return deleteCredential();"><span class="glyphicon glyphicon-remove"></span>
					<spring:message code="label.action.delete" text="Delete" /> </a></li>
	</ul>
	<table class="table table-hover table-condensed">
		<thead>
			<tr>
				<th width="1%"></th>
				<th width="10%"><spring:message code="label.customer" text="Customer"/></th>
				<th width="10%"><spring:message code="label.type" text="Type"/></th>
				<th width="10%"><spring:message code="label.name" text="Name"/></th>
				<th><spring:message code="label.value" text="Value"/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${credentials}" var="credential">
				<tr data-trick-id="${credential.id}" onclick="selectElement(this)" ondblclick="return editCredential('${credential.id}');">
					<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_credential','#menu_credential');"></td>
					<td>
						<c:choose>
							<c:when test="${not empty credential['ticketingSystem']}">
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
							<c:when test="${fn:toLowerCase(credential.type) == 'token'}">
								<spring:message text="${credential.value}"/>
							</c:when>
							<c:otherwise>
								**************
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
