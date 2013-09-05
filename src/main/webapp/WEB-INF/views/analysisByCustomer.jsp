<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<html>
<head>
<title><spring:message code="label.importAnalysis.title" /></title>
<style >
.error {
	color: #ff0000;
}
</style>
</head>
<body>
	<form:errors cssClass="error" element="div"/>
	<a href="${pageContext.request.contextPath}/index">Home</a>
	<c:if test="${!empty customers}">
		<table>
		<tr><th>Customers</th></tr>
		
		<c:forEach items="${customers}" var="customer">
			<tr><td>
			<a href="${pageContext.request.contextPath}/analysis/customer/${customer.id}">${customer.contactPerson}</a>
			</td>
			</tr>
		</c:forEach>
		
		</table>
	</c:if>
</body>
</html>