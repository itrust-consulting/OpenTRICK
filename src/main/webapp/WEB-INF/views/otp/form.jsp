<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!-- ###################################################################### HTML #################################################################### -->
<c:if test="${empty locale }">
	<spring:eval expression="T(org.springframework.web.servlet.support.RequestContextUtils).getLocale(pageContext.request)" var="locale" scope="request" />
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
			<img class="center-block" alt=<spring:message code="label.logo" text="Logo" /> src=<spring:url value="/images/TrickService.png" /> style="height: 200px;">
			<spring:message code="label.action.send.code" text="Send code" var="sendCode" />
			
			<div class="form-signin" id="login" style="margin: 0 auto; max-width: 330px;">
				<form action="${pageContext.request.contextPath}/OTP/Authorise" method="POST" id="otp-option-application">
					<div class='form-group'>
						<label><spring:message code='label.otp.enter.code' text='Please enter code' /></label> <input name="otp-user-code" type="number"
							class="form-control" required="required">
					</div>
					<div class="form-group">
						<button class="btn btn-danger pull-left" type="submit"><spring:message code="label.action.signin" /></button>
						<a href="#" class="btn btn-default pull-right" onclick="return $('#logoutFormSubmiter').click()"><spring:message code="label.action.cancel" /></a>
					</div>
					<input type="hidden" name="username" value='<sec:authentication  property="principal" />'>
					<input type="submit" id="application-form-submit" hidden="hidden" /> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				</form>
				<form action="${pageContext.request.contextPath}/signout" method="post" style="display: none">
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
