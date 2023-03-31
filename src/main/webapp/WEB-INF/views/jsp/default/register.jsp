<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<spring:eval expression="T(lu.itrust.business.ts.constants.Constant).REGEXP_VALID_USERNAME" var="usernameRegex" scope="request" />
<c:set scope="request" var="title">label.title.register</c:set>
<!DOCTYPE html>
<html>
<jsp:include page="../template/header.jsp" />
<body>
	<div class="container">
		<img class="center-block"  alt=<spring:message code="label.logo" text="Logo" /> src='<c:url value="/images/TrickService.png" />' style="height: 200px; margin-top: 50px;">
		<div style="margin: 0 auto; max-width: 900px; padding: 0px 15px">
			<h2 class="form-signin-heading col-sm-offset-4">
				<spring:message code="label.title.user.register" text="Sign up" />
			</h2>
			<span id="success" hidden="hidden"></span>
			<form id="registerform" name="registerform" class="form-horizontal" method="post" action="${pageContext.request.contextPath}/DoRegister?${_csrf.parameterName}=${_csrf.token}">
				<div class="form-group">
					<label for="login" class="col-sm-4 control-label">
						<spring:message code="label.user.login" text="Username" />
					</label>
					<div class="col-sm-8">
						<input type="text" id="login" name="login" class="form-control" required pattern="${usernameRegex}"/>
					</div>
				</div>
				<div class="form-group">
					<label for="password" class="col-sm-4 control-label">
						<spring:message code="label.user.password" text="Password" />
					</label>
					<div class="col-sm-8">
						<input type="password" id="password" name="password" class="form-control" required />
					</div>
				</div>
				<div class="form-group">
					<label for="repeatPassword" class="col-sm-4 control-label">
						<spring:message code="label.user.repeat_password" text="Repeat password" />
					</label>
					<div class="col-sm-8">
						<input type="password" id="repeatPassword" name="repeatPassword" class="form-control" required />
					</div>
				</div>
				<div class="form-group">
					<label for="firstName" class="col-sm-4 control-label">
						<spring:message code="label.user.first_name" text="Firstname" />
					</label>
					<div class="col-sm-8">
						<input type="text" id="firstName" name="firstName" class="form-control" required/>
					</div>
				</div>
				<div class="form-group">
					<label for="lastName" class="col-sm-4 control-label">
						<spring:message code="label.user.last_name" text="Lastname" />
					</label>
					<div class="col-sm-8">
						<input type="text" id="lastName" name="lastName" class="form-control" required />
					</div>
				</div>
				<div class="form-group">
					<label for="email" class="col-sm-4 control-label">
						<spring:message code="label.user.email" text="Email address" />
					</label>
					<div class="col-sm-8">
						<input type="email" id="email" name="email" class="form-control" pattern='^[_A-Za-z0-9-\+]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9]+)*(\.[A-Za-z]{2,})$' required />
					</div>
				</div>
				<div class="form-group">
					<label for="locale" class="col-sm-4 control-label">
						<spring:message code="label.user.default_ui_language" text="Default User Interface Language" />
					</label>
					<div class="col-sm-8">
						<select class="form-control" id="locale" name="locale" id="locale">
							<option value="en" class="list-group-item pull-left" style="margin-right: 5px;background: white url(${pageContext.request.contextPath}/images/flags/en.png) no-repeat 1%;border:1px solid white;padding:0px;padding-top:3px;padding-bottom:3px;padding-left: 25px;" >
								English
							</option>
							<option value="fr" class="list-group-item pull-left" style="margin-right: 5px;background: white url(${pageContext.request.contextPath}/images/flags/fr.png) no-repeat 1%;border:1px solid white;padding:0px;padding-top:3px;padding-bottom:3px;padding-left: 25px;" >
								Français
							</option>
						</select>
					</div>
				</div>
				<div class="form-group">
					
					<div class="col-sm-offset-4 col-sm-8">
						
						<button class="btn btn-primary" type="submit">
							<spring:message code="label.action.sign_up.user" text="Signup" />
						</button>
						<a class="btn btn-default pull-right" href="${pageContext.request.contextPath}/Login" ><spring:message code="label.menu.action" text="Cancel" /></a>
					</div>
				</div>
			</form>
		</div>
		<!-- ################################################################ Include Footer ################################################################ -->
		<jsp:include page="../template/footer.jsp" />
		<jsp:include page="../template/scripts.jsp" />
		<script src="<c:url value="/js/trickservice/register.js" />"></script>
	</div>
	<!-- ################################################################ End Container ################################################################# -->
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>
