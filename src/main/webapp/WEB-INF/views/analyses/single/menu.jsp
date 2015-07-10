<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<ul class="nav nav-tabs affix affix-top nav-analysis col-xs-12">
	<c:if test="${!analysis.isProfile()}">
		<li class="active"><a href="#tabHistory" data-toggle="tab"><fmt:message key="label.menu.analysis.history" /></a></li>
	</c:if>
	<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"><fmt:message key="label.menu.analysis.risk_context" /><span class="caret"></span></a>
		<ul class="dropdown-menu">
			<c:if test="${!analysis.isProfile()}">
				<li><a href="#tabScope" data-toggle="tab"><fmt:message key="label.menu.analysis.item_information" /></a></li>
				<li class="divider"></li>
			</c:if>
			<li class="dropdown-header"><fmt:message key="label.menu.analysis.parmeter" /></li>
			<li><a href="#tabParameterImpactProba" data-toggle="tab"><fmt:message key="label.menu.analysis.parameter.impact_probability" /></a></li>
			<li><a href="#tabParameterOther" data-toggle="tab"><fmt:message key="label.menu.analysis.parameter.various" /></a></li>
		</ul></li>

	<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"><fmt:message key="label.menu.analysis.risk_analysis" /> <span class="caret"></span></a>
		<ul class="dropdown-menu">
			<c:if test="${!analysis.isProfile()}">
				<li class="dropdown-header"><fmt:message key="label.menu.analysis.qualitative_analysis" /></li>
				<li><a href="#tabRiskInformation_Threat" data-toggle="tab"><fmt:message key="label.menu.analysis.threat" /></a></li>
				<li><a href="#tabRiskInformation_Vul" data-toggle="tab"><fmt:message key="label.menu.analysis.vulnerability" /></a></li>
				<li><a href="#tabRiskInformation_Risk" data-toggle="tab"><fmt:message key="label.menu.analysis.risk" /></a></li>
				<li class="divider"></li>
			</c:if>
			<li class="dropdown-header"><fmt:message key="label.menu.analysis.quantitative_analysis" /></li>
			<c:if test="${!analysis.isProfile()}">
				<li><a href="#tabAsset" data-toggle="tab"><fmt:message key="label.menu.analysis.asset" /></a></li>
			</c:if>
			<li><a href="#tabScenario" data-toggle="tab"><fmt:message key="label.menu.analysis.scenario" /></a></li>
		</ul></li>

	<li class="dropdown-submenu" id="tabStandard"><a href="#" class="dropdown-toggle" data-toggle="dropdown"><fmt:message key="label.menu.analysis.risk_treatment_compliance" /><span
			class="caret"></span></a>
		<ul class="dropdown-menu" id="standardmenu">
			<c:if test="${!empty(standards)}">
				<li class="dropdown-header"><fmt:message key="label.menu.analysis.standards" /></li>
				<c:forEach items="${standards}" var="standard">
					<li><a href="#tabStandard_${standard.id}" data-toggle="tab"> <spring:message text="${standard.label}" /></a>
				</c:forEach>
				<li class="divider"></li>
			</c:if>
			<li class="dropdown-header"><fmt:message key="label.menu.analysis.implementation" /></li>
			<li><a href="#tabPhase" data-toggle="tab"> <fmt:message key="label.menu.analysis.phase" /></a></li>
			<c:if test="${!analysis.isProfile()}">
				<li><a href="#tabActionPlan" data-toggle="tab"> <fmt:message key="label.menu.analysis.action_plan" /></a></li>
			</c:if>
			<c:if test="${analysis.isProfile() || isEditable}">
				<li class="divider"></li>
				<li class="dropdown-header"><fmt:message key="label.menu.advanced" /></li>
				<li><a href="#" onclick="return manageStandard();"> <fmt:message key="label.menu.manage_standard" /></a></li>
			</c:if>
		</ul></li>
	<c:if test="${!analysis.isProfile()}">
		<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"> <fmt:message key="label.menu.analysis.risk_communication" /><span class="caret"></span></a>
			<ul class="dropdown-menu">
				<c:if test="${!empty(soa)}">
					<li><a href="#tabSOA" data-toggle="tab"> <fmt:message key="label.menu.analysis.soa" /></a></li>
				</c:if>
				<li><a href="#tabSummary" data-toggle="tab"> <fmt:message key="label.menu.analysis.summary" /></a></li>
				<li><a href="#tabRiskRegister" data-toggle="tab"> <fmt:message key="label.menu.analysis.risk_register" /></a></li>
				<li class="divider"></li>
				<li class="dropdown-header"><fmt:message key="label.menu.analysis.chart" /></li>
				<li><a href="#tabChartAsset" data-toggle="tab"> <fmt:message key="label.chart.asset" /></a></li>
				<li><a href="#tabChartScenario" data-toggle="tab"> <fmt:message key="label.chart.scenario" /></a></li>
				<li><a href="#tabChartCompliance" data-toggle="tab"> <fmt:message key="label.chart.compliance" /></a></li>
				<li><a href="#tabChartEvolution" data-toggle="tab"> <fmt:message key="label.chart.evolution" /></a></li>
				<li><a href="#tabChartBudget" data-toggle="tab"> <fmt:message key="label.chart.budget" /></a></li>
				<li class="divider"></li>
				<li class="dropdown-header"><fmt:message key="label.menu.advanced" /></li>
				<li><a href="#" onclick="return reloadCharts();"> <fmt:message key="label.action.reload.charts" /></a></li>
			</ul></li>
	</c:if>
	<li class="pull-right"><a href="${pageContext.request.contextPath}/Analysis/Deselect" class="text-muted" title='<fmt:message key="label.action.close.analysis" />'
		style="padding-bottom: 5px; padding-top: 5px"><i class="fa fa-sign-out fa-2x"></i></a></li>
	<li class="dropdown-submenu pull-right"><a href="#" class="dropdown-toggle text-muted" data-toggle="dropdown" title='<fmt:message key="label.actions" />'
		style="padding-bottom: 5px; padding-top: 5px"><i class="fa fa-cog fa-2x"></i></a>
		<ul class="dropdown-menu" id="actionmenu">
			<c:if test="${!analysis.isProfile() && analysis.getRightsforUserString(login).right.ordinal()<2 && isEditable}">
				<li class="dropdown-header"><fmt:message key="label.action.export" /></li>
				<li><a href="#" onclick="return exportAnalysisReport('${analysis.id}')"> <fmt:message key="label.word_report" />
				</a></li>
				<li><a href="#" onclick="return exportAnalysis('${analysis.id}');"> <fmt:message key="label.sqlite_data" /></a></li>
				<li class="divider"></li>
			</c:if>
			<c:if test="${analysis.isProfile() || isEditable}">
				<li class="dropdown-header"><fmt:message key="label.title.edit_mode" /></li>
				<li role="enterEditMode"><a href="#" onclick="return enableEditMode()"><fmt:message key="label.action.edit_mode.open" /></a></li>
				<li class="disabled" onclick="return disableEditMode()" role="leaveEditMode"><a href="#"><fmt:message key="label.action.edit_mode.close" /></a></li>
				<li class="divider"></li>
			</c:if>
			<li class="dropdown-header"><fmt:message key="label.title.rrf" /></li>
			<li><a href="#" onclick="return loadRRF();"> <fmt:message key="label.action.open" /></a></li>
			<c:if test="${analysis.isProfile() || isEditable}">
				<li><a href="#" onclick="return importRRF(${sessionScope.selectedAnalysis});"> <fmt:message key="label.action.import" /></a></li>
			</c:if>
			<c:if test="${!analysis.isProfile()}">
				<li class="divider"></li>
				<li class="dropdown-header" style="color: #b94a48" title='<fmt:message key="info.deprecated.analysis.action.computation" />'><fmt:message
						key="label.title.deprecated.computation" /></li>
				<li style="color: #999999" title="<fmt:message key="info.deprecated.analysis.action.computation.action_plan" />"><a style="color: inherit;" href="#"
					onclick="return displayActionPlanOptions('${analysis.id}')"> <fmt:message key="label.menu.analysis.deprecated.action_plan" />
				</a></li>
				<li style="color: #999999" title="<fmt:message key="info.deprecated.analysis.action.computation.risk_register" />"><a style="color: inherit;" href="#"
					onclick="return calculateRiskRegister();"> <fmt:message key="label.menu.analysis.deprecated.risk_register" /></a></li>
				<c:if test="${isEditable}">
					<li class="divider"></li>
					<li class="dropdown-header"><fmt:message key="label.title.assessment" /></li>
					<li><a href="#" onclick="return computeAssessment();"> <fmt:message key="label.action.generate.missing" /></a></li>
					<li><a href="#" onclick="return refreshAssessment();"><fmt:message key="label.action.refresh.assessment" /></a></li>
				</c:if>
			</c:if>
		</ul></li>
	<li id="tabOption" style="display: none;" class="dropdown-submenu pull-right"><a href="#" title='<fmt:message key="label.options" />' class="dropdown-toggle"
		data-toggle="dropdown" style="padding-bottom: 5px; padding-top: 5px"><span class="fa fa-bars fa-2x"></span></a></li>
</ul>