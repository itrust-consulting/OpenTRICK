<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<!-- ################################################################ Set Page Title ################################################################ -->
<c:set scope="request" var="title">title.analysis</c:set>
<!-- ###################################################################### HTML #################################################################### -->
<html>
<!-- Include Header -->
<jsp:include page="../header.jsp" />
<!-- ################################################################# Start Container ############################################################## -->
<body data-spy="scroll" data-target="#nav-container" data-offset="40">
	<div id="wrap">
		<!-- ################################################################### Nav Menu ################################################################### -->
		<jsp:include page="../menu.jsp" />
		<div class="container">
			<!-- #################################################################### Content ################################################################### -->
			<div class="page-header">
				<h1>
					<spring:message code="label.analysis.title" text="Analyses" />
				</h1>
				<jsp:include page="../successErrors.jsp" />
			</div>
			<div class="row nav-container" id="nav-container">
				<!-- #################################################################### Analysis Menu ################################################################### -->
				<c:if test="${!empty(sessionScope.selectedAnalysis)}">
					<jsp:include page="analysisMenu.jsp" />
					<jsp:include page="../successErrors.jsp" />
					<div class="content nav-container" id="content">
						<c:set var="histories" value="${analysis.histories}" scope="request" />
						<jsp:include page="./components/history.jsp" />
						<c:set var="itemInformations" value="${analysis.itemInformations}" scope="request" />
						<jsp:include page="./components/itemInformation.jsp" />
						<c:set var="parameters" value="${analysis.parameters}" scope="request" />
						<jsp:include page="./components/parameter.jsp" />
						<c:set var="assets" value="${analysis.assets}" scope="request" />
						<jsp:include page="./components/asset.jsp" />
						<c:set var="scenarios" value="${analysis.scenarios}" scope="request" />
						<jsp:include page="./components/scenario.jsp" />
						<spring:eval expression="T(lu.itrust.business.component.MeasureManager).ConcatMeasure(analysis.analysisNorms)" var="measures" scope="request" />
						<jsp:include page="./components/measure.jsp" />
						<c:set var="phases" scope="request" value="${analysis.usedPhases}" />
						<jsp:include page="./components/phase.jsp" />
						<jsp:include page="./components/charts.jsp" />
						<jsp:include page="./components/widgets.jsp" />
					</div>
				</c:if>
				<c:if test="${!empty analyses and empty(sessionScope.selectedAnalysis)}">
					<div class="table-responsive" id="section_analysis">
						<table class="table table-bordered">
							<thead>
								<tr class="table-header">
									<th><spring:message code="label.analysis.id" /></th>
									<th><spring:message code="label.analysis.customer" /></th>
									<th><spring:message code="label.analysis.label" /></th>
									<th><spring:message code="label.analysis.creationDate" /></th>
									<th><spring:message code="label.analysis.version" /></th>
									<th><spring:message code="label.analysis.author" /></th>
									<th><spring:message code="label.analysis.basedOnAnalysis" /></th>
									<th><spring:message code="label.analysis.language" /></th>
									<th><spring:message code="label.action" /></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${analyses}" var="analysis">
									<tr>
										<td>${analysis.id}</td>
										<td>${analysis.customer.organisation}</td>
										<td>${analysis.label}</td>
										<td>${analysis.creationDate}</td>
										<td>${analysis.version}</td>
										<td>${analysis.getLastHistory().author}</td>
										<c:choose>
											<c:when test="${analysis.basedOnAnalysis == null}">
												<td><spring:message code="label.analysis.basedonself" /></td>
											</c:when>
											<c:when test="${analysis.basedOnAnalysis.id != analysis.id}">
												<td>${analysis.basedOnAnalysis.version}</td>
											</c:when>
										</c:choose>
										<td>${analysis.language.name}</td>
										<td><a class="btn btn-primary btn-sm" href="${pageContext.request.contextPath}/Analysis/${analysis.id}/Select"> <c:if
													test="${sessionScope.selectedAnalysis != null }">
													<c:if test="${sessionScope.selectedAnalysis == analysis.id}">
														<samp class="glyphicon glyphicon-minus"></samp>
													</c:if>
													<c:if test="${sessionScope.selectedAnalysis != analysis.id}">
														<samp class="glyphicon glyphicon-pushpin"></samp>
													</c:if>
												</c:if> <c:if test="${sessionScope.selectedAnalysis == null }">
													<samp class="glyphicon glyphicon-pushpin"></samp>
												</c:if>
										</a> <a class="btn btn-danger btn-sm" href="${pageContext.request.contextPath}/Analysis/Delete/${analysis.id}" title='<spring:message code="label.action.delete" />'> <samp
													class="glyphicon glyphicon-trash"></samp>
										</a> <a class="btn btn-warning btn-sm" href="#" onclick="javascript:return editSingleAnalysis(${analysis.id});" title='<spring:message code="label.action.edit" />'> <samp
													class="glyphicon glyphicon-edit"></samp>
										</a> <a class="btn btn-warning btn-sm" href="#" title='<spring:message code="label.action.duplicate"  text="Duplicate"/>'
											onclick="return addHistory('${analysis.id}', '${analysis.version}')"> <samp class="glyphicon glyphicon-plus"></samp>
										</a> <c:choose>
												<c:when test="${!analysis.isEmpty()}">
													<a class="btn btn-success btn-sm" href="${pageContext.request.contextPath}/export/analysis/${analysis.id}" title='<spring:message code="label.action.export" />'><samp
															class="glyphicon glyphicon-floppy-open"></samp></a>
													<a class="btn btn-default btn-sm" href="${pageContext.request.contextPath}/analysis/${analysis.id}/compute/actionPlan"
														title='<spring:message code="label.action.compute.actionPlan" />'> <samp class="glyphicon glyphicon-play"></samp>
													</a>
													<a class="btn btn-default btn-sm" href="${pageContext.request.contextPath}/analysis/${analysis.id}/compute/riskRegister"
														title="<spring:message code="label.action.compute.riskRegister" />"> <samp class="glyphicon glyphicon-play-circle"></samp>
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
					<jsp:include page="widgetContent.jsp" />
					<jsp:include page="components/widgets/historyForm.jsp" />
				</c:if>
				<!-- ################################################################ Include Footer ################################################################ -->
			</div>
		</div>
		<!-- ################################################################ End Container ################################################################# -->
		<jsp:include page="../footer.jsp" />
		<jsp:include page="../scripts.jsp" />
	</div>
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>