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
			<spring:message code="label.title.scenario" text="Scenario" />
		</h3>
	</div>
	<div class="panel panel-default">
		<div class="panel-heading" style="min-height: 60px">
			<ul class="nav nav-pills" id="menu_scenario">
				<li><a href="#" onclick="return editScenario(undefined,true);"><span class="glyphicon glyphicon-plus primary"></span> <spring:message code="label.menu.add.scenario"
							text="Add" /> </a></li>
				<li class="disabled" trick-selectable="true"><a href="#" onclick="return editScenario();"><span class="glyphicon glyphicon-edit danger"></span> <spring:message
							code="label.menu.edit.scenario" text="Edit" /> </a></li>
				<li class="disabled" trick-selectable="multi"><a href="#" onclick="return selectScenario(undefined, 'true')"><span class="glyphicon glyphicon-plus-sign"></span> <spring:message
							code="label.menu.select.scenario" text="Select" /> </a></li>
				<li class="disabled" trick-selectable="multi"><a href="#" onclick="return selectScenario(undefined, 'false')"><span class="glyphicon glyphicon-minus-sign "></span> <spring:message
							code="label.menu.unselect.scenario" text="Unselect" /> </a></li>
				<c:if test="${!KowledgeBaseView }">
					<li class="disabled" trick-selectable="true" trick-check="isSelected('scenario')"><a href="#" onclick="return displayAssessmentByScenario()"><span
							class="glyphicon glyphicon-new-window"></span> <spring:message code="label.menu.show.assessment" text="Assessment" /> </a></li>
				</c:if>
				<li class="disabled pull-right" trick-selectable="multi"><a href="#" class="text-danger" onclick="return deleteScenario();"><span class="glyphicon glyphicon-remove"></span>
						<spring:message code="label.menu.delete.scenario" text="Delete" /> </a></li>
			</ul>
		</div>
		<div class="panel-body autofitpanelbodydefinition">
			<table id="scneariotable" class="table table-hover table-fixed-header">
				<thead>
					<tr>
						<th><input type="checkbox" class="checkbox" onchange="return checkControlChange(this,'scenario')"></th>
						<th><spring:message code="label.row.index" text="#" htmlEscape="true" /></th>
						<th colspan="15"><spring:message code="label.scenario.name" text="Name" htmlEscape="true" /></th>
						<th colspan="3"><spring:message code="label.scenario.type" text="Type" htmlEscape="true" /></th>
						<c:choose>
							<c:when test="${empty(show_uncertainty) or show_uncertainty}">
								<th colspan="2"><spring:message code="label.scenario.aleo" text="ALEO" /> (k&euro;)</th>
								<th colspan="2"><spring:message code="label.scenario.ale" text="ALE" /> (k&euro;)</th>
								<th colspan="2"><spring:message code="label.scenario.alep" text="ALEP" /> (k&euro;)</th>
							</c:when>
							<c:otherwise>
								<th colspan="2"><spring:message code="label.scenario.ale" text="ALE" /> (k&euro;)</th>
							</c:otherwise>
						</c:choose>
						<th colspan="20"><spring:message code="label.scenario.description" text="Description" htmlEscape="true" /></th>
					</tr>
				</thead>
				<tfoot></tfoot>
				<tbody>
					<c:forEach items="${scenarios}" var="scenario" varStatus="status">
						<c:set var="cssClass">
								${scenario.selected? 'success' : ''}
							</c:set>
						<tr trick-id="${scenario.id}" trick-selected="${scenario.selected}" ondblclick="return editScenario(${scenario.id})">
							<c:set var="ale" value="${scenarioALE[scenario.id]}" />
							<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_scenario','#menu_scenario');"></td>
							<td>${status.index+1}</td>
							<td class="${cssClass}" colspan="15"><spring:message text="${scenario.name}" /></td>
							<td class="${cssClass}" colspan="3"><spring:message code="label.scenario.type.${fn:toLowerCase(fn:replace(scenario.scenarioType.name,'-','_'))}"
									text="${scenario.scenarioType.name}" /></td>
							<c:choose>
								<c:when test="${empty(show_uncertainty) or show_uncertainty}">
									<td colspan="2" title="<fmt:formatNumber value="${ale[0].value}" maxFractionDigits="2" minFractionDigits="0" />&euro;"><fmt:formatNumber value="${ale[0].value*0.001}"
											maxFractionDigits="2" minFractionDigits="0" /></td>
									<td colspan="2" title="<fmt:formatNumber value="${ale[1].value}" maxFractionDigits="2" minFractionDigits="0" />&euro;"><fmt:formatNumber value="${ale[1].value*0.001}"
											maxFractionDigits="2" minFractionDigits="0" /></td>
									<td colspan="2" title="<fmt:formatNumber value="${ale[2].value}" maxFractionDigits="2" minFractionDigits="0" />&euro;"><fmt:formatNumber value="${ale[2].value*0.001}"
											maxFractionDigits="2" minFractionDigits="0" /></td>
								</c:when>
								<c:otherwise>
									<td colspan="2" title="<fmt:formatNumber value="${ale[1].value}" maxFractionDigits="2" minFractionDigits="0" />&euro;"><fmt:formatNumber value="${ale[1].value*0.001}"
											maxFractionDigits="2" minFractionDigits="0" /></td>
								</c:otherwise>
							</c:choose>
							<td class="${cssClass}" colspan="20"><pre>
									<spring:message text="${scenario.description}" />
								</pre></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</div>
