<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>

<!-- ################################################################ Set Page Title ################################################################ -->

<c:set scope="request" var="title">title.knowledgebase.Customer.Update</c:set>

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
		
			<h1><spring:message code="label.customer.update.form" />: ${customerProfil.organisation}</h1>
		
			<a href="../Display"><spring:message code="menu.navigate.back" /></a>
		
			<form:errors cssClass="error" element="div" />
			<c:if test="${!empty customerProfil}">
				<form:form method="post" action="../Update/${customerProfil.id}" commandName="customer">
					<table class="data" border="1">
						<tr>
							<td><spring:message code="label.customer.id" /></td>
							<td><input type="hidden" id="id" name="id" value="${customerProfil.id}"/>${customerProfil.id}</td>
						</tr>
						<tr>
							<td><spring:message code="label.customer.contactPerson" /></td>
							<td><input id="contactPerson" name="contactPerson" type="text" value="${customerProfil.contactPerson}"></td>
						</tr>
						<tr>
							<td><spring:message code="label.customer.organisation" /></td>
							<td><input id="organisation" name="organisation" type="text" value="${customerProfil.organisation}"></td>
						</tr>
						<tr>
							<td><spring:message code="label.customer.address" /></td>
							<td><input id="address" name="address" type="text" value="${customerProfil.address}"></td>
						</tr>
						<tr>
							<td><spring:message code="label.customer.ZIPCode" /></td>
							<td><input id="ZIPCode" name="ZIPCode" type="text" value="${customerProfil.ZIPCode}"></td>
						</tr>
						<tr>
							<td><spring:message code="label.customer.city" /></td>
							<td><input id="city" name="city" type="text" value="${customerProfil.city}"></td>
						</tr>
						<tr>
							<td><spring:message code="label.customer.country" /></td>
							<td><input id="country" name="country" type="text" value="${customerProfil.country}"></td>
						</tr>
						<tr>
							<td><spring:message code="label.customer.telephoneNumber" /></td>
							<td><input id="telephoneNumber" name="telephoneNumber" type="text" value="${customerProfil.telephoneNumber}"></td>
						</tr>
						<tr>
							<td><spring:message code="label.customer.email" /></td>
							<td><input id="email" name="email" type="text" value="${customerProfil.email}"></td>
						</tr>
						<tr>
							<td colspan="2"><input type="submit" value="<spring:message code="label.customer.update.form" />"></td>
						</tr>
					</table>
				</form:form>
			</c:if>
		</div>

<!-- ################################################################ Include Footer ################################################################ -->

		<jsp:include page="../footer.jsp" />

<!-- ################################################################ End Container ################################################################# -->

	</div>
	<jsp:include page="../scripts.jsp" />
</body>

<!-- ################################################################### End HTML ################################################################### -->

</html>