<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<spring:message code="label.menu.show.impact_scale" var="impactScaleMenu" />
<spring:message code="label.menu.show.probability_scale" var="probabilityScaleMenu" />
<spring:message code="label.title.impact_scale" var="impactScaleTitle" />
<spring:message code="label.title.probability_scale" var="probabilityScaleTitle" />
<spring:message code="label.action.next" var="nextSelected" />
<spring:message code="label.action.previous" var="prevSelected" />
<spring:message code="label.menu.show.dynamic_parameters_list" var="dynamicParametersTitle" />
<spring:message code="label.menu.show.dynamic_parameters_list" var="dynamicParametersMenu" />
<spring:message code="label.menu.analysis.parameter.probability" var="probablityMenu" />
<c:if test="${empty locale }">
	<spring:eval expression="T(org.springframework.web.servlet.support.RequestContextUtils).getLocale(pageContext.request)" var="locale" scope="request" />
</c:if>
<c:if test="${empty langue}">
	<c:set var="langue" value="${fn:toUpperCase(locale.language) }" scope="request" />
</c:if>
<c:set var="impactScaleTitle">
	${fn:replace(impactScaleTitle,"'", "\\'" )}
</c:set>
<c:set var="probabilityScaleTitle">
	${fn:replace(probabilityScaleTitle,"'", "\\'" )}
</c:set>
<div class="tab-pane trick-chart-tab" id="tabEstimationScenario" data-update-required="true" data-trigger="showEstimation">
	<div class="page-header tab-content-header">
		<div class="container">
			<div class="row-fluid">
				<h3 role="title"></h3>
			</div>
		</div>
	</div>
	<ul class="nav nav-pills bordered-bottom" id="menu_scenario_assessment">
		<li data-role='nav-prev'><a href="#" onclick="return prevSelected()"><i class="fa fa-angle-double-left"></i> ${prevSelected}</a></li>
		<li><a href="#" onclick="return switchTab('tabScenario')"><span class="fa fa-home"></span> <spring:message code="label.menu.analysis.scenario" /></a></li>
		<li data-role='nav-next'><a href="#" onclick="return nextSelected()">${nextSelected} <i class="fa fa-angle-double-right"></i></a></li>
		<c:choose>
			<c:when test="${type == 'QUALITATIVE'}">
				<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown">${impactScaleMenu} <span class="caret"></span></a>
					<ul class="dropdown-menu">
						<c:forEach items="${impactTypes}" var="impactType">
							<spring:message var="impactName" text="${impactType.name}" />
							<li><a href="#" onclick='return displayParameters("#Scale_Impact_${impactName}")'><spring:message
										code="label.title.parameter.extended.impact.${fn:toLowerCase(impactName)}"
										text="${empty impactType.translations[language]? impactType.displayName  :  impactType.translations[language]}" /></a></li>
						</c:forEach>
					</ul></li>
				<li><a href="#" onclick='return displayParameters("#Scale_Probability", "${probabilityScaleTitle}")'>${probabilityScaleMenu}</a></li>
			</c:when>
			<c:otherwise>
				<li><a href="#" onclick='return displayParameters("#Scale_Impact", "${impactScaleTitle}")'>${impactScaleMenu}</a></li>
				<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown">${probabilityScaleMenu} <span class="caret"></span></a>
					<ul class="dropdown-menu">
						<li><a href="#" onclick='return displayParameters("#Scale_Probability", "${probabilityScaleTitle}")'>${probabilityScaleTitle}</a></li>
						<li><a href="#" onclick="return displayParameters('#DynamicParameters')">${dynamicParametersTitle}</a></li>
					</ul></li>
			</c:otherwise>
		</c:choose>
	</ul>
	<jsp:include page="scenarios.jsp" />
</div>
<c:if test="${!analysis.isProfile() }">
	<div class="tab-pane" id="tabEstimationAsset" data-update-required="true" data-trigger="showEstimation">
		<div class="page-header tab-content-header">
			<div class="container">
				<div class="row-fluid">
					<h3 role="title"></h3>
				</div>
			</div>
		</div>
		<ul class="nav nav-pills bordered-bottom" id="menu_asset_assessment">
			<li data-role='nav-prev'><a href="#" onclick="return prevSelected()"><i class="fa fa-angle-double-left"></i> ${prevSelected}</a></li>
			<li><a href="#" onclick="return switchTab('tabAsset')"><span class="fa fa-home"></span> <spring:message code="label.menu.analysis.asset" /></a></li>
			<li data-role='nav-next'><a href="#" onclick="return nextSelected()">${nextSelected} <i class="fa fa-angle-double-right"></i></a></li>
			<c:choose>
				<c:when test="${type == 'QUALITATIVE'}">
					<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown">${impactScaleMenu} <span class="caret"></span></a>
						<ul class="dropdown-menu">
							<c:forEach items="${impactTypes}" var="impactType">
								<spring:message var="impactName" text="${impactType.name}" />
								<li><a href="#" onclick='return displayParameters("#Scale_Impact_${impactName}")'><spring:message
											code="label.title.parameter.extended.impact.${fn:toLowerCase(impactName)}"
											text="${empty impactType.translations[language]? impactType.displayName  :  impactType.translations[language]}" /></a></li>
							</c:forEach>
						</ul></li>
					<li><a href="#" onclick='return displayParameters("#Scale_Probability", "${probabilityScaleTitle}")'>${probabilityScaleMenu}</a></li>
				</c:when>
				<c:otherwise>
					<li><a href="#" onclick='return displayParameters("#Scale_Impact", "${impactScaleTitle}")'>${impactScaleMenu}</a></li>
					<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown">${probabilityScaleMenu} <span class="caret"></span></a>
						<ul class="dropdown-menu">
							<li><a href="#" onclick='return displayParameters("#Scale_Probability", "${probabilityScaleTitle}")'>${probabilityScaleTitle}</a></li>
							<li><a href="#" onclick="return displayParameters('#DynamicParameters', '${dynamicParametersTitle}')">${dynamicParametersTitle}</a></li>
						</ul></li>
				</c:otherwise>
			</c:choose>
		</ul>
		<jsp:include page="assets.jsp" />
	</div>
</c:if>
