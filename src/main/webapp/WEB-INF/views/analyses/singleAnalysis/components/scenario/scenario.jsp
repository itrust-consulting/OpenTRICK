<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="tab-pane" id="tabScenario">
	<div class="section" id="section_scenario">
		<ul class="nav nav-pills bordered-bottom" id="menu_scenario">
			<c:if test="${isEditable}">
				<li><a href="#" onclick="return editScenario(undefined,true);"><span class="glyphicon glyphicon-plus primary"></span> <fmt:message key="label.action.add" /> </a></li>
			</c:if>
			<li data-trick-check="isEditable()" class="disabled" data-trick-selectable="true"><a href="#" onclick="return editScenario();"><span class="glyphicon glyphicon-edit danger"></span> <fmt:message
						key="label.action.edit" /> </a></li>
			<li data-trick-check="isEditable()" class="disabled" data-trick-selectable="multi"><a href="#" onclick="return selectScenario(undefined, 'true')"><span class="glyphicon glyphicon-plus-sign"></span> <fmt:message
						key="label.action.select" /> </a></li>
			<li data-trick-check="isEditable()" class="disabled" data-trick-selectable="multi"><a href="#" onclick="return selectScenario(undefined, 'false')"><span class="glyphicon glyphicon-minus-sign "></span> <fmt:message
						key="label.action.unselect" /> </a></li>
			<c:if test="${!analysis.isProfile() }">
				<li class="disabled" data-trick-selectable="true" data-trick-check="isSelected('scenario')"><a href="#" onclick="return displayAssessmentByScenario()"><span
						class="glyphicon glyphicon-new-window"></span> <fmt:message key="label.action.assessment" /> </a></li>
			</c:if>
			<li data-trick-check="isEditable()" class="disabled pull-right" data-trick-selectable="multi"><a href="#" class="text-danger" onclick="return deleteScenario();"><span class="glyphicon glyphicon-remove"></span>
					<fmt:message key="label.action.delete" /> </a></li>
		</ul>
		<table id="scenariotable" class="table table-hover table-fixed-header-analysis">
			<thead>
				<tr>
					<th style="width:2%"><input type="checkbox" class="checkbox" onchange="return checkControlChange(this,'scenario')"></th>
					<th style="width:3%"><fmt:message key="label.row.index" /></th>
					<th style="width:25%"><fmt:message key="label.scenario.name" /></th>
					<th style="width:5%"><fmt:message key="label.scenario.type" /></th>
					<c:choose>
						<c:when test="${show_uncertainty}">
							<th style="width:5%"><fmt:message key="label.scenario.aleo" /></th>
							<th style="width:5%"><fmt:message key="label.scenario.ale" /></th>
							<th style="width:5%"><fmt:message key="label.scenario.alep" /></th>
						</c:when>
						<c:otherwise>
							<th style="width:5%"><fmt:message key="label.scenario.ale" /></th>
						</c:otherwise>
					</c:choose>
					<th><fmt:message key="label.scenario.description" /></th>
				</tr>
			</thead>
			<tfoot></tfoot>
			<tbody>
				<c:forEach items="${scenarios}" var="scenario" varStatus="status">
					<c:set var="cssClass">
							${scenario.selected? 'success' : ''}
						</c:set>
					<tr data-trick-id="${scenario.id}" data-trick-selected="${scenario.selected}" data-trick-class="Scenario" ondblclick="return editScenario(${scenario.id})">
						<c:set var="ale" value="${scenarioALE[scenario.id]}" />
						<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_scenario','#menu_scenario');"></td>
						<td>${status.index+1}</td>
						<td class="${cssClass}"><spring:message text="${scenario.name}" /></td>
						<td class="${cssClass}"><fmt:message key="label.scenario.type.${fn:toLowerCase(fn:replace(scenario.type.name,'-','_'))}" /></td>
						<fmt:setLocale value="fr" scope="session" />
						<c:choose>
							<c:when test="${show_uncertainty}">
								<td title="<fmt:formatNumber value="${fct:round(ale[0].value,0)}"  /> &euro;"><fmt:formatNumber
										value="${fct:round(ale[0].value*0.001,1)}"  /></td>
								<td title="<fmt:formatNumber value="${fct:round(ale[1].value,0)}"  /> &euro;"><fmt:formatNumber
										value="${fct:round(ale[1].value*0.001,1)}"  /></td>
								<td title="<fmt:formatNumber value="${fct:round(ale[2].value,0)}"  /> &euro;"><fmt:formatNumber
										value="${fct:round(ale[2].value*0.001,1)}"  /></td>
							</c:when>
							<c:otherwise>
								<td title="<fmt:formatNumber value="${fct:round(ale[1].value,0)}"  /> &euro;"><fmt:formatNumber
										value="${fct:round(ale[1].value*0.001,1)}"  /></td>
							</c:otherwise>
						</c:choose>
						<fmt:setLocale value="${fn:substring(analysis.language.alpha3,0, 2)}" scope="session" />
						<td class="${cssClass}" onclick="editField(this.firstElementChild);"><pre  data-trick-field="description" data-trick-field-type="string" data-trick-content="text"><spring:message text="${scenario.description}" /></pre></td>
					</tr>
				</c:forEach>
			</tbody>
			<c:if test="${!analysis.isProfile() }">
				<tfoot>
					<tr class="panel-footer" style="font-weight: bold;">
						<spring:eval expression="T(lu.itrust.business.TS.data.assessment.helper.AssessmentManager).ComputeTotalALE(scenarioALE)" var="ale" />
						<td colspan="4"><fmt:message key="label.total.ale" /></td>
						<fmt:setLocale value="fr" scope="session" />
						<c:choose>
							<c:when test="${show_uncertainty}">
								<td title="<fmt:formatNumber value="${fct:round(ale[0].value,0)}"  /> &euro;"><fmt:formatNumber
										value="${fct:round(ale[0].value*0.001,1)}"  /></td>
								<td title="<fmt:formatNumber value="${fct:round(ale[1].value,0)}"  /> &euro;"><fmt:formatNumber
										value="${fct:round(ale[1].value*0.001,1)}"  /></td>
								<td title="<fmt:formatNumber value="${fct:round(ale[2].value,0)}"  /> &euro;"><fmt:formatNumber
										value="${fct:round(ale[2].value*0.001,1)}"  /></td>
							</c:when>
							<c:otherwise>
								<td title="<fmt:formatNumber value="${fct:round(ale[1].value,0)}"  /> &euro;"><fmt:formatNumber
										value="${fct:round(ale[1].value*0.001,1)}"  /></td>
							</c:otherwise>
						</c:choose>
						<fmt:setLocale value="${fn:substring(analysis.language.alpha3,0, 2)}" scope="session" />
						<td colspan="2"></td>
					</tr>
				</tfoot>
			</c:if>
		</table>
	</div>
</div>
