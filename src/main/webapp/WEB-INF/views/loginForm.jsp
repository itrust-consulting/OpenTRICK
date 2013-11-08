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
	<div class="container">

		<div class="form-signin" id="login" style="margin: 0 auto;
    max-width: 330px;
    padding: 15px;">
    		
			<h2 class="form-signin-heading">
				<spring:message code="label.login.title" text="Sign in" />
			</h2>
			<a class="navbar-link pull-right" style="margin-top: -35px;" href="${pageContext.request.contextPath}/user/add">
					<spring:message code="label.signup" text="Sign up" />
			</a>
			<jsp:include page="successErrors.jsp" />
			<form method="post"
				action="<c:url value='${pageContext.request.contextPath}/j_spring_security_check'/>">
				<input name="j_username"
					value="${(!empty (j_username))? j_username : ''}"
					placeholder="<spring:message code='label.signin.login' />"
					class="form-control" />
					<input name="j_password"
					value="${(!empty (j_password))? j_password : ''}" type="password"
					class="form-control"
					placeholder="<spring:message code='label.signin.password' />" /> <label
					class="checkbox"><spring:message
						code="label.signin.rememberMe" text="Remember me" /> <input
					type='checkbox' name='_spring_security_remember_me' /></label>
				<button type="submit" class="btn btn-default navbar-btn">
					<spring:message code="label.signin.connect" text="Sign in" />
				</button>
			</form>
		</div>
		<jsp:include page="footer.jsp" />
	</div>
	<jsp:include page="scripts.jsp" />
</body>

<!-- ################################################################### End HTML ################################################################### -->

</html>