<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<html>
<head>
<title>Trick Service</title>
<style>
.error {
	color: #ff0000;
}
</style>
</head>
<body>
	<form:errors cssClass="error" element="div" />
	<a href="${pageContext.request.contextPath}/index">Home</a>
	<c:if test="${!empty users}">
		<table class="data" border="1">
			<tr>
				<th><spring:message code="label.user.firstName" /></th>
				<th><spring:message code="label.user.lastName" /></th>
				<th><spring:message code="label.user.enabled" /></th>
				<th><spring:message code="label.role" /></th>
				<th><spring:message code="label.user.action" /></th>
			</tr>
			<c:forEach items="${users}" var="user">
				<tr>
					<td><a
						href="${pageContext.request.contextPath}/user/${user.id}">${user.firstName}</a></td>
					<td>${user.lastName}</td>
					<td><spring:message code="label.user.enable.${user.enable}" />
					</td>
					<td><c:forEach items="${user.roles}" var="role">
							<spring:message code="label.role.${role.type}" />
						</c:forEach></td>
					<td><a
						href="${pageContext.request.contextPath}/user/${user.id}/delete"><spring:message
								code="label.action.delete" /></a> <a
						href="${pageContext.request.contextPath}/role/manage/user/${user.id}"><spring:message
								code="label.role.manage" /></a></td>
				</tr>
			</c:forEach>
		</table>
	</c:if>
</body>
</html>