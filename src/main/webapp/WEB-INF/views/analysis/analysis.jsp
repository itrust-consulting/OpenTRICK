<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:set scope="request" var="title">label.title.analysis</c:set>
<html>
<jsp:include page="../header.jsp" />
<body data-spy="scroll" data-target="#analysismenu" data-offset="40">
	<div id="wrap">
		<jsp:include page="../menu.jsp" />
		<div class="container">
			<spring:eval expression="T(lu.itrust.business.component.MeasureManager).ConcatMeasure(analysis.analysisNorms)" var="measures" scope="request" />
			<jsp:include page="analysisMenu.jsp" />
			<jsp:include page="../successErrors.jsp" />
			<div id="nav-container" trick-id="${analysis.id}" trick-class="Analysis" trick-rights-id="${analysis.profile? 0 : analysis.getRightsforUserString(login).right.ordinal()}">
				<c:if test="${!KowledgeBaseView}">
					<h2>${analysis.label}|${ analysis.version }</h2>
					<c:set var="histories" value="${analysis.histories}" scope="request" />
					<jsp:include page="./components/history.jsp" />
				</c:if>
				<c:if test="${KowledgeBaseView}">
					<h2>${analysis.identifier}|${ analysis.version }</h2>
				</c:if>
				<c:if test="${!KowledgeBaseView }">
					<c:set var="itemInformations" value="${analysis.itemInformations}" scope="request" />
					<jsp:include page="./components/itemInformation.jsp" />
				</c:if>
				<c:set var="parameters" value="${analysis.parameters}" scope="request" />
				<jsp:include page="./components/parameter.jsp" />
				<c:if test="${!KowledgeBaseView }">
					<c:set var="riskInformation" value="${analysis.riskInformations}" scope="request" />
					<jsp:include page="./components/riskinformation.jsp" />
					<spring:eval expression="T(lu.itrust.business.component.AssessmentManager).ComputeALE(analysis)" var="ales" />
					<c:set var="assetALE" value="${ales[0]}" scope="request" />
					<c:set var="assets" value="${analysis.assets}" scope="request" />
					<jsp:include page="./components/asset.jsp" />
					<c:set var="scenarioALE" value="${ales[1]}" scope="request" />
				</c:if>
				<c:set var="scenarios" value="${analysis.scenarios}" scope="request" />
				<jsp:include page="./components/scenario.jsp" />
				<c:set var="phases" scope="request" value="${analysis.usedPhases}" />
				<jsp:include page="./components/phase.jsp" />
				<jsp:include page="./components/measure.jsp" />
				<c:if test="${!KowledgeBaseView }">
					<jsp:include page="./components/soa.jsp" />
					<c:set var="actionplans" scope="request" value="${analysis.actionPlans}" />
					<jsp:include page="./components/actionplan.jsp" />
					<c:set var="summaries" scope="request" value="${analysis.summaries}" />
					<jsp:include page="./components/summary.jsp" />
					<c:if test="${empty(show_cssf) or show_cssf}">
						<c:set var="riskregister" scope="request" value="${analysis.riskRegisters}" />
						<jsp:include page="./components/riskregister.jsp" />
					</c:if>
					<jsp:include page="./components/charts.jsp" />
				</c:if>
			</div>
		</div>
		<jsp:include page="./components/widgets.jsp" />
		<jsp:include page="../footer.jsp" />
		<jsp:include page="../scripts.jsp" />
		<script src="<spring:url value="js/trickservice/analysis.js" />"></script>
		<script src="<spring:url value="/js/highcharts/highcharts.js" />"></script>
		<script src="<spring:url value="/js/highcharts/highcharts-more.js" />"></script>
		<script src="<spring:url value="/js/highcharts/exporting.js" />"></script>
		<script src="<spring:url value="/js/jquery.fileDownload.js" />"></script>
		<script type="text/javascript" src="<spring:url value="js/trickservice/rrf.js" />"></script>
		<script type="text/javascript" src="<spring:url value="js/trickservice/fieldeditor.js" />"></script>
		<script type="text/javascript" src="<spring:url value="js/trickservice/scenario.js" />"></script>
		<script type="text/javascript" src="<spring:url value="js/trickservice/phase.js" />"></script>
		<c:if test="${!KowledgeBaseView}">
			<script type="text/javascript" src="<spring:url value="js/trickservice/actionplan.js" />"></script>
			<script type="text/javascript" src="<spring:url value="js/trickservice/assessment.js" />"></script>
			<script type="text/javascript" src="<spring:url value="js/trickservice/asset.js" />"></script>
			<script type="text/javascript" src="<spring:url value="js/bootstrap/typeahead.bundle.js" />"></script>
			<script type="text/javascript" src="<spring:url value="js/trickservice/riskregister.js" />"></script>
		</c:if>
	</div>
</body>
</html>