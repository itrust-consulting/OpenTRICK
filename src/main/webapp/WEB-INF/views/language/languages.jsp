<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>

<!-- ################################################################ Set Page Title ################################################################ -->

<c:set scope="request" var="title">title.knowledgebase.Language</c:set>

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

	<h1><spring:message code="menu.knowledgebase.languages" /></h1>

	<a href="../Display"><spring:message code="menu.navigate.back" /></a>|<a href="Add"><spring:message code="label.language.add.menu" /></a>

	<c:if test="${!empty languages}">
		<table class="data" border="1">
			<tr>
				<th><spring:message code="label.language.id" text="id" /></th>
				<th><spring:message code="label.language.alpha3" /></th>
				<th><spring:message code="label.language.name" /></th>
				<th><spring:message code="label.language.altName" /></th>
				<th><spring:message code="label.action" /></th>
			</tr>
			<c:forEach items="${languages}" var="language">
				<tr>
					<td>${language.id}</td>
					<td>${language.alpha3}</td>
					<td>${language.name}</td>
					<td>${language.altName}</td>
					<td><a href="Edit/${language.id}"><spring:message code="label.action.edit" /></a>|<a href="Delete/${language.id}"><spring:message code="label.action.delete" /></a></td>
				</tr>
			</c:forEach>
		</table>
	</c:if>
	<c:if test="${empty languages}">
	<h4><spring:message code="label.language.notexist" /></h4>	
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