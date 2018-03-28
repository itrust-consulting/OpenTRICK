<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!-- ###################################################################### HTML #################################################################### -->
<c:if test="${empty locale }">
	<spring:eval expression="T(org.springframework.web.servlet.support.RequestContextUtils).getLocale(pageContext.request)" var="locale" scope="request"/>
</c:if>
<!DOCTYPE html>
<html lang="${locale.language}">
<!-- Include Header -->
<c:set scope="request" var="title" value="title.error.500" />
<jsp:include page="../template/header.jsp" />
<!-- ################################################################# Start Container ############################################################## -->
<body>
	<div id="wrap">
		<!-- ################################################################### Nav Menu ################################################################### -->
		<jsp:include page="../template/menu.jsp" />
		<div class="container">
			<jsp:include page="../template/successErrors.jsp" />
			<!-- #################################################################### Content ################################################################### -->
			<div class="page-header">
				<h1>
					<spring:message code="title.error.500" text="500: Internal Server Error" />
				</h1>
			</div>
			<div class="content" id="content" data-spy="scroll">
				<spring:message code="error.500.message" />
			</div>
			<!-- ################################################################ End Container ################################################################# -->
		</div>
		<!-- ################################################################ Include Footer ################################################################ -->
		<jsp:include page="../template/footer.jsp" />
	</div>
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>
				