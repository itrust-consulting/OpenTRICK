<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="col-md-9" id="section_sqlite">
	<table class="table table-hover table-condensed">
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
			<c:forEach items="${sqlites}" var="sqlite">
				<tr data-trick-id="${sqlite.id}">
					<td><spring:message text="${sqlite.identifier}"/></td>
					<td><spring:message text="${sqlite.label}"/></td>
					<td><spring:message text="${sqlite.version}"/></td>
					<td><fmt:formatDate value="${sqlite.exportTime}"/></td>
					<td><fmt:formatNumber value="${sqlite.size/(1024*1024)}" maxFractionDigits="2" /> <spring:message code="label.metric.megabit" text="Mb"/></td>
					<td>
						<a class="btn btn-primary" href="${pageContext.request.contextPath}/Profile/Sqlite/${sqlite.id}/Download" onclick="return downloadExportedSqLite('${sqlite.id}')" title='<spring:message code="label.action.download" text="Download"/>'><i class="fa fa-download"></i></a>
						<a class="btn btn-danger" href="${pageContext.request.contextPath}/Profile/Sqlite/${sqlite.id}/Delete" onclick="return deleteSqlite('${sqlite.id}')" title='<spring:message code="label.action.delete" text="Delete"/>'><i class="fa fa-trash"></i></a>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
