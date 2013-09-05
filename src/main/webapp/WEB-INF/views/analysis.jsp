<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html>
<head>
<title>Trick Service</title>
</head>
<body>
	<form:errors cssClass="error" element="div"/>
	<a href="${pageContext.request.contextPath}/index">Home</a>
	<c:if test="${!empty analyzes}">
		<table class="data" border="1">
			<tr>
				<th><spring:message code="label.analysis.id" /></th>
				<th><spring:message code="label.analysis.identifier" /></th>
				<th><spring:message code="label.analysis.version" /></th>
				<th><spring:message code="label.analysis.creationDate" /></th>
				<th><spring:message code="label.analysis.language" /></th>
				<th><spring:message code="label.analysis.label" /></th>
				<th><spring:message code="label.analysis.action" /></th>
			</tr>
			<c:forEach items="${analyzes}" var="analysis">
				<tr>
					<td>${analysis.id}</td>
					<td>${analysis.identifier}</td>
					<td>${analysis.version}</td>
					<td>${analysis.creationDate}</td>
					<td>${analysis.language.name}</td>
					<td>${analysis.label}</td>
					<td><a
						href="${pageContext.request.contextPath}/analysis/delete/${analysis.id}"><spring:message
								code="label.action.delete" /></a> <a
						href="${pageContext.request.contextPath}/analysis/edit/${analysis.id}"><spring:message
								code="label.action.edit" /></a> <c:choose>
							<c:when test="${!analysis.isEmpty()}">
								<a
									href="${pageContext.request.contextPath}/export/analysis/${analysis.id}"><spring:message
										code="label.action.export" /></a>
										
										<a
									href="${pageContext.request.contextPath}/analysis/${analysis.id}/compute/actionPlan"><spring:message
										code="label.action.compute.actionPlan" /></a>
										
										<a
									href="${pageContext.request.contextPath}/analysis/${analysis.id}/compute/riskRegister"><spring:message
										code="label.action.compute.riskRegister" /></a>
										
										
							</c:when>
							<c:otherwise>
								<spring:message code="label.action.export" />
							</c:otherwise>
						</c:choose></td>
				</tr>
			</c:forEach>
		</table>
	</c:if>
</body>
</html>