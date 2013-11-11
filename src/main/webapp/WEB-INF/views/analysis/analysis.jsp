<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<!-- ################################################################ Set Page Title ################################################################ -->

<c:set scope="request" var="title">title.analysis</c:set>

<!-- ###################################################################### HTML #################################################################### -->

<html>

<!-- Include Header -->
<jsp:include page="../header.jsp" />
<script type="text/javascript">
	
</script>

<!-- ################################################################# Start Container ############################################################## -->

<body>
	<div class="container">

		<!-- ################################################################### Nav Menu ################################################################### -->

		<jsp:include page="../menu.jsp" />

		<jsp:include page="../successErrors.jsp" />
		<!-- #################################################################### Content ################################################################### -->


		<div class="row">
			<div class="page-header">

				<c:choose>
					<c:when test="${!empty(analyses) }">
						<h1>
							<spring:message code="label.analysis.title" />
						</h1>
					</c:when>
					<c:when test="${!empty(analysis) }">
						<h1 title="${analysis.label}">${analysis.customer.contactPerson }
							| ${analysis.getVersion() }</h1>
					</c:when>
				</c:choose>

			</div>
			<!-- #################################################################### Analysis Menu ################################################################### -->

			<c:if test="${!empty(sessionScope.selectedAnalysis)}">
				<jsp:include page="analysisMenu.jsp" />
				<div class="content col-md-9" id="content" role="main" data-spy="scroll">
					<jsp:include page="./components/history.jsp" />
					<jsp:include page="./components/itemInformation.jsp" />
					<jsp:include page="./components/parameter.jsp" />
					<jsp:include page="./components/asset.jsp" />
				</div>
			</c:if>
			<c:if
				test="${!empty analyses and empty(sessionScope.selectedAnalysis)}">
				<div class="table-responsive">

					<table class="table table-bordered">
						<thead>
							<tr class="table-header">
								<th><spring:message code="label.analysis.identifier" /></th>
								<th><spring:message code="label.analysis.version" /></th>
								<th><spring:message code="label.analysis.creationDate" /></th>
								<th><spring:message code="label.analysis.language" /></th>
								<th><spring:message code="label.analysis.label" /></th>
								<th><spring:message code="label.action" /></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${analyses}" var="analysis">
								<tr>
									<td>${analysis.identifier}</td>
									<td>${analysis.version}</td>
									<td>${analysis.creationDate}</td>
									<td>${analysis.language.name}</td>
									<td>${analysis.label}</td>
									<td><a class="btn btn-primary btn-sm"
										href="${pageContext.request.contextPath}/Analysis/${analysis.id}/Select">
											<c:if test="${sessionScope.selectedAnalysis != null }">
												<c:if test="${sessionScope.selectedAnalysis == analysis.id}">
													<samp class="glyphicon glyphicon-minus"></samp>
												</c:if>
												<c:if test="${sessionScope.selectedAnalysis != analysis.id}">
													<samp class="glyphicon glyphicon-pushpin"></samp>
												</c:if>
											</c:if> <c:if test="${sessionScope.selectedAnalysis == null }">
												<samp class="glyphicon glyphicon-pushpin"></samp>
											</c:if>
									</a> <a class="btn btn-danger btn-sm"
										href="${pageContext.request.contextPath}/Analysis/Delete/${analysis.id}"
										title='<spring:message code="label.action.delete" />'> <samp
												class="glyphicon glyphicon-trash"></samp>
									</a> <a class="btn btn-warning btn-sm"
										href="${pageContext.request.contextPath}/Analysis/Edit/${analysis.id}"
										title='<spring:message code="label.action.edit" />'> <samp
												class="glyphicon glyphicon-edit"></samp>
									</a> <c:choose>
											<c:when test="${!analysis.isEmpty()}">
												<a class="btn btn-success btn-sm"
													href="${pageContext.request.contextPath}/export/analysis/${analysis.id}"
													title='<spring:message code="label.action.export" />'><samp
														class="glyphicon glyphicon-floppy-open"></samp></a>
												<a class="btn btn-default btn-sm"
													href="${pageContext.request.contextPath}/analysis/${analysis.id}/compute/actionPlan"
													title='<spring:message code="label.action.compute.actionPlan" />'>
													<samp class="glyphicon glyphicon-play"></samp>
												</a>

												<a class="btn btn-default btn-sm"
													href="${pageContext.request.contextPath}/analysis/${analysis.id}/compute/riskRegister"
													title="<spring:message code="label.action.compute.riskRegister" />">
													<samp class="glyphicon glyphicon-play-circle"></samp>
												</a>
											</c:when>
											<c:otherwise>
										&nbsp;
									</c:otherwise>
										</c:choose></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</c:if>
			<!-- ################################################################ Include Footer ################################################################ -->

		</div>
		<jsp:include page="../footer.jsp" />
	</div>

	<!-- ################################################################ End Container ################################################################# -->

	<jsp:include page="../scripts.jsp" />
</body>

<!-- ################################################################### End HTML ################################################################### -->

</html>