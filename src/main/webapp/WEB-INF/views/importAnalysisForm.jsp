<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
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
		<form method="post" action="${pageContext.request.contextPath}/import/analysis/save.html"
			enctype="multipart/form-data">
			<form:select name="customerId" path="customerId">
				<form:option value="-1">Select the customer</form:option>
				<form:options items="${customers}" itemLabel="contactPerson" itemValue="id"/>
			</form:select>
			<input type="file" name="file" />
			<input type="submit" />
		</form>
	</c:if>
</body>
</html>