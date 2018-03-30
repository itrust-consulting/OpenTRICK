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
		<sec:authorize access="hasAnyRole('ROLE_ADMIN','ROLE_SUPERVISOR','ROLE_CONSULTANT', 'ROLE_USER')">
			<div class="navbar-header">
				<c:if test='${not (empty menu or menu == "Home")}'>
					<c:set var="homeURL" value="${pageContext.request.contextPath}/Home" />
				</c:if>
				<a class="navbar-brand" id='main_menu_brand' href="${empty homeURL? '#' : homeURL }"></a>
				<button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
					<span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span>
				</button>
			</div>
			<div class="collapse navbar-collapse">
				<ul class="nav navbar-nav">
					<sec:authorize access="hasAnyRole('ROLE_ADMIN','ROLE_SUPERVISOR','ROLE_CONSULTANT')">
						<c:if test='${not menu.startsWith("KnowledgeBase")}'>
							<c:set var="knowledgeBaseURL" value="${pageContext.request.contextPath}/KnowledgeBase" />
						</c:if>
						<li ${empty knowledgeBaseURL? "class='active'" : ''}><a href="${empty knowledgeBaseURL? '#' : knowledgeBaseURL}" id='main_menu_knowledgebase'> <spring:message
									code="label.menu.analysis.knowledgebase" text="Knowledge base" /></a></li>
					</sec:authorize>
					<c:choose>
						<c:when test="${menu.startsWith('Analysis/Risk-evolution')}">
							<li><a href="${pageContext.request.contextPath}/Analysis" id='main_menu_analysis'> <spring:message code="label.menu.analysis.all" text="Analysis" /></a></li>
							<li class='active'><a href="#" id='main_menu_risk_evelotion'> <spring:message code="label.menu.analysis.risk_evolution" text="Risk evolution" /></a></li>
						</c:when>
						<c:when test="${menu.startsWith('Analysis')}">
							<li class="active"><a href="#" id='main_menu_analysis'> <spring:message code="label.menu.analysis.all" text="Analysis" /></a></li>
							<li><a href="${pageContext.request.contextPath}/Analysis/Risk-evolution" id='main_menu_risk_evelotion'> <spring:message code="label.menu.analysis.risk_evolution"
										text="Risk evolution" /></a></li>
						</c:when>
						<c:otherwise>
							<li><a href="${pageContext.request.contextPath}/Analysis" id='main_menu_analysis'> <spring:message code="label.menu.analysis.all" text="Analysis" /></a></li>
							<li><a href="${pageContext.request.contextPath}/Analysis/Risk-evolution" id='main_menu_risk_evelotion'> <spring:message code="label.menu.analysis.risk_evolution"
										text="Risk evolution" /></a></li>
						</c:otherwise>
					</c:choose>
				</ul>
				<ul class="nav navbar-nav navbar-right">
					<c:if test='${not menu.equals("Account")}'>
						<c:set var="accountURL" value="${pageContext.request.contextPath}/Account" />
					</c:if>
					<li ${empty accountURL? "class='active'" : "" }><a href="${empty accountURL? '#' : accountURL }" id='main_menu_profile'> <sec:authentication var="currentUsername"
								property="name" /> <c:choose>
								<c:when test="${not empty currentUsername }">
									<spring:message text='${currentUsername}' />
								</c:when>
								<c:otherwise>
									<spring:message code="label.profile" text="Profile" />
								</c:otherwise>
							</c:choose> <span id="invitation-count" class="badge" style="padding-bottom: 4px; padding-top: 2px;" title='<spring:message code='label.info.analysis.invitation.count'/>'>${analysisSharedCount}</span>
					</a></li>
					<sec:authorize access="hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR')">
						<c:if test='${not menu.equals("Admin")}'>
							<c:set var="adminURL" value="${pageContext.request.contextPath}/Admin" />
						</c:if>
						<li ${empty adminURL? "class='active'" : "" }><a href="${empty adminURL? '#' : adminURL}" id='main_menu_admin'> <spring:message code="label.administration"
									text="Admin" /></a></li>
						<c:set var="isAdministration" value="${menu == 'Admin'}" scope="request" />
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
						<form id='logout-form' action="${pageContext.request.contextPath}/signout" method="post" style="display: none">
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="submit" id="logoutFormSubmiter" />
						</form></li>
				</ul>
			</div>
		</sec:authorize>
	</div>
</div>