<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<!-- ###################################################################### HTML #################################################################### -->
<c:if test="${empty locale }">
	<spring:eval expression="T(org.springframework.context.i18n.LocaleContextHolder).getLocale()" var="locale" scope="request" />
</c:if>
<!DOCTYPE html>
<html lang="${locale.language}">
<!-- Include Header -->
<c:set scope="request" var="title">label.title.otp.signin</c:set>
<jsp:include page="../template/header.jsp" />
<!-- ################################################################# Start Container ############################################################## -->
<body>
	<div id="wrap">
		<div class="container">
			<img class="center-block" alt=<spring:message code="label.logo" text="Logo" /> src='<c:url value="/images/brand.png" />' style="height: 200px;">
			<spring:message code="label.action.send.code" text="Send code" var="sendCode"/>
			<spring:message code="label.action.signin" var="signing"/>
			<div class="form-signin" id="login" style="margin: 0 auto; max-width: 330px;">
				<div class="form-horizontal">
					<div class='form-group'>
						<label><spring:message code='label.otp.select.method' text="Please select a method" /></label> <select id='otp-option-method' required="required" class="form-control">
							<c:if test="${application }">
								<option value="application"><spring:message code='label.otp.use.application' text='Use mobile application' /></option>
							</c:if>
							<c:if test="${not empty email }">
								<option value="email"><spring:message code='label.otp.send.email' arguments="${email}" text='Send an email to ${email}' /></option>
							</c:if>
							<c:if test="${not empty phoneNumber}">
								<option value="tel"><spring:message code='label.otp.send.text' arguments="${phoneNumber}" text='Send text to ${phoneNumber}' /></option>
							</c:if>
						</select>
						<c:if test="${not empty error }">
							<div class='label label-danger'><spring:message code='${error}' text="${error}"/></div>
						</c:if>
					</div>
					<c:if test="${not empty email }">
						<form name="application" action="${pageContext.request.contextPath}/OTP/Authorise" method="POST"  id="otp-option-application" data-action-name="${signing}">
							<input hidden="hidden" name="otp-method" value="application">
							<div class='form-group'>
								<label><spring:message code='label.otp.enter.application.code' text='Please enter code' /></label> <input name="otp-user-code" size="8" type="number" class="form-control" required="required" placeholder="000000000" >
							</div>
							<input type="submit" id="application-form-submit" hidden="hidden"/>
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
							<input type="hidden" name="username" value='<sec:authentication  property="name" />'>
						</form>
					</c:if>
					<c:if test="${not empty email}">
						<form name="email" action="${pageContext.request.contextPath}/OTP/Generate-code" method="GET" class="form-horizontal" id="otp-option-email" data-action-name="${sendCode}" >
							<input hidden="hidden" name="otp-method" value="email">
							<div class='form-group'>
								<label><spring:message code='label.otp.enter.email' text='Please enter your email' /></label> <input name="otp-method-value" type="email" placeholder="${email}" class="form-control" required="required">
							</div>
							<input type="submit" id="application-email-submit" hidden="hidden"/>
						</form>
					</c:if>
					<c:if test="${not empty phoneNumber }">
						<form name="tel" action="${pageContext.request.contextPath}/OTP/Generate-code" method="GET" class="form-horizontal" id="otp-option-tel" data-action-name="${sendCode}" >
							<input hidden="hidden" name="otp-method" value="tel">
							<div class='form-group'>
								<label><spring:message code='label.otp.enter.text' text='Please enter your mobile phone' /></label> <input name="otp-method-value" type="tel" placeholder="${phoneNumber}" required="required"
									class="form-control">
							</div>
							<input type="submit" id="application-email-submit" hidden="hidden"/>
						</form>
					</c:if>
					<div class="form-group">
						<button class="btn btn-danger pull-left" id='otp-option-submit'>${signing}</button>
						<a href="#" class="btn btn-default pull-right" onclick="return $('#logoutFormSubmiter').click()"><spring:message code="label.action.cancel" /></a>
					</div>
				</div>
				<form action="${pageContext.request.contextPath}/Signout" method="post" style="display: none">
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="submit" id="logoutFormSubmiter" />
				</form>
			</div>
		</div>
		<jsp:include page="../template/footer.jsp" />
		<jsp:include page="../template/scripts.jsp" />
	</div>
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>
