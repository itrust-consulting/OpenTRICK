			
					<spring:message code="error.405.method.not.allowed" text="Method Not Allowed" />
				
				
				
				
				<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!-- ################################################################ Set Page Title ################################################################ -->
<c:set scope="request" var="title">
	title.405
</c:set>
<!-- ###################################################################### HTML #################################################################### -->
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
					<spring:message code="title.error.405" text="405: Method not allowed" />
				</h1>
			</div>
			<div class="content" id="content" data-spy="scroll">
				<spring:message code="error.405.method_not_allowed" text="The method used for this request is not allowed!" />
			</div>
			<!-- ################################################################ End Container ################################################################# -->
		</div>
		<!-- ################################################################ Include Footer ################################################################ -->
		<jsp:include page="../footer.jsp" />
	</div>
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>
				