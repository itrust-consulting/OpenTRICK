<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<fmt:setLocale value="fr" scope="session" />
<div class="tab-pane" id="tab-scenario">
	<div class="section" id="section_scenario">
		<div class="page-header tab-content-header">
			<div class="container">
				<div class="row-fluid">
					<h3>
						<spring:message code="label.title.scenario" />
					</h3>
				</div>
			</div>
		</div>
		<c:if test="${isEditable}">
			<ul class="nav nav-pills bordered-bottom" id="menu_scenario">
				<li><a href="#" onclick="return editScenario(undefined,true);"><span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.action.add" /> </a></li>
				<li class="disabled" data-trick-selectable="true"><a href="#" onclick="return editScenario();"><span class="glyphicon glyphicon-edit danger"></span> <spring:message
							code="label.action.edit" /> </a></li>
				<li class="disabled" data-trick-single-check="!isSelected('scenario')" data-trick-check="hasSelectedState('scenario','false')" data-trick-selectable="multi"><a href="#"
					onclick="return selectScenario(undefined, 'true')"><span class="glyphicon glyphicon-plus-sign"></span> <spring:message code="label.action.select" /> </a></li>
				<li class="disabled" data-trick-single-check="isSelected('scenario')" data-trick-check="hasSelectedState('scenario','true')" data-trick-selectable="multi"><a href="#"
					onclick="return selectScenario(undefined, 'false')"><span class="glyphicon glyphicon-minus-sign "></span> <spring:message code="label.action.unselect" /> </a></li>
				<li style="display: none;" class="dropdown-header"><spring:message code="label.menu.advanced" /></li>
				<li data-trick-check="isEditable()" class="disabled pull-right" data-trick-selectable="multi"><a href="#" class="text-danger" onclick="return deleteScenario();"><span
						class="glyphicon glyphicon-remove"></span> <spring:message code="label.action.delete" /> </a></li>
			</ul>
		</c:if>
		<table id="scenariotable" class="table table-hover table-fixed-header-analysis table-condensed">
			<thead>
				<tr>
					<c:if test="${isEditable}">
						<th style="width: 2%"><input type="checkbox" class="checkbox" onchange="return checkControlChange(this,'scenario')"></th>
					</c:if>
					<th style="width: 3%"><a href="#" onclick="return sortTable('index',this,true)" data-order='0'><spring:message code="label.row.index" /></a></th>
					<th style="width: 25%"><a href="#" onclick="return sortTable('name',this)" data-order='1'><spring:message code="label.scenario.name" /></a></th>
					<th style="width: 5%"><a href="#" onclick="return sortTable('type',this)" data-order='1'><spring:message code="label.scenario.type" /></a></th>
					<c:if test="${type.quantitative}">
						<c:choose>
							<c:when test="${show_uncertainty}">
								<th style="width: 5%"><a href="#" onclick="return sortTable('aleo',this,true)" data-order='1'><spring:message code="label.scenario.aleo" /></a></th>
								<th style="width: 5%"><a href="#" onclick="return sortTable('ale',this,true)" data-order='1'><spring:message code="label.scenario.ale" /></a></th>
								<th style="width: 5%"><a href="#" onclick="return sortTable('alep',this,true)" data-order='1'><spring:message code="label.scenario.alep" /></a></th>
							</c:when>
							<c:otherwise>
								<th style="width: 5%"><a href="#" onclick="return sortTable('ale',this,true)" data-order='1'><spring:message code="label.scenario.ale" /></a></th>
							</c:otherwise>
						</c:choose>
					</c:if>
					<th><spring:message code="label.scenario.description" /></th>
				</tr>
			</thead>
			<tfoot></tfoot>
			<tbody>
				<c:forEach items="${scenarios}" var="scenario" varStatus="status">
					<tr data-trick-id="${scenario.id}" onclick="selectElement(this)" data-trick-selected="${scenario.selected}" ${scenario.selected? 'class="editable"' : ''}
						data-trick-class="Scenario" ondblclick="return editScenario(${scenario.id})">
						<c:set var="ale" value="${scenarioALE[scenario.id]}" />
						<c:set var="selectClass" value="${scenario.selected?'selected':'unselected'}" />
						<c:if test="${isEditable}">
							<td class="${selectClass}"><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_scenario','#menu_scenario');"></td>
						</c:if>
						<td data-trick-field="index" >${status.index+1}</td>
						<td data-trick-field="name"><spring:message text="${scenario.name}" /></td>
						<td data-trick-field="type"><spring:message code="label.scenario.type.${fn:toLowerCase(fn:replace(scenario.type.name,'-','_'))}" /></td>
						<c:if test="${type.quantitative}">
							<c:choose>
								<c:when test="${show_uncertainty}">
									<td data-trick-field="aleo" title="<fmt:formatNumber value="${fct:round(ale[0].value,0)}"  /> &euro;"><fmt:formatNumber value="${fct:round(ale[0].value*0.001,1)}" /></td>
									<td data-trick-field="ale" title="<fmt:formatNumber value="${fct:round(ale[1].value,0)}"  /> &euro;"><fmt:formatNumber value="${fct:round(ale[1].value*0.001,1)}" /></td>
									<td data-trick-field="alep" title="<fmt:formatNumber value="${fct:round(ale[2].value,0)}"  /> &euro;"><fmt:formatNumber value="${fct:round(ale[2].value*0.001,1)}" /></td>
								</c:when>
								<c:otherwise>
									<td data-trick-field="ale" title="<fmt:formatNumber value="${fct:round(ale[1].value,0)}"  /> &euro;"><fmt:formatNumber value="${fct:round(ale[1].value*0.001,1)}" /></td>
								</c:otherwise>
							</c:choose>
						</c:if>
						<td onclick="editField(this);" data-trick-field="description" data-trick-field-type="string" data-trick-content="text"><spring:message text="${scenario.description}" /></td>
					</tr>
				</c:forEach>
			</tbody>
			<c:if test="${!isProfile and type.quantitative}">
				<tfoot>
					<tr class="panel-footer" style="font-weight: bold;">
						<spring:eval expression="T(lu.itrust.business.TS.component.AssessmentAndRiskProfileManager).ComputeTotalALE(scenarioALE)" var="ale" />
						<td colspan="4"><spring:message code="label.total.ale" /></td>
						<c:choose>
							<c:when test="${show_uncertainty}">
								<td title="<fmt:formatNumber value="${fct:round(ale[0].value,0)}"  /> &euro;"><fmt:formatNumber value="${fct:round(ale[0].value*0.001,1)}" /></td>
								<td title="<fmt:formatNumber value="${fct:round(ale[1].value,0)}"  /> &euro;"><fmt:formatNumber value="${fct:round(ale[1].value*0.001,1)}" /></td>
								<td title="<fmt:formatNumber value="${fct:round(ale[2].value,0)}"  /> &euro;"><fmt:formatNumber value="${fct:round(ale[2].value*0.001,1)}" /></td>
							</c:when>
							<c:otherwise>
								<td title="<fmt:formatNumber value="${fct:round(ale[1].value,0)}"  /> &euro;"><fmt:formatNumber value="${fct:round(ale[1].value*0.001,1)}" /></td>
							</c:otherwise>
						</c:choose>
						<td colspan="2"></td>
					</tr>
				</tfoot>
			</c:if>
		</table>
	</div>
</div>
