<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:set var="language" value="${analysis.language.alpha2}" scope="request" />
<c:set var="isProfile" value="${analysis.profile}" scope="request" />
<c:if test="${empty locale }">
	<spring:eval expression="T(org.springframework.web.servlet.support.RequestContextUtils).getLocale(pageContext.request)" var="locale" scope="request"/>
</c:if>
<!DOCTYPE html>
<html lang="${locale.language}">
<c:set scope="request" var="title">label.title.analysis</c:set>
<jsp:include page="../../template/header.jsp" />
<c:set var="canModify" value="${analysis.profile or analysis.getRightsforUserString(login).right.ordinal()<3}" />
<body>
	<div id="wrap" class="wrap">
		<c:set var="isEditable" value="${canModify && open!='READ'}" scope="request" />
		<c:set var="isLinkedToProject" value="${allowedTicketing and analysis.hasProject()}" scope="request"/>
		<jsp:include page="../../template/menu.jsp" />
		<div class="container">
			<jsp:include page="menu.jsp" />
			<jsp:include page="../../template/successErrors.jsp" />
			<div class="tab-content" id="nav-container" data-trick-id="${analysis.id}" data-trick-class="Analysis"
				data-trick-rights-id="${analysis.profile? 0 : analysis.getRightsforUserString(login).right.ordinal()}" data-trick-language="${locale.language}">
				<c:if test="${!isProfile}">
					<c:set var="histories" value="${analysis.histories}" scope="request" />
					<jsp:include page="./components/history.jsp" />
				</c:if>
				<c:if test="${!isProfile}">
					<c:set var="itemInformations" value="${analysis.itemInformations}" scope="request" />
					<jsp:include page="./components/itemInformation.jsp" />
				</c:if>
				<c:set var="parameters" value="${analysis.parameters}" scope="request" />
				<jsp:include page="./components/parameter.jsp" />
				<c:if test="${!isProfile}">
					<c:set var="riskInformation" value="${analysis.riskInformations}" scope="request" />
					<jsp:include page="./components/riskinformation.jsp" />
					<jsp:include page="./components/assessment/home.jsp" />
					<spring:eval expression="T(lu.itrust.business.TS.model.general.helper.AssessmentAndRiskProfileManager).ComputeALE(analysis)" var="ales" />
					<c:set var="assetALE" value="${ales[0]}" scope="request" />
					<c:set var="assets" value="${analysis.assets}" scope="request" />
					<jsp:include page="./components/asset/asset.jsp" />
					<c:set var="scenarioALE" value="${ales[1]}" scope="request" />
				</c:if>
				<c:set var="scenarios" value="${analysis.scenarios}" scope="request" />
				<jsp:include page="./components/scenario/scenario.jsp" />
				<c:set var="phases" scope="request" value="${analysis.phases}" />
				<jsp:include page="./components/phase/phase.jsp" />
				<jsp:include page="./components/standards/standard/standards.jsp" />
				<c:if test="${!isProfile}">
					<jsp:include page="./components/soa.jsp" />
					<c:set var="actionplans" scope="request" value="${analysis.actionPlans}" />
					<jsp:include page="./components/actionPlan/section.jsp" />
					<c:set var="summaries" scope="request" value="${analysis.summaries}" />
					<jsp:include page="./components/summary.jsp" />
					<c:if test="${show_cssf}">
						<c:set var="riskregister" scope="request" value="${analysis.riskRegisters}" />
						<jsp:include page="./components/riskRegister/home.jsp" />
					</c:if>
					<jsp:include page="./components/charts.jsp" />
				</c:if>
			</div>
			<jsp:include page="./components/widgets.jsp" />
		</div>
		<jsp:include page="../../template/footer.jsp" />
	</div>
	<jsp:include page="../../template/scripts.jsp" />
	<script src="<spring:url value="/js/bootstrap/bootstrap-slider.min.js" />"></script>
	<script src="<spring:url value="/js/bootstrap/bootstrap-datepicker.js" />"></script>
	<script src="<spring:url value="/js/trickservice/analysis.js" />"></script>
	<script src="<spring:url value="/js/highcharts/highcharts.js" />"></script>
	<script src="<spring:url value="/js/highcharts/highcharts-more.js" />"></script>
	<script src="<spring:url value="/js/highcharts/exporting.js" />"></script>
	<script type="text/javascript" src="<spring:url value="/js/trickservice/fieldeditor.js" />"></script>
	<script type="text/javascript" src="<spring:url value="/js/trickservice/scenario.js" />"></script>
	<script type="text/javascript" src="<spring:url value="/js/trickservice/phase.js" />"></script>
	<script type="text/javascript" src="<spring:url value="/js/trickservice/rrfManager.js" />"></script>
	<script type="text/javascript" src="<spring:url value="/js/trickservice/rrf.js" />"></script>
	<script type="text/javascript" src="<spring:url value="/js/trickservice/analysisStandard.js" />"></script>
	<c:if test="${!isProfile}">
		<script type="text/javascript" src="<spring:url value="/js/trickservice/actionplan.js" />"></script>
		<script type="text/javascript" src="<spring:url value="/js/trickservice/assessment.js" />"></script>
		<script type="text/javascript" src="<spring:url value="/js/trickservice/asset.js" />"></script>
		<script type="text/javascript" src="<spring:url value="/js/bootstrap/typeahead.bundle.js" />"></script>
		<c:if test="${show_cssf}">
			<script type="text/javascript" src="<spring:url value="/js/trickservice/riskregister.js" />"></script>
		</c:if>
		<script type="text/javascript" src="<spring:url value="/js/trickservice/analysisExport.js" />"></script>
	</c:if>
	<script type="text/javascript">
	<!--
		application.openMode = OPEN_MODE.valueOf('${open}');
		application.isLinkedToProject = ${isLinkedToProject};
		-->
	</script>
</body>
</html>