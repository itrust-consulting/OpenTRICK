<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:set scope="request" var="title">label.title.home</c:set>
<!DOCTYPE html>
<html>
<jsp:include page="../template/header.jsp" />
<body>
	<div id="wrap">
		<jsp:include page="../template/menu.jsp" />
		<div class="container">
			<div class="content" id="content">
				<img class="center-block" alt=<spring:message code="label.logo" text="Logo" /> src=<spring:url value="/images/TrickService.png" /> style="height: 200px;">
			</div>
			<hr>
			<h1 class="text-center" style="margin-top: 5%; margin-bottom: 2%;">
				<spring:message code="label.welcome" text="Welcome!" />
			</h1>
		</div>
		<jsp:include page="../template/footer.jsp" />
	</div>
	<jsp:include page="../template/scripts.jsp" />
</body>
</html>