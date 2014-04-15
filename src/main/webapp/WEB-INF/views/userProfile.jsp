<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!-- ################################################################ Set Page Title ################################################################ -->
<c:set scope="request" var="title">title.Profile</c:set>
<!-- ###################################################################### HTML #################################################################### -->
<html>
<!-- Include Header -->
<jsp:include page="header.jsp" />
<!-- ################################################################# Start Container ############################################################## -->
<body>
	<div id="wrap">
		<!-- ################################################################### Nav Menu ################################################################### -->
		<jsp:include page="menu.jsp" />
		<div class="container">
			<jsp:include page="successErrors.jsp" />
			<!-- #################################################################### Content ################################################################### -->
			<div class="page-header">
				<h1>
					<spring:message code="title.Profile" text="Profile" />
				</h1>
			</div>
			<div class="content" style="text-align:center;" id="content">
				<c:if test="${!empty userProfil}">
					<form name="user" action="Update" class="form-horizontal" id="user_form" style="width:40%;display:inline-block">
						<input type="hidden" name="id" value="${userProfil.id}" id="id">
						<div class="form-group">
							<label for="login" class="col-sm-2 control-label"> <spring:message code="label.user.login" />
							</label>
							<div class="col-sm-10">
								<input id="login" name="login" class="form-control" type="text" value="${userProfil.login}" disabled="disabled" />
							</div>
						</div>
						<div class="form-group">
							<label for="password" class="col-sm-2 control-label"> <spring:message code="label.user.password" />
							</label>
							<div class="col-sm-10">
								<input id="password" name="password" class="form-control" type="password" value="" />
							</div>
						</div>
						<div class="form-group">
							<label for="repeatpassword" class="col-sm-2 control-label"> <spring:message code="label.user.repeat_password" />
							</label>
							<div class="col-sm-10">
								<input id="repeatpassword" name="repeatpassword" class="form-control" type="password" />
							</div>
						</div>
						<div class="form-group">
							<label for="firstName" class="col-sm-2 control-label"> <spring:message code="label.user.firstName" />
							</label>
							<div class="col-sm-10">
								<input id="firstName" name="firstName" class="form-control" type="text" value="${userProfil.firstName}" />
							</div>
						</div>
						<div class="form-group">
							<label for="lastName" class="col-sm-2 control-label"> <spring:message code="label.user.lastName" />
							</label>
							<div class="col-sm-10">
								<input id="lastName" name="lastName" class="form-control" type="text" value="${userProfil.lastName}" />
							</div>
						</div>
						<div class="form-group">
							<label for="email" class="col-sm-2 control-label"> <spring:message code="label.user.email" />
							</label>
							<div class="col-sm-10">
								<input id="email" name="email" class="form-control" type="text" value="${userProfil.email}" />
							</div>
						</div>
						<div class="form-group">
							<label for="roles" class="col-sm-2 control-label"> <spring:message code="label.role" /></label>
							<div class="col-sm-10" id="rolescontainer">
								<c:forEach items="${userProfil.roles}" var="role">
									<spring:message code="label.role.${role.type}" /> <br>
								</c:forEach>
							</div>
						</div>
						<button id="updateProfile" type="button" class="btn btn-primary" onclick="updateProfile('user_form')">
							<spring:message code="label.update.profile" text="Update Profile" />
					</button>
					</form>
					
				</c:if>
			</div>
		</div>
		<!-- ################################################################ End Container ################################################################# -->
	</div>
	<!-- ################################################################ Include Footer ################################################################ -->
	<jsp:include page="footer.jsp" />
	<jsp:include page="scripts.jsp" />
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>