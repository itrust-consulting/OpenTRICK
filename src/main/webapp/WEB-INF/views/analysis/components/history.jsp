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
		<h3 id="History">
			<spring:message code="label.history" text="History" />
		</h3>
	</div>
	<div class="panel panel-default">
		<div class="panel-heading">&nbsp;</div>
		<div class="panel-body" style="max-height: 700px; overflow: auto;">
			<table class="table" >
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
						<tr trick-class="History" trick-id="${history.id}">
							<td>${history.version}</td>
							<td><fmt:formatDate value="${history.date}" pattern="dd/MM/yyyy"/> </td>
							<td trick-field="author" trick-field-type="string" class="success"
								ondblclick="editField(this);">${history.author}</td>
							<td trick-field="comment" trick-field-type="string" class="success"
								ondblclick="editField(this);">${history.comment}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</div>