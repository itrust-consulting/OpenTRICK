<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set scope="request" var="title">label.title.register</c:set>
<html>
<jsp:include page="header.jsp" />
<body>
	<div class="container">
		<div style="margin: 0 auto; max-width: 600px; padding: 15px;">
			<h2 class="form-signin-heading col-sm-offset-5">
				<spring:message code="label.title.user.register" text="Sign up" />
			</h2>
			<a class="navbar-link pull-right" href="${pageContext.request.contextPath}/Login" style="margin-top: -35px;"><spring:message code="label.menu.navigate.back" text="Back" /></a>
			<span id="success" hidden="hidden"></span>
			<form id="registerform" name="registerform" class="form-horizontal" method="post" action="${pageContext.request.contextPath}/DoRegister">
				<div class="form-group">
					<label for="login" class="col-sm-3 control-label">
						<spring:message code="label.user.login" text="Username" />
					</label>
					<div class="col-sm-9">
						<input type="text" id="login" name="login" class="form-control" required />
					</div>
				</div>
				<div class="form-group">
					<label for="password" class="col-sm-3 control-label">
						<spring:message code="label.user.password" text="Password" />
					</label>
					<div class="col-sm-9">
						<input type="password" id="password" name="password" class="form-control" required />
					</div>
				</div>
				<div class="form-group">
					<label for="repeatPassword" class="col-sm-3 control-label">
						<spring:message code="label.user.repeat_password" text="Repeat password" />
					</label>
					<div class="col-sm-9">
						<input type="password" id="repeatPassword" name="repeatPassword" class="form-control" required />
					</div>
				</div>
				<div class="form-group">
					<label for="firstName" class="col-sm-3 control-label">
						<spring:message code="label.user.first_name" text="Firstname" />
					</label>
					<div class="col-sm-9">
						<input type="text" id="firstName" name="firstName" class="form-control" required />
					</div>
				</div>
				<div class="form-group">
					<label for="lastName" class="col-sm-3 control-label">
						<spring:message code="label.user.last_name" text="Lastname" />
					</label>
					<div class="col-sm-9">
						<input type="text" id="lastName" name="lastName" class="form-control" required />
					</div>
				</div>
				<div class="form-group">
					<label for="email" class="col-sm-3 control-label">
						<spring:message code="label.user.email" text="Email address" />
					</label>
					<div class="col-sm-9">
						<input type="email" id="email" name="email" class="form-control" pattern='^[_A-Za-z0-9-\+]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9]+)*(\.[A-Za-z]{2,})$' required />
					</div>
				</div>
				<div class="form-group">
					<label for="locale" class="col-sm-3 control-label">
						<spring:message code="label.user.default_ui_language" text="Default User Interface Language" />
					</label>
					<div class="col-sm-9">
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
					<div class="col-sm-offset-3 col-sm-9">
						<button class="btn btn-default" type="submit" onclick="return register('registerform');">
							<spring:message code="label.action.sign_up.user" text="Signup" />
						</button>
					</div>
				</div>
			</form>
		</div>
		<!-- ################################################################ Include Footer ################################################################ -->
		<jsp:include page="footer.jsp" />
		<script type="text/javascript" src="<spring:url value="/js/trickservice/register.js" />"></script>
	</div>
	<!-- ################################################################ End Container ################################################################# -->
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>