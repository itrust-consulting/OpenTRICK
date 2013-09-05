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
	<a href="${pageContext.request.contextPath}/user/add">Register</a>
	<form method="post" action="<c:url value='j_spring_security_check'/>">
		<table class="data">
			<tr>
				<td><spring:message code="label.signin.login" /></td>
				<td><input name="j_username"
					value="${(!empty login)? login : ''}" /></td>
				<form:errors path="j_username" cssClass="error" element="td"/>
			</tr>
			<tr>
				<td><spring:message code="label.signin.password" /></td>
				<td><input name="j_password"
					value="${(!empty password)? password : ''}" type="password" /></td>
				<form:errors path="j_password" cssClass="error" element="td"/>
			</tr>
			<tr>
				<td><spring:message code="label.signin.rememberMe" /></td>
				<td><input type='checkbox' name='_spring_security_remember_me' /></td>
			</tr>
			<tr>
				<td colspan="2"><input type="submit"
					value="<spring:message code="label.signin.connect"/>" /></td>
			</tr>
		</table>
	</form>
</body>
</html>