<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<!-- ################################################################ Set Page Title ################################################################ -->

<c:set scope="request" var="title">title.analysis.history</c:set>

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

			<jsp:include page="../../successErrors.jsp" />

			<h1>
				<spring:message code="label.history.title" />
			</h1>

			<a href="Add"><spring:message code="label.history.add.menu" /></a>

			<c:if test="${!empty histories}">
				<table class="table">
					<thead>
						<tr>
							<th><spring:message code="label.history.id" /></th>
							<th><spring:message code="label.history.version" /></th>
							<th><spring:message code="label.history.date" /></th>
							<th><spring:message code="label.history.author" /></th>
							<th><spring:message code="label.history.comment" /></th>
							<th><spring:message code="label.action" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${histories}" var="history">
							<tr>
								<td>${history.id}</td>
								<td>${history.version}</td>
								<td>${history.date}</td>
								<td>${history.author}</td>
								<td>${history.comment}</td>
								<td><a href="Edit/${history.id}"><spring:message
											code="label.action.edit" /></a>|<a href="Delete/${history.id}"><spring:message
											code="label.action.delete" /></a></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:if>
			<c:if test="${empty histories}">
				<h4>
					<spring:message code="label.history.notexist" />
				</h4>
			</c:if>
		</div>

		<!-- ################################################################ Include Footer ################################################################ -->

		<jsp:include page="../../footer.jsp" />

		<!-- ################################################################ End Container ################################################################# -->

	</div>
	<jsp:include page="../../scripts.jsp" />
</body>

<!-- ################################################################### End HTML ################################################################### -->

</html>