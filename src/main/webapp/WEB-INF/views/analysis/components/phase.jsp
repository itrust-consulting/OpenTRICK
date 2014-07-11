<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<span class="anchor" id="anchorPhase"></span>
<div class="section" id="section_phase">
	<div class="page-header">
		<h3 id="Phase">
			<spring:message code="label.phases" text="Phases" />
		</h3>
	</div>
	<div class="panel panel-default">
		<div class="panel-heading" style="min-height: 60px">
			<ul class="nav nav-pills" id="menu_phase">
				<li><a href="#" data-toggle="modal" data-target="#addPhaseModel" onclick="return false"><span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.phase.add" text="Add" /> </a></li>
				<li class="disabled pull-right" trick-selectable="true"><a href="#" class="text-danger" onclick="return deletePhase();"><span class="glyphicon glyphicon-remove"></span> <spring:message code="label.delete.delete"	text="Delete" /> </a></li>
			</ul>
		</div>
		<div class="panel-body" class="autofitpanelbodydefinition">
			<table class="table table-hover table-fixed-header">
				<thead>
					<tr>
						<th><input type="checkbox" class="checkbox" onchange="return checkControlChange(this,'phase')"></th>
						<th><spring:message code="label.table.index" text="#" /></th>
						<th colspan="15"><spring:message code="label.phase.begin.date" text="Begin" /></th>
						<th colspan="15"><spring:message code="label.phase.end.date" text="End" /></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${phases}" var="phase">
						<c:if test="${phase.number>0}">
							<tr trick-id='${phase.id}' trick-class="Phase">
								<td><input type="checkbox" class="checkbox" onchange="return updateMenu('#section_phase','#menu_phase');"></td>
								<td><spring:message text="${phase.number}" /></td>
								<td colspan="15" class="success" trick-field="beginDate" trick-field-type="date" ondblclick="editField(this);"><spring:message text="${phase.beginDate}" /></td>
								<td colspan="15" class="success" trick-field="endDate" trick-field-type="date" ondblclick="editField(this);"><spring:message text="${phase.endDate}" /></td>
							</tr>
						</c:if>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</div>