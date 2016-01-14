<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<fmt:message key="label.menu.show.impact_scale"  var="impactScaleMenu"/>
<fmt:message key="label.menu.show.probability_scale"  var="probabilityScaleMenu"/>
<fmt:message key="label.title.impact_scale"  var="impactScaleTitle"/>
<fmt:message key="label.title.probability_scale"  var="probabilityScaleTitle"/>
<div class="tab-pane trick-chart-tab" id="tabEstimationScenario" data-update-required="true" data-trigger="showEstimation">
	<div class="page-header tab-content-header">
		<div class="container">
			<div class="row-fluid">
				<h3 role="title">
				</h3>
			</div>
		</div>
	</div>
	<ul class="nav nav-pills bordered-bottom" id="menu_scenario_assessment">
		<li><a href="#" onclick="return displayParameters('impact_scale', '${impactScaleTitle}')" >${impactScaleMenu}</a></li>
		<li><a href="#" onclick="return displayParameters('probability_scale', '${probabilityScaleTitle}')" >${probabilityScaleMenu}</a></li>
	</ul>
	<jsp:include page="scenarios.jsp" />
</div>
<c:if test="${!analysis.isProfile() }">
	<div class="tab-pane" id="tabEstimationAsset" data-update-required="true" data-trigger="showEstimation">
		<div class="page-header tab-content-header">
			<div class="container">
				<div class="row-fluid">
					<h3 role="title">
					</h3>
				</div>
			</div>
		</div>
		<ul class="nav nav-pills bordered-bottom" id="menu_asset_assessment">
			<li><a href="#" onclick="return displayParameters('impact_scale', '${impactScaleTitle}')" >${impactScaleMenu}</a></li>
			<li><a href="#" onclick="return displayParameters('probability_scale', '${probabilityScaleTitle}')" >${probabilityScaleMenu}</a></li>
		</ul>
		<jsp:include page="assets.jsp" />
	</div>
</c:if>
