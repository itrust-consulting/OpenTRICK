<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:choose>
	<c:when test="${empty selectedScenario}">
		<label data-trick-controller-name='scenario' class="label label-danger"><spring:message code="error.rrf.no_scenrario" /> </label>
	</c:when>
	<c:otherwise>
		<spring:message text="${typeValue?'success':'danger'}" var="cssclass" />
		<table data-trick-controller-name='scenario' class="table table-condensed" style="margin-bottom: 0;">
			<thead>
				<tr style="text-align: center;">
					<th class="${cssclass} pdlc text-center" data-trick-type="type"><spring:message code="label.rrf.scenario.preventive" /></th>
					<th class="${cssclass} pdlc text-center" data-trick-type="type"><spring:message code="label.rrf.scenario.detective" /></th>
					<th class="${cssclass} pdlc text-center" data-trick-type="type"><spring:message code="label.rrf.scenario.limitative" /></th>
					<th class="${cssclass} pdlc text-center" data-trick-type="type"><spring:message code="label.rrf.scenario.corrective" /></th>
					<th class="warning text-center" data-trick-type="source"><spring:message code="label.rrf.scenario.intentional" /></th>
					<th class="warning text-center" data-trick-type="source"><spring:message code="label.rrf.scenario.accidental" /></th>
					<th class="warning text-center" data-trick-type="source"><spring:message code="label.rrf.scenario.environmental" /></th>
					<th class="warning text-center" data-trick-type="source"><spring:message code="label.rrf.scenario.internalThreat" /></th>
					<th class="warning text-center" data-trick-type="source"><spring:message code="label.rrf.scenario.externalThreat" /></th>
					<c:forEach items="${selectedScenario.assetTypeValues}" var="assetType">
						<th class="text-center"><spring:message text='${assetType.assetType.type}' /></th>
					</c:forEach>
				</tr>
			</thead>
			<tbody>
				<tr style="text-align: center;">
					<td class="${cssclass} pdlc" data-trick-type="type"><input type="text" id="scenario_preventive" class="slider" value="${selectedScenario.preventive}" data-slider-min="0"
						data-slider-max="1" data-slider-step="0.05" data-slider-value="${selectedScenario.preventive}" data-slider-orientation="vertical" data-slider-selection="after"
						name="preventive" data-slider-tooltip="show"></td>
					<td class="${cssclass} pdlc" data-trick-type="type"><input type="text" class="slider" id="scenario_detective" value="${selectedScenario.detective}" data-slider-min="0"
						data-slider-max="1" data-slider-step="0.05" data-slider-value="${selectedScenario.detective}" name="detective" data-slider-orientation="vertical"
						data-slider-selection="after" data-slider-tooltip="show"></td>
					<td class="${cssclass} pdlc" data-trick-type="type"><input type="text" id="scenario_limitative" class="slider" value="${selectedScenario.limitative}" data-slider-min="0"
						data-slider-max="1" data-slider-step="0.05" data-slider-value="${selectedScenario.limitative}" data-slider-orientation="vertical" data-slider-selection="after"
						name="limitative" data-slider-tooltip="show"></td>
					<td class="${cssclass} pdlc" data-trick-type="type"><input type="text" class="slider" id="scenario_corrective" value="${selectedScenario.corrective}" data-slider-min="0"
						data-slider-max="1" data-slider-step="0.05" data-slider-value="${selectedScenario.corrective}" name="corrective" data-slider-orientation="vertical"
						data-slider-selection="after" data-slider-tooltip="show"></td>

					<td class="warning" data-trick-type="source"><input type="text" class="slider" id="scenario_intentional" value="${selectedScenario.intentional}" data-slider-min="0"
						data-slider-max="1" data-slider-step="1" data-slider-value="${selectedScenario.intentional}" name="intentional" data-slider-orientation="vertical"
						data-slider-selection="after" data-slider-tooltip="show"></td>
					<td class="warning" data-trick-type="source"><input type="text" class="slider" id="scenario_accidental" value="${selectedScenario.accidental}" data-slider-min="0"
						data-slider-max="1" data-slider-step="1" data-slider-value="${selectedScenario.accidental}" name="accidental" data-slider-orientation="vertical" data-slider-selection="after"
						data-slider-tooltip="show"></td>
					<td class="warning" data-trick-type="source"><input type="text" class="slider" id="scenario_environmental" value="${selectedScenario.environmental}" data-slider-min="0"
						data-slider-max="1" data-slider-step="1" data-slider-value="${selectedScenario.environmental}" name="environmental" data-slider-orientation="vertical"
						data-slider-selection="after" data-slider-tooltip="show"></td>
					<td class="warning" data-trick-type="source"><input type="text" class="slider" id="scenario_internalThreat" value="${selectedScenario.internalThreat}" data-slider-min="0"
						data-slider-max="1" data-slider-step="1" data-slider-value="${selectedScenario.internalThreat}" name="internalThreat" data-slider-orientation="vertical"
						data-slider-selection="after" data-slider-tooltip="show"></td>
					<td class="warning" data-trick-type="source"><input type="text" class="slider" id="scenario_externalThreat" value="${selectedScenario.externalThreat}" data-slider-min="0"
						data-slider-max="1" data-slider-step="1" data-slider-value="${selectedScenario.externalThreat}" name="externalThreat" data-slider-orientation="vertical"
						data-slider-selection="after" data-slider-tooltip="show"></td>

					<c:forEach items="${selectedScenario.assetTypeValues}" var="assetType">
						<td class="" data-trick-class="AssetType"><input type="text" class="slider" id='scenario_<spring:message text="${assetType.assetType.type}"/>' value="${assetType.value}"
							data-slider-min="0" data-slider-max="1" data-slider-step="1" data-slider-value="${assetType.value}" name='<spring:message text="${assetType.assetType.type}"/>'
							data-slider-orientation="vertical" data-slider-selection="after" data-slider-tooltip="show"></td>
					</c:forEach>
				</tr>
				<tr>
					<td class="${cssclass} pdlc" data-trick-type="type"><input type="text" style="text-align: center;" id="scenario_preventive_value" readonly="readonly" class="form-control"
						value="${selectedScenario.preventive}"></td>
					<td class="${cssclass} pdlc" data-trick-type="type"><input type="text" style="text-align: center;" readonly="readonly" class="form-control" id="scenario_detective_value"
						value="${selectedScenario.detective}"></td>
					<td class="${cssclass} pdlc" data-trick-type="type"><input type="text" style="text-align: center;" id="scenario_limitative_value" readonly="readonly" class="form-control"
						value="${selectedScenario.limitative}"></td>
					<td class="${cssclass} pdlc" data-trick-type="type"><input type="text" style="text-align: center;" readonly="readonly" class="form-control" id="scenario_corrective_value"
						value="${selectedScenario.corrective}"></td>
					<td class="warning" data-trick-type="source"><input type="text" style="text-align: center;" readonly="readonly" class="form-control" id="scenario_intentional_value"
						value="${selectedScenario.intentional}"></td>
					<td class="warning" data-trick-type="source"><input type="text" style="text-align: center;" readonly="readonly" class="form-control" id="scenario_accidental_value"
						value="${selectedScenario.accidental}"></td>
					<td class="warning" data-trick-type="source"><input type="text" style="text-align: center;" readonly="readonly" class="form-control" id="scenario_environmental_value"
						value="${selectedScenario.environmental}"></td>
					<td class="warning" data-trick-type="source"><input type="text" style="text-align: center;" readonly="readonly" class="form-control" id="scenario_internalThreat_value"
						value="${selectedScenario.internalThreat}"></td>
					<td class="warning" data-trick-type="source"><input type="text" style="text-align: center;" readonly="readonly" class="form-control" id="scenario_externalThreat_value"
						value="${selectedScenario.externalThreat}"></td>
					<c:forEach items="${selectedScenario.assetTypeValues}" var="assetType">
						<td class="" data-trick-class="AssetType"><input type="text" style="text-align: center; min-width: 40px;" id='scenario_<spring:message text="${assetType.assetType.type}"/>_value'
							readonly="readonly" class="form-control" value="${assetType.value}" name="<spring:message text="${assetType.assetType.type}" />"></td>
					</c:forEach>
				</tr>
			</tbody>
		</table>
	</c:otherwise>
</c:choose>