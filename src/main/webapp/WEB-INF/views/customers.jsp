<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
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
	<table>
		<tr>
			<th>Commandes</th>
		</tr>
		<tr>
			<td><a href="${pageContext.request.contextPath}/index">Home</a></td>
		</tr>
		<tr>
			<td><a href="${pageContext.request.contextPath}/customer/add">Add
					customer</a></td>
		</tr>
	</table>
	<c:if test="${!empty customers}">
		<table class="data" border="1">
			<tr>
				<th><spring:message code="label.customer.contactPerson" /></th>
				<th><spring:message code="label.customer.organisation" /></th>
				<th><spring:message code="label.customer.action" /></th>
			</tr>
			<c:forEach items="${customers}" var="customer">
				<tr>
					<td><a
						href="${pageContext.request.contextPath}/customer/${customer.id}">${customer.contactPerson}</a></td>
					<td>${customer.organisation}</td>
					<td><a
						href="${pageContext.request.contextPath}/customer/delete/${customer.id}">delete</a></td>
				</tr>
			</c:forEach>
		</table>
	</c:if>

</body>
</html>