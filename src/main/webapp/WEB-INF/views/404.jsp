<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<html>
<head>
<title><spring:message code="label.title.404" text="Error 404: Not Found" /></title>
<link rel="stylesheet" type="text/css"
	href='<spring:url value="/css/main.css" />' />
<META HTTP-EQUIV="refresh"
	CONTENT="<spring:message code='label.error.404.redirect.value' text='3' />;${pageContext.request.contextPath}/index">
</head>
<body>
	<div class="container">
		<div class="content" id="content">
			<c:choose>
				<c:when
					test="${'XMLHttpRequest' != request.getHeader('X-Requested-With')}">
					<spring:message
						code="errors.404.not.found label.redirect error.404.not.found.redirect.value error.404.not.found.redirect.unit"
						text="Not Found, You will be redirected in 3 seconds" />
				</c:when>
				<c:otherwise>
					<spring:message code="errors.404.not.found" text="Not Found" />
				</c:otherwise>
			</c:choose>
		</div>
		<div class="footer"><jsp:include page="footer.jsp" /></div>
	</div>
</body>
</html>