<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="section" id="section_phase">
	<div class="page-header">
		<h3 id="Phase">
			<spring:message code="label.phases" text="Phases" />
		</h3>
	</div>
	<div class="panel panel-default">
		<div class="panel-heading">
			<button class="btn btn-default"  data-toggle="modal"
				data-target="#addPhaseModel">
				<spring:message code="label.action.add.phase" text="Add new phase" />
			</button>
		</div>
		<div class="panel-body">
			<table class="table">
				<thead>
					<tr>
						<th><spring:message code="label.phase.number" text="Number" /></th>
						<th><spring:message code="label.phase.begin.date"
								text="Begin" /></th>
						<th><spring:message code="label.phase.end.date" text="End" /></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${phases}" var="phase">
						<c:if test="${phase.number>0}">
							<tr trick-id='${phase.id}' trick-class="Phase">
								<td><spring:message text="${phase.number}" /></td>
								<td class="success" trick-field="beginDate" trick-field-type="date" ondblclick="editField(this);"><spring:message text="${phase.beginDate}" /></td>
								<td class="success" trick-field="endDate" trick-field-type="date" ondblclick="editField(this);"><spring:message text="${phase.endDate}" /></td>
							</tr>
						</c:if>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</div>