<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:set var="url">
	<%=request.getAttribute("javax.servlet.forward.request_uri")%>
</c:set>
<c:set var="menu">
	${fn:substringAfter(fn:substringAfter(url,pageContext.request.contextPath),"/")}
</c:set>
<div class="navbar navbar-default navbar-fixed-top navbar-custom affix" style="z-index: 1029">
	<div id="analysismenu" class="container">
		<ul class="nav navbar-nav">
			<c:if test="${!KowledgeBaseView}">
				<li><a href="#anchorHistory"><spring:message code="label.menu.analysis.history" text="History" /></a></li>
				<li><a href="#anchorScope"><spring:message code="label.menu.analysis.item_information" text="Scope" /></a></li>
			</c:if>
			<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="label.menu.analysis.parmeter" text="Parameters" /><span
					class="caret"></span></a>
				<ul class="dropdown-menu">
					<li><a href="#anchorParameter_Impact"><spring:message code="label.menu.analysis.parameter.impact" text="Impact" /></a></li>
					<li><a href="#anchorParameter_Probability"><spring:message code="label.menu.analysis.parameter.probability" text="Probability" /></a></li>
					<li><a href="#anchorParameter_ILPS"><spring:message code="label.menu.analysis.parameter.ilps" text="Required level of implmentation per SML" /></a></li>
					<li><a href="#anchorParameter_Various"><spring:message code="label.menu.analysis.parameter.various" text="Various" /></a></li>
					<li><a href="#anchorParameter_MaxEfficiency"><spring:message code="label.menu.analysis.parameter.maxeff" text="Maximal efficiency rate per security maturity level" /></a></li>
					<li><a href="#anchorParameter_ImplementationRate"><spring:message code="label.menu.analysis.parameter.implmentation_rate" text="Implementation scale of SMT" /></a></li>
				</ul></li>
			<c:if test="${!KowledgeBaseView }">
				<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="label.menu.analysis.risk_information"
							text="Risk Information" /><span class="caret"></span></a>
					<ul class="dropdown-menu">
						<li><a href="#anchorRiskInformation_Threat"><spring:message code="label.menu.analysis.threat" text="Threats" /></a></li>
						<li><a href="#anchorRiskInformation_Vul"><spring:message code="label.menu.analysis.vulnerability" text="Vulnerabilities" /></a></li>
						<li><a href="#anchorRiskInformation_Risk"><spring:message code="label.menu.analysis.risk" text="Risks" /></a></li>
					</ul></li>

				<li><a href="#anchorAsset"><spring:message code="label.menu.analysis.asset" text="Assets" /></a></li>
			</c:if>
			<li><a href="#anchorScenario"><spring:message code="label.menu.analysis.scenario" text="Scenarios" /></a></li>
			<li><a href="#anchorPhase"> <spring:message code="label.menu.analysis.phase" text="Phases" /></a></li>
			<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"> <spring:message code="label.menu.analysis.standards" text="Standard" /><span
					class="caret"></span></a> <c:if test="${empty(measureSplited)}">
					<spring:eval expression="T(lu.itrust.business.component.MeasureManager).SplitByNorm(measures)" var="measureSplited" scope="request" />
				</c:if>
				<ul class="dropdown-menu">
					<c:if test="${!empty(measureSplited)}">
						<c:forEach items="${measureSplited.keySet()}" var="norm">
							<li><a href="#anchorMeasure_${norm}"> <spring:message text="${norm}" /></a>
						</c:forEach>
					</c:if>
					<c:if test="${analysis.getRightsforUserString(login).right.ordinal() <= 4}">
						<c:if test="${!measureSplited.isEmpty()}">
							<li class="divider"></li>
						</c:if>
						<li><a href="#" onclick="return manageStandard();"> <spring:message code="label.menu.manage.standard" text="Manage standard" /></a></li>
					</c:if>
				</ul></li>
			<c:if test="${!KowledgeBaseView }">
				<li><a href="#anchorSOA"> <spring:message code="label.menu.analysis.soa" text="SOA" /></a></li>
				<li><a href="#anchorActionPlan"> <spring:message code="label.menu.analysis.action_plan" text="Action Plan" /></a></li>
				<li><a href="#anchorSummary"> <spring:message code="label.menu.analysis.summary" text="Action plan summary" /></a></li>
				<c:if test="${empty(show_cssf) or show_cssf}">
					<li><a href="#anchorRiskRegister"> <spring:message code="label.menu.analysis.risk_register" text="Risk register" /></a></li>
				</c:if>
				<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"> <spring:message code="label.menu.analysis.chart" text="Charts" /><span
						class="caret"></span></a>
					<ul class="dropdown-menu">
						<li><a href="#anchorChartAsset"> <spring:message code="label.chart.asset" text="ALE by asset/asset type" /></a></li>
						<li><a href="#anchorChartScenario"> <spring:message code="label.chart.scenario" text="ALE by scenario/scenario Type" /></a></li>
						<li><a href="#anchorChartCompliance"> <spring:message code="label.chart.compliance" text="Compliance by standard" /></a></li>
						<li><a href="#anchorChartEvolution"> <spring:message code="label.chart.evolution" text="Evolution of profitability by action plan" /></a></li>
						<li><a href="#anchorChartBudget"> <spring:message code="label.chart.budget" text="Budget by action plan" /></a></li>
					</ul></li>
			</c:if>
		</ul>
		<ul class="nav navbar-nav navbar-right">
			<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"> <spring:message code="label.actions" text="Actions" /><span class="caret"></span></a>
				<ul class="dropdown-menu">
					<li class="dropdown-header"><spring:message code="label.analysis" text="Analysis" /></li>
					<li><a href="${pageContext.request.contextPath}/Analysis/Deselect"> <spring:message code="label.action.close.analysis" text="Close analysis" /></a></li>
					<li class="divider"></li>
					<li class="dropdown-header"><spring:message code="label.title.rrf" text="RRF" /></li>
					<li><a href="#" onclick="return editRRF(${sessionScope.selectedAnalysis});"> <spring:message code="label.action.open" text="Open" /></a></li>
					<li><a href="#" onclick="return importRRF(${sessionScope.selectedAnalysis});"> <spring:message code="label.action.import.rrf" text="Import" /></a></li>
					<c:if test="${!KowledgeBaseView }">
						<li class="divider"></li>
						<li class="dropdown-header"><spring:message code="label.title.computation" text="Computation" /></li>
						<li><a href="#" onclick="return displayActionPlanOptions('${analysis.id}')"> <spring:message code="label.action.compute.action_plan" text="Action plan" />
						</a></li>
						<c:if test="${empty(show_cssf) or show_cssf}">
							<li><a href="#" onclick="return calculateRiskRegister('${analysis.id}');"> <spring:message code="label.action.compute.risk_register" text="Registers" /></a></li>
						</c:if>
						<li class="divider"></li>
						<li><a href="#" onclick="return reloadCharts();"> <spring:message code="label.action.reload.charts" text="Reload charts" /></a></li>
						<li class="divider"></li>
						<li class="dropdown-header"><spring:message code="label.title.assessment" text="Assessment" /></li>
						<li><a href="#" onclick="return computeAssessment();"> <spring:message code="label.action.generate.missing" text="Update" /></a></li>
						<li><a href="#" onclick="return refreshAssessment();"><spring:message code="label.action.refresh.assessment" text="Refresh" /></a></li>
						<li class="divider"></li>
					</c:if>
					<li class="dropdown-header"><spring:message code="label.settings" text="Settings" /></li>
					<c:if test="${!KowledgeBaseView }">
						<li><a href="#" onclick="return updateSettings(this.firstElementChild,'analysis','${analysis.id}','show_cssf');" style="padding: 6px;"><span
								class="glyphicon ${empty(show_cssf) or show_cssf? 'glyphicon-ok' : ''}" style="min-width: 12px;"></span><span>&nbsp;<spring:message code="label.settings.show_cssf"
										text="Display CSSF" /></span></a></li>
					</c:if>
					<li><a href="#" onclick="return updateSettings(this.firstElementChild,'analysis','${analysis.id}','show_uncertainty');" style="padding: 6px;"><span
							class="glyphicon ${empty(show_uncertainty) or show_uncertainty? 'glyphicon-ok':''}" style="min-width: 12px;" trick-section-dependency="section_asset,section_scenario">
						</span><span>&nbsp;<spring:message code="label.settings.show_uncertainty" text="Display Uncertainty" /></span></a></li>

				</ul></li>
		</ul>
	</div>
</div>