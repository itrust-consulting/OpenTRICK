<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!-- ################################################################ Set Page Title ################################################################ -->
<c:set scope="request" var="title">label.home</c:set>
<!-- ###################################################################### HTML #################################################################### -->
<html>
<!-- Include Header -->
<jsp:include page="header.jsp" />
<!-- ################################################################# Start Container ############################################################## -->
<body>
	<div id="wrap">
		<!-- ################################################################### Nav Menu ################################################################### -->
		<jsp:include page="menu.jsp" />
		<div class="container">
			<!-- #################################################################### Content ################################################################### -->

			<h1 class="text-center" style="margin-top: 15%">
				<spring:message code="label.welcome" text="Welcome to TRICK Service" />
			</h1>


			<!-- ################################################################ Include Footer ################################################################ -->
		</div>
		<jsp:include page="footer.jsp" />
		<!-- ################################################################ End Container ################################################################# -->
		<jsp:include page="scripts.jsp" />
	</div>
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>