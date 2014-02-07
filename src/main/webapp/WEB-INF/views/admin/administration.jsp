<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!-- ################################################################ Set Page Title ################################################################ -->
<c:set scope="request" var="title">title.administration</c:set>
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
					<spring:message code="title.administration" text="Administration" />
				</h1>
			</div>
			<div class="content" id="content" role="main" data-spy="scroll">
				<div id="messages"></div>
				<jsp:include page="user/users.jsp" />
				<jsp:include page="user/widgetcontent.jsp" />
				<jsp:include page="./customer/customers.jsp" />
				<jsp:include page="./customer/widgetcontent.jsp" />
				<jsp:include page="./analysis/analyses.jsp" />
				<jsp:include page="./analysis/widgetContent.jsp" />
			</div>
			<!-- ################################################################ End Container ################################################################# -->
		</div>
		<!-- ################################################################ Include Footer ################################################################ -->
		<jsp:include page="../footer.jsp" />
		<jsp:include page="../scripts.jsp" />
		<script type="text/javascript">
			$(document).ready(function() {
				$("input[type='checkbox']").removeAttr("checked");
			});
		</script>
	</div>
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>