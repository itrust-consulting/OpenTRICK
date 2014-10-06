<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set scope="request" var="title">label.title.profile</c:set>
<html>
<!-- Include Header -->
<jsp:include page="header.jsp" />
<body>
	<div id="wrap">
		<jsp:include page="menu.jsp" />
		<div class="container">
			<div class="page-header">
				<h1>
					<spring:message code="label.title.profile" text="Profile" />
				</h1>
			</div>
			<span id="success" hidden="hidden"></span>
			<div style="margin: 0 auto; max-width: 600px;">
				<form id="updateprofileform" name="updateprofileform" class="form-horizontal" method="post" action="${pageContext.request.contextPath}/Profile/Update">
					<div class="page-header">
						<h3>
							<spring:message code="label.user.title.login_information" text="Login Information" />
						</h3>
					</div>
					<div class="form-group">
						<label for="login" class="col-sm-3 control-label"> <spring:message code="label.user.login" text="Username" />
						</label>
						<div class="col-sm-9">
							<input class="form-control" disabled="disabled" value="${user.login}" />
						</div>
					</div>
					<div class="form-group">
						<label for="oldPassword" class="col-sm-3 control-label"> <spring:message code="label.user.current_password" text="Current Password" />
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
						<label for="repeatPassword" class="col-sm-3 control-label"> <spring:message code="label.user.repeat_password" text="Repeat password" />
						</label>
						<div class="col-sm-9">
							<input type="password" id="repeatPassword" name="repeatPassword" class="form-control" required="required" />
						</div>
					</div>
					<div class="page-header">
						<h3>
							<spring:message code="label.user.title.personal_information" text="Personal Information" />
						</h3>
					</div>
					<div class="form-group">
						<label for="firstName" class="col-sm-3 control-label"> <spring:message code="label.user.first_name" text="Firstname" />
						</label>
						<div class="col-sm-9">
							<input type="text" id="firstName" name="firstName" class="form-control" required value="${user.firstName}" />
						</div>
					</div>
					<div class="form-group">
						<label for="lastName" class="col-sm-3 control-label"> <spring:message code="label.user.last_name" text="Lastname" />
						</label>
						<div class="col-sm-9">
							<input type="text" id="lastName" name="lastName" class="form-control" required value="${user.lastName}" />
						</div>
					</div>
					<div class="form-group">
						<label for="email" class="col-sm-3 control-label"> <spring:message code="label.user.email" text="Email address" />
						</label>
						<div class="col-sm-9">
							<input type="text" id="email" name="email" class="form-control" required pattern='^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$'
								value="${user.email}" />
						</div>
					</div>
					<div class="form-group">
						<c:if test="${user.roles.size()>1}">
							<label for="roles" class="col-sm-3 control-label"> <spring:message code="label.user.account.roles" text="Roles" />
							</label>
						</c:if>
						<c:if test="${user.roles.size()==1}">
							<label for="roles" class="col-sm-3 control-label"> <spring:message code="label.user.account.role" text="Role" />
							</label>
						</c:if>
						<div class="col-sm-9">
							<c:forEach items="${user.roles}" var="role">
								<c:set var="role_value" value="${fn:replace(role.type,'ROLE_','')}" />
								<div style="padding: 6px; border: 1px solid #dddddd; text-align: center; border-radius: 4px; background-color: #eeeeee;">
									<spring:message code="label.role.${fn:toLowerCase(role_value)}" text="${role_value}" />
								</div>
							</c:forEach>
						</div>
					</div>
					<div class="page-header">
						<h3>
							<spring:message code="label.user.title.application_settings" text="Application Settings" />
						</h3>
					</div>
					<div class="form-group">
						<label for="default_ui_language" class="col-sm-6 control-label"> <spring:message code="label.user.default_ui_language" text="Default User Interface Language" />
						</label>
						<div class="col-sm-6">
							<select class="form-control" name="defaultlanguage" id="defaultlanguage">
								<option value="en" ${defaultlang.equals("en")?"selected='selected'":"" } class="list-group-item pull-left"
									style="margin-right: 5px;background: white url(${pageContext.request.contextPath}/images/flags/en.png) no-repeat 1%;border:1px solid white;padding:0px;padding-top:3px;padding-bottom:3px;padding-left: 25px;">English</option>
								<option value="fr" ${defaultlang.equals("fr")?"selected='selected'":"" } class="list-group-item pull-left"
									style="margin-right: 5px;background: white url(${pageContext.request.contextPath}/images/flags/fr.png) no-repeat 1%;border:1px solid white;padding:0px;padding-top:3px;padding-bottom:3px;padding-left: 25px;">Français</option>
							</select>
						</div>
						<input type="hidden" value="${defaultlang}" id="perviouslanguage" />
					</div>
					<div class="form-group">
						<label for="default_show_uncertainty" class="col-sm-6 control-label"> <spring:message code="label.user.default_show_uncertainty" text="Default Show Uncertainty" />
						</label>
						<div class="col-sm-6">
							<div class="radio-inline">
								<label> 
									<input type="radio" name="default_show_uncertainty" value="true" ${defaultShowUncertainty.equals("true")?"checked='checked'":"" }> 
									<spring:message code="label.yes_no.yes" text="Yes" />
								</label>
							</div>
							<div class="radio-inline">
								<label> 
									<input type="radio" name="default_show_uncertainty" value="false" ${defaultShowUncertainty.equals("false")?"checked='checked'":"" }> 
									<spring:message code="label.yes_no.no" text="No" />
								</label>
							</div>
						</div>
					</div>
					<div class="form-group">
						<label for="default_show_cssf" class="col-sm-6 control-label"> <spring:message code="label.user.default_show_cssf" text="Default Show CSSF" />
						</label>
						<div class="col-sm-6">
							<div class="radio-inline">
								<label> 
									<input type="radio" name="default_show_cssf" value="true" ${defaultShowCssf.equals("true")?"checked='checked'":"" }> 
									<spring:message code="label.yes_no.yes" text="Yes" />
								</label>
							</div>
							<div class="radio-inline">
								<label> 
									<input type="radio" name="default_show_cssf" value="false" ${defaultShowCssf.equals("false")?"checked='checked'":"" }>
									<spring:message code="label.yes_no.no" text="No" />
								</label>
							</div>
						</div>
					</div>
					<div class="form-group">
						<div class="col-sm-12" style="text-align: center;">
							<button class="btn btn-primary" onclick="return updateProfile('updateprofileform');" type="button">
								<spring:message code="label.user.update" text="Update" />
							</button>
						</div>
					</div>
				</form>
			</div>
		</div>
		<jsp:include page="footer.jsp" />
		<jsp:include page="scripts.jsp" />
		<script type="text/javascript" src="<spring:url value="/js/trickservice/profile.js" />"></script>
	</div>
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>