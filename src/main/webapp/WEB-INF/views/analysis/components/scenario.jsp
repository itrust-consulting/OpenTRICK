<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<span class="anchor" id="anchorScenario"></span>
<div class="section" id="section_scenario">
	<div class="page-header">
		<h3 id="Scenario">
			<spring:message code="label.scenario" text="Scenario" />
		</h3>
	</div>
	<div class="panel panel-default">
		<div class="panel-heading" style="min-height: 60px">
			<ul class="nav nav-pills" id="menu_scenario">
				<li><a href="#" onclick="return editScenario(undefined,true);"><span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.scenario.add" text="Add" /> </a></li>
				<li class="disabled" trick-selectable="true"><a href="#" onclick="return editScenario();"><span class="glyphicon glyphicon-edit danger"></span> <spring:message code="label.scenario.edit" text="Edit" /> </a></li>
				<li class="disabled" trick-selectable="multi"><a href="#" onclick="return deleteScenario();"><span class="glyphicon glyphicon-remove"></span> <spring:message code="label.scenario.delete" text="Delete" /> </a></li>
				<li class="disabled" trick-selectable="multi"><a href="#" onclick="return selectScenario(undefined, 'true')"><span class="glyphicon glyphicon-plus-sign"></span> <spring:message code="label.scenario.select" text="Select" /> </a></li>
				<li class="disabled" trick-selectable="multi"><a href="#" onclick="return selectScenario(undefined, 'false')"><span class="glyphicon glyphicon-minus-sign "></span> <spring:message code="label.scenario.unselect" text="Unselect" /> </a></li>
				<li class="disabled" trick-selectable="true"><a href="#" onclick="return displayAssessmentByScenario()"><span class="glyphicon glyphicon-new-window"></span> <spring:message code="label.scenario.assessment" text="Assessment" /> </a></li>
			</ul>
		</div>
		<div class="panel-body autofitpanelbodydefinition">
			<table id="scneariotable" class="table table-hover headertofixtable">
				<thead>
					<tr>
						<th class="checkboxtableheader" style="width:50px"><input type="checkbox" class="checkbox checkboxselectable" onchange="return checkControlChange(this,'scenario')"></th>
						<th style="width:50px"><spring:message code="label.row.index" text="#" htmlEscape="true" /></th>
						<th colspan="2"><spring:message code="label.scenario.name" text="Name" htmlEscape="true" /></th>
						<th><spring:message code="label.scenario.type" text="Type" htmlEscape="true" /></th>
						<th colspan="2"><spring:message code="label.scenario.description" text="Description" htmlEscape="true" /></th>
					</tr>
				</thead>
				<tfoot></tfoot>
				<tbody>
					<c:forEach items="${scenarios}" var="scenario" varStatus="status">
						<c:set var="cssClass">
								${scenario.selected? 'success' : ''}
							</c:set>
						<tr trick-id="${scenario.id}" trick-selected="${scenario.selected}" ondblclick="return editScenario(${scenario.id})">
							<td><input type="checkbox" class="checkbox checkboxselectable" onchange="return updateMenu('#section_scenario','#menu_scenario');"></td>
							<td>${status.index+1}</td>
							<td class="${cssClass}" colspan="2"><spring:message text="${scenario.name}" /></td>
							<td class="${cssClass}"><spring:message text="${scenario.scenarioType.name}" /></td>
							<td class="${cssClass}" colspan="2"><pre><spring:message text="${scenario.description}" /></pre></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</div>
