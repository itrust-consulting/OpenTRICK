<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="section" id="section_chart">
	<div class="page-header">
		<h3 id="Charts">
			<fmt:message key="label.title.charts" />
		</h3>
	</div>
	<span class="anchor" id="anchorChartAsset"></span>
	<div id="chart_asset" class="sectionpanel">
		<div class="panel panel-default">
			<div class="panel-heading">
				<fmt:message key="label.title.chart.asset"  />
			</div>
			<div class="panel-body">
				<div id="chart_ale_asset"></div>
				<hr style="margin: 30px 0;" />
				<div id="chart_ale_asset_type"></div>
			</div>
		</div>
	</div>
	<span class="anchor" id="anchorChartScenario"></span>
	<div id="chart_scenario" class="sectionpanel">
		<div class="panel panel-default">
			<div class="panel-heading">
				<fmt:message key="label.title.chart.scenario"  />
			</div>
			<div class="panel-body">
				<div id="chart_ale_scenario"></div>
				<hr style="margin: 30px 0;" />
				<div id="chart_ale_scenario_type"></div>
			</div>
		</div>
	</div>
	<span class="anchor" id="anchorChartCompliance"></span>
	<div id="chart_compliance" class="sectionpanel">
		<div class="panel panel-default">
			<div class="panel-heading">
				<fmt:message key="label.title.chart.compliance"  />
			</div>
			<div class="panel-body">
				<div id="chart_compliance_27001"></div>
				<hr style="margin: 30px 0;" />
				<div id="chart_compliance_27002"></div>
			</div>
		</div>
	</div>
	<span class="anchor" id="anchorChartEvolution"></span>
	<div id="chart_evolution" class="sectionpanel">
		<div class="panel panel-default">
			<div class="panel-heading">
				<fmt:message key="label.title.chart.evolution.profitability.compliance"  />
			</div>
			<div class="panel-body">
				<div id="chart_evolution_profitability_compliance_APPN"></div>
				<c:if test="${show_uncertainty}">
					<hr style="margin: 30px 0;" />
					<div id="chart_evolution_profitability_compliance_APPO"></div>
					<hr style="margin: 30px 0;" />
					<div id="chart_evolution_profitability_compliance_APPP"></div>
				</c:if>
			</div>
		</div>
	</div>
	<span class="anchor" id="anchorChartBudget"></span>
	<div id="chart_budget" class="sectionpanel">
		<div class="panel panel-default">
			<div class="panel-heading">
				<fmt:message key="label.title.chart.budget"  />
			</div>
			<div class="panel-body">
				<div id="chart_budget_APPN"></div>
				<c:if test="${show_uncertainty}">
					<hr style="margin: 30px 0;" />
					<div id="chart_budget_APPO"></div>
					<hr style="margin: 30px 0;" />
					<div id="chart_budget_APPP"></div>
				</c:if>
			</div>
		</div>
	</div>
</div>