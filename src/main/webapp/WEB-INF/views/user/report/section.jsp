<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="col-md-9 col-lg-10" id="section_report">
	<table class="table table-hover table-condensed">
		<thead>
			<tr>
				<th><spring:message code="label.analysis.identifier" text="TRICK name"/></th>
				<th><spring:message code="label.type" text="Type"/></th>
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
					<td><spring:message code='label.word_report.type.${fn:toLowerCase(report.type)}' text="${report.type}"/></td>
					<td><spring:message text="${report.label}"/></td>
					<td><spring:message text="${report.version}"/></td>
					<td><fmt:formatDate value="${report.created}"/></td>
					<td><fmt:formatNumber value="${report.size/(1024*1024)}" maxFractionDigits="2" /> <spring:message code="label.metric.megabit" text="Mb"/></td>
					<td>
						<a class="btn btn-primary" href="${pageContext.request.contextPath}/Account/Report/${report.id}/Download" onclick="return downloadWordReport('${report.id}')" title='<spring:message code="label.action.download" text="Download"/>'><i class="fa fa-download"></i></a>
						<a class="btn btn-danger" href="${pageContext.request.contextPath}/Account/Report/${report.id}/Delete" onclick="return deleteReport('${report.id}')" title='<spring:message code="label.action.delete" text="Delete"/>'><i class="fa fa-trash"></i></a>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>