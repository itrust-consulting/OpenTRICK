<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="tab-pane" id="tab-phase">
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
				<li><a href="#" onclick="return addPhase();"><span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.action.add" /> </a></li>
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
					<th data-helper-content='<spring:message code="help.phase.statistical.data"/>' ><spring:message code="label.phase.duration" /></th>
					<th style="text-align: right;" data-helper-content='<spring:message code="help.phase.statistical.data"/>' ><spring:message code="label.phase.internal.workload" /></th>
					<th style="text-align: right;" data-helper-content='<spring:message code="help.phase.statistical.data"/>'><spring:message code="label.phase.external.workload" /></th>
					<th style="text-align: right;" data-helper-content='<spring:message code="help.phase.statistical.data"/>'><spring:message code="label.phase.measure.count" /></th>
					<th style="text-align: center;" data-helper-content='<spring:message code="help.phase.statistical.data"/>'><spring:message code="label.phase.compliance.rate" /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${phases}" var="phase">
					<c:if test="${phase.number>0}">
						<tr data-trick-id='${phase.id}' data-trick-index='${phase.number}' onclick="selectElement(this)" data-trick-class="Phase"
							${not empty previousEndDate and phase.beginDate < previousEndDate? "class='warning'":"class='editable'"}>
							<c:if test="${isEditable}">
								<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_phase','#menu_phase');"></td>
							</c:if>
							<td data-trick-field='number'><spring:message text="${phase.number}" /></td>
							<td data-trick-field="beginDate" data-trick-field-type="date" ondblclick="editPhase(${phase.id});"><fmt:formatDate value="${phase.beginDate}" pattern="yyyy-MM-dd" /></td>
							<td data-trick-field="endDate" data-trick-field-type="date" ondblclick="editPhase(${phase.id});"><fmt:formatDate value="${phase.endDate}" pattern="yyyy-MM-dd" /></td>
							<td><spring:message code='label.phase.period.format'>
									<spring:argument value="${phase.formatCode}" />
									<spring:argument value="${phase.period.years}" />
									<spring:argument value="${phase.period.months}" />
									<spring:argument value="${phase.period.days}" />
								</spring:message></td>
							<td align="right"><fmt:formatNumber value="${phase.internalWorkload}" maxFractionDigits="2" /> <spring:message code="label.metric.man_day" /></td>
							<td align="right"><fmt:formatNumber value="${phase.externalWorkload}" maxFractionDigits="2" /> <spring:message code="label.metric.man_day" /></td>
							<td align="right"><fmt:formatNumber value="${phase.measureCount}" maxFractionDigits="2" /></td>
							<td align="center"><fmt:formatNumber value="${phase.complianceRate*100}" maxFractionDigits="0" var="complianceRate" />
								<div class="progress" style="margin-bottom: 0">
									<div class="progress-bar ${phase.outToDate? 'progress-bar-danger' : 'progress-bar-success' }" role="progressbar" aria-valuenow="${complianceRate}" aria-valuemin="0" aria-valuemax="100" style="width: ${complianceRate}%;">${complianceRate}%</div>
								</div></td>

						</tr>
						<c:set var="previousEndDate" value="${phase.endDate}" />
					</c:if>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>