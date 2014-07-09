<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<html>
<head>
<title><spring:message code="label.title.401"
		text="Error 401: Unauthorized" /></title>
<link rel="stylesheet" type="text/css"
	href='<spring:url value="/css/main.css" />' />
<META HTTP-EQUIV="refresh"
	CONTENT="<spring:message code='label.error.401.redirect.value' text='3' />;${pageContext.request.contextPath}/index">
</head>
<body>
	<div class="container">
		<div class="content" id="content">
			<c:choose>
				<c:when
					test="${'XMLHttpRequest' != request.getHeader('X-Requested-With')}">
					<spring:message
						code="errors.401.unauthorized label.redirect label.error.401.redirect.value label.error.401.redirect.unit"
						text="Unauthorized, You will be redirected in 3 seconds" />
				</c:when>
				<c:otherwise>
					<spring:message code="errors.401.unauthorized" text="Unauthorized" />
				</c:otherwise>
			</c:choose>
		</div>
		<div class="footer"><jsp:include page="../footer.jsp" /></div>
	</div>
	<jsp:include page="../scripts.jsp" />
</body>
</html>