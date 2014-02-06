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
<jsp:include page="../../../header.jsp" />
<!-- ################################################################# Start Container ############################################################## -->
<body>
	<div id="wrap">
		<!-- ################################################################### Nav Menu ################################################################### -->
		<jsp:include page="../../../menu.jsp" />
		<div class="container">
			<jsp:include page="../../../successErrors.jsp" />
			<!-- #################################################################### Content ################################################################### -->
			<div class="row">
				<div class="page-header">
					<h1>
						<spring:message code="label.norm" />
						: ${norm.name}
					</h1>
				</div>
				<div class="content col-md-10" id="content" role="main" data-spy="scroll">
					<c:if test="${!empty norm}">
						<table class="table" border="1">
							<tr>
								<td><spring:message code="label.norm.id" /></td>
								<td>${norm.id}</td>
							</tr>
							<tr>
								<td><spring:message code="label.norm.label" /></td>
								<td>${norm.label}</td>
							</tr>
							<tr>
								<td><spring:message code="label.norm.version" /></td>
								<td>${norm.version}</td>
							</tr>
							<tr>
								<td><spring:message code="label.norm.Computable" /></td>
								<td>${norm.computable?'Yes':'No'}</td>
							</tr>
						</table>
					</c:if>
				</div>
			</div>
		</div>
		<jsp:include page="../../../footer.jsp" />
		<jsp:include page="../../../scripts.jsp" />
	</div>
</body>
</html>