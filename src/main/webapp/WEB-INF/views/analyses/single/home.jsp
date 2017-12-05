<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:set var="isProfile" value="${analysis.profile}" scope="request" />
<c:if test="${empty locale }">
	<spring:eval expression="T(org.springframework.web.servlet.support.RequestContextUtils).getLocale(pageContext.request)" var="locale" scope="request" />
</c:if>
<c:set var="language" value="${locale.language}" scope="request" />
<!DOCTYPE html>
<html lang="${language}">
<c:set scope="request" var="title">label.title.analysis</c:set>
<jsp:include page="../../template/header.jsp" />
<c:set var="canModify" value="${analysis.profile or analysis.getRightsforUserString(login).right.ordinal()<3}" />
<body>
	<div id="wrap" class="wrap">
		<c:set var="isEditable" value="${canModify && open!='READ'}" scope="request" />
		<c:set var="isLinkedToProject" value="${allowedTicketing and analysis.hasProject()}" scope="request" />
		<jsp:include page="../../template/menu.jsp" />
		<div class="container" data-ug-root="analysis">
			<jsp:include page="menu.jsp" />
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
				<c:if test="${type.qualitative}">
					<c:set var="impactTypes" value="${analysis.impacts}" scope="request" />
				</c:if>
				<c:set var="parameters" value="${analysis.parameters}" scope="request" />
				<jsp:include page="./components/parameters/home.jsp" />
				<c:set var="scenarios" value="${analysis.scenarios}" scope="request" />
				<c:if test="${!isProfile}">
					<jsp:include page="./components/risk-information/home.jsp" />
					<c:set var="assets" value="${analysis.assets}" scope="request" />
					<jsp:include page="./components/risk-estimation/home.jsp" />
					<c:if test="${type.quantitative}">
						<spring:eval expression="T(lu.itrust.business.TS.model.general.helper.AssessmentAndRiskProfileManager).ComputeALE(analysis)" var="ales" />
						<c:set var="assetALE" value="${ales[0]}" scope="request" />
						<c:set var="scenarioALE" value="${ales[1]}" scope="request" />
					</c:if>
					<jsp:include page="./components/asset/asset.jsp" />
				</c:if>
				<jsp:include page="./components/scenario/scenario.jsp" />
				<c:set var="phases" scope="request" value="${analysis.phases}" />
				<jsp:include page="./components/phase/phase.jsp" />
				<jsp:include page="./components/standards/standard/standards.jsp" />
				<jsp:include page="./components/standards/edition/home.jsp" />
				<c:if test="${!isProfile}">
					<jsp:include page="./components/soa/home.jsp" />
					<c:set var="actionplans" scope="request" value="${analysis.actionPlans}" />
					<jsp:include page="./components/actionPlan/section.jsp" />
					<c:set var="summaries" scope="request" value="${analysis.summaries}" />
					<jsp:include page="./components/summary.jsp" />
					<c:if test="${type.qualitative}">
						<c:set var="riskregister" scope="request" value="${analysis.riskRegisters}" />
						<jsp:include page="./components/riskRegister/home.jsp" />
					</c:if>
					<jsp:include page="./components/charts.jsp" />
				</c:if>
			</div>
		</div>
		<jsp:include page="../../template/footer.jsp" />
		<jsp:include page="./components/widgets.jsp" />
	</div>
	<jsp:include page="../../template/scripts.jsp" />
	<script src="<spring:url value="/js/chartjs/Chart.bundle.min.js?version=${jsVersion}" />"></script>
	<script src="<spring:url value="/js/chartjs/plugins.js?version=${jsVersion}" />"></script>
	<script src="<spring:url value="/js/chartjs/Chart.HeatMap.js?version=${jsVersion}" />"></script>
	<script src="<spring:url value="/js/bootstrap/bootstrap-slider.min.js?version=${jsVersion}" />"></script>
	<script src="<spring:url value="/js/bootstrap/bootstrap-datepicker.js?version=${jsVersion}" />"></script>
	<script src="<spring:url value="/js/trickservice/analysis.js?version=${jsVersion}" />"></script>
	<script type="text/javascript" src="<spring:url value="/js/trickservice/fieldeditor.js?version=${jsVersion}" />"></script>
	<script type="text/javascript" src="<spring:url value="/js/trickservice/scenario.js?version=${jsVersion}" />"></script>
	<script type="text/javascript" src="<spring:url value="/js/trickservice/phase.js?version=${jsVersion}" />"></script>
	<script type="text/javascript" src="<spring:url value="/js/trickservice/rrfManager.js?version=${jsVersion}" />"></script>
	<script type="text/javascript" src="<spring:url value="/js/trickservice/rrf.js?version=${jsVersion}" />"></script>
	<script type="text/javascript" src="<spring:url value="/js/trickservice/analysisStandard.js?version=${jsVersion}" />"></script>
	<script type="text/javascript" src="<spring:url value="/js/trickservice/analysis-measure.js?version=${jsVersion}" />"></script>
	<c:if test="${!isProfile}">
		<script type="text/javascript" src="<spring:url value="/js/trickservice/actionplan.js?version=${jsVersion}" />"></script>
		<script type="text/javascript" src="<spring:url value="/js/trickservice/asset.js?version=${jsVersion}" />"></script>
		<script type="text/javascript" src="<spring:url value="/js/bootstrap/typeahead.bundle.js?version=${jsVersion}" />"></script>
		<script type="text/javascript" src="<spring:url value="/js/trickservice/analysisExport.js?version=${jsVersion}" />"></script>
		<script type="text/javascript" src="<spring:url value="/js/trickservice/risk-estimation.js?version=${jsVersion}" />"></script>
		<c:if test="${type.qualitative}" >
			<script type="text/javascript" src="<spring:url value="/js/trickservice/riskregister.js?version=${jsVersion}" />"></script>
			<script type="text/javascript">
				<!-- 
					application['measureStatus'] = { 'NA' : {title : '<spring:message code="label.title.measure.status.na"/>',value : '<spring:message code="label.measure.status.na"/>'},'AP' : {title : '<spring:message code="label.title.measure.status.ap"/>',value : '<spring:message code="label.measure.status.ap"/>'},'M' :{title : '<spring:message code="label.title.measure.status.m"/>',value : '<spring:message code="label.measure.status.m"/>'}};
					resolveMessage("label.title.impact", "<spring:message code='label.title.impact' />");
					resolveMessage("label.title.likelihood", "<spring:message code='label.title.likelihood' />");
					resolveMessage("label.status.na", "<spring:message code='label.status.na' />");
				-->
			</script>
		</c:if>
	</c:if>
	<script type="text/javascript">
	<!--
		application['analysisType'] = ANALYSIS_TYPE.valueOf('${type}');
		application['isProfile'] = ${isProfile};
		application['openMode'] = OPEN_MODE.valueOf('${open}');
		application['isLinkedToProject'] = ${isLinkedToProject};
		application['hasMaturity'] = ${hasMaturity==true};
		application['isDynamic']= ${type.quantitative && showDynamicAnalysis};
		application['actionPlanType']  = "${type=='QUALITATIVE'? 'APQ' : type == 'QUANTITATIVE'? show_uncertainty? 'APPO,APPN,APPP' : 'APPN' : show_uncertainty? 'APPO,APPN,APPP,APQ' : 'APPN,APQ'}".split(',');
		application['complianceType']  = "${type=='QUALITATIVE'? 'APQ' : type == 'QUANTITATIVE'? 'APPN' : 'APPN,APQ'}".split(',');
		resolveMessage("label.index.chapter", "<spring:message code='label.index.chapter' />");
		resolveMessage("label.metric.man_day", "<spring:message code='label.metric.man_day' />");
		resolveMessage("info.leave.page.in_mode_editing", "<spring:message code='info.leave.page.in_mode_editing' />");
		resolveMessage("label.dynamicparameter.evolution", "<spring:message code='label.dynamicparameter.evolution' />");
		-->
	</script>
</body>
</html>