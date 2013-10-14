<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>

<!-- ################################################################ Set Page Title ################################################################ -->

<c:set scope="request" var="title">title.knowledgebase.Standard</c:set>

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

	<h1><spring:message code="menu.knowledgebase.standards.norms" /></h1>

	<a href="../../Display"><spring:message code="menu.navigate.back" /></a>|<a href="Add"><spring:message code="label.norm.add.menu" /></a>

	<c:if test="${!empty norms}">
		<table class="data" border="1">
			<tr>
				<th><spring:message code="label.norm.id" text="id" /></th>
				<th><spring:message code="label.norm.label" /></th>
				<th><spring:message code="label.action" /></th>
			</tr>
			<c:forEach items="${norms}" var="norm">
				<tr>
					<td>${norm.id}</td>
					<td>${norm.label}</td>
					<td><a href="Edit/${norm.id}"><spring:message code="label.action.edit" /></a>|<a href="Delete/${norm.id}"><spring:message code="label.action.delete" /></a></td>
				</tr>
			</c:forEach>
		</table>
	</c:if>
	<c:if test="${empty norms}">
	<h4><spring:message code="label.norm.notexist" /></h4>	
	</c:if>
</div>
		
<!-- ################################################################ Include Footer ################################################################ -->

<jsp:include page="../footer.jsp" />

<!-- ################################################################ End Container ################################################################# -->

</div>
</body>

<!-- ################################################################### End HTML ################################################################### -->

</html>