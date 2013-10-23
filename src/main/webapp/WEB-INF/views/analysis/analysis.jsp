<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>

<!-- ################################################################ Set Page Title ################################################################ -->

<c:set scope="request" var="title">title.analysis</c:set>

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
	
	
<!-- #################################################################### Analysis Menu ################################################################### -->		
	
		<c:if test="${sessionScope.selectedAnalysis != null}">
			<c:if test="${sessionScope.selectedAnalysis > 0 }">
				<jsp:include page="analysisMenu.jsp" />
			</c:if>
		</c:if>
	
			<form:errors cssClass="error" element="div" />

			<h1><spring:message code="label.analysis.title" /></h1>

			<c:if test="${!empty analyses}">
				<table class="data" border="1">
					<tr>
						<th><spring:message code="label.analysis.identifier" /></th>
						<th><spring:message code="label.analysis.version" /></th>
						<th><spring:message code="label.analysis.creationDate" /></th>
						<th><spring:message code="label.analysis.language" /></th>
						<th><spring:message code="label.analysis.label" /></th>
						<th><spring:message code="label.action" /></th>
					</tr>
					<c:forEach items="${analyses}" var="analysis">
						<tr>
							<td>${analysis.identifier}</td>
							<td>${analysis.version}</td>
							<td>${analysis.creationDate}</td>
							<td>${analysis.language.name}</td>
							<td>${analysis.label}</td>
							<td>
								<a href="${pageContext.request.contextPath}/Analysis/${analysis.id}/Select">
									<c:if test="${sessionScope.selectedAnalysis != null }">
										<c:if test="${sessionScope.selectedAnalysis == analysis.id}">
											Unselect
										</c:if>
										<c:if test="${sessionScope.selectedAnalysis != analysis.id}">
											Select
										</c:if>
									</c:if>
									<c:if test="${sessionScope.selectedAnalysis == null }">
										Select
									</c:if>
								</a>
								<a href="${pageContext.request.contextPath}/Analysis/Delete/${analysis.id}" title='<spring:message code="label.action.delete" />'>
									<img src='<spring:url value="/images/delete.png"/>' alt='<spring:message code="label.action.delete" />' />
								</a>
								<a href="${pageContext.request.contextPath}/Analysis/Edit/${analysis.id}" title='<spring:message code="label.action.edit" />'> 
									<img src='<spring:url value="/images/edit.png"/>' alt='<spring:message code="label.action.edit" />' />
								</a> 
								<c:choose>
									<c:when test="${!analysis.isEmpty()}">
										<a href="${pageContext.request.contextPath}/export/analysis/${analysis.id}" title='<spring:message code="label.action.export" />'> 
											<img src='<spring:url value="/images/export.png"/>' alt='<spring:message code="label.action.export" />' />
										</a>
										<a href="${pageContext.request.contextPath}/analysis/${analysis.id}/compute/actionPlan">
											<spring:message	code="label.action.compute.actionPlan" />
										</a>

										<a href="${pageContext.request.contextPath}/analysis/${analysis.id}/compute/riskRegister">
											<spring:message code="label.action.compute.riskRegister" />
										</a>
									</c:when>
									<c:otherwise>
										&nbsp;
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
					</c:forEach>
				</table>
			</c:if>
	</div>
		
<!-- ################################################################ Include Footer ################################################################ -->

<jsp:include page="../footer.jsp" />

<!-- ################################################################ End Container ################################################################# -->

</div>
</body>

<!-- ################################################################### End HTML ################################################################### -->

</html>