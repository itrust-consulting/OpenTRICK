<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div id="customerusers">
	<jsp:include page="../../successErrors.jsp" />
	<form action="Update" name="usercustomer" id="customerusersform">
		<div>
			<input type="hidden" value="${customer.id}" name="customerid" />
			<spring:message code="label.users.customer.access" text="Users having access to customer: " />
			<b>${customer.organisation}</b>
			<hr />
			<p>
				<b>Note: press CTRL or COMMAND button to select multiple entries! On normal click: previous values are reset</b>
			</p>
		</div>
		<div class="panel-body">
			<c:choose>
				<c:when test="${!empty users}">
					<select multiple name="usercustomer" class="form-control">
						<c:forEach items="${users}" var="user">
							<option value="user_${user.id}" ${customerusers.contains(user) ? 'selected="selected"' : ""}>${user.firstName.concat(' ').concat(user.lastName)}</option>
						</c:forEach>
					</select>
				</c:when>
				<c:otherwise>
					<h4>
						<spring:message code="label.user.notexist" text="no users exist" />
					</h4>
				</c:otherwise>
			</c:choose>
		</div>
	</form>
</div>