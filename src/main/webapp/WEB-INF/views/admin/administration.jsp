<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!-- ################################################################ Set Page Title ################################################################ -->
<c:set scope="request" var="title">label.title.administration</c:set>
<!-- ###################################################################### HTML #################################################################### -->
<!DOCTYPE html>
<html>
<!-- Include Header -->
<jsp:include page="../header.jsp" />
<!-- ################################################################# Start Container ############################################################## -->
<body>
	<div id="wrap">
		<!-- ################################################################### Nav Menu ################################################################### -->
		<jsp:include page="../menu.jsp" />
		<div class="container">
			<jsp:include page="../successErrors.jsp" />
			<!-- #################################################################### Content ################################################################### -->
			<div class="page-header">
				<h1>
					<spring:message code="label.title.administration" text="Administration" />
				</h1>
			</div>
			<div class="content" id="content">
				<div id="messages"></div>
				<jsp:include page="status.jsp" />
				<jsp:include page="user/users.jsp" />
				<jsp:include page="./customer/customers.jsp" />
				<jsp:include page="./analysis/analyses.jsp" />
				<jsp:include page="widget.jsp" />
			</div>
			<!-- ################################################################ End Container ################################################################# -->
		</div>
		<!-- ################################################################ Include Footer ################################################################ -->
		<jsp:include page="../footer.jsp" />
		<jsp:include page="../scripts.jsp" />
		<script type="text/javascript" src="<spring:url value="js/trickservice/administration.js" />"></script>
		<script type="text/javascript" src="<spring:url value="js/trickservice/customer.js" />"></script>
		<script type="text/javascript" src="<spring:url value="js/trickservice/user.js" />"></script>
	</div>
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>