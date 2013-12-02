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
<<<<<<< HEAD


=======
>>>>>>> refs/heads/knowldegebase_menu
<!-- ################################################################# Start Container ############################################################## -->
<body>
<<<<<<< HEAD
	<div class="container">

<!-- ################################################################### Nav Menu ################################################################### -->

		<jsp:include page="../../menu.jsp" />

<!-- #################################################################### Content ################################################################### -->

		<div class="content" id="content">
			
			<h1>Customer: ${customerProfil.organisation}</h1>
			
			<form:errors cssClass="error" element="div" />
			<c:if test="${!empty customerProfil}">
				<table class="data" border="1">
					<tr>
						<td><spring:message code="label.customer.id" /></td>
						<td>${customerProfil.id}</td>
					</tr>
					<tr>
						<td><spring:message code="label.customer.contactPerson" /></td>
						<td>${customerProfil.contactPerson}</td>
					</tr>
					<tr>
						<td><spring:message code="label.customer.organisation" /></td>
						<td>${customerProfil.organisation}</td>
					</tr>
					<tr>
						<td><spring:message code="label.customer.address" /></td>
						<td>${customerProfil.address}</td>
					</tr>
					<tr>
						<td><spring:message code="label.customer.ZIPCode" /></td>
						<td>${customerProfil.ZIPCode}</td>
					</tr>
					<tr>
						<td><spring:message code="label.customer.city" /></td>
						<td>${customerProfil.city}</td>
					</tr>
					<tr>
						<td><spring:message code="label.customer.country" /></td>
						<td>${customerProfil.country}</td>
					</tr>
					<tr>
						<td><spring:message code="label.customer.telephoneNumber" /></td>
						<td>${customerProfil.telephoneNumber}</td>
					</tr>
					<tr>
						<td><spring:message code="label.customer.email" /></td>
						<td>${customerProfil.email}</td>
					</tr>
				</table>
			</c:if>
=======
	<div id="wrap">
		<!-- ################################################################### Nav Menu ################################################################### -->
		<jsp:include page="../../menu.jsp" />
		<div class="container">
			<jsp:include page="../../successErrors.jsp" />
			<!-- #################################################################### Content ################################################################### -->
			<div class="row">
				<div class="page-header">
					<h1><spring:message code="label.language" />: ${language.name}</h1>
				</div>
				<div class="content col-md-10" id="content" role="main" data-spy="scroll">
					<c:if test="${!empty language}">
						<table class="table" border="1">
							<tr>
								<td><spring:message code="label.language.id" /></td>
								<td>${language.id}</td>
							</tr>
							<tr>
								<td><spring:message code="label.language.name" /></td>
								<td>${language.name}</td>
							</tr>
							<tr>
								<td><spring:message code="label.language.altName" /></td>
								<td>${language.altName}</td>
							</tr>
						</table>
					</c:if>
				</div>
			</div>
>>>>>>> refs/heads/knowldegebase_menu
		</div>
<<<<<<< HEAD

<!-- ################################################################ Include Footer ################################################################ -->

		<jsp:include page="../../footer.jsp" />

<!-- ################################################################ End Container ################################################################# -->

=======
		<jsp:include page="../../footer.jsp" />
		<jsp:include page="../../scripts.jsp" />
>>>>>>> refs/heads/knowldegebase_menu
	</div>
<<<<<<< HEAD
	<jsp:include page="../../scripts.jsp" />
=======
>>>>>>> refs/heads/knowldegebase_menu
</body>
</html>
