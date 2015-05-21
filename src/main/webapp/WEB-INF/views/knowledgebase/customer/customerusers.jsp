<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div id="customerusers">
	<jsp:include page="../../template/successErrors.jsp" />
	<form action="Update" name="usercustomer" id="customerusersform">
		<div>
			<input type="hidden" value="${customer.id}" name="customerid" />
			<spring:message code="label.users.customer.access" text="Users having access to customer" />:
			<b><spring:message text="${customer.organisation}"/></b>
			<hr />
		</div>
		<div class="panel-body" >
			<c:choose>
				<c:when test="${!empty users}">
					<select multiple id="usercustomer" name="usercustomer" class="form-control">
						<c:forEach items="${users}" var="user">
							<option value="user_${user.id}" ${customerusers.contains(user) ? 'selected="selected"' : ""}><spring:message text="${user.firstName}" />&nbsp;<spring:message text="${user.lastName}"/></option>
						</c:forEach>
					</select>
				</c:when>
				<c:otherwise>
					<h4>
						<spring:message code="label.user.empty" text="No user" />
					</h4>
				</c:otherwise>
			</c:choose>
		</div>
	</form>
</div>