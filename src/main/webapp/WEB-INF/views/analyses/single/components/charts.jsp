<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:if test="${type=='QUANTITATIVE'}">
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
			<div id="chart_ale_asset" class="col-xs-12"></div>
			<hr style="margin: 30px 0;" class="col-xs-12" />
			<div id="chart_ale_asset_type" class="col-xs-12"></div>
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
			<div id="chart_ale_scenario" class="col-xs-12"></div>
			<hr style="margin: 30px 0;" class="col-xs-12" />
			<div id="chart_ale_scenario_type" class="col-xs-12"></div>
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
		<div id="chart_compliance_body" class="col-xs-12"></div>
	</div>
</div>
<c:if test="${type=='QUANTITATIVE'}">
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
			<div id="chart_evolution_profitability_compliance_APPN" class="col-xs-12"></div>
			<c:if test="${show_uncertainty}">
				<hr style="margin: 30px 0;" class="col-xs-12" />
				<div id="chart_evolution_profitability_compliance_APPO" class="col-xs-12"></div>
				<hr style="margin: 30px 0;" class="col-xs-12" />
				<div id="chart_evolution_profitability_compliance_APPP" class="col-xs-12"></div>
			</c:if>
	
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
			<div id="chart_budget_APPN" class="col-xs-12"></div>
			<c:if test="${show_uncertainty}">
				<hr style="margin: 30px 0;" class="col-xs-12" />
				<div id="chart_budget_APPO" class="col-xs-12"></div>
				<hr style="margin: 30px 0;" class="col-xs-12" />
				<div id="chart_budget_APPP" class="col-xs-12"></div>
			</c:if>
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
			<div id="chart_parameterevolution_body" class="col-xs-12"></div>
		</div>
	</div>
	<div class="tab-pane trick-chart-tab" id="tab-chart-ale-evolution-by-asset-type" data-update-required="true" data-trigger="loadChartDynamicAleEvolutionByAssetType">
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
			<div id="chart_aleevolutionbyassettype_body" class="col-xs-12"></div>
		</div>
	</div>
	<div class="tab-pane trick-chart-tab" id="tab-chart-ale-evolution-by-scenario" data-update-required="true" data-trigger="loadChartDynamicAleEvolutionByScenario">
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
			<div id="chart_aleevolutionbyscenario_body" class="col-xs-12"></div>
		</div>
	</div>
</c:if>