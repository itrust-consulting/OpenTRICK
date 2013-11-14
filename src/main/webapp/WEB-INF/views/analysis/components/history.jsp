<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="section" id="section_history">
	<div class="page-header">
		<h3 id="history">
			<spring:message code="label.history" text="History" />
		</h3>
	</div>
	<div class="panel panel-default">
		<div class="panel-heading">&nbsp;</div>
		<div class="panel-body">
			<table class="table">
				<thead>
					<tr>
						<!--  <th><spring:message code="label.history.id" /></th>-->
						<th><spring:message code="label.history.version" /></th>
						<th><spring:message code="label.history.date" /></th>
						<th><spring:message code="label.history.author" /></th>
						<th><spring:message code="label.history.comment" /></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${histories}" var="history">
						<tr>
							<!--<td>${history.id}</td>-->
							<td>${history.version}</td>
							<td ondblclick="editField(this, 'history','${history.id}', 'date', 'date');">${history.date}</td>
							<td ondblclick="editField(this, 'history','${history.id}', 'author', 'string');">${history.author}</td>
							<td ondblclick="editField(this, 'history','${history.id}', 'comment', 'string');">${history.comment}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</div>