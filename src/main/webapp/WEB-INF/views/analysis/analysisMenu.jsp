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
<div class="navbar navbar-default navbar-static-top navbar-custom">
	<div id="analysismenu" class="container" role="navigation">
		<a class="navbar-brand" href="#" style="white-space:nowrap; width:8em; overflow:hidden;">${analysis.customer.organisation } | ${analysis.getVersion()}</a>
		<ul class="nav navbar-nav">
			
			<c:if test="${!KowledgeBaseView}">
				<li class="active"><a href="#History"><spring:message code="menu.analysis.history" text="History" /></a></li>
			</c:if>
			
			<li><a href="#ItemInformation"><spring:message code="menu.analysis.iteminformation" text="Item Information" /></a></li>
			<li><a href="#Parameter"><spring:message code="menu.analysis.parameter" text="Parameters" /></a></li>
			<li><a href="#Asset"><spring:message code="menu.analysis.asset" text="Assets" /></a></li>
			<li><a href="#Scenario"><spring:message code="menu.analysis.scenario" text="Scenarios" /></a></li>
			<!--
			<li><a href="#RiskInformation"><spring:message code="menu.analysis.riskinformation" text="Risk Information" /></a></li>
			<ul class="nav">
				<li><a href="#Threats"><spring:message code="menu.analysis.threat" text="Threats" /></a></li>
				<li><a href="#Risks"><spring:message code="menu.analysis.risk" text="Risks" /></a></li>
				<li><a href="#Vulnerabilities"><spring:message code="menu.analysis.vulnerability" text="Vulnerability" /></a></li>
			</ul>
			-->
			
			<li class="dropdown-submenu">
			<a href="#" class="dropdown-toggle" data-toggle="dropdown"> 
				<spring:message code="menu.analysis.norms" text="Norms" /><span class="caret"></span>
			</a>
				<ul class="dropdown-menu">
					<li><a href="#section_measure_27001"> <spring:message code="menu.measure.27001" text="27001" /></a></li>
						<li><a href="#section_measure_27002"> <spring:message code="menu.measure.27002" text="27002" /></a></li>
						<li><a href="#section_measure_Maturity"> <spring:message code="menu.measure.maturity" text="Maturity" /></a></li>
						<li><a href="#section_measure_custom"> <spring:message code="menu.measure.custom" text="Custom" /></a></li>
				</ul>
			</li>
			
			<c:if test="${!KowledgeBaseView }">
				<li><a href="#ActionPlan"> <spring:message code="menu.analysis.actionplan" text="Action Plans" /></a></li>
			</c:if>
			
			<li><a href="#Phase"> <spring:message code="menu.analysis.phase" text="Phases" /></a></li>
			
			<c:if test="${!KowledgeBaseView }">
			
				<li><a href="#Charts"> <spring:message code="menu.analysis.chart" text="Charts" /></a></li>
				
				<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"> <spring:message code="label.action" text="Action" /><span class="caret"></span></a>
					<ul class="dropdown-menu">
						<li><a href="#" onclick="return computeAssessment();"> <spring:message code="label.assessment.generate.missing" text="Update Assessment" /></a></li>
						<li class="divider"></li>
						<li><a href="#" onclick="return displayActionPlanOptions('${analysis.id}')"> <spring:message code="label.analysis.compute.actionPlan" text="Compute ActionPlan" /></a></li>
						<li><a href="#"> <spring:message code="label.analysis.compute.riskRegister" text="Compute Registers" /></a></li>
						<li class="divider"></li>
						<li><a href="#" onclick="return wipeAssessment();"> <spring:message code="label.analysis.assessment.wipe" text="Wipe assessments" /></a></li>
						<li class="divider"></li>
						<li><a href="#" onclick="return reloadCharts();"> <spring:message code="label.analysis.charts.reload" text="Reload Charts" /></a></li>
					</ul>
				</li>
			</c:if>
		</ul>
	</div>
</div>