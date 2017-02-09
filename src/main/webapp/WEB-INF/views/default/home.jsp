<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:if test="${empty locale }">
	<spring:eval expression="T(org.springframework.web.servlet.support.RequestContextUtils).getLocale(pageContext.request)" var="locale" scope="request" />
</c:if>
<!DOCTYPE html>
<html lang="${locale.language}">
<c:set scope="request" var="title">label.title.home</c:set>
<jsp:include page="../template/header.jsp" />
<body>
	<div id="wrap">
		<jsp:include page="../template/menu.jsp" />
		<div class="container">
			<div class="content" id="content">
				<div style="margin-top: 120px; display: block;">
					<img class="center-block" alt=<spring:message code="label.logo" text="Logo" /> src=<spring:url value="/images/TrickService.png" />
						style="height: 200px; margin-bottom: -110px;">
					<hr style="border-top-color: #3e3e3e; z-index: -1">
				</div>
				<div style="position: fixed; right: 40px; bottom: 35px;">
					<strong style="border-bottom: 1px #3e3e3e solid; margin-bottom: 10px; float: right;"><spring:message code='label.entity.support'/></strong>
					<div style="display: inline-block; clear: both; float: right;">
						<img class="support-logo" alt=<spring:message code="label.logo" text="Logo" /> src=<spring:url value="/images/support/diamonds.jpg" />> <img class="support-logo"
							alt=<spring:message code="label.logo" text="Logo" /> src=<spring:url value="/images/support/cockpitci.jpg" />> <img class="support-logo"
							alt=<spring:message code="label.logo" text="Logo" /> src=<spring:url value="/images/support/trespass.png" />> <img class="support-logo"
							alt=<spring:message code="label.logo" text="Logo" /> src=<spring:url value="/images/support/itea2.jpg" />> <img class="support-logo"
							alt=<spring:message code="label.logo" text="Logo" /> src=<spring:url value="/images/support/seventh_framework_programme.png" />> <img class="support-logo"
							alt=<spring:message code="label.logo" text="Logo" /> src=<spring:url value="/images/support/ministere_eco.jpg" /> class="support-logo"> <img class="support-logo"
							alt=<spring:message code="label.logo" text="Logo" /> src=<spring:url value="/images/support/eu.jpg" />>
					</div>
				</div>
			</div>
		</div>
		<jsp:include page="../template/footer.jsp" />
	</div>
	<jsp:include page="../template/scripts.jsp" />
</body>
</html>