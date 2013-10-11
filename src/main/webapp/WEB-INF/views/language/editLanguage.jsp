<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>

<!-- ################################################################ Set Page Title ################################################################ -->

<c:set scope="request" var="title">title.knowledgebase.Language.Update</c:set>

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
		
			<h1><spring:message code="label.language.update.form" />: ${language.name}</h1>
		
			<a href="../Display"><spring:message code="menu.navigate.back" /></a>
		
			<form:errors cssClass="error" element="div" />
			<c:if test="${!empty language}">
				<form:form method="post" action="../Update/${language.id}" commandName="language">
					<table class="data" border="1">
						<tr>
							<td><spring:message code="label.language.id" /></td>
							<td><input type="hidden" id="id" name="id" value="${language.id}"/>${language.id}</td>
						</tr>
						<tr>
							<td><spring:message code="label.language.alpha3" /></td>
							<td><input id="contactPerson" name="alpha3" type="text" value="${language.alpha3}"></td>
						</tr>
						<tr>
							<td><spring:message code="label.language.name" /></td>
							<td><input id="organisation" name="name" type="text" value="${language.name}"></td>
						</tr>
						<tr>
							<td><spring:message code="label.language.altName" /></td>
							<td><input id="address" name="altName" type="text" value="${language.altName}"></td>
						</tr>
						<tr>
							<td colspan="2"><input type="submit" value="<spring:message code="label.language.update.form" />"></td>
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