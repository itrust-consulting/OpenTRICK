<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!-- ################################################################ Set Page Title ################################################################ -->
<c:set scope="request" var="title">label.home</c:set>
<!-- ###################################################################### HTML #################################################################### -->
<!DOCTYPE html>
<html>
<!-- Include Header -->
<jsp:include page="../../../template/header.jsp" />
<!-- ################################################################# Start Container ############################################################## -->
<body>
	<div id="wrap">
		<!-- ################################################################### Nav Menu ################################################################### -->
		<jsp:include page="../../../template/menu.jsp" />
		<div class="container">
			<jsp:include page="../../../template/successErrors.jsp" />
			<!-- #################################################################### Content ################################################################### -->
			<div class="row">
				<div class="page-header">
					<h1>
						<spring:message code="label.title.norm" text="Standard" />
						:
						<spring:message text="${standard.name}" />
					</h1>
				</div>
				<div class="content col-md-10" id="content" role="main" data-spy="scroll">
					<c:if test="${!empty standard}">
						<table class="table" border="1">
							<tr>
								<td><spring:message code="label.norm.id" text="Id" /></td>
								<td>${standard.id}</td>
							</tr>
							<tr>
								<td><spring:message code="label.norm.label" text="Name" /></td>
								<td><spring:message text="${standard.label}" /></td>
							</tr>
							<tr>
								<td><spring:message code="label.norm.version" text="Version" /></td>
								<td><spring:message text="${standard.version}" /></td>
							</tr>
							<tr>
								<td><spring:message code="label.norm.computable" text="Computable" /></td>
								<td><spring:message code="label.yes_no.${standard.computable}" text="${standard.computable?'Yes':'No'}" /></td>
							</tr>
						</table>
					</c:if>
				</div>
			</div>
		</div>
		<jsp:include page="../../../template/footer.jsp" />
	</div>
	<jsp:include page="../../../template/scripts.jsp" />
</body>
</html>