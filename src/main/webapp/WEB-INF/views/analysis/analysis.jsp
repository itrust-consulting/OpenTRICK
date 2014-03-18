<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!-- ################################################################ Set Page Title ################################################################ -->
<c:set scope="request" var="title">title.analysis</c:set>
<!-- ###################################################################### HTML #################################################################### -->
<html>
<!-- Include Header -->
<jsp:include page="../header.jsp" />
<!-- ################################################################# Start Container ############################################################## -->
<body data-spy="scroll" data-target="#analysismenu" data-offset="40">
	<div id="wrap">
		<!-- ################################################################### Nav Menu ################################################################### -->
		<jsp:include page="../menu.jsp" />
		<div class="container">
			<!-- #################################################################### Content ################################################################### -->
				<!-- #################################################################### Analysis Menu ################################################################### -->
				<c:choose>
					<c:when test="${!empty(sessionScope.selectedAnalysis)}">
						<spring:eval expression="T(lu.itrust.business.component.MeasureManager).ConcatMeasure(analysis.analysisNorms)" var="measures" scope="request" />
						<jsp:include page="analysisMenu.jsp" />
						<jsp:include page="../successErrors.jsp" />
						<div id="nav-container" trick-id="${analysis.id}" trick-rights-id="${analysis.getRightsforUserString(login).right.ordinal()}">
							<c:if test="${!KowledgeBaseView}">
								<h2> ${analysis.label} | ${ analysis.version } </h2>
								<c:set var="histories" value="${analysis.histories}" scope="request" />
								<jsp:include page="./components/history.jsp" />
							</c:if>
							<c:if test="${KowledgeBaseView}">
								<h2> ${analysis.identifier} | ${ analysis.version } </h2>
							</c:if>
							<c:set var="itemInformations" value="${analysis.itemInformations}" scope="request" />
							<jsp:include page="./components/itemInformation.jsp" />
							<c:set var="parameters" value="${analysis.parameters}" scope="request" />
							<jsp:include page="./components/parameter.jsp" />
							<c:set var="assets" value="${analysis.assets}" scope="request" />
							<jsp:include page="./components/asset.jsp" />
							<c:set var="scenarios" value="${analysis.scenarios}" scope="request" />
							<jsp:include page="./components/scenario.jsp" />
							<c:set var="phases" scope="request" value="${analysis.usedPhases}" />
							<jsp:include page="./components/phase.jsp" />
							<jsp:include page="./components/measure.jsp" />
							<c:if test="${!KowledgeBaseView }">
								<c:set var="actionplans" scope="request" value="${analysis.actionPlans}" />
								<jsp:include page="./components/actionplan.jsp" />
								<script type="text/javascript" src="<spring:url value="js/actionplan.js" />"></script>
								<c:set var="summaries" scope="request" value="${analysis.summaries}" />
								<jsp:include page="./components/summary.jsp" />
								<c:set var="riskregister" scope="request" value="${analysis.riskRegisters}" />
								<jsp:include page="./components/riskregister.jsp" />
								<script type="text/javascript" src="<spring:url value="js/riskregister.js" />"></script>
								<jsp:include page="./components/charts.jsp" />
								<jsp:include page="./components/widgets.jsp" />
								
							</c:if>
						</div>
					</c:when>
					<c:otherwise>
						<jsp:include page="analyses.jsp" />
					</c:otherwise>
				</c:choose>
				<!-- ################################################################ Include Footer ################################################################ -->
			</div>
		</div>
		<!-- ################################################################ End Container ################################################################# -->
		<jsp:include page="../footer.jsp" />
		<jsp:include page="../scripts.jsp" />
		<c:if test="${!empty(sessionScope.selectedAnalysis)}">
			<script type="text/javascript">
				reloadCharts();
				
			</script>
		</c:if>
		<script type="text/javascript">
			$(document).ready(function() {
				$("input[type='checkbox']").removeAttr("checked");
				//adaptHeaderTabletoData("section_measure_27001");
			});
			
			function adaptHeaderTabletoData(container) {
				var tableheader = $("#"+container).find("table[class~='tableheaderrow']:first");
				var tablescrollarea = $("#"+container).find("table[class~='tablescrollarea']:first");
				tablescrollarea.find("tr:nth-child(3) td").each(function(index){
					var bodywidth=$(this).outerWidth();
					tableheader.find("tr:first th:nth-child("+(index+1)+")").outerWidth(bodywidth);
										
				});
			}
		
		</script>
			<c:if test="${empty(sessionScope.selectedAnalysis)}">
			<script type="text/javascript">
			$(function() {
				analysisTableSortable();
			});
			</script>
			</c:if>
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>
