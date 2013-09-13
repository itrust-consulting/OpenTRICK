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
	
	<c:if test="${!empty customer}">
		<c:if test="${!empty analysis}">
			<form:form method="post"
				action="${pageContext.request.contextPath}/analysis/edit/${analysis.id}/save.html"
				commandName="analysis">
				<table class="data" border="1">
					<tr>
						<td><form:label path="identifier">
								<spring:message code="label.analysis.identifier" />
							</form:label></td>

						<td><form:input path="identifier" /></td>
					</tr>

					<tr>
						<td><form:label path="version">
								<spring:message code="label.analysis.version" />
							</form:label></td>
						<td><form:input path="version" /></td>
					</tr>

					<tr>
						<td><form:label path="creationDate">
								<spring:message code="label.analysis.creationDate" />
							</form:label></td>
						<td><form:input path="creationDate" /></td>
					</tr>

					<tr>
						<td><form:label path="language.id">
								<spring:message code="label.analysis.language" />
							</form:label>
						<td><form:select path="language.id"
								itemLabel="${language.alpha3}">
								<form:options items="${languages}" itemLabel="alpha3"
									itemValue="id" />
							</form:select></td>
					</tr>
					<tr>
						<td><form:label path="label">
								<spring:message code="label.analysis.label" />
							</form:label></td>
						<td><form:input path="label" /></td>
					</tr>
					<tr>
						<td colspan="2"><input type="submit"
							value="<spring:message code="label.action.save"/>" /></td>
					</tr>
				</table>
			</form:form>
		</c:if>
	</c:if>
</div>
		<div class="footer">
			<jsp:include page="footer.jsp" />
		</div>
	</div>
</body>
</html>