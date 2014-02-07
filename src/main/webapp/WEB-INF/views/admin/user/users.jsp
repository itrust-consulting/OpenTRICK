<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="section" id="section_user">
	<div class="page-header">
		<h3 id="Users">
			<spring:message code="menu.admin.user" />
		</h3>
	</div>
	<c:if test="${!empty users}">
		<div class="panel panel-default">
			<div class="panel-heading" style="min-height: 60px">
				<ul class="nav nav-pills" id="menu_user">
					<li><a href="#" onclick="return newUser();"><span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.action.add" text="Add" /> </a></li>
					<li class="disabled" trick-selectable="true"><a href="#" onclick="return editSingleUser();"><span class="glyphicon glyphicon-edit danger"></span> <spring:message
								code="label.action.edit" text="Edit" /> </a></li>
					<li class="disabled" trick-selectable="true"><a href="#" onclick="return deleteUser();"><span class="glyphicon glyphicon-remove"></span> <spring:message
								code="label.action.delete" text="Delete" /> </a></li>
				</ul>
			</div>
			<div class="panel-body">
				<table class="table">
					<thead>
						<tr>
							<th><input type="checkbox" class="checkbox" onchange="return checkControlChange(this,'user')"></th>
							<th><spring:message code="label.user.login" /></th>
							<th><spring:message code="label.user.firstName" /></th>
							<th><spring:message code="label.user.lastName" /></th>
							<th><spring:message code="label.user.email" /></th>
							<th><spring:message code="label.user.enabled" /></th>
							<th><spring:message code="label.role" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${users}" var="user">
							<tr trick-id="${user.id}" ondblclick="return editSingleUser(${user.id});">
								<td><input type="checkbox" class="checkbox" onchange="return updateMenu('#section_user','#menu_user');"></td>
								<td>${user.login}</td>
								<td>${user.firstName}</td>
								<td>${user.lastName}</td>
								<td>${user.email}</td>
								<td><spring:message code="label.user.enable.${user.enable}" /></td>
								<td><c:forEach items="${user.roles}" var="role">
										<spring:message code="label.role.${role.type}" />
									</c:forEach></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</c:if>
	<c:if test="${empty users}">
		<div class="panel panel-default">
			<div class="panel-heading">
				<button class="btn btn-default" onclick="newUser();">
					<spring:message code="label.user.add.menu" text="Add new User" />
				</button>
			</div>
			<div class="panel-body">
				<h4>
					<spring:message code="label.user.notexist" />
				</h4>
			</div>
		</div>
	</c:if>
</div>