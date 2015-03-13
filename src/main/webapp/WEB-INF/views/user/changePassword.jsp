<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set scope="request" var="title">label.title.register</c:set>
<!DOCTYPE html>
<html>
<jsp:include page="../template/header.jsp" />
<body>
	<div class="container">
		<img class="center-block" alt=<spring:message code="label.logo" text="Logo" /> src=<spring:url value="/data/TrickService.png" /> style="height: 200px; margin-top: 50px;">
		<div style="margin: 0 auto; max-width: 600px; padding: 0px 15px">
			<h2 class="form-signin-heading col-sm-offset-3">
				<spring:message code="label.title.user.change.password" text="Change your password" />
			</h2>
			<a class="navbar-link pull-right" href="${pageContext.request.contextPath}/Login" style="margin-top: -30px;"><spring:message code="label.menu.navigate.back" text="Back" /></a>
			<form:form modelAttribute="changePassword" class="form-horizontal" method="post" action="${pageContext.request.contextPath}/ChangePassword/Save">
				<form:hidden path="requestId" />
				<div class="form-group" style="margin-top: 20px;">
					<form:label path="password" class="col-sm-3 control-label">
						<spring:message code="label.user.new.password" text="new password" />
					</form:label>
					<div class="col-sm-9">
						<form:password path="password" class="form-control" required="true" />
						<form:errors path="password" cssClass="label label-danger" element="span"/>
					</div>
				</div>
				<div class="form-group">
					<form:label path="repeatPassword" class="col-sm-3 control-label">
						<spring:message code="label.user.repeat_password" text="Repeat password" />
					</form:label>
					<div class="col-sm-9">
						<form:password  path="repeatPassword" class="form-control" required="true" />
						<form:errors path="repeatPassword" cssClass="label label-danger" element="span"/>
					</div>
				</div>
				<div class="form-group">
					<div class="col-sm-offset-3 col-sm-9">
						<button class="btn btn-primary" type="submit">
							<spring:message code="label.action.update" text="Update" />
						</button>
						<a class="btn btn-danger pull-right" href="${pageContext.request.contextPath}/ChangePassword/${changePassword.requestId}/Cancel">
							<spring:message code="label.action.cancel" />
						</a>
					</div>
				</div>
			</form:form>
		</div>
		<!-- ################################################################ Include Footer ################################################################ -->
		<jsp:include page="../template/footer.jsp" />
		<jsp:include page="../template/scripts.jsp" />
		<script type="text/javascript" src="<spring:url value="/js/trickservice/register.js" />"></script>
	</div>
	<!-- ################################################################ End Container ################################################################# -->
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>