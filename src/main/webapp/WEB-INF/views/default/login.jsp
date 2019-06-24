<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!-- ###################################################################### HTML #################################################################### -->
<c:if test="${empty locale }">
	<spring:eval expression="T(org.springframework.web.servlet.support.RequestContextUtils).getLocale(pageContext.request)" var="locale" scope="request" />
</c:if>
<!DOCTYPE html>
<html lang="${locale.language}">
<!-- Include Header -->
<c:set scope="request" var="title">label.title.signin</c:set>
<jsp:include page="../template/header.jsp" />
<!-- ################################################################# Start Container ############################################################## -->
<body>
	<div id="wrap">
		<div class="container">
			<img class="center-block" alt=<spring:message code="label.logo" text="Logo" /> src='<c:url value="/images/TrickService.png" />' style="height: 200px;">
			<div class="form-signin" id="login" style="margin: 0 auto; max-width: 330px;">
				<h2 class="form-signin-heading">
					<spring:message code="label.title.login" text="Sign in" />
				</h2>
				<c:if test="${allowRegister}">
					<a class="navbar-link pull-right" style="margin-top: -30px;" href="${pageContext.request.contextPath}/Register"> <spring:message code="label.signup" text="Sign up" />
					</a>
				</c:if>
				<c:if test="${!empty(sessionScope.LOGIN_ERROR)}">
					<c:set var="error" value="${sessionScope.LOGIN_ERROR}" scope="request" />
					<c:remove var="LOGIN_ERROR" scope="session" />
				</c:if>
				<c:if test="${!empty sessionScope.LOGIN_ERROR_EXCEPTION}">
					<c:set var="errorTRICKException" scope="request" value="${sessionScope.LOGIN_ERROR_EXCEPTION}" />
					<c:remove var="LOGIN_ERROR_EXCEPTION" scope="session" />
				</c:if>
				<c:if test="${!empty sessionScope.LOGIN_ERROR_HANDLER}">
					<c:set var="errorHANDLER" scope="request" value="${sessionScope.LOGIN_ERROR_HANDLER}" />
					<c:remove var="LOGIN_ERROR_HANDLER" scope="session" />
				</c:if>
				<jsp:include page="../template/successErrors.jsp" />
				<form id="login_form" method="post" action="${pageContext.request.contextPath}/signin">
					<div class="form-group">
						<input id="username" name="username" autofocus="autofocus" value="${(!empty (username))? username : ''}"
							placeholder="<spring:message code='label.signin.login' text='Username'/>" required="required" class="form-control"
							pattern="^([a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð_0-9]+[.]?){1,4}" />
					</div>
					<div class="form-group">
						<input name="password" value="${(!empty (password))? password : ''}" type="password" class="form-control"
							placeholder="<spring:message code='label.signin.password' text='Password' />" required="required" />
					</div>
					<c:if test="${resetPassword}">
						<div class="form-group">
							<a class="navbar-link pull-right" href="${pageContext.request.contextPath}/ResetPassword"> <spring:message code="label.reset.password" text="Reset password" /></a>
						</div>
					</c:if>
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<div class="form-group">
						<button type="submit" id="login_signin_button" class="btn btn-danger navbar-btn" style="width: 100%;">
							<spring:message code="label.action.signin" text="Sign in" />
						</button>
						<a href="${pageContext.request.contextPath}" id="login_reload_button" title='<spring:message code="label.info.reload.token.expired"/>' class="btn btn-primary navbar-btn"
							style="width: 100%; display: none"> <spring:message code="label.action.reload" text="Reload" />
						</a>
					</div>
				</form>
			</div>
		</div>
		
		<jsp:include page="../template/footer.jsp" />
		<jsp:include page="../template/scripts.jsp" />
		<script type="text/javascript">
		<!--
			setTimeout(function() {
				var $errorMessage = $("<div class='alert alert-danger'><a href='#' class='close' data-dismiss='alert' style='margin-right: -10px; margin-top: -12px'>×</a> "
						+ '<spring:message code="label.info.reload.token.expired"/>' + " </div>"), $error = $("#error");
				if ($error.length)
					$error.replaceWith($errorMessage);
				else
					$("#login_form").before($errorMessage);
				$('#login_form button,input').prop("disabled", true);
				$('#login_reload_button').show();
				$("#login_signin_button").hide();
			}, 899400);
			-->
		</script>
	</div>
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>
