<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:choose>
	<c:when test="${empty selectedScenario}">
		<label data-trick-controller-name='scenario' class="label label-danger"><spring:message code="error.rrf.no_scenrario" /> </label>
	</c:when>
	<c:otherwise>
		<spring:message text="${typeValue?'success':'danger'}" var="cssclass" />
		<table data-trick-controller-name='scenario' class="table table-condensed rrf-values" style="margin-bottom: 0;">
			<thead>
				<tr>
					<th class="${cssclass} pdlc text-center" data-trick-type="type"><spring:message code="label.rrf.scenario.preventive" /></th>
					<th class="${cssclass} pdlc text-center" data-trick-type="type"><spring:message code="label.rrf.scenario.detective" /></th>
					<th class="${cssclass} pdlc text-center" data-trick-type="type"><spring:message code="label.rrf.scenario.limitative" /></th>
					<th class="${cssclass} pdlc text-center" data-trick-type="type"><spring:message code="label.rrf.scenario.corrective" /></th>
					<th class="warning text-center" data-trick-type="source"><spring:message code="label.rrf.scenario.intentional" /></th>
					<th class="warning text-center" data-trick-type="source"><spring:message code="label.rrf.scenario.accidental" /></th>
					<th class="warning text-center" data-trick-type="source"><spring:message code="label.rrf.scenario.environmental" /></th>
					<th class="warning text-center" data-trick-type="source"><spring:message code="label.rrf.scenario.internalThreat" /></th>
					<th class="warning text-center" data-trick-type="source"><spring:message code="label.rrf.scenario.externalThreat" /></th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class="text-center ${cssclass} pdlc" data-trick-type="type"><input type="range" id="scenario_preventive" orient="vertical" value="${selectedScenario.preventive}"
						min="0" max="1" step="0.05" value="${selectedScenario.preventive}" 
						selection="after" name="preventive" tooltip="show"></td>
					<td class="text-center ${cssclass} pdlc" data-trick-type="type"><input type="range" orient="vertical" id="scenario_detective" value="${selectedScenario.detective}"
						min="0" max="1" step="0.05" value="${selectedScenario.detective}" name="detective" 
						></td>
					<td class="text-center ${cssclass} pdlc" data-trick-type="type"><input type="range" id="scenario_limitative" orient="vertical" value="${selectedScenario.limitative}"
						min="0" max="1" step="0.05" value="${selectedScenario.limitative}" 
						selection="after" name="limitative" tooltip="show"></td>
					<td class="text-center ${cssclass} pdlc" data-trick-type="type"><input type="range" orient="vertical" id="scenario_corrective" value="${selectedScenario.corrective}"
						min="0" max="1" step="0.05" value="${selectedScenario.corrective}" name="corrective" 
						></td>

					<td class="text-center warning" data-trick-type="source"><input type="range" orient="vertical" id="scenario_intentional" value="${selectedScenario.intentional}"
						min="0" max="1" step="1" value="${selectedScenario.intentional}" name="intentional" 
						></td>
					<td class="text-center warning" data-trick-type="source"><input type="range" orient="vertical" id="scenario_accidental" value="${selectedScenario.accidental}"
						min="0" max="1" step="1" value="${selectedScenario.accidental}" name="accidental" 
						></td>
					<td class="text-center warning" data-trick-type="source"><input type="range" orient="vertical" id="scenario_environmental" value="${selectedScenario.environmental}"
						min="0" max="1" step="1" value="${selectedScenario.environmental}" name="environmental" 
						></td>
					<td class="text-center warning" data-trick-type="source"><input type="range" orient="vertical" id="scenario_internalThreat" value="${selectedScenario.internalThreat}"
						min="0" max="1" step="1" value="${selectedScenario.internalThreat}" name="internalThreat" 
						></td>
					<td class="text-center warning" data-trick-type="source"><input type="range" orient="vertical" id="scenario_externalThreat" value="${selectedScenario.externalThreat}"
						min="0" max="1" step="1" value="${selectedScenario.externalThreat}" name="externalThreat"></td>
				</tr>
				<tr>
					<td class="text-center ${cssclass}  pdlc" data-trick-type="type"><input type="text" id="scenario_preventive_value" readonly="readonly" class="text-center form-control"
						value="${selectedScenario.preventive}"></td>
					<td class="text-center ${cssclass} pdlc" data-trick-type="type"><input type="text" readonly="readonly" class="text-center form-control" id="scenario_detective_value"
						value="${selectedScenario.detective}"></td>
					<td class="text-center ${cssclass} pdlc" data-trick-type="type"><input type="text" id="scenario_limitative_value" readonly="readonly" class="text-center form-control"
						value="${selectedScenario.limitative}"></td>
					<td class="text-center ${cssclass} pdlc" data-trick-type="type"><input type="text" readonly="readonly" class="text-center form-control" id="scenario_corrective_value"
						value="${selectedScenario.corrective}"></td>
					<td class="text-center warning" data-trick-type="source"><input type="text" readonly="readonly" class="text-center form-control" id="scenario_intentional_value"
						value="${selectedScenario.intentional}"></td>
					<td class="text-center warning" data-trick-type="source"><input type="text" readonly="readonly" class="text-center form-control" id="scenario_accidental_value"
						value="${selectedScenario.accidental}"></td>
					<td class="text-center warning" data-trick-type="source"><input type="text" readonly="readonly" class="text-center form-control" id="scenario_environmental_value"
						value="${selectedScenario.environmental}"></td>
					<td class="text-center warning" data-trick-type="source"><input type="text" readonly="readonly" class="text-center form-control" id="scenario_internalThreat_value"
						value="${selectedScenario.internalThreat}"></td>
					<td class="text-center warning" data-trick-type="source"><input type="text" readonly="readonly" class="text-center form-control" id="scenario_externalThreat_value"
						value="${selectedScenario.externalThreat}"></td>
				</tr>
			</tbody>
		</table>
	</c:otherwise>
</c:choose>