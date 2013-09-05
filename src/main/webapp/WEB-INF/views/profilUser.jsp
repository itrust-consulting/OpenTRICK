<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html>
<head>
<title>Trick Service</title>
<style >
.error {
	color: #ff0000;
}
</style>
</head>
<body>
	<form:errors cssClass="error" element="div"/>
	<a href="${pageContext.request.contextPath}/index">Home</a>
	<c:if test="${!empty userProfil}">
		<table class="data" border="1">
			<tr>
				<td><spring:message code="label.user.id" /></td>
				<td>${userProfil.id}</td>
			</tr>
			<tr>
				<td><spring:message code="label.user.login" /></td>
				<td>${userProfil.login}</td>
			</tr>
			<tr>
				<td><spring:message code="label.user.firstName" /></td>
				<td>${userProfil.firstName}</td>
			</tr>
			<tr>
				<td><spring:message code="label.user.lastName" /></td>
				<td>${userProfil.lastName}</td>
			</tr>
			<tr>
				<td><spring:message code="label.user.country" /></td>
				<td>${userProfil.country}</td>
			</tr>
			<tr>
				<td><spring:message code="label.user.email" /></td>
				<td>${userProfil.email}</td>
			</tr>
		</table>
	</c:if>
</body>
</html>