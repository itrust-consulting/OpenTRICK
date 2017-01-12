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
<div class="navbar navbar-inverse navbar-fixed-top" role="main-menu" style="z-index: 1030;">
	<div class="container">
		<div class="navbar-header">
			<a class="navbar-brand" id='main_menu_brand' href="${pageContext.request.contextPath}/Home"></a>
			<button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
				<span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span>
			</button>
		</div>
		<sec:authorize access="authenticated">
			<div class="collapse navbar-collapse">
				<ul class="nav navbar-nav">
					<sec:authorize access="hasAnyRole('ROLE_ADMIN','ROLE_SUPERVISOR','ROLE_CONSULTANT')">
						<li ${menu.startsWith("KnowledgeBase")? "class='active'" : ""}><a href="${pageContext.request.contextPath}/KnowledgeBase" id='main_menu_knowledgebase'> <spring:message
									code="label.menu.analysis.knowledgebase" text="Knowledge base" /></a></li>
					</sec:authorize>
					<c:choose>
						<c:when test="${not menu.startsWith('Analysis')}">
							<li><a href="${pageContext.request.contextPath}/Analysis" id='main_menu_analysis'> <spring:message code="label.menu.analysis.all" text="Analysis" /></a></li>
							<li><a href="${pageContext.request.contextPath}/Analysis/Risk-evolution" id='main_menu_risk_evelotion'> <spring:message code="label.menu.analysis.risk_evolution"
										text="Risk evolution" /></a></li>
							<li><a href="${pageContext.request.contextPath}/Analysis/Import" id='main_menu_analysis_import'> <spring:message code="label.menu.import.analysis" text="Import" /></a></li>
						</c:when>
						<c:when test="${menu.startsWith('Analysis/Risk-evolution')}">
							<li><a href="${pageContext.request.contextPath}/Analysis" id='main_menu_analysis'> <spring:message code="label.menu.analysis.all" text="Analysis" /></a></li>
							<li class='active'><a href="${pageContext.request.contextPath}/Analysis" id='main_menu_risk_evelotion'> <spring:message code="label.menu.analysis.risk_evolution"
										text="Risk evolution" /></a></li>
							<li><a href="${pageContext.request.contextPath}/Analysis/Import" id='main_menu_analysis_import'> <spring:message code="label.menu.import.analysis" text="Import" /></a></li>
						</c:when>
						<c:when test="${menu.startsWith('Analysis/Import')}">
							<li><a href="${pageContext.request.contextPath}/Analysis" id='main_menu_analysis'> <spring:message code="label.menu.analysis.all" text="Analysis" /></a></li>
							<li><a href="${pageContext.request.contextPath}/Analysis/Risk-evolution" id='main_menu_risk_evelotion'> <spring:message code="label.menu.analysis.risk_evolution"
										text="Risk evolution" /></a></li>
							<li class='active'><a href="${pageContext.request.contextPath}/Analysis/Import" id='main_menu_analysis_import'> <spring:message code="label.menu.import.analysis"
										text="Import" /></a></li>
						</c:when>
						<c:otherwise>
							<li class="active"><a href="${pageContext.request.contextPath}/Analysis" id='main_menu_analysis'> <spring:message code="label.menu.analysis.all" text="Analysis" /></a></li>
							<li><a href="${pageContext.request.contextPath}/Analysis/Risk-evolution" id='main_menu_risk_evelotion'> <spring:message code="label.menu.analysis.risk_evolution"
										text="Risk evolution" /></a></li>
							<li><a href="${pageContext.request.contextPath}/Analysis/Import" id='main_menu_analysis_import'> <spring:message code="label.menu.import.analysis" text="Import" /></a></li>
						</c:otherwise>
					</c:choose>
				</ul>
				<ul class="nav navbar-nav navbar-right">
					<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="taskmanager" style="padding-bottom: 12px" id='main_menu_task'><spring:message
								code="label.background.task" /> <span id="task-counter" class="fa badge">0</span></a>
						<ul class="dropdown-menu" id="task-manager"></ul></li>
					<li ${menu.equals("Profile")? "class='active'" : "" }><a href="${pageContext.request.contextPath}/Profile" id='main_menu_profile'> <spring:message
								code="label.profile" text="Profile" /></a></li>
					<sec:authorize access="hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR')">
						<li ${menu.equals("Admin")? "class='active'" : "" }><a href="${pageContext.request.contextPath}/Admin" id='main_menu_admin'> <spring:message
									code="label.administration" text="Admin" /></a></li>
					</sec:authorize>
					<sec:authorize access="hasAnyRole('ROLE_SUPERVISOR')">
						<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown" id='main_menu_runtime'><spring:message code="label.runtime" text="Runtime" /><span
								class="caret"></span></a>
							<ul class="dropdown-menu">
								<li class="dropdown-header"><spring:message code="label.runtime.dropdown_header.Anytime" text="Anytime patches" /></li>
								<li><a href="#" onclick="return updateMeasureAssetTypeValue()"> <spring:message code="label.measure.update.asset_type_value"
											text="Create missing asset type values for measures" />
								</a></li>
								<li><a href="#" onclick="return fixAllScenarioCategories()"> <spring:message code="label.scenario.fix.categories"
											text="Update category of Scenarios from all analyses" />
								</a></li>
								<li><a href="#" onclick="return fixAllAssessments()"> <spring:message code="label.scenario.fix.assessments" text="Update assessments of all analyses" />
								</a></li>
								<li><a href="#" onclick="return addCSSFParameters()"> <spring:message code="label.add.css_parameters" text="Add CSSF Parameters" />
								</a></li>
								<li class="divider"></li>
								<li class="dropdown-header"><spring:message code="label.runtime.dropdown_header.Major" text="Major patches" /></li>
								<li><a href="#" onclick="return updateAnalysesRiskAndItemInformation()"> <spring:message code="label.update.analyses.risk_item.information"
											text="Copy missing risk or item information from default profile" />
								</a></li>
								<li><a href="#" onclick="return restoreAnalysisRights()"> <spring:message code="label.restore.analysis.right" text="Restore analysis rights" />
								</a></li>
								<li><a href="#" onclick="return updateAnalysesScopes()"> <spring:message code="label.update.analyses.scopes" text="Update analyses scopes" />
								</a></li>
							</ul></li>
					</sec:authorize>
					<li><a href="#" onclick="return $('#logoutFormSubmiter').click()" id='main_menu_logout'><spring:message code="label.menu.logout" text="Logout" /></a>
						<form action="${pageContext.request.contextPath}/signout" method="post" style="display: none">
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="submit" id="logoutFormSubmiter" />
						</form></li>
				</ul>
			</div>
		</sec:authorize>
	</div>
</div>
