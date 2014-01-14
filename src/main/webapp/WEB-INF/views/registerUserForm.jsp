<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<!-- ################################################################ Set Page Title ################################################################ -->

<c:set scope="request" var="title">title.register</c:set>

<!-- ###################################################################### HTML #################################################################### -->

<html>

<!-- ##################################################################### Header ################################################################### -->
<jsp:include page="header.jsp" />


<!-- ################################################################# Start Container ############################################################## -->

<body>
	<div class="container">
		<div style="margin: 0 auto; max-width: 600px; padding: 15px;">
			<h2 class="form-signin-heading col-sm-offset-5">
				<spring:message code="title.user.register" />
			</h2>
			<a class="navbar-link pull-right"
				href="${pageContext.request.contextPath}/login"
				style="margin-top: -35px;"><spring:message
					code="menu.navigate.back" /></a>
			<form:form cssClass="form-horizontal" method="post"
				action="${pageContext.request.contextPath}/DoRegister"
				commandName="user">
				<div class="form-group">
					<form:label path="login" cssClass="col-sm-3 control-label">
						<spring:message code="label.user.login" />
					</form:label>
					<div class="col-sm-9">
						<form:input path="login" cssClass="form-control" htmlEscape="true" required='true' />
						<form:errors path="login" cssClass="label label-danger"
							element="span" />
					</div>
				</div>
				<div class="form-group">
					<form:label path="password" cssClass="col-sm-3 control-label">
						<spring:message code="label.user.password" arguments=""/>
					</form:label>
					<div class="col-sm-9">
						<form:password path="password" cssClass="form-control" required='true' htmlEscape="true"/>
						<form:errors path="password" cssClass="label label-danger"
							element="span" />
					</div>
				</div>
				
				<div class="form-group">
					<form:label path="repeatPassword" cssClass="col-sm-3 control-label">
						<spring:message code="label.user.repeatPassword" text="Repeat password"/>
					</form:label>
					<div class="col-sm-9">
						<form:password path="repeatPassword" cssClass="form-control"
							htmlEscape="true" required='true' />
						<form:errors path="repeatPassword" cssClass="label label-danger"
							element="span" />
					</div>
				</div>
				
				<div class="form-group">
					<form:label path="firstName" cssClass="col-sm-3 control-label">
						<spring:message code="label.user.firstName" />
					</form:label>
					<div class="col-sm-9">
						<form:input path="firstName" cssClass="form-control" 
							htmlEscape="true" required='true' />
						<form:errors path="firstName" cssClass="label label-danger"
							element="span" />
					</div>
				</div>
				<div class="form-group">
					<form:label path="lastName" cssClass="col-sm-3 control-label">
						<spring:message code="label.user.lastName" />
					</form:label>
					<div class="col-sm-9">
						<form:input path="lastName" cssClass="form-control" required='true'
							htmlEscape="true" />
						<form:errors path="lastName" cssClass="label label-danger"
							element="span" />
					</div>
				</div>
				<div class="form-group">
					<form:label path="email" cssClass="col-sm-3 control-label">
						<spring:message code="label.user.email" />
					</form:label>
					<div class="col-sm-9">
						<form:input path="email" cssClass="form-control" htmlEscape="true" required='true' />
						<form:errors path="email" cssClass="label label-danger"
							element="span" />
					</div>
				</div>
				<div class="form-group">
					<div class="col-sm-offset-3 col-sm-9">
						<button class="btn btn-default"
							type="submit">
							<spring:message code="label.user.submit" />
						</button>
					</div>
				</div>
			</form:form>
		</div>

		<!-- ################################################################ Include Footer ################################################################ -->
		<jsp:include page="footer.jsp" />

	</div>

	<!-- ################################################################ End Container ################################################################# -->

	<jsp:include page="scripts.jsp" />
</body>

<!-- ################################################################### End HTML ################################################################### -->

</html>