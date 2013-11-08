<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<html>
<head>
<title>TRICK Service</title>
<link rel="stylesheet" type="text/css"
	href='<spring:url value="/css/main.css" />' />
<style>
.error {
	color: #ff0000;
}
</style>
</head>
<body>
	<div class="container">
		<div class="menu">
			<jsp:include page="../menu.jsp" />
		</div>
		<div class="content" id="content">
			<form:errors cssClass="error" element="div" />
	
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
</div>
		<div class="footer">
			<jsp:include page="../footer.jsp" />
		</div>
	</div>
	<jsp:include page="../scripts.jsp" />
</body>
</html>