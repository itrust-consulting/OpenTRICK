<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="tab-pane" id="tabPhase">
	<div class="section" id="section_phase">
		<ul class="nav nav-pills bordered-bottom" id="menu_phase">
			<c:if test="${isEditable}">
				<li><a href="#" onclick="return newPhase();"><span class="glyphicon glyphicon-plus primary"></span> <fmt:message key="label.action.add" /> </a></li>
			</c:if>
			<li data-trick-check="isEditable()" class="disabled" data-trick-selectable="true"><a href="#" onclick="return editPhase(null);"><span class="glyphicon glyphicon-edit danger"></span> <fmt:message
						key="label.action.edit" /> </a></li>
			<li data-trick-check="isEditable()" class="disabled pull-right" data-trick-selectable="true"><a href="#" class="text-danger" onclick="return deletePhase();"><span class="glyphicon glyphicon-remove"></span>
					<fmt:message key="label.action.delete" /> </a></li>
		</ul>
		<table class="table table-hover table-fixed-header-analysis">
			<thead>
				<tr>
					<th style="width:2%">&nbsp;</th>
					<th style="width:3%"><fmt:message key="label.table.index" /></th>
					<th><fmt:message key="label.phase.begin_date" /></th>
					<th><fmt:message key="label.phase.end_date" /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${phases}" var="phase">
					<c:if test="${phase.number>0}">
						<tr data-trick-id='${phase.id}' data-trick-class="Phase" ${not empty previousEndDate and phase.beginDate < previousEndDate? "class='warning'":""}>
							<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_phase','#menu_phase');"></td>
							<td><spring:message text="${phase.number}" /></td>
							<td class="success" data-trick-field="beginDate" data-trick-field-type="date" ondblclick="editPhase(${phase.id});"><spring:message text="${phase.beginDate}" /></td>
							<td class="success" data-trick-field="endDate" data-trick-field-type="date" ondblclick="editPhase(${phase.id});"><spring:message text="${phase.endDate}" /></td>
						</tr>
						<c:set var="previousEndDate" value="${phase.endDate}"/>
					</c:if>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>