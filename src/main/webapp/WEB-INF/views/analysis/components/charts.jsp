<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="section" id="section_chart">
	<div class="page-header">
		<h3 id="Charts">
			<spring:message code="label.charts" text="Charts" />
		</h3>
	</div>
	<div id="chart_asset" class="sectionpanel">
		<div class="panel panel-default">
			<div class="panel-heading">
				<spring:message code="label.chart.title.asset" text="Asset" />
			</div>
			<div class="panel-body">
				<div id="chart_ale_asset"></div>
				<hr style="margin: 30px 0;" />
				<div id="chart_ale_asset_type"></div>
			</div>
		</div>
	</div>
	<div id="chart_scenario" class="sectionpanel">
		<div class="panel panel-default">
			<div class="panel-heading">
				<spring:message code="label.chart.title.scenario" text="Scenario" />
			</div>
			<div class="panel-body">
				<div id="chart_ale_scenario"></div>
				<hr style="margin: 30px 0;" />
				<div id="chart_ale_scenario_type"></div>
			</div>
		</div>
	</div>
	<div id="chart_compliance" class="sectionpanel">
		<div class="panel panel-default">
			<div class="panel-heading">
				<spring:message code="label.chart.title.compliance" text="Compliance" />
			</div>
			<div class="panel-body">
				<div id="chart_compliance_27001"></div>
				<hr style="margin: 30px 0;" />
				<div id="chart_compliance_27002"></div>
			</div>
		</div>
	</div>
	<div id="chart_evolution" class="sectionpanel">
		<div class="panel panel-default">
			<div class="panel-heading">
				<spring:message code="label.chart.title.evolution.profitability.compliance" text="Evolution of profitability and ISO compliance" />
			</div>
			<div class="panel-body">
				<div id="chart_evolution_profitability_compliance_APPN"></div>
				<hr style="margin: 30px 0;" />
				<div id="chart_evolution_profitability_compliance_APPO"></div>
				<hr style="margin: 30px 0;" />
				<div id="chart_evolution_profitability_compliance_APPP"></div>
			</div>
		</div>
	</div>
	<div id="chart_budget" class="sectionpanel">
		<div class="panel panel-default">
			<div class="panel-heading">
				<spring:message code="label.chart.title.budget" text="Budget" />
			</div>
			<div class="panel-body">
				<div id="chart_budget_APPN"></div>
				<hr style="margin: 30px 0;" />
				<div id="chart_budget_APPO"></div>
				<hr style="margin: 30px 0;" />
				<div id="chart_budget_APPP"></div>
			</div>
		</div>
	</div>
</div>