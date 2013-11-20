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
<jsp:include page="../../header.jsp" />
<!-- ################################################################# Start Container ############################################################## -->
<body>
	<div id="wrap">
		<!-- ################################################################### Nav Menu ################################################################### -->
		<jsp:include page="../../menu.jsp" />
		<div class="container">
			<jsp:include page="../../successErrors.jsp" />
			<!-- #################################################################### Content ################################################################### -->
			<div class="row">
				<div class="page-header">
					<h1>Customer: ${customer.organisation}</h1>
				</div>
				<div class="content col-md-10" id="content" role="main" data-spy="scroll">
					<c:if test="${!empty customer}">
						<table class="table" border="1">
							<tr>
								<td><spring:message code="label.customer.id" /></td>
								<td>${customer.id}</td>
							</tr>
							<tr>
								<td><spring:message code="label.customer.organisation" /></td>
								<td>${customer.organisation}</td>
							</tr>
							<tr>
								<td><spring:message code="label.customer.contactPerson" /></td>
								<td>${customer.contactPerson}</td>
							</tr>
							<tr>
								<td><spring:message code="label.customer.telephoneNumber" /></td>
								<td>${customer.telephoneNumber}</td>
							</tr>
							<tr>
								<td><spring:message code="label.customer.email" /></td>
								<td>${customer.email}</td>
							</tr>
							<tr>
								<td><spring:message code="label.customer.address" /></td>
								<td>${customer.address}</td>
							</tr>
							<tr>
								<td><spring:message code="label.customer.ZIPCode" /></td>
								<td>${customer.ZIPCode}</td>
							</tr>
							<tr>
								<td><spring:message code="label.customer.city" /></td>
								<td>${customer.city}</td>
							</tr>
							<tr>
								<td><spring:message code="label.customer.country" /></td>
								<td>${customer.country}</td>
							</tr>
						</table>
					</c:if>
				</div>
			</div>
		</div>
		<jsp:include page="../../footer.jsp" />
		<jsp:include page="../../scripts.jsp" />
	</div>
</body>
</html>