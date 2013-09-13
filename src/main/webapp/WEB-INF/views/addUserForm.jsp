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
			<jsp:include page="menu.jsp" />
		</div>
		<div class="content" id="content">
			<form:errors cssClass="error" element="div" />
	<form:form method="post"
		action="${pageContext.request.contextPath}/user/save.html"
		commandName="user">

			<spring:hasBindErrors name="*"  />
		<table>
			<tr>
				<td><form:label path="login">
						<spring:message code="label.user.login" />
					</form:label></td>
				<td><form:input path="login" /></td>
				<form:errors path="login" cssClass="error" element="td"/>
			</tr>
			<tr>
				<td><form:label path="password">
						<spring:message code="label.user.password" />
					</form:label></td>
				<td><form:password path="password" /></td>
				<form:errors path="password" cssClass="error" element="td"/>
			</tr>
			<tr>
				<td><form:label path="firstName">
						<spring:message code="label.user.firstName" />
					</form:label></td>
				<td><form:input path="firstName" /></td>
				<form:errors path="firstName" cssClass="error" element="td"/>
			</tr>
			<tr>
				<td><form:label path="lastName">
						<spring:message code="label.user.lastName" />
					</form:label></td>
				<td><form:input path="lastName" /></td>
				<form:errors path="lastName" cssClass="error" element="td"/>
			</tr>
			<tr>
				<td><form:label path="country">
						<spring:message code="label.user.country" />
					</form:label></td>
				<td><form:input path="country" /></td>
				<form:errors path="country" cssClass="error" element="td"/>
			</tr>
			<tr>
				<td><form:label path="email">
						<spring:message code="label.user.email" />
					</form:label></td>
				<td><form:input path="email" /></td>
				<form:errors path="email" cssClass="error" element="td"/>
			</tr>
			<tr>
				<td colspan="2"><input type="submit"
					value="<spring:message code="label.action.save"/>" /></td>
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