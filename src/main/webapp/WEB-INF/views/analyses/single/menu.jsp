<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:set var="canExport" value="${analysis.getRightsforUserString(login).right.ordinal()<2}" />
<ul class="nav nav-tabs affix affix-top nav-analysis col-xs-12">
	<c:if test="${!isProfile}">
		<li class="active"><a href="#tabHistory" data-toggle="tab"><spring:message code="label.menu.analysis.history" /></a></li>
	</c:if>
	<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="label.menu.analysis.risk_context" /><span class="caret"></span></a>
		<ul class="dropdown-menu">
			<c:if test="${!isProfile}">
				<li><a href="#tabScope" data-toggle="tab"><spring:message code="label.menu.analysis.item_information" /></a></li>
				<li class="divider"></li>
			</c:if>
			<li class="dropdown-header"><spring:message code="label.menu.analysis.parmeter" /></li>
			<c:choose>
				<c:when test="${type=='QUALITATIVE' }">
					<li><a href="#tabParameterImpact" data-toggle="tab"><spring:message code="label.menu.analysis.parameter.impact" /></a></li>
					<li><a href="#tabParameterProbability" data-toggle="tab"><spring:message code="label.menu.analysis.parameter.probability" /></a></li>
				</c:when>
				<c:otherwise>
					<li><a href="#tabParameterImpactProba" data-toggle="tab"><spring:message code="label.menu.analysis.parameter.impact_probability" /></a></li>
				</c:otherwise>
			</c:choose>
			
			<li><a href="#tabParameterOther" data-toggle="tab"><spring:message code="label.menu.analysis.parameter.various" /></a></li>
		</ul></li>
	<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="label.menu.analysis.risk_analysis" /> <span class="caret"></span></a>
		<ul class="dropdown-menu">
			<c:if test="${!isProfile}">
				<li class="dropdown-header"><spring:message code="label.menu.analysis.qualitative_analysis" /></li>
				<li><a href="#tabRiskInformation_Threat" data-toggle="tab"><spring:message code="label.menu.analysis.threat" /></a></li>
				<li><a href="#tabRiskInformation_Vul" data-toggle="tab"><spring:message code="label.menu.analysis.vulnerability" /></a></li>
				<li><a href="#tabRiskInformation_Risk" data-toggle="tab"><spring:message code="label.menu.analysis.risk" /></a></li>
				<li class="divider"></li>
			</c:if>
			<li class="dropdown-header"><spring:message code="label.menu.analysis.quantitative_analysis" /></li>
			<c:if test="${!isProfile}">
				<li><a href="#tabAsset" data-toggle="tab"><spring:message code="label.menu.analysis.asset" /></a></li>
			</c:if>
			<li><a href="#tabScenario" data-toggle="tab"><spring:message code="label.menu.analysis.scenario" /></a></li>
			<c:if test="${!isProfile}">
				<li class="divider"></li>
				<li class="dropdown-header"><spring:message code="label.action.assessment.by" /></li>
				<li><a href="?open=${open.readOnly?'read-only' : 'edit'}-estimation"><spring:message code="label.action.risk_sheet" /></a></li>
				<li hidden="hidden" data-menu='estimation' data-type='asset'><a href="#tabEstimationAsset" data-toggle="tab"><spring:message code="label.action.table_view" /></a></li>
				<li hidden="hidden" data-menu='estimation' data-type='scenario'><a href="#tabEstimationScenario" data-toggle="tab"><spring:message code="label.action.table_view" /></a></li>
			</c:if>
		</ul></li>

	<li class="dropdown-submenu" id="tabStandard"><a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message
				code="label.menu.analysis.risk_treatment_compliance" /><span class="caret"></span></a>
		<ul class="dropdown-menu" id="standardmenu">
			<li class="dropdown-header"><spring:message code="label.menu.analysis.standards" /></li>
			<c:if test="${!empty(standards)}">
				<c:forEach items="${standards}" var="standard">
					<li><a href="#tabStandard_${standard.id}" data-toggle="tab"> <spring:message text="${standard.label}" /></a>
				</c:forEach>
				<li class="divider"></li>
			</c:if>
			<c:if test="${isProfile || isEditable}">
				<c:if test="${!empty(standards)}">
					<li title='<spring:message code="label.menu.view.measures"/>'><a href="?open=edit-measure"><i class='glyphicon glyphicon-edit'></i> <spring:message
								code="label.action.edit" /></a></li>
				</c:if>
				<li title='<spring:message code="label.menu.manage_standard"/>'><a href="#" onclick="return manageStandard();"><i class='glyphicon glyphicon-cog'></i> <spring:message
							code="label.action.manage" /></a></li>
				<li class="divider"></li>
			</c:if>
			<li class="dropdown-header"><spring:message code="label.menu.analysis.implementation" /></li>
			<li><a href="#tabPhase" data-toggle="tab"> <spring:message code="label.menu.analysis.phase" /></a></li>
			<c:if test="${!isProfile}">
				<li><a href="#tabActionPlan" data-toggle="tab"> <spring:message code="label.menu.analysis.action_plan" /></a></li>
			</c:if>
		</ul></li>
	<c:if test="${!isProfile}">
		<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"> <spring:message code="label.menu.analysis.risk_communication" /><span
				class="caret"></span></a>
			<ul class="dropdown-menu">
				<c:if test="${!empty(soa)}">
					<li><a href="#tabSOA" data-toggle="tab"> <spring:message code="label.menu.analysis.soa" /></a></li>
				</c:if>
				<li><a href="#tabSummary" data-toggle="tab"> <spring:message code="label.menu.analysis.summary" /></a></li>
				<c:if test="${type=='QUALITATIVE'}">
					<li><a href="#tabRiskRegister" data-toggle="tab"> <spring:message code="label.menu.analysis.risk_register" /></a></li>
				</c:if>
				<li class="divider"></li>
				<li class="dropdown-header"><spring:message code="label.menu.analysis.chart" /></li>
				<li><a href="#tabChartAsset" data-toggle="tab"> <spring:message code="label.chart.asset" /></a></li>
				<li><a href="#tabChartScenario" data-toggle="tab"> <spring:message code="label.chart.scenario" /></a></li>
				<li><a href="#tabChartCompliance" data-toggle="tab"> <spring:message code="label.chart.compliance" /></a></li>
				<li><a href="#tabChartEvolution" data-toggle="tab"> <spring:message code="label.chart.evolution" /></a></li>
				<li><a href="#tabChartBudget" data-toggle="tab"> <spring:message code="label.chart.budget" /></a></li>
				<li><a href="#tabChartParameterEvolution" data-toggle="tab"> <spring:message code="label.title.chart.dynamic" /></a></li>
				<li><a href="#tabChartAleEvolutionByAssetType" data-toggle="tab"> <spring:message code="label.title.chart.aleevolution" /></a></li>
				<li><a href="#tabChartAleEvolutionByScenario" data-toggle="tab"> <spring:message code="label.title.chart.aleevolution_by_asset_type" /></a></li>
				<li class="divider"></li>
				<li class="dropdown-header"><spring:message code="label.menu.advanced" /></li>
				<li><a href="#" onclick="return reloadCharts();"> <spring:message code="label.action.reload.charts" /></a></li>
			</ul></li>
	</c:if>
	<li class="pull-right"><a id='nav_menu_analysis_close' href="${pageContext.request.contextPath}/Analysis/Deselect" class="text-muted"
		title='<spring:message code="label.action.close.analysis" />' style="padding-bottom: 5px; padding-top: 5px"><i class="fa fa-sign-out fa-2x"></i></a></li>
	<li class="dropdown-submenu pull-right"><a href="#" class="dropdown-toggle text-muted" data-toggle="dropdown" title='<spring:message code="label.actions" />'
		style="padding-bottom: 5px; padding-top: 5px"><i class="fa fa-cog fa-2x"></i></a>
		<ul class="dropdown-menu" id="actionmenu">
			<c:if test="${not isProfile}">
				<li class="dropdown-header"><spring:message code="label.title.computation" /></li>
				<li><a href="#" onclick="return displayActionPlanOptions('${analysis.id}')"> <spring:message code="label.menu.analysis.action_plan" />
				</a></li>
				<c:choose>
					<c:when test="${type=='QUALITATIVE'}">
						<li><a href="#" onclick="return calculateRiskRegister();"> <spring:message code="label.menu.analysis.risk_register" /></a></li>
					</c:when>
					<c:when test="${not isEditable}">
						<li class="divider"></li>
					</c:when>
				</c:choose>
				<c:if test="${canExport and isEditable}">
					<li class="divider"></li>
					<li class="dropdown-header"><spring:message code="label.action.export" /></li>
					<li><a href="#" onclick="return exportAnalysisReport('${analysis.id}')"> <spring:message code="label.word_report" /></a></li>
					<c:if test="${type=='QUALITATIVE'}">
						<li><a href="#" onclick="return exportRiskRegister('${analysis.id}')"> <spring:message code="label.risk_register" />
						</a></li>
						<li><a href="#" onclick="return exportRiskSheet('${analysis.id}','REPORT')"> <spring:message code="label.risk_sheet" />
						</a></li>
						<li><a href="#" onclick="return exportRiskSheet('${analysis.id}','RAW')"> <spring:message code="label.raw_risk_sheet" />
						</a></li>
					</c:if>
					<li><a href="${pageContext.request.contextPath}/Analysis/Export/Raw-Action-plan/${analysis.id}" download><spring:message code="label.raw_action_plan" /></a></li>
					<li class="divider"></li>
				</c:if>
			</c:if>
			<c:if test="${isProfile or isEditable}">
				<li class="dropdown-header"><spring:message code="label.title.edit_mode" /></li>
				<li role="enterEditMode"><a href="#" onclick="return enableEditMode()"><spring:message code="label.action.edit_mode.open" /></a></li>
				<li class="disabled" onclick="return disableEditMode()" role="leaveEditMode"><a href="#"><spring:message code="label.action.edit_mode.close" /></a></li>
				<c:if test="${not(isProfile and type=='QUALITATIVE')}">
					<li class="divider"></li>
				</c:if>
			</c:if>
			<c:if test="${type=='QUANTITATIVE'}">
				<li class="dropdown-header"><spring:message code="label.title.rrf" /></li>
				<li><a href="#" onclick="return loadRRF();"> <spring:message code="label.action.open" /></a></li>
				<c:if test="${isProfile or isEditable}">
					<li><a href="#" onclick="return importRRF(${analysis.id});"> <spring:message code="label.action.import" /></a></li>
					<li><a href="#" onclick="return importRawRRFForm(${analysis.id});"> <spring:message code="label.action.import.rrf.raw" /></a></li>
					<c:set var="importRRF" value="true" />
				</c:if>
				<c:if test="${canExport and isEditable}">
					<li><a href="${pageContext.request.contextPath}/Analysis/RRF/Export/Raw/${analysis.id}" download> <spring:message code="label.action.export.rrf.raw" /></a></li>
					<c:set var="exportRRF" value="true" />
				</c:if>
				<c:if test="${not isProfile and isEditable}">
					<li class="divider"></li>
				</c:if>
			</c:if>
			<c:if test="${not isProfile and isEditable}">
				<li class="dropdown-header"><spring:message code="label.title.assessment" /></li>
				<li><a href="#" onclick="return computeAssessment();"> <spring:message code="label.action.generate.missing" /></a></li>
				<li><a href="#" onclick="return refreshAssessment();"><spring:message code="label.action.refresh.assessment" /></a></li>
			</c:if>
		</ul></li>
	<li id="tabOption" style="display: none;" class="dropdown-submenu pull-right"><a href="#" title='<spring:message code="label.options" />' class="dropdown-toggle"
		data-toggle="dropdown" style="padding-bottom: 5px; padding-top: 5px"><span class="fa fa-bars fa-2x"></span></a></li>
</ul>