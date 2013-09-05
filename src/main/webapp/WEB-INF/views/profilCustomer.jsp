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
	<c:if test="${!empty customerProfil}">
		<table class="data" border="1">
			<tr>
				<td><spring:message code="label.customer.id" /></td>
				<td>${customerProfil.id}</td>
			</tr>
			<tr>
				<td><spring:message code="label.customer.contactPerson" /></td>
				<td>${customerProfil.contactPerson}</td>
			</tr>
			<tr>
				<td><spring:message code="label.customer.address" /></td>
				<td>${customerProfil.organisation}</td>
			</tr>
			<tr>

				<td><spring:message code="label.customer.city" /></td>
				<td>${customerProfil.address}</td>

			</tr>
			<tr>
				<td><spring:message code="label.customer.ZIPCode" /></td>
				<td>${customerProfil.city}</td>
			</tr>
			<tr>
				<td><spring:message code="label.customer.city" /></td>
				<td>${customerProfil.ZIPCode}</td>
			</tr>
			<tr>
				<td><spring:message code="label.customer.country" /></td>
				<td>${customerProfil.country}</td>
			</tr>
			<tr>
				<td><spring:message code="label.customer.telephoneNumber" /></td>
				<td>${customerProfil.telephoneNumber}</td>
			</tr>
			<tr>
				<td><spring:message code="label.customer.email" /></td>
				<td>${customerProfil.email}</td>
			</tr>
		</table>
	</c:if>
</body>
</html>