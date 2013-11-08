<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>

<!-- ################################################################ Set Page Title ################################################################ -->

<c:set scope="request" var="title">title.knowledgebase.Measure</c:set>

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

	<h1><spring:message code="label.measure.measures" /> ${normLabel}</h1>

	<a href="../../Display"><spring:message code="menu.navigate.back" /></a>|<a href="Add"><spring:message code="label.measure.add.menu" /></a>

	<c:if test="${!empty measureDescriptions}">
		<table class="data" border="1">
			<tr>
				<th><spring:message code="label.measure.id" text="id" /></th>
				<th><spring:message code="label.measure.level" /></th>
				<th><spring:message code="label.measure.reference" /></th>
				<th><spring:message code="label.measure.domain" /></th>
				<th><spring:message code="label.measure.description" /></th>
				<th><spring:message code="label.action" /></th>
			</tr>
			<c:forEach items="${measureDescriptions}" var="measureDescription">
				<tr>
					<td>${measureDescription.id}</td>
					<td>${measureDescription.level}</td>
					<td>${measureDescription.reference}</td>
					<td>${measureDescription.measureDescriptionTexts[0].domain.equals("")==false?measureDescription.measureDescriptionTexts[0].domain:"&nbsp;"}</td>
					<td>${measureDescription.measureDescriptionTexts[0].description.equals("")==false?measureDescription.measureDescriptionTexts[0].description:"&nbsp;"}</td>
					<td>
						<a href="Edit/${measureDescription.id}"><spring:message code="label.action.edit" /></a>|
						<a href="Delete/${measureDescription.id}"><spring:message code="label.action.delete" /></a>
					</td>
				</tr>
			</c:forEach>
		</table>
	</c:if>
	<c:if test="${empty measureDescriptions}">
	<h4><spring:message code="label.measure.notexist" /></h4>	
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