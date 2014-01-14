<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<!-- ################################################################ Set Page Title ################################################################ -->

<c:set scope="request" var="title">label.signin.login</c:set>

<!-- ###################################################################### HTML #################################################################### -->

<html>

<!-- Include Header -->
<jsp:include page="header.jsp" />


<!-- ################################################################# Start Container ############################################################## -->

<body>
	<div id="wrap">
		<div class="container">

			<div class="form-signin" id="login"
				style="margin: 0 auto; max-width: 330px; padding: 15px;">

				<h2 class="form-signin-heading">
					<spring:message code="label.login.title" text="Sign in" />
				</h2>
				<a class="navbar-link pull-right" style="margin-top: -35px;"
					href="${pageContext.request.contextPath}/register"> <spring:message
						code="label.signup" text="Sign up" />
				</a>
				<jsp:include page="successErrors.jsp" />
				<form method="post"
					action="<c:url value='${pageContext.request.contextPath}/j_spring_security_check'/>">
					<div class="form-group">
						<input name="j_username"
							value="${(!empty (j_username))? j_username : ''}"
							placeholder="<spring:message code='label.signin.login' />" required="required"
							class="form-control" />
					</div>
					<div class="form-group">
						<input name="j_password"
							value="${(!empty (j_password))? j_password : ''}" type="password"
							class="form-control"
							placeholder="<spring:message code='label.signin.password' />" required="required"/>
					</div>
					<div class="checkbox">
						<label><spring:message code="label.signin.rememberMe"
								text="Remember me" /> <input type='checkbox'
							name='_spring_security_remember_me' /></label>
					</div>
					<div class="form-group">
						<button type="submit" class="btn btn-default navbar-btn">
							<spring:message code="label.signin.connect" text="Sign in" />
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