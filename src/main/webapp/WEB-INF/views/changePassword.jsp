<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set scope="request" var="title">label.title.register</c:set>
<!DOCTYPE html>
<html>
<jsp:include page="header.jsp" />
<body>
	<div class="container">
		<img class="center-block"  alt=<spring:message code="label.logo" text="Logo" /> src=<spring:url value="/data/TrickService.png" /> style="height: 200px; margin-top: 50px;">
		<div style="margin: 0 auto; max-width: 600px; padding: 0px 15px">
			<h2 class="form-signin-heading col-sm-offset-3">
				<spring:message code="label.title.user.change.password" text="Change your password" />
			</h2>
			<a class="navbar-link pull-right" href="${pageContext.request.contextPath}/Login" style="margin-top: -35px;"><spring:message code="label.menu.navigate.back" text="Back" /></a>
			<span id="success" hidden="hidden"></span>
			<form id="changePassword" name="registerform" class="form-horizontal" method="post" action="${pageContext.request.contextPath}/ChangePawword/Save">
				<input hidden="true" name="requestId" value='<spring:message text="${changePassword.requestId}" />' >
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
					<div class="col-sm-offset-3 col-sm-9">
						<button class="btn btn-default" type="submit">
							<spring:message code="label.action.update" text="Update" />
						</button>
					</div>
				</div>
			</form>
		</div>
		<!-- ################################################################ Include Footer ################################################################ -->
		<jsp:include page="footer.jsp" />
		<jsp:include page="scripts.jsp" />
		<script type="text/javascript" src="<spring:url value="/js/trickservice/register.js" />"></script>
	</div>
	<!-- ################################################################ End Container ################################################################# -->
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>