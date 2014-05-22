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
<div class="navbar navbar-default navbar-fixed-top navbar-custom affix" style="z-index:1029">
	<div id="analysismenu" class="container">
		<ul class="nav navbar-nav">
			<c:if test="${!KowledgeBaseView}">
				<li><a href="#anchorHistory"><spring:message code="menu.analysis.history" text="History" /></a></li>
			</c:if>
			<li><a href="#anchorScope"><spring:message code="menu.analysis.iteminformation" text="Scope" /></a></li>
			<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="menu.analysis.Parmeter" text="Parameters" /><span
					class="caret"></span></a>
				<ul class="dropdown-menu">
					<li><a href="#anchorParameter_Impact"><spring:message code="menu.analysis.parameter.impact" text="Impact" /></a></li>
					<li><a href="#anchorParameter_Probability"><spring:message code="menu.analysis.parameter.probability" text="Probability" /></a></li>
					<li><a href="#anchorParameter_ImplementationRate"><spring:message code="menu.analysis.parameter.implmentationrate" text="Implementation scale of SMT" /></a></li>
					<li><a href="#anchorParameter_Various"><spring:message code="menu.analysis.parameter.various" text="Various" /></a></li>
					<li><a href="#anchorParameter_MaxEfficiency"><spring:message code="menu.analysis.parameter.maxeff" text="Maximal efficiency rate per security maturity level" /></a></li>
					<li><a href="#anchorParameter_ILPS"><spring:message code="menu.analysis.parameter.ilps" text="Required level of implmentation per SML" /></a></li>
				</ul></li>
			<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="menu.analysis.riskinformation" text="Risk Information" /><span
					class="caret"></span></a>
				<ul class="dropdown-menu">
					<li><a href="#anchorRiskInformation_Threat"><spring:message code="menu.analysis.threat" text="Threats" /></a></li>
					<li><a href="#anchorRiskInformation_Vul"><spring:message code="menu.analysis.vulnerability" text="Vulnerabilities" /></a></li>
					<li><a href="#anchorRiskInformation_Risk"><spring:message code="menu.analysis.risk" text="Risks" /></a></li>
				</ul></li>
			<li><a href="#anchorAsset"><spring:message code="menu.analysis.asset" text="Assets" /></a></li>
			<li><a href="#anchorScenario"><spring:message code="menu.analysis.scenario" text="Scenarios" /></a></li>
			<li><a href="#anchorPhase"> <spring:message code="menu.analysis.phase" text="Phases" /></a></li>
			<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"> <spring:message code="menu.analysis.standards" text="Standard" /><span
					class="caret"></span></a> <c:if test="${empty(measureSplited)}">
					<spring:eval expression="T(lu.itrust.business.component.MeasureManager).SplitByNorm(measures)" var="measureSplited" scope="request" />
				</c:if>
				<ul class="dropdown-menu">
					<c:if test="${!empty(measureSplited)}">
						<c:forEach items="${measureSplited.keySet()}" var="norm">
							<li><a href="#anchorMeasure_${norm}"> <spring:message code="menu.measure.${norm}" text="${norm}" /></a>
						</c:forEach>
					</c:if>
					<c:if test="${analysis.getRightsforUserString(login).right.ordinal() <= 4}">
						<c:if test="${!measureSplited.isEmpty()}">
							<li class="divider"></li>
						</c:if>
						<li><a href="#" onclick="return addStandard();"> <spring:message code="label.analysis.add.standard" text="Add a standard" /></a></li>
					</c:if>
				</ul></li>
			<c:if test="${!KowledgeBaseView }">
				<li><a href="#anchorSOA"> <spring:message code="menu.analysis.soa" text="SOA" /></a></li>
				<li><a href="#anchorActionPlan"> <spring:message code="menu.analysis.actionplan" text="Action Plan" /></a></li>
				<li><a href="#anchorSummary"> <spring:message code="menu.analysis.Summary" text="Action Plan Summary" /></a></li>
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
		</ul>
		<ul class="nav navbar-nav navbar-right">
			<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"> <spring:message code="label.actions" text="Actions" /><span class="caret"></span></a>
				<ul class="dropdown-menu">
					<li><a href="${pageContext.request.contextPath}/Analysis/Deselect"> <spring:message code="label.analysis.release" text="Close Analysis" /></a></li>
					<li class="divider"></li>
					<li><a href="#" onclick="return editRRF(${sessionScope.selectedAnalysis});"> <spring:message code="label.edit.rrf" text="Edit RRF" /></a></li>
					<c:if test="${!KowledgeBaseView }">
						<li class="divider"></li>
						<li><a href="#" onclick="return computeAssessment();"> <spring:message code="label.assessment.generate.missing" text="Update Assessment" /></a></li>
						<li><a href="#" onclick="return wipeAssessment();"> <spring:message code="label.analysis.assessment.wipe" text="Wipe assessments" /></a></li>
						<li class="divider"></li>
						<li><a href="#" onclick="return displayActionPlanOptions('${analysis.id}')"> <spring:message code="label.analysis.compute.actionPlan" text="Compute Action Plan" />
						</a></li>
						<li><a href="#" onclick="return calculateRiskRegister('${analysis.id}');"> <spring:message code="label.analysis.compute.riskRegister" text="Compute Registers" /></a></li>
						<li class="divider"></li>
						<li><a href="#" onclick="return reloadCharts();"> <spring:message code="label.analysis.charts.reload" text="Reload Charts" /></a></li>
					</c:if>
				</ul></li>
		</ul>
	</div>
</div>