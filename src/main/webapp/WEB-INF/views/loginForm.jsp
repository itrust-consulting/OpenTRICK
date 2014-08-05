<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!-- ################################################################ Set Page Title ################################################################ -->
<c:set scope="request" var="title">label.title.signin</c:set>
<!-- ###################################################################### HTML #################################################################### -->
<html>
<!-- Include Header -->
<jsp:include page="header.jsp" />
<!-- ################################################################# Start Container ############################################################## -->
<body>
	<div id="wrap">
		<div class="container">
			<img class="center-block" alt=<spring:message code="label.logo" text="Logo" /> src=<spring:url value="/data/TrickService.png" /> style="height: 200px; width auto;">
			<div class="form-signin" id="login" style="margin: 0 auto; max-width: 330px;">
				<h2 class="form-signin-heading">
					<spring:message code="label.title.login" text="Sign in" />
				</h2>
				<a class="navbar-link pull-right" style="margin-top: -35px;" href="${pageContext.request.contextPath}/Register"> <spring:message code="label.signup" text="Sign up" />
				</a>
				<jsp:include page="successErrors.jsp" />
				<form id="login_form" method="post" action="<c:url value='${pageContext.request.contextPath}/j_spring_security_check'/>">
					<div class="form-group">
						<input name="j_username" value="${(!empty (j_username))? j_username : ''}" placeholder="<spring:message code='label.signin.login' text='Username'/>" required="required"
							class="form-control" />
					</div>
					<div class="form-group">
						<input name="j_password" value="${(!empty (j_password))? j_password : ''}" type="password" class="form-control"
							placeholder="<spring:message code='label.signin.password' text='Password' />" required="required" />
					</div>
					<!-- <div class="checkbox">
						<label><spring:message code="label.signin.rememberMe" text="Remember me" /> <input type='checkbox' name='_spring_security_remember_me' /></label>
					</div> -->
					<div class="form-group">
						<button type="submit" id="login_signin_button" class="btn btn-default navbar-btn">
							<spring:message code="label.action.signin" text="Sign in" />
						</button>
					</div>
				</form>

			</div>
		</div>
		<jsp:include page="footer.jsp" />
		<jsp:include page="scripts.jsp" />
	</div>
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>
