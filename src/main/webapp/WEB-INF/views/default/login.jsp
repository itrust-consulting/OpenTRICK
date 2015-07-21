<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!-- ################################################################ Set Page Title ################################################################ -->
<c:set scope="request" var="title">label.title.signin</c:set>
<!-- ###################################################################### HTML #################################################################### -->
<!DOCTYPE html>
<html>
<!-- Include Header -->
<jsp:include page="../template/header.jsp" />
<!-- ################################################################# Start Container ############################################################## -->
<body>
	<div id="wrap">
		<div class="container">
			<img class="center-block" alt=<spring:message code="label.logo" text="Logo" /> src=<spring:url value="/data/TrickService.png" /> style="height: 200px;">
			<div class="form-signin" id="login" style="margin: 0 auto; max-width: 330px;">
				<h2 class="form-signin-heading">
					<spring:message code="label.title.login" text="Sign in" />
				</h2>
				<a class="navbar-link pull-right" style="margin-top: -30px;" href="${pageContext.request.contextPath}/Register"> <spring:message code="label.signup" text="Sign up" />
				</a>

				<c:if test="${!empty(sessionScope.LOGIN_ERROR)}">
					<c:set var="error" value="${sessionScope.LOGIN_ERROR}" scope="request" />
					<c:remove var="LOGIN_ERROR" scope="session" />
				</c:if>

				<jsp:include page="../template/successErrors.jsp" />
				<form id="login_form" method="post" action="${pageContext.request.contextPath}/signin?${_csrf.parameterName}=${_csrf.token}">
					<div class="form-group">
						<input id="username" name="username" value="${(!empty (username))? username : ''}" placeholder="<spring:message code='label.signin.login' text='Username'/>"
							required="required" class="form-control" pattern="^([a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð_0-9]+[.]?){1,4}" />
					</div>
					<div class="form-group">
						<input name="password" value="${(!empty (password))? password : ''}" type="password" class="form-control"
							placeholder="<spring:message code='label.signin.password' text='Password' />" required="required" />
					</div>
					<div class="form-group">
						<a class="navbar-link pull-right" href="${pageContext.request.contextPath}/ResetPassword"> <spring:message code="label.reset.password" text="Reset password" /></a>
					</div>
					<div class="form-group">
						<button type="submit" id="login_signin_button" class="btn btn-danger navbar-btn" style="width: 100%;">
							<spring:message code="label.action.signin" text="Sign in" />
						</button>
					</div>
				</form>
			</div>
		</div>
		<jsp:include page="../template/footer.jsp" />
		<jsp:include page="../template/scripts.jsp" />
		<script type="text/javascript">
			$("input[name='j_username']").focus();
		</script>
	</div>
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>
