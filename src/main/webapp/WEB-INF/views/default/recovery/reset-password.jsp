<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set scope="request" var="title">label.title.register</c:set>
<!DOCTYPE html>
<html>
<jsp:include page="../../template/header.jsp" />
<body>
	<div class="container">
		<img class="center-block" alt=<spring:message code="label.logo" text="Logo" /> src='<c:url value="/images/TrickService.png" />?version=${imageVersion}' style="height: 200px; margin-top: 50px;">
		<div style="margin: 0 auto; max-width: 600px; padding: 0px 15px">
			<h2 class="form-signin-heading col-sm-offset-3">
				<spring:message code="label.title.user.reset.password" text="Reset your password" />
			</h2>
			<form:form modelAttribute="resetPassword" class="form-horizontal" method="post"
				action="${pageContext.request.contextPath}/ResetPassword/Save?${_csrf.parameterName}=${_csrf.token}">
				<div class="form-group" style="margin-top: 20px; margin-bottom: 5px;">
					<form:label path="username" class="col-sm-3 control-label">
						<spring:message code="label.user.login" text="Username" />
					</form:label>
					<div class="col-sm-9">
						<form:input path="username" class="form-control" />
					</div>
				</div>

				<div class="form-group" style="margin: 0px auto">
					<div class="col-sm-offset-3 col-sm-9">
						<label class="col-sm-offset-5" style="font-size: 15px"> <spring:message code="label.or" text="or" />
						</label>
					</div>
				</div>

				<div class="form-group">
					<form:label path="email" class="col-sm-3 control-label">
						<spring:message code="label.user.email" text="Email address" />
					</form:label>
					<div class="col-sm-9">
						<form:input type="email" path="email" class="form-control" pattern='^[_A-Za-z0-9-\+]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9]+)*(\.[A-Za-z]{2,})$' />
					</div>
				</div>
				<spring:hasBindErrors name="resetPassword">
					<div class="form-group">
						<div class="col-sm-offset-3 col-sm-9">
							<form:errors path="*" element="label" cssClass="label label-danger " cssStyle="font-size: 12px" />
						</div>
					</div>
				</spring:hasBindErrors>
				<div class="form-group">
					<div class="col-sm-offset-3 col-sm-9">
						<button class="btn btn-primary" type="submit">
							<spring:message code="label.action.reset.password" text="Reset password" />
						</button>
						<a class="btn btn-default pull-right" href="${pageContext.request.contextPath}/Login"><spring:message code="label.action.cancel" text="Cancel" /></a>
					</div>
				</div>
			</form:form>
		</div>
		<!-- ################################################################ Include Footer ################################################################ -->
		<jsp:include page="../../template/footer.jsp" />
	</div>
	<!-- ################################################################ End Container ################################################################# -->
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>
