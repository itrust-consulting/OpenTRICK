<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<html>
<head>
<title>All analysis</title>
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
			<jsp:include page="menu.jsp" />
		</div>
		<div class="content" id="content">
			
	<form:errors cssClass="error" element="div"/>
<form:form method="post" action="${pageContext.request.contextPath}/customer/save.html" commandName="customer">
 
    <table border="1">
    <tr>
        <td><form:label path="contactPerson"><spring:message code="label.customer.contactPerson"/></form:label></td>
        <td><form:input path="contactPerson" /></td>
    </tr>
    <tr>
        <td><form:label path="organisation"><spring:message code="label.customer.organisation"/></form:label></td>
        <td><form:input path="organisation" /></td>
    </tr>
     <tr>
        <td><form:label path="address"><spring:message code="label.customer.address"/></form:label></td>
        <td><form:input path="address" /></td>
    </tr>
     <tr>
        <td><form:label path="city"><spring:message code="label.customer.city"/></form:label></td>
        <td><form:input path="city" /></td>
    </tr>
     <tr>
        <td><form:label path="ZIPCode"><spring:message code="label.customer.ZIPCode"/></form:label></td>
        <td><form:input path="ZIPCode" /></td>
    </tr>
     <tr>
        <td><form:label path="country"><spring:message code="label.customer.country"/></form:label></td>
        <td><form:input path="country" /></td>
    </tr>
     <tr>
        <td><form:label path="telephoneNumber"><spring:message code="label.customer.telephoneNumber"/></form:label></td>
        <td><form:input path="telephoneNumber" /></td>
    </tr>
     <tr>
        <td><form:label path="email"><spring:message code="label.customer.email"/></form:label></td>
        <td><form:input path="email" /></td>
    </tr>
    <tr>
        <td colspan="2">
            <input type="submit" value="<spring:message code="label.addCustomer"/>"/>
        </td>
    </tr>
</table> 
</form:form>
</div>
		<div class="footer">
			<jsp:include page="footer.jsp" />
		</div>
	</div>
</body>
</html>
</body>
</html>