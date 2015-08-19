<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="tab-pane trick-chart-tab" id="tabChartAsset" data-update-required="true" data-trigger="loadChartAsset">
	<div id="chart_asset" class="sectionpanel">
		<div class="page-header tab-content-header">
			<div class="container">
				<div class="row-fluid">
					<h3>
						<fmt:message key="label.title.chart.asset" />
					</h3>
				</div>
			</div>
		</div>
		<div id="chart_ale_asset" class="col-xs-12"></div>
		<hr style="margin: 30px 0;" class="col-xs-12" />
		<div id="chart_ale_asset_type" class="col-xs-12"></div>
	</div>
</div>
<div class="tab-pane trick-chart-tab" id="tabChartScenario" data-update-required="true" data-trigger="loadChartScenario">
	<div id="chart_scenario">
		<div class="page-header tab-content-header">
			<div class="container">
				<div class="row-fluid">
					<h3>
						<fmt:message key="label.title.chart.scenario" />
					</h3>
				</div>
			</div>
		</div>
		<div id="chart_ale_scenario" class="col-xs-12"></div>
		<hr style="margin: 30px 0;" class="col-xs-12" />
		<div id="chart_ale_scenario_type" class="col-xs-12"></div>
	</div>
</div>
<div class="tab-pane trick-chart-tab" id="tabChartCompliance" data-update-required="true" data-trigger="compliances">
	<div id="chart_compliance">
		<div class="page-header tab-content-header">
			<div class="container">
				<div class="row-fluid">
					<h3>
						<fmt:message key="label.title.chart.compliance" />
					</h3>
				</div>
			</div>
		</div>
		<div id="chart_compliance_body" class="col-xs-12"></div>
	</div>
</div>
<div class="tab-pane trick-chart-tab" id="tabChartEvolution" data-update-required="true" data-trigger="loadChartEvolution">
	<div id="chart_evolution">
		<div class="page-header tab-content-header">
			<div class="container">
				<div class="row-fluid">
					<h3>
						<fmt:message key="label.title.chart.evolution.profitability.compliance" />
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
<div class="tab-pane trick-chart-tab" id="tabChartBudget" data-update-required="true" data-trigger="loadChartBudget">
	<div id="chart_budget">
		<div class="page-header tab-content-header">
			<div class="container">
				<div class="row-fluid">
					<h3>
						<fmt:message key="label.title.chart.budget" />
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
<div class="tab-pane trick-chart-tab" id="tabChartDynamic" data-update-required="true" data-trigger="loadChartDynamic">
	<div id="chart_dynamic">
		<div class="page-header tab-content-header">
			<div class="container">
				<div class="row-fluid">
					<h3>
						<fmt:message key="label.title.chart.dynamic" />
					</h3>
				</div>
			</div>
		</div>
		<div id="chart_dynamic_body" class="col-xs-12"></div>
	</div>
</div>
<div class="tab-pane trick-chart-tab" id="tabChartAleEvolution" data-update-required="true" data-trigger="loadChartAleEvolution">
	<div id="chart_aleevolution">
		<div class="page-header tab-content-header">
			<div class="container">
				<div class="row-fluid">
					<h3>
						<fmt:message key="label.title.chart.aleevolution" />
					</h3>
				</div>
			</div>
		</div>
		<div id="chart_aleevolution_body" class="col-xs-12"></div>
	</div>
</div>
