<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<!-- ################################################################ Set Page Title ################################################################ -->

<c:set scope="request" var="title">title.knowledgebase</c:set>

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

			<div class="row">
				<div class="page-header">
				
					<h1><spring:message code="title.knowledgebase" text="Knowledge Base" /></h1>
				</div>
				<jsp:include page="knowledgebasemenu.jsp" />

				<div class="content col-md-10" id="content" role="main"
					data-spy="scroll">
					<jsp:include page="customer/customers.jsp" />
					<jsp:include page="language/languages.jsp" />
					<jsp:include page="standard/norm/norms.jsp" />
					<jsp:include page="widget.jsp" />
				</div>

			</div>
			
			

			<!-- ################################################################ End Container ################################################################# -->

		</div>

		<!-- ################################################################ Include Footer ################################################################ -->

		<jsp:include page="../footer.jsp" />
		<jsp:include page="../scripts.jsp" />
	</div>
</body>

<!-- ################################################################### End HTML ################################################################### -->

</html>