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
		<li><a href="#" onclick='return displayParameters("#Scale_Impact", "${impactScaleTitle}")'>${impactScaleMenu}</a></li>
		<li><a href="#" onclick='return displayParameters("#Scale_Probability", "${probabilityScaleTitle}")'>${probabilityScaleMenu}</a></li>
		<li><a href="#" onclick="return displayParameters('#DynamicParameters', '${dynamicParametersTitle}')">${dynamicParametersMenu}</a></li>
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
			<li><a href="#" onclick='return displayParameters("#Scale_Impact", "${impactScaleTitle}")'>${impactScaleMenu}</a></li>
			<li><a href="#" onclick='return displayParameters("#Scale_Probability", "${probabilityScaleTitle}")'>${probabilityScaleMenu}</a></li>
			<li><a href="#" onclick="return displayParameters('#DynamicParameters', '${dynamicParametersTitle}')">${dynamicParametersMenu}</a></li>
		</ul>
		<jsp:include page="assets.jsp" />
	</div>
</c:if>
