<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!-- ################################################################ Set Page Title ################################################################ -->
<c:set scope="request" var="title">
	title.403
</c:set>
<!-- ###################################################################### HTML #################################################################### -->
<!DOCTYPE html>
<html>
<!-- Include Header -->
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
					<spring:message code="title.error.403" text="403: Permission denied" />
				</h1>
			</div>
			<div class="content" id="content" role="main" data-spy="scroll">
				<spring:message code="error.403.access.denied" text="You do not have the nessesary permissions to perform this action!" />
			</div>
			<!-- ################################################################ End Container ################################################################# -->
		</div>
		<!-- ################################################################ Include Footer ################################################################ -->
		<jsp:include page="../template/footer.jsp" />
	</div>
	<jsp:include page="../template/scripts.jsp" />
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>
