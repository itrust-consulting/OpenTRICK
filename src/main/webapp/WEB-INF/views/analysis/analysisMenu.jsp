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
<div class="navbar navbar-default navbar-fixed-top navbar-custom affix">
	<div id="analysismenu" class="container">
		<ul class="nav navbar-nav">
			<c:if test="${!KowledgeBaseView}">
				<li><a href="#anchorHistory"><spring:message code="menu.analysis.history" text="History" /></a></li>
			</c:if>
			<li><a href="#anchorScope"><spring:message code="menu.analysis.iteminformation" text="Scope" /></a></li>
			<li><a href="#anchorParameter"><spring:message code="menu.analysis.parameter" text="Parameters" /></a></li>
			<li><a href="#anchorAsset"><spring:message code="menu.analysis.asset" text="Assets" /></a></li>
			<li><a href="#anchorScenario"><spring:message code="menu.analysis.scenario" text="Scenarios" /></a></li>
			<!--
			<li><a href="#RiskInformation"><spring:message code="menu.analysis.riskinformation" text="Risk Information" /></a></li>
			<ul class="nav">
				<li><a href="#Threats"><spring:message code="menu.analysis.threat" text="Threats" /></a></li>
				<li><a href="#Risks"><spring:message code="menu.analysis.risk" text="Risks" /></a></li>
				<li><a href="#Vulnerabilities"><spring:message code="menu.analysis.vulnerability" text="Vulnerability" /></a></li>
			</ul>
			-->
			<li><a href="#anchorPhase"> <spring:message code="menu.analysis.phase" text="Phases" /></a></li>
			<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"> <spring:message code="menu.analysis.standards" text="Standard" /><span
					class="caret"></span></a> <c:if test="${empty(measureSplited)}">
					<spring:eval expression="T(lu.itrust.business.component.MeasureManager).SplitByNorm(measures)" var="measureSplited" scope="request" />
				</c:if> <c:if test="${!empty(measureSplited)}">
					<ul class="dropdown-menu">
						<c:forEach items="${measureSplited.keySet()}" var="norm">
							<li><a href="#anchorMeasure_${norm}"> <spring:message code="menu.measure.${norm}" text="${norm}" /></a>
						</c:forEach>
					</ul>
				</c:if></li>
			<c:if test="${!KowledgeBaseView }">
				<li><a href="#anchorActionPlan"> <spring:message code="menu.analysis.actionplan" text="Action Plans" /></a></li>
				<li><a href="#anchorSummary"> <spring:message code="menu.analysis.Summary" text="Action Plan Summaries" /></a></li>
				<li><a href="#anchorRiskRegister"> <spring:message code="menu.analysis.riskregister" text="Risk Register" /></a></li>
			</c:if>
			<c:if test="${!KowledgeBaseView }">
				<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"> <spring:message code="menu.analysis.chart" text="Charts" /><span
						class="caret"></span></a>
					<ul class="dropdown-menu">
						<li><a href="#anchorChartAsset"> <spring:message code="label.chart.asset" text="ALE by Asset/Asset Type" /></a></li>
						<li><a href="#anchorChartScenario"> <spring:message code="label.chart.scenario" text="ALE by Scenario/Scenario Type" /></a></li>
						<li><a href="#anchorChartCompliance"> <spring:message code="label.chart.compliance" text="Compliance by Standard" /></a></li>
						<li><a href="#anchorChartEvolution"> <spring:message code="label.chart.evolution" text="Evolution of Profitability by Action Plan" /></a></li>
						<li><a href="#anchorChartBudget"> <spring:message code="label.chart.bufget" text="Budget by Action Plan" /></a></li>
					</ul></li>
			</c:if>
			<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"> <spring:message code="label.action" text="Action" /><span class="caret"></span></a>
				<ul class="dropdown-menu">
					<li><a href="${pageContext.request.contextPath}/Analysis/${sessionScope.selectedAnalysis}/Select"> <spring:message code="label.analysis.release" text="Close Analysis" /></a></li>
					<li class="divider"></li>
					<li><a href="#" onclick="return editRRF(${sessionScope.selectedAnalysis});"> <spring:message code="label.edit.rrf" text="Edit RRF" /></a></li>
					<c:if test="${!KowledgeBaseView }">
						<li class="divider"></li>
						<li><a href="#" onclick="return computeAssessment();"> <spring:message code="label.assessment.generate.missing" text="Update Assessment" /></a></li>
						<li class="divider"></li>
						<li><a href="#" onclick="return displayActionPlanOptions('${analysis.id}')"> <spring:message code="label.analysis.compute.actionPlan" text="Compute ActionPlan" />
						</a></li>
						<li><a href="#" onclick="return calculateRiskRegister('${analysis.id}');"> <spring:message code="label.analysis.compute.riskRegister" text="Compute Registers" /></a></li>
						<li class="divider"></li>
						<li><a href="#" onclick="return wipeAssessment();"> <spring:message code="label.analysis.assessment.wipe" text="Wipe assessments" /></a></li>
						<li class="divider"></li>
						<li><a href="#" onclick="return reloadCharts();"> <spring:message code="label.analysis.charts.reload" text="Reload Charts" /></a></li>
					</c:if>
				</ul></li>

		</ul>
	</div>
</div>