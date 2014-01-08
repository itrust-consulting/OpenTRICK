<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="section" id="section_scenario">
	<div class="page-header">
		<h3 id="Scenario">
			<spring:message code="label.scenario" text="Scenario" />
		</h3>
	</div>
	<div class="panel panel-default">
		<div class="panel-heading">
			<button class="btn btn-default" data-toggle="modal"
				onclick="findAllScenarioType('scenario_scenariotype_id');"
				data-target="#addScenarioModal">
				<spring:message code="label.scenario.add" text="Add new scenario" />
			</button>
		</div>
		<div class="panel-body">
			<table class="table">
				<thead>
					<tr>
						<th><spring:message code="label.row.index" text="#"
								htmlEscape="true" /></th>
						<th colspan="2"><spring:message code="label.scenario.name"
								text="Name" htmlEscape="true" /></th>
						<th><spring:message code="label.scenario.type" text="Type"
								htmlEscape="true" /></th>
						<th colspan="2"><spring:message
								code="label.scenario.description" text="Description"
								htmlEscape="true" /></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${scenarios}" var="scenario" varStatus="status">
						<c:set var="cssClass">
								${scenario.selected? 'success' : ''}
							</c:set>
						<tr trick-id="${scenario.id}"
							trick-selected="${scenario.selected}"
							ondblclick="return editScenarioRow(${scenario.id})">
							<td>${status.index+1}</td>
							<td class="${cssClass}" colspan="2">${scenario.name}</td>
							<td class="${cssClass}">${scenario.type.getTypeName()}</td>
							<td class="${cssClass}" colspan="2">${scenario.description}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</div>