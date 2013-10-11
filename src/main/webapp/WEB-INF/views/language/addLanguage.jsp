<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>

<!-- ################################################################ Set Page Title ################################################################ -->

<c:set scope="request" var="title">title.knowledgebase.Language.Add</c:set>

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

	<h1><spring:message code="label.language.add.menu" /></h1>
	<a href="../Language/Display"><spring:message code="menu.navigate.back" /></a>
	<form:errors cssClass="error" element="div" />
	<form:form method="post" action="Create" commandName="language">
		<table border="1">
			<tr>
				<td><form:label path="alpha3">
						<spring:message code="label.language.alpha3" />
					</form:label></td>
				<td><form:input path="alpha3" /></td>
			</tr>
			<tr>
				<td><form:label path="name">
						<spring:message code="label.language.name" />
					</form:label></td>
				<td><form:input path="name" /></td>
			</tr>
			<tr>
				<td><form:label path="altName">
						<spring:message code="label.language.altName" />
					</form:label></td>
				<td><form:input path="altName" /></td>
			</tr>
			<tr>
				<td colspan="2"><input type="submit"
					value="<spring:message code="label.language.add.form"/>" /></td>
			</tr>
		</table>
	</form:form>
</div>
		
<!-- ################################################################ Include Footer ################################################################ -->

<jsp:include page="../footer.jsp" />

<!-- ################################################################ End Container ################################################################# -->

</div>
</body>

<!-- ################################################################### End HTML ################################################################### -->

</html>