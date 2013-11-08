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
		<div  style="margin: 0 auto; max-width:500px; padding: 15px;">
			<h2 class="form-signin-heading">
				<spring:message code="title.user.register" />
			</h2>
			<a class="navbar-link pull-right" href="../login" style="margin-top: -35px;"><spring:message code="menu.navigate.back" /></a>
			<form:form cssClass="form-horizontal"
				method="post" action="${pageContext.request.contextPath}/user/save"
				commandName="user">
				<div class="form-group">
					<form:label path="login" cssClass="col-sm-2 control-label">
						<spring:message code="label.user.login" />
					</form:label>
					<div class="col-sm-10">
						<form:input path="login" cssClass="form-control" />
						<form:errors path="login" cssClass="label label-danger" element="span" />
					</div>
				</div>
				<div class="form-group">
					<form:label path="password" cssClass="col-sm-2 control-label">
						<spring:message code="label.user.password" />
					</form:label>
					<div class="col-sm-10">
						<form:password path="password" cssClass="form-control" />
						<form:errors path="password" cssClass="label label-danger" element="span" />
					</div>
				</div>
				<div class="form-group">
					<form:label path="firstName" cssClass="col-sm-2 control-label">
						<spring:message code="label.user.firstName" />
					</form:label>
					<div class="col-sm-10">
						<form:input path="firstName" cssClass="form-control" />
						<form:errors path="firstName" cssClass="label label-danger" element="span" />
					</div>
				</div>
				<div class="form-group">
					<form:label path="lastName" cssClass="col-sm-2 control-label">
						<spring:message code="label.user.lastName" />
					</form:label>
					<div class="col-sm-10">
						<form:input path="lastName" cssClass="form-control" />
						<form:errors path="lastName" cssClass="label label-danger" element="span" />
					</div>
				</div>
				<div class="form-group">
					<form:label path="email" cssClass="col-sm-2 control-label">
						<spring:message code="label.user.email" />
					</form:label>
					<div class="col-sm-10">
						<form:input path="email" cssClass="form-control" />
						<form:errors path="email" cssClass="label label-danger" element="span" />
					</div>
				</div>
				<button class="btn btn-default navbar-btn" type="submit">
					<spring:message code="label.user.submit" />
				</button>
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