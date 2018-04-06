<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set scope="request" var="title">label.title.profile</c:set>
<!DOCTYPE html>
<html>
<jsp:include page="../template/header.jsp" />
<body>
	<div id="wrap" class="wrap">
		<jsp:include page="../template/menu.jsp" />
		<div class="container" data-ug-root="account">
			<ul class="nav nav-tabs affix affix-top col-xs-12 nav-tab">
				<li class="active"><a href="#tab-profile" data-toggle="tab"><spring:message code="label.menu.profile" text="My Profile" /></a></li>
				<li><a href="#tab-invitation" data-toggle="tab"><spring:message code="label.menu.invitation" text="My invitation" /></a></li>
				<li><a href="#tab-sqlite" data-toggle="tab"><spring:message code="label.menu.sqlite" text="My sqlites" /></a></li>
				<li><a href="#tab-report" data-toggle="tab"><spring:message code="label.menu.report" text="My reports" /></a></li>
				<c:if test="${enabledOTP}">
					<li><a href="#tab-otp" data-toggle="tab"><spring:message code="label.menu.otp.options" text="My sign-in settings" /></a></li>
				</c:if>
			</ul>
			<div class="tab-content" id="tab-container">
				<!-- profile -->
				<jsp:include page="profile/section.jsp" />

				<jsp:include page="invitation/home.jsp" />

				<jsp:include page="sqlite/home.jsp" />

				<jsp:include page="report/home.jsp" />
				<!-- otp -->
				<c:if test="${enabledOTP}">
					<jsp:include page="otp/section.jsp" />
				</c:if>

				<jsp:include page="../template/tab-option.jsp" />

			</div>
		</div>
		<jsp:include page="../template/footer.jsp" />
	</div>
	<jsp:include page="../template/scripts.jsp" />
	<script src="<spring:url value="/js/trickservice/profile.js?version=${jsVersion}" />"></script>
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>