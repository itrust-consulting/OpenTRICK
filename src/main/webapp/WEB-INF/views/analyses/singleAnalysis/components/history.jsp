<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="tab-pane active" id="tabHistory">
	<div class="section" id="section_history">
		<div class="panel panel-default">
			<div class="panel-heading"><fmt:message key="label.title.history" /></div>
			<div class="panel-body">
				<table class="table table-hover table-fixed-header-analysis">
					<thead>
						<tr>
							<th><fmt:message key="label.history.version" /></th>
							<th><fmt:message key="label.history.date" /></th>
							<th><fmt:message key="label.history.author" /></th>
							<th colspan="10"><fmt:message key="label.history.comment" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${histories}" var="history">
							<tr trick-class="History" trick-id="${history.id}">
								<td><spring:message text="${history.version}" /></td>
								<td><fmt:formatDate value="${history.date}" pattern="dd/MM/yyyy" /></td>
								<td trick-field="author" trick-field-type="string" class="success" ondblclick="editField(this);"><spring:message text="${history.author}" /></td>
								<td colspan="10" ondblclick="editField(this.firstElementChild);" class="success"><pre trick-field="comment" trick-field-type="string" trick-content="text">
										<spring:message text="${history.comment}" />
									</pre></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>