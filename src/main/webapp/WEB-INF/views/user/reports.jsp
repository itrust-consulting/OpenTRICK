<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="col-xs-10" id="section_report">
	<table class="table">
		<thead>
			<tr>
				<th><spring:message code="label.analysis.identifier" text="TRICK name"/></th>
				<th><spring:message code="label.analysis.label" text="Name"/></th>
				<th><spring:message code="label.analysis.version" text="Version"/></th>
				<th><spring:message code="label.date.created" text="Created date"/></th>
				<th><spring:message code="label.file.size" text="Size"/></th>
				<th><spring:message code="label.action"/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${reports}" var="report">
				<tr data-trick-id="${report.id}">
					<td><spring:message text="${report.identifier}"/></td>
					<td><spring:message text="${report.label}"/></td>
					<td><spring:message text="${report.version}"/></td>
					<td><fmt:formatDate value="${report.created}"/></td>
					<td><fmt:formatNumber value="${report.size/(1024*1024)}" maxFractionDigits="2" /> <spring:message code="label.metric.megabit" text="Mb"/></td>
					<td>
						<a class="btn btn-primary" href="${pageContext.request.contextPath}/Profile/Report/${report.id}/Download" onclick="return downloadWordReport('${report.id}')" title='<spring:message code="label.action.download" text="Download"/>'><i class="fa fa-download"></i></a>
						<a class="btn btn-danger" href="${pageContext.request.contextPath}/Profile/Report/${report.id}/Delete" onclick="return deleteReport('${report.id}')" title='<spring:message code="label.action.delete" text="Delete"/>'><i class="fa fa-trash"></i></a>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>