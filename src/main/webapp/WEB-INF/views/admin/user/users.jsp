<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="tab-pane" id="tab-user">
	<div class="section" id="section_user">
		<c:if test="${!empty users}">
			<ul class="nav nav-pills bordered-bottom" id="menu_user">
				<li><a href="#" onclick="return newUser();"><span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.action.add" text="Add" /> </a></li>
				<li class="disabled" data-trick-selectable="true"><a href="#" onclick="return editSingleUser();"><span class="glyphicon glyphicon-edit danger"></span> <spring:message
							code="label.action.edit" text="Edit" /> </a></li>
				<li class="disabled pull-right" data-trick-selectable="true"><a href="#" class="text-danger" onclick="return deleteUser();"><span class="glyphicon glyphicon-remove"></span>
						<spring:message code="label.action.delete" text="Delete" /> </a></li>
			</ul>
			<table class="table table-hover table-condensed">
				<thead>
					<tr>
						<th></th>
						<th><spring:message code="label.user.login" text="Username" /></th>
						<th><spring:message code="label.user.first_name" text="Firstname" /></th>
						<th><spring:message code="label.user.last_name" text="Lastname" /></th>
						<th><spring:message code="label.user.email" text="Email address" /></th>
						<th class='text-center' ><spring:message code="label.user.account.status" text="Status" /></th>
						<c:if test="${enabledOTP}">
							<th class='text-center'><spring:message code="label.user.account.otp" text="OTP" /></th>
						</c:if>
						<th class='text-center'><spring:message code="label.user.connexion.type" text="Authentication type" /></th>
						<th><spring:message code="label.user.account.role" text="Roles" /></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${users}" var="user">
						<tr class="${enabledOTP and not  user.using2FA? 'warning' : ''}" data-trick-id="${user.id}" onclick="selectElement(this)" ondblclick="return editSingleUser(${user.id});">
							<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_user','#menu_user');"></td>
							<td><spring:message text="${user.login}" /></td>
							<td><spring:message text="${user.firstName}" /></td>
							<td><spring:message text="${user.lastName}" /></td>
							<td><spring:message text="${user.email}" /></td>
							<td class='text-center' ><spring:message code="label.user.account.state_${fn:toLowerCase(user.enable)}" text="${user.enable?'Enabled':'Disabled'}" /></td>
							<c:if test="${enabledOTP}">
								<td class='text-center'><spring:message code="label.action.${user.using2FA?'enable':'disable'}" text="${user.using2FA?'Enabled':'Disabled'}" /></td>
							</c:if>
							<td data-trick-real-value="${user.connexionType}" class='text-center' ><c:choose>
									<c:when test="${user.connexionType == -1}">
										<spring:message code="label.user.connexion.standard" text="Standard" />
									</c:when>
									<c:when test="${user.connexionType == 0}">
										<spring:message code="label.user.connexion.both" text="Both" />
									</c:when>
									<c:when test="${user.connexionType == 1}">
										<spring:message code="label.user.connexion.ldap" text="LDAP" />
									</c:when>
								</c:choose></td>
							<td><c:forEach items="${user.roles}" var="role">
									<c:set var="role_value" value="${fn:replace(role.type,'ROLE_','')}" />
									<div style="padding: 2px 4px; border: 1px solid #dddddd; text-align: center; border-radius: 2px; background-color: #eeeeee; display: inline-block;">
										<spring:message code="label.role.${fn:toLowerCase(role_value)}" text="${role_value}" />
									</div>
								</c:forEach></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:if>
		<c:if test="${empty users}">
			<button class="btn btn-default" onclick="newUser();">
				<spring:message code="label.menu.action.add" text="Add new user" />
			</button>
			<h4>
				<spring:message code="label.user.empty" text="No users exist" />
			</h4>
		</c:if>
	</div>
</div>