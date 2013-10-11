<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>

<!-- ################################################################ Set Page Title ################################################################ -->

<c:set scope="request" var="title">label.knowledgebase.knowledgebase</c:set>

<!-- ###################################################################### HTML #################################################################### -->

<html>

<!-- Include Header -->
<jsp:include page="../header.jsp" />


<!-- ################################################################# Start Container ############################################################## -->

<body>
<div class="container">

<!-- ################################################################### Nav Menu ################################################################### -->

<jsp:include page="../menu.jsp" />

<!-- #################################################################### Content ################################################################### -->

<div class="content" id="content">
	<jsp:include page="../successErrors.jsp" />

	<h1>Customers</h1>

	<a href="../Display">Back</a>|<a href="Add">Add new customer</a>

	<c:if test="${!empty customers}">
		<table class="data" border="1">
			<tr>
				<th><spring:message code="label.customer.contactPerson" /></th>
				<th><spring:message code="label.customer.organisation" /></th>
				<th><spring:message code="label.customer.address" /></th>
				<th><spring:message code="label.customer.city" /></th>
				<th><spring:message code="label.customer.ZIPCode" /></th>
				<th><spring:message code="label.customer.country" /></th>
				<th><spring:message code="label.customer.telephoneNumber" /></th>
				<th><spring:message code="label.customer.email" /></th>
				<th><spring:message code="label.customer.action" /></th>
			</tr>
			<c:forEach items="${customers}" var="customer">
				<tr>
					<td>${customer.contactPerson}</td>
					<td>${customer.organisation}</td>
					<td>${customer.address}</td>
					<td>${customer.ZIPCode}</td>
					<td>${customer.city}</td>
					<td>${customer.country}</td>
					<td>${customer.telephoneNumber}</td>
					<td>${customer.email}</td>
					<td><a href="Edit/${customer.id}">Update/Save</a>|<a href="Delete/${customer.id}">Delete</a></td>
				</tr>
			</c:forEach>
		</table>
	</c:if>
	<c:if test="${empty customers}">
	<h4>No Customers exist!</h4>	
	</c:if>
</div>
		
<!-- ################################################################ Include Footer ################################################################ -->

<jsp:include page="../footer.jsp" />

<!-- ################################################################ End Container ################################################################# -->

</div>
</body>

<!-- ################################################################### End HTML ################################################################### -->

</html>