<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="tab-pane" id="tabPhase">
	<div class="section" id="section_phase">
		<div class="page-header tab-content-header">
			<div class="container">
				<div class="row-fluid">
					<h3>
						<spring:message code="label.title.phase" />
					</h3>
				</div>
			</div>
		</div>
		<c:if test="${isEditable}">
			<ul class="nav nav-pills bordered-bottom" id="menu_phase">
				<li><a href="#" onclick="return newPhase();"><span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.action.add" /> </a></li>
				<li data-trick-check="isEditable()" class="disabled" data-trick-selectable="true"><a href="#" onclick="return editPhase(null);"><span
						class="glyphicon glyphicon-edit danger"></span> <spring:message code="label.action.edit" /> </a></li>
				<li style="display: none;" class="dropdown-header"><spring:message code="label.menu.advanced" /></li>
				<li data-trick-check="isEditable()" class="disabled pull-right" data-trick-selectable="true"><a href="#" class="text-danger" onclick="return deletePhase();"><span
						class="glyphicon glyphicon-remove"></span> <spring:message code="label.action.delete" /> </a></li>
			</ul>
		</c:if>
		<table class="table table-hover table-fixed-header-analysis table-condensed">
			<thead>
				<tr>
					<c:if test="${isEditable}">
						<th style="width: 2%">&nbsp;</th>
					</c:if>
					<th style="width: 3%"><spring:message code="label.table.index" /></th>
					<th><spring:message code="label.phase.begin_date" /></th>
					<th><spring:message code="label.phase.end_date" /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${phases}" var="phase">
					<c:if test="${phase.number>0}">
						<tr data-trick-id='${phase.id}' data-trick-class="Phase" ${not empty previousEndDate and phase.beginDate < previousEndDate? "class='warning'":"class='success'"}>
							<c:if test="${isEditable}">
								<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_phase','#menu_phase');"></td>
							</c:if>
							<td><spring:message text="${phase.number}" /></td>
							<td data-trick-field="beginDate" data-trick-field-type="date" ondblclick="editPhase(${phase.id});"><spring:message text="${phase.beginDate}" /></td>
							<td data-trick-field="endDate" data-trick-field-type="date" ondblclick="editPhase(${phase.id});"><spring:message text="${phase.endDate}" /></td>
						</tr>
						<c:set var="previousEndDate" value="${phase.endDate}" />
					</c:if>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>