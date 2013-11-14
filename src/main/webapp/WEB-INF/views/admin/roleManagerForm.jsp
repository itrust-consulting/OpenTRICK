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
</head>
<body>
	<div id="wrap">
		<div class="menu">
			<jsp:include page="../menu.jsp" />
		</div>
		<div class="container">
			<div class="content" id="content">
				<form:errors cssClass="error" element="div" />
				<c:if test="${!empty userManageRole}">
					<table class="table">
						<tr>
							<th><spring:message code="label.role" /></th>
							<th><spring:message code="label.action" /></th>
						</tr>
						<c:forEach items="${userManageRole.roles}" var="role">
							<tr>
								<td><spring:message code="label.role.${role.type}" /></td>
								<td><a
									href="${pageContext.request.contextPath}/role/delete/${role.id}/user/${userManageRole.id}"><spring:message
											code="label.action.delete" /></a></td>
							</tr>
						</c:forEach>
					</table>
					<form:form method="post"
						action="${pageContext.request.contextPath}/role/add/user/${userManageRole.id}"
						commandName="userRole">
						<form:select path="type">
							<c:forEach items="${roles}" var="type">
								<option value="${type}"
									label="<spring:message code="label.role.${type}" />">
									<spring:message code="label.role.${type}" />
								</option>
							</c:forEach>
						</form:select>
						<input type="submit"
							value="<spring:message code="label.action.addRole"/>" />
					</form:form>
				</c:if>
			</div>
		</div>
		<jsp:include page="../footer.jsp" />
		<jsp:include page="../scripts.jsp" />
	</div>
</body>
</html>