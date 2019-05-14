<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<ul class="nav nav-tabs affix affix-top nav-analysis">
	<c:if test="${!isProfile}">
		<li class="active"><a href="#tab-history" data-toggle="tab"><spring:message code="label.menu.analysis.history" /></a></li>
	</c:if>
	<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="label.menu.analysis.risk_context" /><span class="caret"></span></a>
		<ul class="dropdown-menu">
			<c:if test="${!isProfile}">
				<li><a href="#tab-scope" data-toggle="tab"><spring:message code="label.menu.analysis.item_information" /></a></li>
				<li class="divider"></li>
			</c:if>
			<li class="dropdown-header"><spring:message code="label.menu.analysis_parameters" text='Analysis parameters' /></li>
			<li><a href="#tab-parameter" data-toggle="tab" title='<spring:message code="label.title.other.parameters" />'><spring:message code="label.menu.analysis.parameter" /></a></li>
			<li><a href="#tab-parameter-impact-probability" data-toggle="tab"><spring:message code="label.menu.analysis.parameter.impact_probability" /></a></li>
			<c:if test="${type.quantitative and hasMaturity}">
				<li><a href="#tab-parameter-maturity" data-toggle="tab"><spring:message code="label.menu.analysis.parameter.maturity" /></a></li>
			</c:if>
		</ul></li>
	<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="label.menu.analysis.risk_analysis" /> <span class="caret"></span></a>
		<ul class="dropdown-menu">
			<c:if test="${!isProfile}">
				<li class="dropdown-header"><spring:message code="label.menu.analysis.qualitative_analysis" /></li>
				<li><a href="#tab-risk-information-threat" data-toggle="tab"><spring:message code="label.menu.analysis.threat" /></a></li>
				<li><a href="#tab-risk-information-vul" data-toggle="tab"><spring:message code="label.menu.analysis.vulnerability" /></a></li>
				<li><a href="#tab-risk-information-risk" data-toggle="tab"><spring:message code="label.menu.analysis.risk" /></a></li>
				<li class="divider"></li>
			</c:if>
			<li class="dropdown-header"><spring:message code="label.menu.analysis.quantitative_analysis" /></li>
			<c:if test="${!isProfile}">
				<li><a href="#tab-asset" data-toggle="tab"><spring:message code="label.menu.analysis.asset" /></a></li>
			</c:if>
			<li><a href="#tab-scenario" data-toggle="tab"><spring:message code="label.menu.analysis.scenario" /></a></li>
			<c:if test="${!isProfile}">
				<li><a href="#tab-risk-estimation" data-toggle="tab"><spring:message code="label.menu.analysis.risk_estimation" /></a></li>
			</c:if>
		</ul></li>

	<li class="dropdown-submenu" id="tab-standard"><a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message
				code="label.menu.analysis.risk_treatment_compliance" /><span class="caret"></span></a>
		<ul class="dropdown-menu" id="standardmenu">
			<li class="dropdown-header"><spring:message code="label.menu.analysis.standards" /></li>
			<li><a href="#tab-standards" data-toggle="tab"><spring:message code="label.menu.analysis.view_by_collection" /></a>
			<li title='<spring:message code="label.menu.view.measures"/>'><a href="#tab-measure-edition" data-toggle="tab"><spring:message
						code="label.menu.analysis.view_by_measure" /></a></li>
			<c:if test="${isProfile || isEditable}">
				<li title='<spring:message code="label.title.manage_standard"/>'><a href="#" onclick="return manageStandard();"><spring:message code="label.menu.manage_standard" /></a></li>
			</c:if>
			<li class="divider"></li>
			<li class="dropdown-header"><spring:message code="label.menu.analysis.implementation" /></li>
			<li><a href="#tab-phase" data-toggle="tab"> <spring:message code="label.menu.analysis.phase" /></a></li>
			<c:if test="${!isProfile}">
				<li><a href="#tab-action-plan" data-toggle="tab"> <spring:message code="label.menu.analysis.action_plan" /></a></li>
			</c:if>
		</ul></li>
	<c:if test="${!isProfile}">
		<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"> <spring:message code="label.menu.analysis.risk_communication" /><span
				class="caret"></span></a>
			<ul class="dropdown-menu">
				<c:if test="${not empty soas}">
					<li><a href="#tab-soa" data-toggle="tab"> <spring:message code="label.menu.analysis.soa" /></a></li>
				</c:if>
				<li><a href="#tab-summary" data-toggle="tab"> <spring:message code="label.menu.analysis.summary" /></a></li>
				<c:if test="${type.qualitative}">
					<li><a href="#tab-risk-register" data-toggle="tab"> <spring:message code="label.menu.analysis.risk_register" /></a></li>
				</c:if>
				<li class="divider"></li>
				<li class="dropdown-header"><spring:message code="label.menu.analysis.chart" /></li>
				<li><a href="#tab-chart-compliance" data-toggle="tab"> <spring:message code="label.chart.compliance" /></a></li>
				<c:if test="${type.qualitative}">
					<c:if test="${type.quantitative}">
						<li class="dropdown-header"><spring:message code="label.menu.analysis.chart.qualitative" /></li>
					</c:if>
					<li><a href="#tab-chart-heat-map" data-toggle="tab"> <spring:message code="label.chart.risk_acceptance_heat_map" /></a></li>
					<li><a href="#tab-chart-risk-asset" data-toggle="tab"> <spring:message code="label.chart.risk_by_assets" /></a></li>
					<li><a href="#tab-chart-risk-asset-type" data-toggle="tab"> <spring:message code="label.chart.risk_by_asset_types" /></a></li>
					<li><a href="#tab-chart-risk-scenario" data-toggle="tab"> <spring:message code="label.chart.risk_by_scenarios" /></a></li>
					<li><a href="#tab-chart-risk-scenario-type" data-toggle="tab"> <spring:message code="label.chart.risk_by_scenario_types" /></a></li>
				</c:if>
				<c:if test="${type.quantitative}">
					<c:if test="${type.qualitative}">
						<li class="dropdown-header"><spring:message code="label.menu.analysis.chart.quantitative" /></li>
					</c:if>
					<li><a href="#tab-chart-asset" data-toggle="tab"> <spring:message code="label.chart.asset" /></a></li>
					<li><a href="#tab-chart-scenario" data-toggle="tab"> <spring:message code="label.chart.scenario" /></a></li>
					<li><a href="#tab-chart-evolution" data-toggle="tab"> <spring:message code="label.chart.evolution" /></a></li>
					<li><a href="#tab-chart-budget" data-toggle="tab"> <spring:message code="label.chart.budget" /></a></li>
					<c:if test="${showDynamicAnalysis}">
						<li class="dropdown-header"><spring:message code="label.menu.analysis.chart.dynamic" /></li>
						<li><a href="#tab-chart-ale-evolution" data-toggle="tab"> <spring:message code="label.title.chart.aleevolution" /></a></li>
						<li><a href="#tab-chart-ale-evolution-by-asset-type" data-toggle="tab"> <spring:message code="label.title.chart.aleevolution_by_asset_type" /></a></li>
						<li><a href="#tab-chart-parameter-evolution" data-toggle="tab"> <spring:message code="label.title.chart.dynamic" /></a></li>
					</c:if>
				</c:if>
			</ul></li>
	</c:if>
	<li class="pull-right" data-role='analysis-menu-control'><a id='nav_menu_analysis_close' href="${pageContext.request.contextPath}/Analysis/Deselect" class="text-danger"
		title='<spring:message code="label.action.close.analysis" />'><i class="fa fa-sign-out fa-2x"></i></a></li>
	<li class="dropdown-submenu pull-right" data-role='analysis-menu-control'><a href="#" class="dropdown-toggle" data-toggle="dropdown"
		title='<spring:message code="label.actions" />'><i class="fa fa-cog fa-2x"></i></a>
		<ul class="dropdown-menu" id="actionmenu">
			<c:if test="${not isProfile}">
				<li class="dropdown-header"><spring:message code="label.menu.title.manual.update" /></li>
				<c:choose>
					<c:when test="${type.qualitative}">
						<li><a href="#" onclick="return calculateAction({'id':'${analysis.id}'})"> <spring:message code="label.action.compute.action_plan" /></a></li>
					</c:when>
					<c:otherwise>
						<li><a href="#" onclick="return displayActionPlanOptions('${analysis.id}')"> <spring:message code="label.action.compute.action_plan" /></a></li>
						<c:if test="${not isEditable}">
							<li class="divider"></li>
						</c:if>
					</c:otherwise>
				</c:choose>
				<li><a href="#" onclick="return reloadCharts();"> <spring:message code="label.action.reload.charts" /></a></li>
				<c:if test="${isEditable}">
					<li><a href="#" onclick="return computeAssessment();"> <spring:message code="label.action.generate.missing" /></a></li>
					<c:if test="${type.quantitative and hasMaturity}">
						<li><a href="#" onclick="return updateMeasureEffience(undefined, true)"><spring:message code="label.action.update.efficiency" /></a></li>
					</c:if>
				</c:if>
				<c:if test="${isEditable}">
					<li class="divider"></li>
					<c:if test="${canExport}">
						<li><a href="#" onclick="return exportDataManager(${analysis.id})"> <spring:message code="label.action.export" /></a></li>
					</c:if>
					<li><a href="#" onclick="return importDataManager(${analysis.id})"> <spring:message code="label.action.import" /></a></li>
					<li class="divider"></li>
				</c:if>
			</c:if>
			<c:if test="${isProfile or isEditable}">
				<c:if test="${type.quantitative}">
					<li><a href="#" onclick="return loadRRF();"><spring:message code="label.title.rrf" /></a></li>
					<li class="divider"></li>
				</c:if>
				<li class="dropdown-header"><spring:message code="label.title.edit_mode" /></li>
				<li><a class='btn-group btn-group-xs clearfix'><span role="enterEditMode" class="btn btn-default" onclick="return enableEditMode()"><spring:message
								code="label.action.edit_mode.open" /></span> <span role="leaveEditMode" class="btn btn-default active disabled" onclick="return disableEditMode()"><spring:message
								code="label.action.edit_mode.close" /></span> </a></li>
				<c:if test="${not isProfile and isEditable}">
					<li class="divider"></li>
				</c:if>
			</c:if>
			<c:if test="${not isProfile and isEditable}">
				<li class="dropdown-header"><spring:message code="label.menu.settings" /></li>
				<li><a href="#" onclick="return manageSOA();"> <spring:message code="label.action.manage.soa" /></a></li>
				<li><a href="#" onclick="return manageImpactScale();"> <spring:message code="label.action.manage.impact" /></a></li>
				<li><a href="#" onclick="return manageScaleLevel();"> <spring:message code="label.action.manage.scale.level" /></a></li>
				<li><a href="#" onclick="return manageAnalysisSettings();"><spring:message code="label.action.analysis.setting" /></a></li>
			</c:if>
		</ul></li>
	<li id="tabOption" style="display: none;" class="dropdown-submenu pull-right" data-role='analysis-menu-control'><a href="#" title='<spring:message code="label.options" />'
		class="dropdown-toggle" data-toggle="dropdown"><span class="fa fa-bars fa-2x"></span></a></li>
</ul>