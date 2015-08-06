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
<div class="navbar navbar-inverse navbar-fixed-top" role="main-menu" style="z-index: 1030;" >
	<div class="container">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
				<span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span>
			</button>
		</div>
		<sec:authorize access="authenticated">
			<a class="navbar-brand" style="color: #ffffff; font-weight: bold;" href="${pageContext.request.contextPath}/Home">TRICK SERVICE</a>
			<div class="collapse navbar-collapse">
				<ul class="nav navbar-nav">
					<li ${menu.equals("Home")? "class='active'" : "" }><a href="${pageContext.request.contextPath}/Home"> <spring:message code="label.menu.home" text="Home" /></a></li>
					<sec:authorize access="hasAnyRole('ROLE_ADMIN','ROLE_SUPERVISOR','ROLE_CONSULTANT')">
						<li ${menu.startsWith("KnowledgeBase")? "class='active'" : ""}><a href="${pageContext.request.contextPath}/KnowledgeBase"> <spring:message
									code="label.menu.analysis.knowledgebase" text="Knowledge base" /></a></li>
					</sec:authorize>
					<li ${menu.startsWith('Analysis') && !menu.startsWith('Analysis/Import')?'class="active"':''}><a href="${pageContext.request.contextPath}/Analysis"> <spring:message
								code="label.menu.analysis.all" text="Analysis" /></a></li>
					<li ${menu.startsWith('Analysis/Import')?'class="active"':''}><a href="${pageContext.request.contextPath}/Analysis/Import"> <spring:message
								code="label.menu.import.analysis" text="Import" /></a></li>
				</ul>
				<ul class="nav navbar-nav navbar-right">
					<li ${menu.equals("Profile")? "class='active'" : "" }><a href="${pageContext.request.contextPath}/Profile"> <spring:message code="label.profile" text="Profile" /></a></li>
					<sec:authorize access="hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR')">
						<li ${menu.equals("Admin")? "class='active'" : "" }><a href="${pageContext.request.contextPath}/Admin"> <spring:message code="label.administration" text="Admin" /></a></li>
					</sec:authorize>
					<sec:authorize access="hasAnyRole('ROLE_SUPERVISOR')">
						<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="label.runtime" text="Runtime" /><span class="caret"></span></a>
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
								<li class="divider"></li>
								<li class="dropdown-header"><spring:message code="label.runtime.dropdown_header.Major" text="Major patches" /></li>
								<li><a href="#" onclick="return restoreAnalysisRights()"> <spring:message code="label.restore.analysis.right" text="Restore analysis rights" />
								</a></li>
								<li><a href="#" onclick="return updateAnalysesScopes()"> <spring:message code="label.update.analyses.scopes" text="Update analyses scopes" />
								</a></li>
							</ul></li>
					</sec:authorize>
					<li><a href="${pageContext.request.contextPath}/j_spring_security_logout"> <spring:message code="label.menu.logout" text="Logout" /></a></li>
				</ul>
			</div>
		</sec:authorize>
	</div>
</div>
