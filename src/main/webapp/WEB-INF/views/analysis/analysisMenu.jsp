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
<div class="navbar navbar-default navbar-fixed-top navbar-custom affix" style="z-index: 1029; min-width: 1400px;">
	<div id="analysismenu" class="container">
		<ul class="nav navbar-nav">
			<c:if test="${!analysis.isProfile()}">
				<li><a href="#anchorHistory"><fmt:message key="label.menu.analysis.history" /></a></li>
				<li><a href="#anchorScope"><fmt:message key="label.menu.analysis.item_information"  /></a></li>
			</c:if>
			<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"><fmt:message key="label.menu.analysis.parmeter"  /><span
					class="caret"></span></a>
				<ul class="dropdown-menu">
					<li><a href="#anchorParameter_Impact"><fmt:message key="label.menu.analysis.parameter.impact"  /></a></li>
					<li><a href="#anchorParameter_Probability"><fmt:message key="label.menu.analysis.parameter.probability"  /></a></li>
					<li><a href="#anchorParameter_ILPS"><fmt:message key="label.menu.analysis.parameter.ilps"  /></a></li>
					<li><a href="#anchorParameter_Various"><fmt:message key="label.menu.analysis.parameter.various"  /></a></li>
					<li><a href="#anchorParameter_MaxEfficiency"><fmt:message key="label.menu.analysis.parameter.maxeff"  /></a></li>
					<li><a href="#anchorParameter_ImplementationRate"><fmt:message key="label.menu.analysis.parameter.implmentation_rate"  /></a></li>
				</ul></li>
			<c:if test="${!analysis.isProfile() }">
				<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"><fmt:message key="label.menu.analysis.risk_information" /><span class="caret"></span></a>
					<ul class="dropdown-menu">
						<li><a href="#anchorRiskInformation_Threat"><fmt:message key="label.menu.analysis.threat"  /></a></li>
						<li><a href="#anchorRiskInformation_Vul"><fmt:message key="label.menu.analysis.vulnerability"  /></a></li>
						<li><a href="#anchorRiskInformation_Risk"><fmt:message key="label.menu.analysis.risk"  /></a></li>
					</ul></li>
				<li><a href="#anchorAsset"><fmt:message key="label.menu.analysis.asset"  /></a></li>
			</c:if>
			<li><a href="#anchorScenario"><fmt:message key="label.menu.analysis.scenario"  /></a></li>
			<li><a href="#anchorPhase"> <fmt:message key="label.menu.analysis.phase"  /></a></li>
			<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"> <fmt:message key="label.menu.analysis.standards"  /><span
					class="caret"></span></a> <c:if test="${empty(measureSplited)}">
					<spring:eval expression="T(lu.itrust.business.component.MeasureManager).SplitByStandard(measures)" var="measureSplited" scope="request" />
				</c:if>
				<ul class="dropdown-menu" id="standardmenu">
					<c:if test="${!empty(measureSplited)}">
						<c:forEach items="${measureSplited.keySet()}" var="standard">
							<li><a href="#anchorMeasure_${standard}"> <spring:message text="${standard}" /></a>
						</c:forEach>
					</c:if>
					<c:if test="${analysis.getRightsforUserString(login).right.ordinal() <= 4}">
						<c:if test="${!measureSplited.isEmpty()}">
							<li class="divider"></li>
						</c:if>
						<li><a href="#" onclick="return manageStandard();"> <fmt:message key="label.menu.manage.standard"  /></a></li>
					</c:if>
				</ul></li>
			<c:if test="${!analysis.isProfile() }">
				<li><a href="#anchorSOA"> <fmt:message key="label.menu.analysis.soa"  /></a></li>
				<li><a href="#anchorActionPlan"> <fmt:message key="label.menu.analysis.action_plan"  /></a></li>
				<li><a href="#anchorSummary"> <fmt:message key="label.menu.analysis.summary"  /></a></li>
				<c:if test="${show_cssf}">
					<li><a href="#anchorRiskRegister"> <fmt:message key="label.menu.analysis.risk_register"  /></a></li>
				</c:if>
				<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"> <fmt:message key="label.menu.analysis.chart"  /><span
						class="caret"></span></a>
					<ul class="dropdown-menu">
						<li><a href="#anchorChartAsset"> <fmt:message key="label.chart.asset"  /></a></li>
						<li><a href="#anchorChartScenario"> <fmt:message key="label.chart.scenario"  /></a></li>
						<li><a href="#anchorChartCompliance"> <fmt:message key="label.chart.compliance"  /></a></li>
						<li><a href="#anchorChartEvolution"> <fmt:message key="label.chart.evolution"  /></a></li>
						<li><a href="#anchorChartBudget"> <fmt:message key="label.chart.budget"  /></a></li>
					</ul></li>
			</c:if>
		</ul>
		<ul class="nav navbar-nav navbar-right">
			<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"> <fmt:message key="label.actions" /><span class="caret"></span></a>
				<ul class="dropdown-menu" id="actionmenu">
					<li class="dropdown-header"><fmt:message key="label.analysis"  /></li>
					<li><a href="${pageContext.request.contextPath}/Analysis/Deselect"> <fmt:message key="label.action.close.analysis"  /></a></li>
					<li class="divider"></li>
					<li class="dropdown-header"><fmt:message key="label.title.rrf"  /></li>
					<li><a href="#" onclick="return editRRF(${sessionScope.selectedAnalysis});"> <fmt:message key="label.action.open"  /></a></li>
					<li><a href="#" onclick="return importRRF(${sessionScope.selectedAnalysis});"> <fmt:message key="label.action.import.rrf"  /></a></li>
					<c:if test="${!analysis.isProfile() }">
						<li class="divider"></li>
						<li class="dropdown-header"><fmt:message key="label.title.computation"  /></li>
						<li><a href="#" onclick="return displayActionPlanOptions('${analysis.id}')"> <fmt:message key="label.action.compute.action_plan"  />
						</a></li>
						<c:if test="${show_cssf}">
							<li><a href="#" onclick="return calculateRiskRegister('${analysis.id}');"> <fmt:message key="label.action.compute.risk_register"  /></a></li>
						</c:if>
						<li class="divider"></li>
						<li><a href="#" onclick="return reloadCharts();"> <fmt:message key="label.action.reload.charts"  /></a></li>
						<li class="divider"></li>
						<li class="dropdown-header"><fmt:message key="label.title.assessment"  /></li>
						<li><a href="#" onclick="return computeAssessment();"> <fmt:message key="label.action.generate.missing"  /></a></li>
						<li><a href="#" onclick="return refreshAssessment();"><fmt:message key="label.action.refresh.assessment"  /></a></li>
					</c:if>
				</ul></li>
		</ul>
	</div>
</div>