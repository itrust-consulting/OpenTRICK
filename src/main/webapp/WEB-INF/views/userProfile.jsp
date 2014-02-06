<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!-- ################################################################ Set Page Title ################################################################ -->
<c:set scope="request" var="title">title.Profile</c:set>
<!-- ###################################################################### HTML #################################################################### -->
<html>
<!-- Include Header -->
<jsp:include page="header.jsp" />
<!-- ################################################################# Start Container ############################################################## -->
<body>
	<div id="wrap">
		<!-- ################################################################### Nav Menu ################################################################### -->
		<jsp:include page="menu.jsp" />
		<div class="container">
			<jsp:include page="successErrors.jsp" />
			<!-- #################################################################### Content ################################################################### -->
			<div class="page-header">
				<h1>
					<spring:message code="title.Profile" text="Profile" />
				</h1>
			</div>
			<div class="content" id="content">
				<c:if test="${!empty userProfil}">
					<table class="data">
						<tr>
							<td><spring:message code="label.user.id" /></td>
							<td>${userProfil.id}</td>
						</tr>
						<tr>
							<td><spring:message code="label.user.login" /></td>
							<td>${userProfil.login}</td>
						</tr>
						<tr>
							<td><spring:message code="label.user.firstName" /></td>
							<td>${userProfil.firstName}</td>
						</tr>
						<tr>
							<td><spring:message code="label.user.lastName" /></td>
							<td>${userProfil.lastName}</td>
						</tr>
						<tr>
							<td><spring:message code="label.user.email" /></td>
							<td>${userProfil.email}</td>
						</tr>
						<tr>
							<td><spring:message code="label.user.roles" /></td>
							<td><c:forEach items="${userProfil.roles}" var="role">
									<spring:message code="label.role.${role.type}" />
								</c:forEach></td>
						</tr>
					</table>
				</c:if>
			</div>
			<!-- ################################################################ End Container ################################################################# -->
		</div>
		<!-- ################################################################ Include Footer ################################################################ -->
		<jsp:include page="footer.jsp" />
		<jsp:include page="scripts.jsp" />
	</div>
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>