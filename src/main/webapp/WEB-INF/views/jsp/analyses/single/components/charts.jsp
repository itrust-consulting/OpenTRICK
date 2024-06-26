<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:if test="${type.quantitative}">
	<div class="tab-pane trick-chart-tab" id="tab-chart-asset" data-update-required="true" data-trigger="loadChartAsset">
		<div id="chart_asset" class="container">
			<div class="page-header tab-content-header">
				<div class="container">
					<div class="row-fluid">
						<h3>
							<spring:message code="label.title.chart.asset" />
						</h3>
					</div>
				</div>
			</div>
			<div id="chart_ale_asset" class="col-xs-6"></div>
			<div id="chart_ale_asset_type" class="col-xs-6"></div>
		</div>
	</div>
	<div class="tab-pane trick-chart-tab" id="tab-chart-scenario" data-update-required="true" data-trigger="loadChartScenario">
		<div id="chart_scenario" class="container">
			<div class="page-header tab-content-header">
				<div class="container">
					<div class="row-fluid">
						<h3>
							<spring:message code="label.title.chart.scenario" />
						</h3>
					</div>
				</div>
			</div>
			<div id="chart_ale_scenario" class="col-xs-6"></div>
			<div id="chart_ale_scenario_type" class="col-xs-6"></div>
		</div>
	</div>
</c:if>
<div class="tab-pane trick-chart-tab" id="tab-chart-compliance" data-update-required="true" data-trigger="compliances">
	<div id="chart_compliance" class="container">
		<div class="page-header tab-content-header">
			<div class="container">
				<div class="row-fluid">
					<h3>
						<spring:message code="label.title.chart.compliance" />
					</h3>
				</div>
			</div>
		</div>
		<div id="chart_compliance_body" class="row"></div>
	</div>
</div>
<c:if test="${type.qualitative}">
	<c:set var="riskAcceptanceTablePlaceHolder">
		<fieldset>
			<legend>
				<spring:message code="label.title.parameter.risk.acceptance.threshold" />
			</legend>
			<table class="table table-hover table-condensed">
				<thead>
					<tr>
						<th><spring:message code="label.importance.threshold" /></th>
						<th style="width: 20%; text-align: center;"><spring:message code="label.parameter.label" /></th>
						<th style="width: 50%"><spring:message code="label.description" /></th>
						<th><spring:message code="label.color" /></th>
					</tr>
				</thead>
				<tbody>
					<tr class='warning'>
						<td colspan="4"><spring:message code='info.risk_acceptance.empty' /></td>
					</tr>
				</tbody>
			</table>
		</fieldset>
	</c:set>
	<div class="tab-pane trick-chart-tab" id="tab-chart-heat-map" data-update-required="true" data-trigger="reloadRiskHeatMapSection" data-parameters='true'>
		<div id="risk_acceptance_heat_map">
			<div class="page-header tab-content-header">
				<div class="container">
					<div class="row-fluid">
						<h3>
							<spring:message code="label.title.risk_acceptance.heat_map_evolution" />
						</h3>
					</div>
				</div>
			</div>
			<div class="row">
				<div class='col-sm-8'>
					<fieldset>
						<legend>
							<spring:message code="label.title.risk_acceptance.heat_map" />
						</legend>
						<canvas id="risk_acceptance_heat_map_canvas" style="max-width: 900px; margin-left: auto; margin-right: auto;"></canvas>
					</fieldset>
				</div>
				<div class='col-sm-4'>${riskAcceptanceTablePlaceHolder}</div>
			</div>
		</div>
		<div class="row">
			<div class='col-sm-8'>
				<fieldset>
					<legend>
						<spring:message code="label.title.risk_acceptance.evolution" />
					</legend>
					<canvas id="risk_acceptance_evolution_canvas" style="max-width: 1000px; margin-left: auto; margin-right: auto;"></canvas>
				</fieldset>
			</div>
			<div class="col-sm-4">
				<fieldset>
					<legend>
						<span class="col-md-4 hidden-sm"><spring:message code="label.title.risk_acceptance.legend" /></span> <span class='col-md-8'> <span class='pull-right'><span class='col-xs-9 text-right'><spring:message
										code="label.chart.item.display" /></span><span class='col-xs-3'><input class='form-control' type="number" min="0" value="10" id='chart-show-x-element'></span></span>
						</span>
					</legend>
					<div id="risk_acceptance_evolution_legend" style="overflow-x: auto; resize: vertical;"></div>
				</fieldset>
			</div>
		</div>
	</div>
	<div class="tab-pane trick-chart-tab" id="tab-chart-risk-asset" data-update-required="true" data-trigger="reloadRiskAssetSection" data-parameters='true'>
		<div id="risk_by_asset">
			<div class="page-header tab-content-header">
				<div class="container">
					<div class="row-fluid">
						<h3>
							<spring:message code="label.title.risk_by.asset" />
						</h3>
					</div>
				</div>
			</div>
			<div class='col-sm-8' id="risk_acceptance_assets"></div>
			<div class='col-sm-4'>${riskAcceptanceTablePlaceHolder}</div>
		</div>
	</div>
	<div class="tab-pane trick-chart-tab" id="tab-chart-risk-asset-type" data-update-required="true" data-trigger="reloadRiskAssetTypeSection" data-parameters='true'>
		<div id="risk_by_asset_type">
			<div class="page-header tab-content-header">
				<div class="container">
					<div class="row-fluid">
						<h3>
							<spring:message code="label.title.risk_by.asset_type" />
						</h3>
					</div>
				</div>
			</div>
			<div class='col-sm-8' id="risk_acceptance_asset_types"></div>
			<div class='col-sm-4'>${riskAcceptanceTablePlaceHolder}</div>
		</div>
	</div>

	<div class="tab-pane trick-chart-tab" id="tab-chart-risk-scenario" data-update-required="true" data-trigger="reloadRiskScenarioSection" data-parameters='true'>
		<div id="risk_by_scenario">
			<div class="page-header tab-content-header">
				<div class="container">
					<div class="row-fluid">
						<h3>
							<spring:message code="label.title.risk_by.scenario" />
						</h3>
					</div>
				</div>
			</div>
			<div class='col-sm-8' id="risk_acceptance_scenarios"></div>
			<div class='col-sm-4'>${riskAcceptanceTablePlaceHolder}</div>
		</div>
	</div>
	<div class="tab-pane trick-chart-tab" id="tab-chart-risk-scenario-type" data-update-required="true" data-trigger="reloadRiskScenarioTypeSection" data-parameters='true'>
		<div id="risk_by_scenario_type">
			<div class="page-header tab-content-header">
				<div class="container">
					<div class="row-fluid">
						<h3>
							<spring:message code="label.title.risk_by.scenario_type" />
						</h3>
					</div>
				</div>
			</div>
			<div class='col-sm-8' id="risk_acceptance_scenario_types"></div>
			<div class='col-sm-4'>${riskAcceptanceTablePlaceHolder}</div>
		</div>
	</div>
</c:if>
<c:if test="${type.quantitative}">
	<div class="tab-pane trick-chart-tab" id="tab-chart-evolution" data-update-required="true" data-trigger="loadChartEvolution">
		<div id="chart_evolution" class="container">
			<div class="page-header tab-content-header">
				<div class="container">
					<div class="row-fluid">
						<h3>
							<spring:message code="label.title.chart.evolution.profitability.compliance" />
						</h3>
					</div>
				</div>
			</div>
			<c:choose>
				<c:when test="${show_uncertainty}">
					<div id="chart_evolution_profitability_APPO" class="col-sm-6"></div>
					<div id="chart_compliance_APPO" class="col-sm-6"></div>
					<div id="chart_evolution_profitability_APPN" class="col-sm-6"></div>
					<div id="chart_compliance_APPN" class="col-sm-6"></div>
					<div id="chart_evolution_profitability_APPP" class="col-sm-6"></div>
					<div id="chart_compliance_APPP" class="col-sm-6"></div>
				</c:when>
				<c:otherwise>
					<div id="chart_evolution_profitability_APPN" class="col-sm-6"></div>
					<div id="chart_compliance_APPN" class="col-sm-6"></div>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
	<div class="tab-pane trick-chart-tab" id="tab-chart-budget" data-update-required="true" data-trigger="loadChartBudget">
		<div id="chart_budget" class="container">
			<div class="page-header tab-content-header">
				<div class="container">
					<div class="row-fluid">
						<h3>
							<spring:message code="label.title.chart.budget" />
						</h3>
					</div>
				</div>
			</div>
			<c:choose>
				<c:when test="${show_uncertainty}">
					<div id="chart_budget_cost_APPO" class="col-sm-6"></div>
					<div id="chart_budget_workload_APPO" class="col-sm-6"></div>
					<div id="chart_budget_cost_APPN" class="col-sm-6"></div>
					<div id="chart_budget_workload_APPN" class="col-sm-6"></div>
					<div id="chart_budget_cost_APPP" class="col-sm-6"></div>
					<div id="chart_budget_workload_APPP" class="col-sm-6"></div>
				</c:when>
				<c:otherwise>
					<div id="chart_budget_cost_APPN" class="col-sm-6"></div>
					<div id="chart_budget_workload_APPN" class="col-sm-6"></div>
				</c:otherwise>
			</c:choose>

		</div>
	</div>
	<c:if test="${showDynamicAnalysis}">
		<div class="tab-pane trick-chart-tab" id="tab-chart-ale-evolution" data-update-required="true" data-trigger="loadChartDynamicAleEvolution">
			<div id="chart_aleevolutionbyassettype" class="container">
				<div class="page-header tab-content-header">
					<div class="container">
						<div class="row-fluid">
							<h3>
								<fmt:message key="label.title.chart.aleevolution" />
							</h3>
						</div>
					</div>
				</div>
				<div id="chart_aleevolution_body"></div>
			</div>
		</div>
		<div class="tab-pane trick-chart-tab" id="tab-chart-ale-evolution-by-asset-type" data-update-required="true" data-trigger="loadChartDynamicAleEvolutionByAssetType">
			<div id="chart_aleevolutionbyscenario" class="container">
				<div class="page-header tab-content-header">
					<div class="container">
						<div class="row-fluid">
							<h3>
								<fmt:message key="label.title.chart.aleevolution_by_asset_type" />
							</h3>
						</div>
					</div>
				</div>
				<div id="chart_aleevolutionbyassettype_body"></div>
			</div>
		</div>
		<div class="tab-pane trick-chart-tab" id="tab-chart-parameter-evolution" data-update-required="true" data-trigger="loadChartDynamicParameterEvolution">
			<div id="chart_parameterevolution" class="container">
				<div class="page-header tab-content-header">
					<div class="container">
						<div class="row-fluid">
							<h3>
								<fmt:message key="label.title.chart.dynamic" />
							</h3>
						</div>
					</div>
				</div>
				<div id="chart_parameterevolution_body" class="row"></div>
			</div>
		</div>
	</c:if>
</c:if>