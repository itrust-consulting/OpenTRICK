<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>

<!-- ############################################################### Set Page Title ################################################################# -->

<c:set scope="request" var="title">title.analysis.update</c:set>

<!-- #################################################################### HTML ###################################################################### -->

<html>

<!-- ################################################################ Include Header ################################################################ -->
<jsp:include page="../header.jsp" />


<!-- ############################################################### Start Container ################################################################ -->

<body>
	<div class="container">

		<!-- ################################################################### Nav Menu ################################################################### -->

		<jsp:include page="../menu.jsp" />

		<!-- ################################################################### Content #################################################################### -->

		<div class="content" id="content">

			<h1><spring:message code="label.analysis.update.title" /></h1>

			<a href="../Display"><spring:message code="menu.navigate.back" /></a>

			<c:if test="${!empty customers and !empty analysis and !empty languages }">
						
				<form:errors cssClass="error" element="div" />

				<form:form method="post" action="${analysis.id}/Save" commandName="analysis">
					<table class="data" border="1">
						<tr>
							<td><form:label path="id"><spring:message code="label.analysis.id" /></form:label></td>
							<td><input type="hidden" value="${analysis.id}" name="id" id="id"/>${analysis.id}</td>
						</tr>
						<tr>
							<td><form:label path="identifier"><spring:message code="label.analysis.identifier" /></form:label></td>
							<td><input type="hidden" value="${analysis.identifier}" name="identifier" id="identifier"/>${analysis.identifier}</td>
						</tr>
						<tr>
							<td><form:label path="version"><spring:message code="label.analysis.version" /></form:label></td>
							<td><input type="hidden" value="${analysis.version}" name="version" id="version"/>${analysis.version}</td>
						</tr>
						<tr>
							<td><form:label path="creationDate"><spring:message code="label.analysis.creationDate" /></form:label></td>
							<td><input type="hidden" value="${analysis.creationDate}" name="creationDate" id="creationDate"/>${analysis.creationDate}</td>
						</tr>
						<tr>
							<td><form:label path="customer.id"><spring:message code="label.analysis.customer" /></form:label></td>
							<td>
								<form:select path="customer.id" itemLabel="${customer.alpha3}">
									<form:options items="${customers}" itemLabel="organisation" itemValue="id" />
								</form:select>
							</td>
						</tr>
						<tr>
							<td><form:label path="language.id"><spring:message code="label.analysis.language" /></form:label></td>
							<td>
								<form:select path="language.id" itemLabel="${language.alpha3}">
									<form:options items="${languages}" itemLabel="alpha3" itemValue="id" />
								</form:select>
							</td>
						</tr>
						<tr>
							<td><form:label path="label"><spring:message code="label.analysis.label" /></form:label></td>
							<td><form:input path="label" /></td>
						</tr>
						<tr>
							<td colspan="2"><input type="submit" value="<spring:message code="label.analysis.update.submit"/>" /></td>
						</tr>
					</table>
				</form:form>
			</c:if>
		</div>

		<!-- ################################################################ Include Footer ################################################################ -->

		<jsp:include page="../footer.jsp" />

		<!-- ################################################################ End Container ################################################################# -->

	</div>
</body>

<!-- ################################################################### End HTML ################################################################### -->

</html>