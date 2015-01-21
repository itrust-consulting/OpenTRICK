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
<ul class="nav nav-tabs affix affix-top nav-analysis col-xs-12">
	<c:if test="${!analysis.isProfile()}">
		<li class="active"><a href="#tabHistory" data-toggle="tab"><fmt:message key="label.menu.analysis.history" /></a></li>
		<li><a href="#tabScope" data-toggle="tab"><fmt:message key="label.menu.analysis.item_information" /></a></li>
	</c:if>
	<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"><fmt:message key="label.menu.analysis.parmeter" /><span class="caret"></span></a>
		<ul class="dropdown-menu">
			<li><a href="#tabParameterImpactProba" data-toggle="tab"><fmt:message key="label.menu.analysis.parameter.impact_probability" /></a></li>
			<li><a href="#tabParameterOther" data-toggle="tab"><fmt:message key="label.menu.analysis.parameter.various" /></a></li>
		</ul></li>
	<c:if test="${!analysis.isProfile()}">
		<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"><fmt:message key="label.menu.analysis.risk_information" /><span class="caret"></span></a>
			<ul class="dropdown-menu">
				<li><a href="#tabRiskInformation_Threat" data-toggle="tab"><fmt:message key="label.menu.analysis.threat" /></a></li>
				<li><a href="#tabRiskInformation_Vul" data-toggle="tab"><fmt:message key="label.menu.analysis.vulnerability" /></a></li>
				<li><a href="#tabRiskInformation_Risk" data-toggle="tab"><fmt:message key="label.menu.analysis.risk" /></a></li>
			</ul></li>
		<li><a href="#tabAsset" data-toggle="tab"><fmt:message key="label.menu.analysis.asset" /></a></li>
	</c:if>
	<li><a href="#tabScenario" data-toggle="tab"><fmt:message key="label.menu.analysis.scenario" /></a></li>
	<li><a href="#tabPhase" data-toggle="tab"> <fmt:message key="label.menu.analysis.phase" /></a></li>
	<li class="dropdown-submenu" id="tabStandard"><a href="#" class="dropdown-toggle" data-toggle="dropdown"><fmt:message key="label.menu.analysis.standards" /><span
			class="caret"></span></a>
		<ul class="dropdown-menu" id="standardmenu">
			<c:if test="${!empty(standards)}">
				<c:forEach items="${standards}" var="standard">
					<li><a href="#tabStandard_${standard.id}" data-toggle="tab"> <spring:message text="${standard.label}" /></a>
				</c:forEach>
			</c:if>
			<c:if test="${analysis.isProfile() || isEditable}">
				<c:if test="${!measureSplited.isEmpty()}">
					<li class="divider"></li>
				</c:if>
				<li><a href="#" onclick="return manageStandard();"> <fmt:message key="label.menu.manage_standard" /></a></li>
			</c:if>
		</ul></li>
	<c:if test="${!analysis.isProfile()}">
		<c:if test="${!empty(soa)}">
			<li><a href="#tabSOA" data-toggle="tab"> <fmt:message key="label.menu.analysis.soa" /></a></li>
		</c:if>
		<li><a href="#tabActionPlan" data-toggle="tab"> <fmt:message key="label.menu.analysis.action_plan" /></a></li>
		<li><a href="#tabSummary" data-toggle="tab"> <fmt:message key="label.menu.analysis.summary" /></a></li>
		<c:if test="${show_cssf}">
			<li><a href="#tabRiskRegister" data-toggle="tab"> <fmt:message key="label.menu.analysis.risk_register" /></a></li>
		</c:if>
		<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"> <fmt:message key="label.menu.analysis.chart" /><span class="caret"></span></a>
			<ul class="dropdown-menu">
				<li><a href="#tabChartAsset" data-toggle="tab"> <fmt:message key="label.chart.asset" /></a></li>
				<li><a href="#tabChartScenario" data-toggle="tab"> <fmt:message key="label.chart.scenario" /></a></li>
				<li><a href="#tabChartCompliance" data-toggle="tab"> <fmt:message key="label.chart.compliance" /></a></li>
				<li><a href="#tabChartEvolution" data-toggle="tab"> <fmt:message key="label.chart.evolution" /></a></li>
				<li><a href="#tabChartBudget" data-toggle="tab"> <fmt:message key="label.chart.budget" /></a></li>
			</ul></li>
	</c:if>
	<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"> <fmt:message key="label.actions" /><span class="caret"></span></a>
		<ul class="dropdown-menu" id="actionmenu">
			<li class="dropdown-header"><fmt:message key="label.analysis" /></li>
			<li><a href="${pageContext.request.contextPath}/Analysis/Deselect"> <fmt:message key="label.action.close.analysis" /></a></li>
			<li class="divider"></li>
			<li class="dropdown-header"><fmt:message key="label.title.rrf" /></li>
			<li><a href="#" onclick="return loadRRF();"> <fmt:message key="label.action.open" /></a></li>
			<c:if test="${analysis.isProfile() || isEditable }">
				<li><a href="#" onclick="return importRRF(${sessionScope.selectedAnalysis});"> <fmt:message key="label.action.import.rrf" /></a></li>
			</c:if>
			<c:if test="${!analysis.isProfile() && isEditable }">
				<li class="divider"></li>
				<li class="dropdown-header"><fmt:message key="label.title.computation" /></li>
				<li><a href="#" onclick="return displayActionPlanOptions('${analysis.id}')"> <fmt:message key="label.action.compute.action_plan" />
				</a></li>
				<c:if test="${show_cssf}">
					<li><a href="#" onclick="return calculateRiskRegister('${analysis.id}');"> <fmt:message key="label.action.compute.risk_register" /></a></li>
				</c:if>
				<li class="divider"></li>
				<li><a href="#" onclick="return reloadCharts();"> <fmt:message key="label.action.reload.charts" /></a></li>
				<li class="divider"></li>
				<li class="dropdown-header"><fmt:message key="label.title.assessment" /></li>
				<li><a href="#" onclick="return computeAssessment();"> <fmt:message key="label.action.generate.missing" /></a></li>
				<li><a href="#" onclick="return refreshAssessment();"><fmt:message key="label.action.refresh.assessment" /></a></li>
			</c:if>
		</ul></li>
</ul>