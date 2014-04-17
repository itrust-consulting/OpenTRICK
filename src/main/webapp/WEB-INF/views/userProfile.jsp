<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:set scope="request" var="title">title.Profile</c:set>
<html>
<!-- Include Header -->
<jsp:include page="header.jsp" />
<body>
	<div id="wrap">
		<jsp:include page="menu.jsp" />
		<div class="container">
			<div class="page-header">
				<h1>
					<spring:message code="title.Profile" text="Profile" />
				</h1>
			</div>
			<span id="success" hidden="hidden"></span>
			<div style="margin: 0 auto; max-width: 600px; padding: 15px;">
				<form id="updateprofileform" name="updateprofileform" class="form-horizontal" method="post" action="${pageContext.request.contextPath}/Profile/Update">
					<div class="form-group">
						<label for="login" class="col-sm-3 control-label"> <spring:message code="label.user.login" />
						</label>
						<div class="col-sm-9">
							<input class="form-control" disabled="disabled" value="${user.login}" />
						</div>
					</div>
					<div class="form-group">
						<label for="oldPassword" class="col-sm-3 control-label"> <spring:message code="label.user.currentpassword" text="Current Password" />
						</label>
						<div class="col-sm-9">
							<input type="password" id="currentPassword" name="currentPassword" class="form-control" required="required" />
						</div>
					</div>
					<div class="form-group">
						<label for="password" class="col-sm-3 control-label"> <spring:message code="label.user.password" text="Password" />
						</label>
						<div class="col-sm-9">
							<input type="password" id="password" name="password" class="form-control" required="required" />
						</div>
					</div>
					<div class="form-group">
						<label for="repeatPassword" class="col-sm-3 control-label"> <spring:message code="label.user.repeatPassword" text="Repeat password" />
						</label>
						<div class="col-sm-9">
							<input type="password" id="repeatPassword" name="repeatPassword" class="form-control" required="required" />
						</div>
					</div>
					<div class="form-group">
						<label for="firstName" class="col-sm-3 control-label"> <spring:message code="label.user.firstName" />
						</label>
						<div class="col-sm-9">
							<input type="text" id="firstName" name="firstName" class="form-control" required value="${user.firstName}" />
						</div>
					</div>
					<div class="form-group">
						<label for="lastName" class="col-sm-3 control-label"> <spring:message code="label.user.lastName" />
						</label>
						<div class="col-sm-9">
							<input type="text" id="lastName" name="lastName" class="form-control" required value="${user.lastName}" />
						</div>
					</div>
					<div class="form-group">
						<label for="email" class="col-sm-3 control-label"> <spring:message code="label.user.email" />
						</label>
						<div class="col-sm-9">
							<input type="text" id="email" name="email" class="form-control" required
								pattern='^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$' value="${user.email}" />
						</div>
					</div>
					<div class="form-group">
						<label for="roles" class="col-sm-2 control-label"> <spring:message code="label.role" />
						</label>
						<div class="col-sm-10">
							<ul class="list-group">
								<c:forEach items="${user.roles}" var="role">
									<li class="list-group-item"><spring:message code="label.role.${role.type}" /></li>
								</c:forEach>
							</ul>
						</div>
					</div>
				</form>
				<div style="text-align:center">
					<button class="btn btn-primary" onclick="return updateProfile('updateprofileform');" type="button"><spring:message code="label.user.update" text="Update" /></button>
				</div>
			</div>
		</div>
		<jsp:include page="footer.jsp" />
		<jsp:include page="scripts.jsp" />
		<script type="text/javascript" src="<spring:url value="/js/profile.js" />"></script>
	</div>
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>