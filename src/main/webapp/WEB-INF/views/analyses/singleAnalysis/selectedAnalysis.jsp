<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<html lang="${fn:substring(analysis.language.alpha3,0, 2)}">
<fmt:setLocale value="${fn:substring(analysis.language.alpha3,0, 2)}" scope="session" />
<c:set scope="request" var="title">label.title.analysis</c:set>
<jsp:include page="../../template/header.jsp" />
<body>
	<div id="wrap">
		<c:set var="isEditable" value="${analysis.getRightsforUserString(login).right.ordinal()<5}" scope="request" />
		<jsp:include page="../../template/menu.jsp" />
		<div class="container">
			<jsp:include page="analysisMenu.jsp" />
			<jsp:include page="../../template/successErrors.jsp" />
			<div class="tab-content" id="nav-container" data-trick-id="${analysis.id}" data-trick-class="Analysis" data-trick-rights-id="${analysis.profile? 0 : analysis.getRightsforUserString(login).right.ordinal()}"
				data-trick-language="${fn:substring(analysis.language.alpha3,0,2)}">
				<c:if test="${!analysis.isProfile()}">
					<c:set var="histories" value="${analysis.histories}" scope="request" />
					<jsp:include page="./components/history.jsp" />
				</c:if>
				<c:if test="${!analysis.isProfile() }">
					<c:set var="itemInformations" value="${analysis.itemInformations}" scope="request" />
					<jsp:include page="./components/itemInformation.jsp" />
				</c:if>
				<c:set var="parameters" value="${analysis.parameters}" scope="request" />
				<jsp:include page="./components/parameter.jsp" />
				<c:if test="${!analysis.isProfile() }">
					<c:set var="riskInformation" value="${analysis.riskInformations}" scope="request" />
					<jsp:include page="./components/riskinformation.jsp" />
					<spring:eval expression="T(lu.itrust.business.TS.data.assessment.helper.AssessmentManager).ComputeALE(analysis)" var="ales" />
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
				<c:if test="${!analysis.isProfile() }">
					<jsp:include page="./components/soa.jsp" />
					<c:set var="actionplans" scope="request" value="${analysis.actionPlans}" />
					<jsp:include page="./components/actionplan.jsp" />
					<c:set var="summaries" scope="request" value="${analysis.summaries}" />
					<jsp:include page="./components/summary.jsp" />
					<c:set var="riskregister" scope="request" value="${analysis.riskRegisters}" />
					<jsp:include page="./components/riskregister.jsp" />
					<jsp:include page="./components/charts.jsp" />
				</c:if>
			</div>
			<jsp:include page="./components/widgets.jsp" />
		</div>
		<jsp:include page="../../template/footer.jsp" />
	</div>
	<jsp:include page="../../template/scripts.jsp" />
	<script src="<spring:url value="js/trickservice/analysis.js" />"></script>
	<script src="<spring:url value="/js/highcharts/highcharts.js" />"></script>
	<script src="<spring:url value="/js/highcharts/highcharts-more.js" />"></script>
	<script src="<spring:url value="/js/highcharts/exporting.js" />"></script>
	<script src="<spring:url value="/js/jquery.fileDownload.js" />"></script>
	<!-- <script type="text/javascript" src="<spring:url value="js/trickservice/rrf.js" />"></script> -->
	
	<script type="text/javascript" src="<spring:url value="js/trickservice/fieldeditor.js" />"></script>
	<script type="text/javascript" src="<spring:url value="js/trickservice/scenario.js" />"></script>
	<script type="text/javascript" src="<spring:url value="js/trickservice/phase.js" />"></script>
	<script type="text/javascript" src="<spring:url value="js/trickservice/rrfManager.js" />"></script>
	<script type="text/javascript" src="<spring:url value="js/trickservice/rrf.js" />"></script>
	<script type="text/javascript" src="<spring:url value="js/trickservice/analysisStandard.js" />"></script>
	<c:if test="${!analysis.isProfile()}">
		<script type="text/javascript" src="<spring:url value="js/trickservice/actionplan.js" />"></script>
		<script type="text/javascript" src="<spring:url value="js/trickservice/assessment.js" />"></script>
		<script type="text/javascript" src="<spring:url value="js/trickservice/asset.js" />"></script>
		<script type="text/javascript" src="<spring:url value="js/bootstrap/typeahead.bundle.js" />"></script>
		<script type="text/javascript" src="<spring:url value="js/trickservice/riskregister.js" />"></script>
	</c:if>
</body>
</html>