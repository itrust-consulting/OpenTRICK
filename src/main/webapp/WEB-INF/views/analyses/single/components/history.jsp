<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="tab-pane active" id="tabHistory">
	<div class="page-header tab-content-header">
		<div class="container">
			<div class="row-fluid">
				<h3>
					<fmt:message key="label.title.history"/>
				</h3>
			</div>
		</div>
	</div>
	<div class="section" id="section_history">
		<table class="table table-hover table-fixed-header-analysis table-condensed">
			<thead>
				<tr>
					<th style="width: 3%"><fmt:message key="label.history.version" /></th>
					<th style="width: 5%"><fmt:message key="label.history.date" /></th>
					<th style="width: 15%"><fmt:message key="label.history.author" /></th>
					<th><fmt:message key="label.history.comment" /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${histories}" var="history">
					<tr data-trick-class="History" data-trick-id="${history.id}">
						<td><spring:message text="${history.version}" /></td>
						<td><fmt:formatDate value="${history.date}" pattern="dd/MM/yyyy" /></td>
						<td data-trick-field="author" data-trick-field-type="string" class="success" onclick="editField(this);"><spring:message text="${history.author}" /></td>
						<td onclick="editField(this.firstElementChild);" class="success"><pre data-trick-field="comment" data-trick-field-type="string" data-trick-content="text">
								<spring:message text="${history.comment}" />
							</pre></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>