<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<!-- ################################################################ Set Page Title ################################################################ -->

<c:set scope="request" var="title">title.analysis.history.add</c:set>

<!-- ###################################################################### HTML #################################################################### -->

<html>

<!-- Include Header -->
<jsp:include page="../../header.jsp" />


<!-- ################################################################# Start Container ############################################################## -->

<body>
	<div class="container">

		<!-- ################################################################### Nav Menu ################################################################### -->

		<jsp:include page="../../menu.jsp" />

		<!-- #################################################################### Content ################################################################### -->

		<div class="content" id="content">

			<!-- #################################################################### Analysis Menu ################################################################### -->

			<c:if test="${sessionScope.selectedAnalysis != null}">
				<c:if test="${sessionScope.selectedAnalysis > 0 }">
					<jsp:include page="../analysisMenu.jsp" />
				</c:if>
			</c:if>

			<h1>
				<spring:message code="label.history.add.menu" />
			</h1>

			<jsp:include page="../../successErrors.jsp" />

			<a href="../History/Display">
				<spring:message code="menu.navigate.back" />
			</a>
			
			<form:errors cssClass="error" element="div" />
			
			<form:form method="post" action="Create" commandName="history">
			
				<table border="1">
					<tr>
						<td><form:label path="version"><spring:message code="label.history.version" /></form:label></td>
						<td><input id="version" name="version" value="<c:if test="${history != null }">${history.version}</c:if>" /></td>
					</tr>
					<tr>
						<td><form:label path="date"><spring:message code="label.history.date" /></form:label></td>
						<td><input id="date" name="date" value="<c:if test="${history != null }"></c:if>" /></td>
					</tr>
					<tr>
						<td><form:label path="author"><spring:message code="label.history.author" /></form:label></td>
						<td><input id="author" name="author" value="<c:if test="${history != null }">${history.author}</c:if>" /></td>
					</tr>
					<tr>
						<td><form:label path="comment"><spring:message code="label.history.comment" /></form:label></td>
						<td><textarea id="comment" name="comment"><c:if test="${history != null }">${history.comment}</c:if></textarea></td>
					</tr>
					<tr>
						<td colspan="2"><input type="submit" value="<spring:message code="label.history.add.form"/>" /></td>
					</tr>
				</table>
			</form:form>
		</div>

		<!-- ################################################################ Include Footer ################################################################ -->

		<jsp:include page="../../footer.jsp" />

		<!-- ################################################################ End Container ################################################################# -->

	</div>
</body>

<!-- ################################################################### End HTML ################################################################### -->

</html>