<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>

<!-- ################################################################ Set Page Title ################################################################ -->

<c:set scope="request" var="title">title.register</c:set>

<!-- ###################################################################### HTML #################################################################### -->

<html>

<!-- ##################################################################### Header ################################################################### -->
<jsp:include page="header.jsp" />


<!-- ################################################################# Start Container ############################################################## -->

<body>
<div class="container">

<!-- #################################################################### Content ################################################################### -->

<div class="content" id="content">

	<h1><spring:message code="title.user.register" /></h1>
	<a href="../login"><spring:message code="menu.navigate.back" /></a>
	<form:errors cssClass="error" element="div" />
	<form:form method="post" action="${pageContext.request.contextPath}/user/save"	commandName="user">
		<spring:hasBindErrors name="*" />
		<table>
			<tr>
				<td><form:label path="login">
						<spring:message code="label.user.login" />
					</form:label></td>
				<td><form:input path="login" /></td>
				<form:errors path="login" cssClass="error" element="td" />
			</tr>
			<tr>
				<td><form:label path="password">
						<spring:message code="label.user.password" />
					</form:label></td>
				<td><form:password path="password" /></td>
				<form:errors path="password" cssClass="error" element="td" />
			</tr>
			<tr>
				<td><form:label path="firstName">
						<spring:message code="label.user.firstName" />
					</form:label></td>
				<td><form:input path="firstName" /></td>
				<form:errors path="firstName" cssClass="error" element="td" />
			</tr>
			<tr>
				<td><form:label path="lastName">
						<spring:message code="label.user.lastName" />
					</form:label></td>
				<td><form:input path="lastName" /></td>
				<form:errors path="lastName" cssClass="error" element="td" />
			</tr>
			<tr>
				<td><form:label path="email">
						<spring:message code="label.user.email" />
					</form:label></td>
				<td><form:input path="email" /></td>
				<form:errors path="email" cssClass="error" element="td" />
			</tr>
			<tr>
				<td colspan="2"><input type="submit"
					value="<spring:message code="label.user.submit"/>" /></td>
			</tr>
		</table>
	</form:form>
</div>
		
<!-- ################################################################ Include Footer ################################################################ -->

<jsp:include page="footer.jsp" />

<!-- ################################################################ End Container ################################################################# -->

</div>
</body>

<!-- ################################################################### End HTML ################################################################### -->

</html>