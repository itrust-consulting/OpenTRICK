<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="tab-pane active" id="tab-history">
	<div class="section" id="section_history">
		<div class="page-header tab-content-header">
			<div class="container">
				<div class="row-fluid">
					<h3>
						<spring:message code='format.analysis.name' arguments="${analysis.customer.organisation}, ${analysis.label}, ${analysis.version}" text="${analysis.customer.organisation}: ${analysis.label} - v${analysis.version}" />
					</h3>
				</div>
			</div>
		</div>
		<table class="table table-hover table-fixed-header-analysis table-condensed"  data-fh-scroll-multi="1.09">
			<thead>
				<tr>
					<th style="width: 6%"><spring:message code="label.history.version" /></th>
					<th style="width: 6%"><spring:message code="label.history.date" /></th>
					<th style="width: 15%"><spring:message code="label.history.author" /></th>
					<th><spring:message code="label.history.comment" /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${histories}" var="history">
					<tr data-trick-class="History" data-trick-id="${history.id}">
						<td><spring:message text="${history.version}" /></td>
						<td><fmt:formatDate value="${history.date}" pattern="dd/MM/yyyy" /></td>
						<td data-trick-field="author" data-trick-field-type="string" class="editable" onclick="editField(this);"><spring:message text="${history.author}" /></td>
						<td onclick="editField(this);" class="editable" data-trick-field="comment" data-trick-field-type="string" data-trick-content="text"><spring:message text="${history.comment}" /></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>